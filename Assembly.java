import java.io.FileWriter;
import java.io.IOException;

class Assembly {

    private final int SizeOfInt = 4;
    private final FileWriter writer;
    private final SymbolTable symtable;
    String main;
    int varsize;
    int np = 0;
    private String current_fun;

    Assembly(FileWriter wr, SymbolTable st) {
        writer = wr;
        symtable = st;

        try {

            writer.append(".intel_syntax noprefix\n\n.text\n\n.global main\n\nmain:    	jmp ");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private void getAR(String a) {

        Node node = symtable.lookup(new Key(a));            //At this point node will never be null

        int na = node.scope;
        Integer size = 2 * SizeOfInt;
        String of = size.toString();

        try {
            writer.append("		mov esi, DWORD PTR [ebp+").append(of).append("]			#Get Activation Record\n");

            for (int i = 0; i < np - na - 1; i++) {
                writer.append("		mov esi, DWORD PTR [esi+").append(of).append("]			#Get Activation Record\n");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private void updateAL(int nx) {

        Integer size = 2 * SizeOfInt;
        String of = size.toString();
        int nnp = np - 1;

        try {
            if (nnp < nx) {
                writer.append("		push ebp			#Update Access Link\n");
            } else if (nnp == nx) {
                writer.append("		push DWORD PTR [ebp+").append(of).append("]			#Update Access Link\n");
            } else {
                writer.append("		mov esi, DWORD PTR [ebp+").append(of).append("]			#Update Access Link\n");
                for (int i = 0; i < nnp - nx; i++) {
                    writer.append("		mov esi, DWORD PTR [esi+").append(of).append("]\n");
                }

                writer.append("		push esi\n");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private void load(String R, String a, ScopeTemp temps) {

        try {
            a = a.trim();

            if (a.charAt(0) >= '0' && a.charAt(0) <= '9') {                //a is a number
                writer.append("		mov ").append(R).append(", ").append(a).append(("			#Load Value\n"));
            } else if (a.charAt(0) == '[') {                                //array element

                char[] str = new char[a.length() + 1];

                a.getChars(1, a.length() - 1, str, 0);
                str[a.length() - 2] = '\0';

                String mystr = new String(str);
                load("edi", mystr, temps);

                Temp tmp = temps.findElement(mystr.trim());

                if (tmp.type.equals("int"))
                    writer.append("		mov ").append(R).append(", DWORD PTR [edi]\n");
                else writer.append("		movzx ").append(R).append(", BYTE PTR [edi]\n");

            } else if (a.charAt(0) == '\'') {                                // a is a character

                String str = mySloppyFun(a);
                writer.append("		mov ").append(R).append(", ").append(str).append("			#Load Value\n");
            } else if (a.charAt(0) == '$') {                                        //temporary variable -- by value

                Temp tmp = temps.findElement(a);
                Integer offset = tmp.offset;

                if (tmp.type.equals("int") || tmp.tempname.endsWith("*"))
                    writer.append("		mov ").append(R).append(", DWORD PTR [ebp-").append(offset.toString()).append("]			#Load Value\n");

                else
                    writer.append("		movzx ").append(R).append(", BYTE PTR [ebp-").append(offset.toString()).append("]			#Load Value\n");

            } else {
                Node myNode = symtable.lookup(new Key(a));    //It won't be null
                Integer offset = myNode.offset;

                if (myNode.scope == temps.scope) {        //LOCAL

                    if (myNode.par) {

                        if (!myNode.reference) {                                            //LOCAL PARAMETER - BY VALUE

                            if (myNode.type.equals("int"))
                                writer.append("		mov ").append(R).append(", DWORD PTR [ebp+").append(offset.toString()).append("]			#Load Value\n");

                            else
                                writer.append("		movzx ").append(R).append(", BYTE PTR [ebp+").append(offset.toString()).append("]			#Load Value\n");
                        } else {                                                                    //LOCAL PARAMETER - BY REFERENCE
                            writer.append("		mov esi, DWORD PTR [ebp+").append(offset.toString()).append("]			#Load Value\n");
                            writer.append("		mov ").append(R).append(", DWORD PTR [esi]\n");
                        }
                    } else {                                                                        //LOCAL VARIABLE

                        if (myNode.type.equals("int"))
                            writer.append("		mov ").append(R).append(", DWORD PTR [ebp-").append(offset.toString()).append("]			#Load Value\n");

                        else
                            writer.append("		movzx ").append(R).append(", BYTE PTR [ebp-").append(offset.toString()).append("]			#Load Value\n");
                    }
                } else {                                    //NON LOCAL
                    getAR(a);

                    if (myNode.par) {

                        if (!myNode.reference) {                                            //LOCAL PARAMETER - BY VALUE

                            if (myNode.type.equals("int"))
                                writer.append("		mov ").append(R).append(", DWORD PTR [esi+").append(offset.toString()).append("]			#Load Value\n");

                            else
                                writer.append("		movzx ").append(R).append(", BYTE PTR [esi+").append(offset.toString()).append("]			#Load Value\n");
                        } else {                                                                    //LOCAL PARAMETER - BY REFERENCE
                            writer.append("		mov esi, DWORD PTR [esi+").append(offset.toString()).append("]			#Load Value\n");
                            writer.append("		mov ").append(R).append(", DWORD PTR [esi]\n");
                        }
                    } else {                                                                        //NON LOCAL VARIABLE

                        if (myNode.type.equals("int"))
                            writer.append("		mov ").append(R).append(", DWORD PTR [esi-").append(offset.toString()).append("]			#Load Value\n");

                        else
                            writer.append("		movzx ").append(R).append(", BYTE PTR [esi-").append(offset.toString()).append("]			#Load Value\n");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private void loadAddr(String R, String a, ScopeTemp temps) {
        try {
            a = a.trim();

            if (a.charAt(0) == '[') {                                                //array element
                char[] str = new char[a.length() + 1];

                a.getChars(1, a.length() - 1, str, 0);
                str[a.length() - 2] = '\0';

                String mystr = new String(str);

                load(R, mystr, temps);
            } else if (a.charAt(0) == '$') {                                        //temporary variable -- by value

                Temp tmp = temps.findElement(a);
                Integer offset = tmp.offset;

                if (tmp.strname != null)                //It's a string
                    writer.append("		lea ").append(R).append(", DWORD PTR [ebp-").append(offset.toString()).append("]			#Load String's Address\n");

                else if (a.endsWith("*"))            //Then temp holds already a reference
                    writer.append("		mov ").append(R).append(", DWORD PTR [ebp-").append(offset.toString()).append("]			#Load Address\n");

                else
                    writer.append("		lea ").append(R).append(", DWORD PTR [ebp-").append(offset.toString()).append("]			#Load Address\n");
            } else {
                Node myNode = symtable.lookup(new Key(a));    //It won't be null
                Integer offset = myNode.offset;

                if (myNode.scope == temps.scope) {        //LOCAL

                    if (myNode.par) {

                        if (!myNode.reference)                                            //LOCAL PARAMETER - BY VALUE
                            writer.append("		lea ").append(R).append(", DWORD PTR [ebp+").append(offset.toString()).append("]			#Load Address\n");

                        else                                                                    //LOCAL PARAMETER - BY REFERENCE
                            writer.append("		mov ").append(R).append(", DWORD PTR [ebp+").append(offset.toString()).append("]			#Load Address\n");
                    } else {                                                                            //LOCAL VARIABLE
                        writer.append("		lea ").append(R).append(", DWORD PTR [ebp-").append(offset.toString()).append("]			#Load Address\n");
                    }
                } else {                                    //NON LOCAL
                    getAR(a);

                    if (myNode.par) {

                        if (!myNode.reference)                                            //LOCAL PARAMETER - BY VALUE
                            writer.append("		lea ").append(R).append(", DWORD PTR [esi+").append(offset.toString()).append("]			#Load Address\n");

                        else                                                                    //LOCAL PARAMETER - BY REFERENCE
                            writer.append("		mov ").append(R).append(", DWORD PTR [esi+").append(offset.toString()).append("]			#Load Address\n");
                    } else {                                                                        //NON LOCAL VARIABLE		
                        writer.append("		lea ").append(R).append(", DWORD PTR [esi-").append(offset.toString()).append("]			#Load Address\n");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }


    private void store(String R, String a, ScopeTemp temps) {

        try {
            a = a.trim();

            if (a.charAt(0) == '[') {                                //array element
                char[] str = new char[a.length() + 1];

                a.getChars(1, a.length() - 1, str, 0);
                str[a.length() - 2] = '\0';

                String mystr = new String(str);

                load("edi", mystr, temps);

                Temp tmp = temps.findElement(mystr.trim());

                if (tmp.type.equals("int"))
                    writer.append("		mov DWORD PTR [edi], ").append(R).append("\n");
                else writer.append("		mov BYTE PTR [edi], ").append(R.substring(1, 2)).append("l").append("\n");
            } else if (a.charAt(0) == '$') {                                        //temporary variable -- by value

                Temp tmp = temps.findElement(a);
                Integer offset = tmp.offset;

                if (tmp.type.equals("int") || tmp.tempname.endsWith("*")) {
                    writer.append("		mov DWORD PTR [ebp-").append(offset.toString()).append("], ").append(R).append("			#Store Value\n");
                } else
                    writer.append("		mov BYTE PTR [ebp-").append(offset.toString()).append("], ").append(R.substring(1, 2)).append("l").append("			#Store Value\n");
            } else {
                Node myNode = symtable.lookup(new Key(a));    //It won't be null
                Integer offset = myNode.offset;

                if (myNode.scope == temps.scope) {        //LOCAL

                    if (myNode.par) {

                        if (!myNode.reference) {                                            //LOCAL PARAMETER - BY VALUE

                            if (myNode.type.equals("int"))
                                writer.append("		mov DWORD PTR [ebp+").append(offset.toString()).append("], ").append(R).append("			#Store Value\n");
                            else
                                writer.append("		mov BYTE PTR [ebp+").append(offset.toString()).append("], ").append(R.substring(1, 2)).append("l").append("			#Store Value\n");
                        } else {                                                                    //LOCAL PARAMETER - BY REFERENCE
                            writer.append("		mov esi, DWORD PTR [ebp+").append(offset.toString()).append("]			#Store Value\n");
                            writer.append("		mov DWORD PTR [esi], ").append(R).append("\n");
                        }
                    } else {                                                                        //LOCAL VARIABLE			
                        if (myNode.type.equals("int"))
                            writer.append("		mov DWORD PTR [ebp-").append(offset.toString()).append("], ").append(R).append("			#Store Value\n");
                        else
                            writer.append("		mov BYTE PTR [ebp-").append(offset.toString()).append("], ").append(R.substring(1, 2)).append("l").append("			#Store Value\n");
                    }
                } else {                                    //NON LOCAL
                    getAR(a);

                    if (myNode.par) {

                        if (!myNode.reference) {                                            //LOCAL PARAMETER - BY VALUE

                            if (myNode.type.equals("int"))
                                writer.append("		mov DWORD PTR [esi+").append(offset.toString()).append("], ").append(R).append("			#Store Value\n");
                            else
                                writer.append("		mov BYTE PTR [esi+").append(offset.toString()).append("], ").append(R.substring(1, 2)).append("l").append("			#Store Value\n");
                        } else {                                                                    //LOCAL PARAMETER - BY REFERENCE
                            writer.append("		mov esi, DWORD PTR [esi+").append(offset.toString()).append("]			#Store Value\n");
                            writer.append("		mov DWORD PTR [esi], ").append(R).append("\n");
                        }
                    } else {                                                                        //NON LOCAL VARIABLE
                        if (myNode.type.equals("int"))
                            writer.append("		mov DWORD PTR [esi-").append(offset.toString()).append("], ").append(R).append("			#Store Value\n");
                        else
                            writer.append("		mov BYTE PTR [esi-").append(offset.toString()).append("], ").append(R.substring(1, 2)).append("l").append("			#Store Value\n");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    /* ************************************************************************************************************************************ */
    void createAssembly(Quad quad, Integer i, ScopeTemp temps) {

        try {

            Integer scope;
            Node nd;
            String type;
            Integer size;

            writer.append("\n_").append(i.toString()).append(":\n");


            switch (quad.opcode) {

                case "nop":

                    break;

                case "<-":

                    load("eax", quad.op1, temps);
                    store("eax", quad.dest, temps);

                    break;


                case "array":

                    Temp tmp;

                    tmp = temps.findElement(quad.op1);
                    if (tmp != null && tmp.strname != null) {            //It's a string

                        Integer offset = passStringToStack(tmp);

                        writer.append("		mov BYTE PTR [ebp-").append(offset.toString()).append("], 0\n");
                    }


                    type = "";
                    nd = symtable.lookup(new Key(quad.op1));
                    if (nd != null)
                        type = nd.type;
                    else {
                        tmp = temps.findElement(quad.op1);
                        if (tmp != null) {
                            type = tmp.type;
                        }
                    }

                    int sizeOfChar = 1;
                    if (type.equals("int"))
                        size = SizeOfInt;
                    else size = sizeOfChar;

                    load("eax", quad.op2, temps);
                    writer.append("		mov ecx, ").append(size.toString()).append("\n");
                    writer.append("		imul ecx\n");
                    loadAddr("ecx", quad.op1, temps);
                    writer.append("		add eax, ecx\n");
                    store("eax", quad.dest, temps);

                    break;





                /* *************** Expressions **************** */

                case "+":

                    load("eax", quad.op1, temps);
                    load("ecx", quad.op2, temps);
                    writer.append("		add eax, ecx\n");
                    store("eax", quad.dest, temps);
                    break;


                case "-":

                    load("eax", quad.op1, temps);
                    load("ecx", quad.op2, temps);
                    writer.append("		sub eax, ecx\n");
                    store("eax", quad.dest, temps);

                    break;


                case "*":

                    load("eax", quad.op1, temps);
                    load("ebx", quad.op2, temps);
                    writer.append("		imul eax, ebx\n");
                    store("eax", quad.dest, temps);

                    break;


                case "/":

                    load("eax", quad.op1, temps);
                    writer.append("		cdq\n");
                    load("ebx", quad.op2, temps);
                    writer.append("		idiv ebx\n");
                    store("eax", quad.dest, temps);

                    break;


                case "%":

                    load("eax", quad.op1, temps);
                    writer.append("		cdq\n");
                    load("ebx", quad.op2, temps);
                    writer.append("		idiv ebx\n");
                    store("edx", quad.dest, temps);

                    break;





                /* *************** Conditions **************** */

                case "=":

                    load("eax", quad.op1, temps);
                    load("edx", quad.op2, temps);
                    writer.append("		cmp eax, edx\n");
                    writer.append("		je ");
                    label("_" + quad.dest);

                    break;

                case "#":

                    load("eax", quad.op1, temps);
                    load("edx", quad.op2, temps);
                    writer.append("		cmp eax, edx\n");
                    writer.append("		jne ");
                    label("_" + quad.dest);

                    break;


                case "<":

                    load("eax", quad.op1, temps);
                    load("edx", quad.op2, temps);
                    writer.append("		cmp eax, edx\n");
                    writer.append("		jl ");
                    label("_" + quad.dest);

                    break;

                case ">":

                    load("eax", quad.op1, temps);
                    load("edx", quad.op2, temps);
                    writer.append("		cmp eax, edx\n");
                    writer.append("		jg ");
                    label("_" + quad.dest);

                    break;

                case "<=":

                    load("eax", quad.op1, temps);
                    load("edx", quad.op2, temps);
                    writer.append("		cmp eax, edx\n");
                    writer.append("		jle ");
                    label("_" + quad.dest);

                    break;

                case ">=":

                    load("eax", quad.op1, temps);
                    load("edx", quad.op2, temps);
                    writer.append("		cmp eax, edx\n");
                    writer.append("		 jge ");
                    label("_" + quad.dest);

                    break;


                /* ************************************************ */


                case "jump":

                    writer.append("		jmp ");
                    label("_" + quad.dest);

                    break;


                case ":-":

                    load("eax", quad.op1, temps);
                    size = 3 * SizeOfInt;

                    writer.append("		mov esi, DWORD PTR [ebp+").append(size.toString()).append("]\n");

                    nd = symtable.lookup(new Key(current_fun));

                    if (nd.retvalue.equals("int"))
                        writer.append("		mov DWORD PTR [esi], eax\n");
                    else writer.append("		mov BYTE PTR [esi], eax\n");

                    break;



                /* ************************************************ */

                case "par":

                    if (quad.op2.equals("V")) {

                        load("eax", quad.op1, temps);
                        writer.append("		push eax				#Push Parameter\n");
                    } else {

                        tmp = temps.findElement(quad.op1);
                        if (tmp != null && tmp.strname != null) {            //It's a string

                            Integer offset = passStringToStack(tmp);

                            writer.append("		mov BYTE PTR [ebp-").append(offset.toString()).append("], 0\n");
                            offset = tmp.offset;
                            writer.append("		lea  esi, DWORD PTR [ebp-").append(offset.toString()).append("]\n");
                            writer.append("		push esi				#Push String Address\n");
                        } else {
                            loadAddr("esi", quad.op1, temps);
                            writer.append("		push esi				#Push Address\n");
                        }
                    }

                    break;


                case "call":

                    nd = symtable.lookup(new Key(quad.dest));                //Callee
                    scope = nd.scope;
                    Integer offset;
                    Integer ofs = SizeOfInt;

                    if (nd.retvalue.equals("nothing")) {
                        writer.append("		sub esp, ").append(ofs.toString());
                        writer.append("				#Skip the address of returned value\n");
                    }

                    size = 0;
                    if (nd.params != null)
                        size = SizeOfInt * nd.params.size();

                    if (nd.scope != 0 || nd.name.name.equals(main)) {                        //If it is a library function -> no access link field
                        updateAL(nd.scope);
                        offset = 2 * SizeOfInt + size;
                    } else offset = SizeOfInt + size;

                    writer.append("		call _");
                    writer.append(nd.name.name).append("_").append(scope.toString());
                    writer.append("\n");
                    writer.append("		add esp, ").append(offset.toString());
                    writer.append("				#Clean Parameters\n");

                    break;


                case "ret":

                    scope = temps.scope - 1;
                    writer.append("		jmp end_").append(current_fun).append("_");
                    writer.append(scope.toString());
                    writer.append("\n");
                    break;


                case "unit":

                    nd = symtable.lookup(new Key(quad.op1));
                    scope = nd.scope;
                    String name = nd.name.name + "_" + scope.toString();

                    name(name);

                    size = varsize;
                    writer.append("		push ebp\n");
                    writer.append("		mov ebp, esp\n");
                    writer.append("		sub esp, ").append(size.toString()).append("				#Allocate memory for all local variables\n");

                    current_fun = nd.name.name;

                    break;


                case "endu":

                    nd = symtable.lookup(new Key(quad.op1));
                    scope = nd.scope;
                    endof(nd.name.name + "_" + scope.toString());

                    writer.append("		mov esp, ebp\n");
                    writer.append("		pop ebp\n");
                    writer.append("		ret\n");

                    break;

                /* ************************************************ */


                default:
                    System.out.println("Wrong quadruple");
                    System.exit(1);
                    break;

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

    }

    private Integer passStringToStack(Temp tmp) throws IOException {
        Integer offset = tmp.offset;
        String value;

        for (int x = 1; x < tmp.strlen; x++) {

            if (tmp.strname.charAt(x) == '\\') {

                if (tmp.strname.charAt(x + 1) == 'x') {
                    value = tmp.strname.substring(x, x + 3);
                    value = "\'" + value + "\'";
                    value = mySloppyFun(value);
                    x += 3;
                } else {
                    value = tmp.strname.substring(x, x + 2);
                    value = "\'" + value + "\'";
                    value = mySloppyFun(value);
                    x++;
                }
            } else {
                Integer n = (int) tmp.strname.charAt(x);
                value = n.toString();
            }

            writer.append("		mov BYTE PTR [ebp-").append(offset.toString()).append("], ").append(value).append("\n");
            offset -= 1;
        }
        return offset;
    }

    /* ************************************************** */

    private void endof(String name) {
        try {
            writer.append("\nend_").append(name).append(":\n");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private void name(String name) {
        try {
            writer.append("\n_").append(name).append(":\n");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private void label(String label) {
        try {
            writer.append(label).append("\n");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }


    private String mySloppyFun(String a) {

        Character asci = null;

        String str;
        Integer n;
        if (a.charAt(1) == '\\' && a.charAt(2) != 'x') {

            switch (a.charAt(2)) {

                case 'n':
                    asci = '\n';
                    break;

                case 't':
                    asci = '\t';
                    break;

                case 'r':
                    asci = '\r';
                    break;

                case '0':
                    asci = '\0';
                    break;

                case '\\':
                    asci = '\\';
                    break;

                case '\'':
                    asci = '\\';
                    break;

                case '"':
                    asci = '\"';
                    break;

                default:
                    break;
            }
            n = (int) asci;
            str = n.toString();
        
		}else if (a.charAt(1) == '\\' && a.charAt(2) == 'x') {
            str = String.valueOf(Long.parseLong(a.substring(3, 5), 16));

        } else {
            asci = a.charAt(1);
            n = (int) asci;
            str = n.toString();
        }

        return str;
    }

}
