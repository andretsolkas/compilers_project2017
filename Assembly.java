import java.io.FileWriter;
import java.util.LinkedList;

public class Assembly{

	int SizeOfInt = 4;
	int SizeOfChar = 1;
	String main;
	String current_fun;
	int parsize;
	int varsize;
	int np = 0;
	boolean par;
	FileWriter writer;
	SymbolTable symtable;
	
	public Assembly(FileWriter wr, SymbolTable st){
		writer = wr;
		symtable = st;
		par = false;
		
		try{
			
			writer.append(".intel_syntax noprefix\n\n.text\n\n.global main\n\nmain:    	jmp ");
		}
    	catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
								
	public void getAR(String a){
		
		Node node = symtable.lookup(new Key(a));			//At this point node will never be null

		int na = node.scope;
		Integer size = 2*SizeOfInt;
		String of = size.toString();

		try{
			writer.append("		mov esi, DWORD PTR [ebp+".concat(of).concat("]			#Get Activation Record\n"));
			
			for(int i=0; i < np-na-1; i++){	
				writer.append("		mov esi, DWORD PTR [esi+".concat(of).concat("]			#Get Activation Record\n"));
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
	
	public void updateAL(int nx){
		
		Integer size = 2*SizeOfInt;
		String of = size.toString(); 
		int nnp = np-1;

		try{
			if(nnp < nx){
				writer.append("		push ebp			#Update Access Link\n");
			}
			
			else if(nnp == nx){
				writer.append("		push DWORD PTR [ebp+".concat(of).concat("]			#Update Access Link\n"));
			}
			
			else{
				writer.append("		mov esi, DWORD PTR [ebp+".concat(of).concat("]			#Update Access Link\n"));
				for(int i=0; i < nnp-nx; i++){
					writer.append("		mov esi, DWORD PTR [esi+".concat(of).concat("]\n"));
				}
				
				writer.append("		push DWORD PTR [esi]\n");
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}

	public void load(String R, String a, ScopeTemp temps, LinkedList<Param> params){
		
		try{
			a = a.trim();
			
			if(a.charAt(0) >= '0' && a.charAt(0) <= '9'){				//a is a number
				writer.append("		mov ".concat(R).concat(", ".concat(a).concat(("			#Load Value\n"))));
			}
			
			else if(a.charAt(0) == '['){								//array element
				
				char[] str = new char[a.length()+1];
				
				a.getChars(1, a.length()-1, str, 0);
				str[a.length()-2] = '\0';
				
				String mystr = new String(str);
				load("edi", mystr, temps, params);

				Temp tmp = temps.findElement(mystr.trim());
				
				if(tmp.type.equals("int"))
					writer.append("		mov ".concat(R).concat(", DWORD PTR [edi]\n"));
				else writer.append("		movzx ".concat(R).concat(", BYTE PTR [edi]\n"));

			}
			
			
			else if(a.charAt(0) == '\''){								// a is a character

				String str = mySloppyFun(a);
				writer.append("		mov ".concat(R).concat(", ").concat(str).concat("			#Load Value\n"));
			}
			

			else if(a.charAt(0) == '$'){										//temporary variable -- by value
				
				Temp tmp = temps.findElement(a);
				Integer offset = tmp.offset;
				
				if(tmp.type.equals("int") || tmp.tempname.endsWith("*"))
					writer.append("		mov ".concat(R).concat(", DWORD PTR [ebp-".concat(offset.toString().concat("]			#Load Value\n"))));

				else
					writer.append("		movzx ".concat(R).concat(", BYTE PTR [ebp-".concat(offset.toString().concat("]			#Load Value\n"))));
				
                }
				
			else{	
					Node myNode = symtable.lookup(new Key(a));	//It won't be null
					Integer offset = myNode.offset;

					if(myNode.scope == temps.scope){		//LOCAL
		
						if(myNode.par == true){
							
							if(myNode.reference == false){											//LOCAL PARAMETER - BY VALUE
									
								if(myNode.type.equals("int"))
									writer.append("		mov ".concat(R).concat(", DWORD PTR [ebp+".concat(offset.toString().concat("]			#Load Value\n"))));
								
								else
									writer.append("		movzx ".concat(R).concat(", BYTE PTR [ebp+".concat(offset.toString().concat("]			#Load Value\n"))));
							}
						
							else{																	//LOCAL PARAMETER - BY REFERENCE
								writer.append("		mov esi, DWORD PTR [ebp+".concat(offset.toString().concat("]			#Load Value\n"))); 
								writer.append("		mov ".concat(R).concat(", DWORD PTR [esi]\n"));
							}
						}

						else{																		//LOCAL VARIABLE

							if(myNode.type.equals("int"))
								writer.append("		mov ".concat(R).concat(", DWORD PTR [ebp-".concat(offset.toString().concat("]			#Load Value\n"))));
							
							else
								writer.append("		movzx ".concat(R).concat(", BYTE PTR [ebp-".concat(offset.toString().concat("]			#Load Value\n"))));
						}
					}
					else{									//NON LOCAL
						getAR(a);
						
						if(myNode.par == true){	
							
							if(myNode.reference == false){											//LOCAL PARAMETER - BY VALUE
									
								if(myNode.type.equals("int"))
									writer.append("		mov ".concat(R).concat(", DWORD PTR [esi+".concat(offset.toString().concat("]			#Load Value\n"))));
								
								else
									writer.append("		movzx ".concat(R).concat(", BYTE PTR [esi+".concat(offset.toString().concat("]			#Load Value\n"))));					
							}
						
							else{																	//LOCAL PARAMETER - BY REFERENCE
								writer.append("		mov esi, DWORD PTR [esi+".concat(offset.toString().concat("]			#Load Value\n"))); 
								writer.append("		mov ".concat(R).concat(", DWORD PTR [esi]\n"));
							}
						}	
						else{																		//NON LOCAL VARIABLE
							
							if(myNode.type.equals("int"))
								writer.append("		mov ".concat(R).concat(", DWORD PTR [esi-".concat(offset.toString().concat("]			#Load Value\n"))));
							
							else 
								writer.append("		movzx ".concat(R).concat(", BYTE PTR [esi-".concat(offset.toString().concat("]			#Load Value\n"))));
						}
					}
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
	
	public void loadAddr(String R, String a, ScopeTemp temps, LinkedList<Param> params){	
		try{
			a = a.trim();

			if(a.charAt(0) == '['){												//array element
				char[] str = new char[a.length()+1];
				
				a.getChars(1, a.length()-1, str, 0);
				str[a.length()-2] = '\0';
				
				String mystr = new String(str);
				
				load(R, mystr, temps, params);
			}

			else if(a.charAt(0) == '$'){										//temporary variable -- by value
				
				Temp tmp = temps.findElement(a);
				Integer offset = tmp.offset;
				
				if(tmp.strname != null)				//It's a string
					writer.append("		lea ".concat(R).concat(", DWORD PTR [ebp-".concat(offset.toString().concat("]			#Load String's Address\n"))));
				
				else if(a.endsWith("*"))			//Then temp holds already a reference
					writer.append("		mov ".concat(R).concat(", DWORD PTR [ebp-".concat(offset.toString().concat("]			#Load Address\n"))));
				
				else writer.append("		lea ".concat(R).concat(", DWORD PTR [ebp-".concat(offset.toString().concat("]			#Load Address\n"))));
			}
				
			else{
					Node myNode = symtable.lookup(new Key(a));	//It won't be null
					Integer offset = myNode.offset;
					
					if(myNode.scope == temps.scope){		//LOCAL
		
						if(myNode.par == true){
							
							if(myNode.reference == false)											//LOCAL PARAMETER - BY VALUE
								writer.append("		lea ".concat(R).concat(", DWORD PTR [ebp+".concat(offset.toString().concat("]			#Load Address\n"))));
						
							else																	//LOCAL PARAMETER - BY REFERENCE
								writer.append("		mov ".concat(R).concat(", DWORD PTR [ebp+".concat(offset.toString().concat("]			#Load Address\n")))); 
						}

						else{																			//LOCAL VARIABLE
							writer.append("		lea ".concat(R).concat(", DWORD PTR [ebp-".concat(offset.toString().concat("]			#Load Address\n"))));
						}
					}
					else{									//NON LOCAL
						getAR(a);
						
						if(myNode.par == true){
							
							if(myNode.reference == false)											//LOCAL PARAMETER - BY VALUE
								writer.append("		lea ".concat(R).concat(", DWORD PTR [esi+".concat(offset.toString().concat("]			#Load Address\n"))));
						
							else																	//LOCAL PARAMETER - BY REFERENCE
								writer.append("		mov ".concat(R).concat(", DWORD PTR [esi+".concat(offset.toString().concat("]			#Load Address\n")))); 
						}	
						else{																		//NON LOCAL VARIABLE		
							writer.append("		lea ".concat(R).concat(", DWORD PTR [esi-".concat(offset.toString().concat("]			#Load Address\n"))));
						}
					}
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
	
	
	public void store(String R, String a, ScopeTemp temps, LinkedList<Param> params){
		
		try{
			a = a.trim();
			
			if(a.charAt(0) == '['){								//array element
				char[] str = new char[a.length()+1];
				
				a.getChars(1, a.length()-1, str, 0);
				str[a.length()-2] = '\0';
				
				String mystr = new String(str);
				
				load("edi", mystr,temps, params);

				Temp tmp = temps.findElement(mystr.trim());
				
				if(tmp.type.equals("int"))
					writer.append("		mov DWORD PTR [edi], ".concat(R).concat("\n"));
				else writer.append("		mov BYTE PTR [edi], ".concat(R.substring(1, 2).concat("l")).concat("\n"));
			}
			

			else if(a.charAt(0) == '$'){										//temporary variable -- by value
				
				Temp tmp = temps.findElement(a);
				Integer offset = tmp.offset;
				
				if(tmp.type.equals("int") || tmp.tempname.endsWith("*")){
					writer.append("		mov DWORD PTR [ebp-".concat(offset.toString().concat("], ".concat(R).concat("			#Store Value\n"))));
				}
				else writer.append("		mov BYTE PTR [ebp-".concat(offset.toString().concat("], ".concat(R.substring(1, 2).concat("l")).concat("			#Store Value\n"))));
			}
				
			else{
					Node myNode = symtable.lookup(new Key(a));	//It won't be null
					Integer offset = myNode.offset;;
					
					if(myNode.scope == temps.scope){		//LOCAL
		
						if(myNode.par == true){
							
							if(myNode.reference == false){											//LOCAL PARAMETER - BY VALUE
									
								if(myNode.type.equals("int"))
									writer.append("		mov DWORD PTR [ebp+".concat(offset.toString().concat("], ".concat(R).concat("			#Store Value\n"))));
								else writer.append("		mov BYTE PTR [ebp+".concat(offset.toString().concat("], ".concat(R.substring(1, 2).concat("l")).concat("			#Store Value\n"))));
							}
						
							else{																	//LOCAL PARAMETER - BY REFERENCE
								writer.append("		mov esi, DWORD PTR [ebp+".concat(offset.toString().concat("]			#Store Value\n"))); 
								writer.append("		mov DWORD PTR [esi], ".concat(R).concat("\n"));
							}
						}

						else{																		//LOCAL VARIABLE			
							if(myNode.type.equals("int"))
								writer.append("		mov DWORD PTR [ebp-".concat(offset.toString().concat("], ".concat(R).concat("			#Store Value\n"))));
							else writer.append("		mov BYTE PTR [ebp-".concat(offset.toString().concat("], ".concat(R.substring(1, 2).concat("l")).concat("			#Store Value\n"))));
						}
					}
					else{									//NON LOCAL
						getAR(a);
						
						if(myNode.par == true){
							
							if(myNode.reference == false){											//LOCAL PARAMETER - BY VALUE
									
								if(myNode.type.equals("int"))
									writer.append("		mov DWORD PTR [esi+".concat(offset.toString().concat("], ".concat(R).concat("			#Store Value\n"))));
								else writer.append("		mov BYTE PTR [esi+".concat(offset.toString().concat("], ".concat(R.substring(1, 2).concat("l")).concat("			#Store Value\n"))));
							}
						
							else{																	//LOCAL PARAMETER - BY REFERENCE
								writer.append("		mov esi, DWORD PTR [esi+".concat(offset.toString().concat("]			#Store Value\n"))); 
								writer.append("		mov DWORD PTR [esi], ".concat(R).concat("\n"));
							}
						}	
						else{																		//NON LOCAL VARIABLE
							if(myNode.type.equals("int"))
								writer.append("		mov DWORD PTR [esi-".concat(offset.toString().concat("], ".concat(R).concat("			#Store Value\n"))));
							else writer.append("		mov BYTE PTR [esi-".concat(offset.toString().concat("], ".concat(R.substring(1, 2).concat("l")).concat("			#Store Value\n"))));
						}
					}
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}	
	
	/**************************************************************************************************************************************/
	public void createAssembly(Quad quad, Integer i, ScopeTemp temps, LinkedList<Param> params){
		
		try{	
			
			Integer scope;
			Node nd;
			String type;
            Integer size;
            
				writer.append("\n_".concat(i.toString().concat(":")));
			
			
			switch(quad.opcode) {
	        
			case "<-":

	        	load("eax", quad.op1, temps, params);
	        	store("eax", quad.dest, temps, params);
			
	            break;
	            
	
	        case "array":
	        	
	        	Temp tmp;

	        	tmp = temps.findElement(quad.op1);
        		if(tmp != null && tmp.strname != null){			//It's a string

	        			Integer offset = tmp.offset;
	        			String value = null;
	        			
	        			for(int x=1; x<tmp.strlen; x++){
	        				
	        				if(tmp.strname.charAt(x) == '\\'){
        					
        					if(tmp.strname.charAt(x+1) == 'x'){
        						value = tmp.strname.substring(x, x+3);
        						
        						value = "\'".concat(value.concat("\'"));
        						value = mySloppyFun(value);
        						x += 3;
        					}
        					
        					else{ 
        						value = new String(tmp.strname.substring(x, x+2));
        						value = "\'".concat(value.concat("\'"));
        						value = mySloppyFun(value);
        						x++;
        					}
        				}
        				
        				else{
	        				Integer n = (int)tmp.strname.charAt(x);	
	        				value = n.toString();
        				}
        				
        				writer.append("		mov BYTE PTR [ebp-".concat(offset.toString().concat("], ".concat(value.concat("\n")))));
        				offset -= 1;
        			}
        		
        			writer.append("		mov BYTE PTR [ebp-".concat(offset.toString().concat("], 0\n")));
        		}
	        	

                type = "";
                nd = symtable.lookup(new Key(quad.op1));
                if(nd != null)
                    type = nd.type;
                else{
                    tmp = temps.findElement(quad.op1);
                    if(tmp != null){
                        type = tmp.type;
                    }    
                }

                if(type.equals("int"))
                    size = SizeOfInt;
                else size = SizeOfChar;

                load("eax", quad.op2, temps, params);
                writer.append("		mov ecx, ".concat(size.toString().concat("\n")));
                writer.append("		imul ecx\n");
                loadAddr("ecx", quad.op1, temps, params);
                writer.append("		add eax, ecx\n");
                store("eax", quad.dest, temps, params);
	                
	            break;
	
	            
	            
	            
	            
	            /**************** Expressions *****************/
	            
	        case "+":

        		load("eax", quad.op1, temps, params);
        		load("ecx", quad.op2, temps, params);
        		writer.append("		add eax, ecx\n");
        		store("eax", quad.dest, temps, params);
	            break;
	        

	        case "-":

        		load("eax", quad.op1, temps, params);
        		load("ecx", quad.op2, temps, params);
        		writer.append("		sub eax, ecx\n");
        		store("eax", quad.dest, temps, params);

	            break;
	
	
	        case "*":

        		load("eax", quad.op1, temps, params);
        		load("ebx", quad.op2, temps, params);
        		writer.append("		imul eax, ebx\n");
        		store("eax", quad.dest, temps, params);

	            break;
	            
	        
	        case "/":

        		load("eax", quad.op1, temps, params);
        		writer.append("		cdq\n");
        		load("ebx", quad.op2, temps, params);
        		writer.append("		idiv ebx\n");
        		store("eax", quad.dest, temps, params);
        	
	            break;
	        
	            
	        case "%":

        		load("eax", quad.op1, temps, params);
        		writer.append("		cdq\n");
        		load("ebx", quad.op2, temps, params);
        		writer.append("		idiv ebx\n");
        		store("edx", quad.dest, temps, params);
	        	
	            break;
	            

	            
	            

	            /**************** Conditions *****************/
	            
	        case "=":

	        	load("eax", quad.op1, temps, params);
	        	load("edx", quad.op2, temps, params);
	        	writer.append("		cmp eax, edx\n");
	        	writer.append("		je "); label("_".concat(quad.dest));
	
	            break;
	        
	        case "#":
	            
	        	load("eax", quad.op1, temps, params);
	        	load("edx", quad.op2, temps, params);
	        	writer.append("		cmp eax, edx\n");
	        	writer.append("		jne "); label("_".concat(quad.dest));
	        	
	            break;
	            
	            
	        case "<":

	        	load("eax", quad.op1, temps, params);
	        	load("edx", quad.op2, temps, params);
	        	writer.append("		cmp eax, edx\n");
	        	writer.append("		jl "); label("_".concat(quad.dest));

	            break;
	        
	        case ">":
	            
	        	load("eax", quad.op1, temps, params);
	        	load("edx", quad.op2, temps, params);
	        	writer.append("		cmp eax, edx\n");
	        	writer.append("		jg "); label("_".concat(quad.dest));

	            break;
	            
	        case "<=":
	            
	        	load("eax", quad.op1, temps, params);
	        	load("edx", quad.op2, temps, params);
	        	writer.append("		cmp eax, edx\n");
	        	writer.append("		jle "); label("_".concat(quad.dest));
	        	
	            break;
	        
	        case ">=":

	        	load("eax", quad.op1, temps, params);
	        	load("edx", quad.op2, temps, params);
	        	writer.append("		cmp eax, edx\n");
	        	writer.append("		 jge "); label("_".concat(quad.dest));
        	
	            break;
	     

	            /**************************************************/
	        
	        
	        
	        
	        case "jump":
	
		        writer.append("		jmp "); label("_".concat(quad.dest));
		        
	            break;
	            
	            
	        case ":-":
				
	        	load("eax", quad.op1, temps, params);
	        	size = 3*SizeOfInt;
	        	
	        	writer.append("		mov esi, DWORD PTR [ebp+".concat(size.toString().concat("]\n"))); 
				
	        	nd = symtable.lookup(new Key(current_fun));
	        	
	        	if(nd.retvalue.equals("int"))
	        		writer.append("		mov DWORD PTR [esi], eax\n");
	        	else	writer.append("		mov BYTE PTR [esi], eax\n");
	            
	        	break;
	            
	        
	            
	            /**************************************************/
	            
	        case "par":
	        	
	        	par = true;
	        	
	        	if(quad.op2.equals("V")){
	        		
	        		load("eax", quad.op1, temps, params);
	        		writer.append("		push eax				#Push Parameter\n");
	        	}
	        	
	        	else{

	        		tmp = temps.findElement(quad.op1);
	        		if(tmp != null && tmp.strname != null){			//It's a string
	        			
	        			Integer offset = tmp.offset;
	        			String value;
	        			
	        			for(int x=1; x<tmp.strlen; x++){
	        				
	        				if(tmp.strname.charAt(x) == '\\'){
	        					
	        					if(tmp.strname.charAt(x+1) == 'x'){
	        						value = tmp.strname.substring(x, x+3);
	        						
	        						value = "\'".concat(value.concat("\'"));
	        						value = mySloppyFun(value);
	        						x += 3;
	        					}
	        					
	        					else{ 
	        						value = new String(tmp.strname.substring(x, x+2));
	        						value = "\'".concat(value.concat("\'"));
	        						value = mySloppyFun(value);
	        						x++;
	        					}
	        				}
	        				
	        				else{
		        				Integer n = (int)tmp.strname.charAt(x);	
		        				value = n.toString();
	        				}
	        				
	        				writer.append("		mov BYTE PTR [ebp-".concat(offset.toString().concat("], ".concat(value.concat("\n")))));
	        				offset -= 1;
	        			}
	        		
	        			writer.append("		mov BYTE PTR [ebp-".concat(offset.toString().concat("], 0\n")));
	        			offset = tmp.offset;
	        			writer.append("		lea  esi, DWORD PTR [ebp-".concat(offset.toString().concat("]\n")));
	        			writer.append("		push esi				#Push String Address\n");
	        		}
	        		
	        		else{
		        		loadAddr("esi", quad.op1, temps, params);
		        		writer.append("		push esi				#Push Address\n");
	        		}       
	        	} 	

	        	par = false;
	        	
	        	break;
	            
	        	
	        	
	        case "call":
	        	
	        	nd = symtable.lookup(new Key(quad.dest));				//Callee
	        	scope = nd.scope;
	        	Integer offset;
	        	Integer ofs = SizeOfInt;

	        	if(nd.retvalue.equals("nothing")){
	        		writer.append("		sub esp, ".concat(ofs.toString()));
	         		writer.append("				#Skip the address of returned value\n");
	        	}
	        	
	        	size = 0;;
	        	if(nd.params != null)
	        		size = SizeOfInt*nd.params.size();
	        	
	        	if(nd.scope != 0 || nd.name.name.equals(main)){						//If it is a library function -> no access link field
	        		updateAL(nd.scope);
	        		offset = 2*SizeOfInt + size;
	        	}
	        	else offset = SizeOfInt + size;
	        	
	        	writer.append("		call near ptr _");
	        	writer.append(nd.name.name.concat("_".concat(scope.toString())));
	        	writer.append("\n");
	        	writer.append("		add esp, ".concat(offset.toString()));
	        	writer.append("				#Clean Parameters\n");
	        	
	        	break;
	            
	        	
	        case "ret":

	        	scope = temps.scope-1;
	        	writer.append("		jmp end_".concat(current_fun).concat("_"));
	        	writer.append(scope.toString());
	        	writer.append("\n");
	        	break;
	        	
	        	
	        	
	        case "unit":
	        	
        		nd = symtable.lookup(new Key(quad.op1));
        		scope = nd.scope;
        		String name =nd.name.name.concat("_".concat(scope.toString())); 
        		
        		name(name);
        		
        		size = varsize;
	        	writer.append("		push ebp\n");
	        	writer.append("		mov ebp, esp\n");
	        	writer.append("		sub esp, ".concat(size.toString().concat("				#Allocate memory for all local variables\n")));

	        	current_fun = nd.name.name;
	        	
	            break;
	
	
	        case "endu":

        		nd = symtable.lookup(new Key(quad.op1));
        		scope = nd.scope;
        		endof(nd.name.name.concat("_".concat(scope.toString())));

	        	writer.append("		mov esp, ebp\n");
	        	writer.append("		pop ebp\n");
	        	writer.append("		ret\n");
	        	
	            break;
	            
	            /**************************************************/	
	            
	            
	        default: 
	        	System.out.println("Wrong quadruple");
	        	System.exit(1);
	            break;
	    
			}
		}
		
    	catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(1);
		}
	
	}
	
	/****************************************************/
	
	private void endof(String name){
	    try{
	    	writer.append("\nend_".concat(name.concat(":\n")));
	    }
	    
	    catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(1);
		}	
	}
	
	private void name(String name){
	    try{
	    	writer.append("\n_".concat(name.concat(":\n")));
	    }
	    
	    catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(1);
		}	
	}
	
	private void label(String label){
	    try{
	    	writer.append(label.concat("\n"));
	    }
	    
	    catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(1);
		}	
	}

	
	private String mySloppyFun(String a){					//I hope no one sees it. #Ashamed
		
		Character asci=null;;
		String str = null;
		Integer n;
		if(a.charAt(1) == '\\' && a.charAt(2) != 'x'){
			
			switch (a.charAt(2)){
			
			case 'n':
				asci = 10;
				break;
					
			case 't':
				asci = 9;
				break;
			
			case 'r':
				asci = 13;
				break;
					
			case '0':
				asci = 0;
				break;
				
			case '\\':
				asci = 47;
				break;
			
			case '\'':
				asci = 44;
				break;
				
			case '"':
				asci = 34;
				break;
				
				default: break;
			}
			n = (int)asci;
			str = n.toString();
		}

		else if(a.charAt(1) == 'x'){

			int d1,d2;
			
			if(a.charAt(1) == 'a')
				d1 = 10;
			else if(a.charAt(1) == 'b')
				d1 = 11;
			else if(a.charAt(1) == 'c')
				d1 = 12;
			else if(a.charAt(1) == 'd')
				d1 = 13;
			else if(a.charAt(1) == 'e')
				d1 = 14;
			else if(a.charAt(1) == 'f')
				d1 = 15;
			else d1 = (int)a.charAt(1);
			
			if(a.charAt(2) == 'a')
				d2 = 10;
			else if(a.charAt(2) == 'b')
				d2 = 11;
			else if(a.charAt(2) == 'c')
				d2 = 12;
			else if(a.charAt(2) == 'd')
				d2 = 13;
			else if(a.charAt(2) == 'e')
				d2 = 14;
			else if(a.charAt(2) == 'f')
				d2 = 15;
			else d2 = (int)a.charAt(2);
			
			Integer hex = d1*16 + d2;
			str = hex.toString();
		}
		
		else{ 
			asci = a.charAt(1);
			n = (int)asci;
			str = n.toString();
		}
		
		return str;
	}

}
