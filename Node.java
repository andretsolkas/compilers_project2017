import java.util.LinkedList;

class Node {

    final Key name;                                            //Identifier
    final String type;                                        //Variable's type
    final int scope;                                            //Scope
    final Boolean reference;                                    //reference
    final LinkedList<Param> params;                            //If not function, gets null- holds a function's parameters
    final LinkedList<Integer> arraylist;                        //In case it's an array
    final String retvalue;                                    //Return Value
    final boolean par;
    final int offset;
    final Node prevNode;
    Boolean defined;                                    //In case of function - True if it's been defined, false if not


    public Node(Key idName, String tp, int sp, Boolean ref, LinkedList<Param> pm, LinkedList<Integer> arlist, String ret, Boolean def, int of, boolean param, Node prevnode) {

        name = new Key(idName.name);

        if (tp != null) {                                                            //Copying all values
            type = tp;
        } else type = null;

        scope = sp;

        if (ref != null) {
            reference = ref;
        } else reference = null;

        if (pm != null) {
            params = new LinkedList<>();

            for (Param aPm : pm) {
                params.addLast(new Param(aPm.type, aPm.idname, aPm.reference, aPm.arraylist));
            }
        } else params = null;

        if (arlist != null && !arlist.isEmpty()) {
            arraylist = new LinkedList<>();

            for (Integer anArlist : arlist) arraylist.addLast(anArlist);
        } else arraylist = null;

        if (ret != null) {
            retvalue = ret;
        } else retvalue = null;

        if (def != null) {
            defined = def;
        } else defined = null;

        offset = of;

        par = param;
        prevNode = prevnode;

    }


    void print() {

        System.out.println("Name = " + name.name);

        System.out.println("Type = " + type);

        System.out.println("Scope = " + scope);

        System.out.println("Reference = " + reference);

        System.out.println("Return Value = " + retvalue);

        System.out.println("Defined = " + defined);

        System.out.println("Offset = " + offset);

        System.out.println("Is Param = " + par);

        int i;

        if (params != null) {

            System.out.printf("Parameters = ");

            for (i = 0; i < params.size() - 1; i++) {

                params.get(i).print();
                System.out.printf(",  ");
            }
            params.get(i).print();
            System.out.printf("\n");
        } else {

            System.out.println("Parameters = null");


            if (retvalue == null) {                                //Not a function

                if (arraylist != null && !arraylist.isEmpty()) {

                    System.out.printf("ArrayList = ");

                    for (i = 0; i < arraylist.size() - 1; i++)
                        System.out.printf("%d, ", arraylist.get(i));
                    System.out.printf("%d\n", arraylist.get(i));
                }
            }
        }

        System.out.print("\n\n");

    }
}
