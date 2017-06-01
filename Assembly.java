import java.io.FileWriter;
import java.util.LinkedList;

public class Assembly{

	int SizeOfInt = 4;
	int SizeOfChar = 1;
	
	int np = 0;
	
	FileWriter writer;
	SymbolTable symtable;
	
	public Assembly(FileWriter wr, SymbolTable st){
		writer = wr;
		symtable = st;
	}
								
	public void getAR(String a){
		
		Node node = symtable.lookup(new Key(a));			//At this point node will never be null

		int np = node.scope;
		Integer size = 2*SizeOfInt;
		String of = size.toString();

		try{
			writer.append("			mov esi, DWORD PTR [ebp+".concat(of).concat("]\n"));
			
			for(int i=0; i < np-np-1; i++){	
				writer.append("			mov esi, DWORD PTR [e	si+".concat(of).concat("]\n"));
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
		
		try{
			if(np < nx){
				writer.append("				push ebp\n");
			}
			
			else if(np == nx){
				writer.append("				push DWORD PTR [sbp+".concat(of).concat("]\n"));
			}
			
			else{
				writer.append("				mov esi, DWORD PTR [sbp+".concat(of).concat("]\n"));
				for(int i=0; i < np-nx; i++){
					writer.append("				push DWORD PTR [esi+".concat(of).concat("]\n"));
				}
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}

	public void load(String R, String a, LinkedList<LinkedList<Node>> scopesLocal, ScopeTemp temps, LinkedList<Param> params){
		
		try{
			if(a.charAt(0) >= '0' && a.charAt(0) <= '9'){				//a is a number
				writer.append("				mov ".concat(R).concat(", a\n"));
			}
			
			else if(a.charAt(0) == '['){								//array element
				char[] str = new char[a.length()-2];
				a.getChars(1, a.length()-1, str, 0);
				load(R, str.toString(), scopesLocal, temps, params);
			}
			
			else if(a.charAt(0) == '\''){								// a is a character
				Integer ch = (int)'\'';
				String str = ch.toString();
				writer.append("				mov ".concat(R).concat(", ").concat(str).concat("\n"));
			}
			

			else if(a.charAt(0) == '$'){										//temporary variable -- by value
				Integer offset = findOffset(a, scopesLocal, temps);
				Temp tmp = temps.findElement(a);
				
				if(tmp.type.equals("int")){
					writer.append("				mov ".concat(R).concat(", DWORD PTR [ebp+".concat(offset.toString().concat("]\n"))));
				}
			
				else writer.append("				mov ".concat(R).concat(", BYTE PTR [ebp+".concat(offset.toString().concat("]\n"))));
			}
				
			else{
					Node myNode = symtable.lookup(new Key(a));	//It won't be null
					Integer offset;
					
					if(myNode.scope == temps.scope){		//LOCAL
		
						if(isParam(a, params)){
							
							offset = findOffsetParam(a, params);
							
							if(myNode.reference == false){											//LOCAL PARAMETER - BY VALUE
									
								if(myNode.type.equals("int"))
									writer.append("				mov ".concat(R).concat(", DWORD PTR [bp+".concat(offset.toString().concat("]\n"))));
								else writer.append("				mov ".concat(R).concat(", BYTE PTR [bp+".concat(offset.toString().concat("]\n"))));
							}
						
							else{																	//LOCAL PARAMETER - BY REFERENCE
								writer.append("				mov esi, DWORD PTR [ebp+".concat(offset.toString().concat("]\n"))); 
								writer.append("				mov ".concat(R).concat(", DWORD PTR [esi]\n"));
							}
						}

						else{																		//LOCAL VARIABLE
							offset = findOffset(a, scopesLocal, temps);			
							if(myNode.type.equals("int"))
								writer.append("				mov ".concat(R).concat(", DWORD PTR [ebp+".concat(offset.toString().concat("]\n"))));
							else writer.append("				mov ".concat(R).concat(", BYTE PTR [ebp+".concat(offset.toString().concat("]\n"))));
						}
					}
					else{									//NON LOCAL
						getAR(a);
						
						if(isParam(a, params)){

							offset = findOffsetParam(a, params);
							
							if(myNode.reference == false){											//LOCAL PARAMETER - BY VALUE
									
								if(myNode.type.equals("int"))
									writer.append("				mov ".concat(R).concat(", DWORD PTR [esi+".concat(offset.toString().concat("]\n"))));
								else writer.append("				mov ".concat(R).concat(", BYTE PTR [esi+".concat(offset.toString().concat("]\n"))));
							}
						
							else{																	//LOCAL PARAMETER - BY REFERENCE
								writer.append("				mov esi, DWORD PTR [esi+".concat(offset.toString().concat("]\n"))); 
								writer.append("				mov ".concat(R).concat(", DWORD PTR [esi]\n"));
							}
						}	
						else{																		//NON LOCAL VARIABLE
							offset = findOffset(a, scopesLocal, temps);
							if(myNode.type.equals("int"))
								writer.append("				mov ".concat(R).concat(", DWORD PTR [esi+".concat(offset.toString().concat("]\n"))));
							else writer.append("				mov ".concat(R).concat(", BYTE PTR [esi+".concat(offset.toString().concat("]\n"))));
						}
					}
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
	
	public void loadAddr(String R, String a, LinkedList<LinkedList<Node>> scopesLocal, ScopeTemp temps, LinkedList<Param> params){	
		try{

			if(a.charAt(0) == '['){												//array element
				char[] str = new char[a.length()-2];
				a.getChars(1, a.length()-1, str, 0);
				load(R, str.toString(), scopesLocal, temps, params);
			}
			
			else if(a.charAt(0) == '"'){										// a is a string
				writer.append("				lea ".concat(R).concat(", BYTE PTR a"));
			}
			

			else if(a.charAt(0) == '$'){										//temporary variable -- by value
				Integer offset = findOffset(a, scopesLocal, temps);
				writer.append("				lea ".concat(R).concat(", DWORD PTR [ebp+".concat(offset.toString().concat("]\n"))));

			}
				
			else{
					Node myNode = symtable.lookup(new Key(a));	//It won't be null
					Integer offset;
					
					if(myNode.scope == temps.scope){		//LOCAL
		
						if(isParam(a, params)){
							
							offset = findOffsetParam(a, params);
							
							if(myNode.reference == false){											//LOCAL PARAMETER - BY VALUE
								writer.append("				lea ".concat(R).concat(", DWORD PTR [ebp+".concat(offset.toString().concat("]\n"))));
							}
						
							else{																	//LOCAL PARAMETER - BY REFERENCE
								writer.append("				mov ".concat(R).concat(", DWORD PTR [ebp+".concat(offset.toString().concat("]\n")))); 
							}
						}

						else{																		//LOCAL VARIABLE
							offset = findOffset(a, scopesLocal, temps);			
							writer.append("				lea ".concat(R).concat(", DWORD PTR [ebp+".concat(offset.toString().concat("]\n"))));
						}
					}
					else{									//NON LOCAL
						getAR(a);
						
						if(isParam(a, params)){

							offset = findOffsetParam(a, params);
							
							if(myNode.reference == false){											//LOCAL PARAMETER - BY VALUE
								writer.append("				lea ".concat(R).concat(", DWORD PTR [esi+".concat(offset.toString().concat("]\n"))));
							}
						
							else{																	//LOCAL PARAMETER - BY REFERENCE
								writer.append("				mov ".concat(R).concat(", DWORD PTR [esi+".concat(offset.toString().concat("]\n")))); 
							}
						}	
						else{																		//NON LOCAL VARIABLE
							offset = findOffset(a, scopesLocal, temps);
								writer.append("				lea ".concat(R).concat(", DWORD PTR [esi+".concat(offset.toString().concat("]\n"))));
						}
					}
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
	
	public void store(String R, String a, LinkedList<LinkedList<Node>> scopesLocal, ScopeTemp temps, LinkedList<Param> params){
		
		try{
			
			if(a.charAt(0) == '['){								//array element
				char[] str = new char[a.length()-2];
				a.getChars(1, a.length()-1, str, 0);
			}
			

			else if(a.charAt(0) == '$'){										//temporary variable -- by value
				Integer offset = findOffset(a, scopesLocal, temps);
				Temp tmp = temps.findElement(a);

				if(tmp.type.equals("int")){
					writer.append("				mov DWORD PTR [ebp+".concat(offset.toString().concat("], ".concat(R).concat("\n"))));
				}
				else writer.append("				mov BYTE PTR [ebp+".concat(offset.toString().concat("], ".concat(R).concat("\n"))));
			}
				
			else{
					Node myNode = symtable.lookup(new Key(a));	//It won't be null
					Integer offset;
					
					if(myNode.scope == temps.scope){		//LOCAL
		
						if(isParam(a, params)){
							
							offset = findOffsetParam(a, params);
							
							if(myNode.reference == false){											//LOCAL PARAMETER - BY VALUE
									
								if(myNode.type.equals("int"))
									writer.append("				mov DWORD PTR [ebp+".concat(offset.toString().concat("], ".concat(R).concat("\n"))));
								else writer.append("				mov BYTE PTR [ebp+".concat(offset.toString().concat("], ".concat(R).concat("\n"))));
							}
						
							else{																	//LOCAL PARAMETER - BY REFERENCE
								writer.append("				mov si, DWORD PTR [ebp+".concat(offset.toString().concat("]\n"))); 
								writer.append("				mov DWORD PTR [esi], ".concat(R).concat("\n"));
							}
						}

						else{																		//LOCAL VARIABLE
							offset = findOffset(a, scopesLocal, temps);			
							if(myNode.type.equals("int"))
								writer.append("				mov DWORD PTR [ebp+".concat(offset.toString().concat("], ".concat(R).concat("\n"))));
							else writer.append("				mov BYTE PTR [ebp+".concat(offset.toString().concat("], ".concat(R).concat("\n"))));
						}
					}
					else{									//NON LOCAL
						getAR(a);
						
						if(isParam(a, params)){

							offset = findOffsetParam(a, params);
							
							if(myNode.reference == false){											//LOCAL PARAMETER - BY VALUE
									
								if(myNode.type.equals("int"))
									writer.append("				mov DWORD PTR [esi+".concat(offset.toString().concat("], ".concat(R).concat("\n"))));
								else writer.append("				mov BYTE PTR [esi+".concat(offset.toString().concat("], ".concat(R).concat("\n"))));
							}
						
							else{																	//LOCAL PARAMETER - BY REFERENCE
								writer.append("				mov esi, DWORD PTR [esi+".concat(offset.toString().concat("]\n"))); 
								writer.append("				mov DWORD PTR [esi], ".concat(R).concat("\n"));
							}
						}	
						else{																		//NON LOCAL VARIABLE
							offset = findOffset(a, scopesLocal, temps);
							if(myNode.type.equals("int"))
								writer.append("				mov DWORD PTR [esi+".concat(offset.toString().concat("], ".concat(R).concat("\n"))));
							else writer.append("				mov BYTE PTR [esi+".concat(offset.toString().concat("], ".concat(R).concat("\n"))));
						}
					}
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
		
	public int findOffsetParam(String a, LinkedList<Param> params){
	
		int offset = 4*SizeOfInt;
		
		for(int i=0; i<params.size(); i++){
			Param pm = params.get(params.size()-1-i);
			if(pm.idname.name.equals(a)){
				return offset;
			}

			if(pm.type.equals("int")){
				offset += SizeOfInt;
			}
			
			else if(pm.type.equals("char")){
				offset += SizeOfChar;
			}
		}
		
		return offset;
	}

	
	public int findSizeParams(LinkedList<Param> params){
		
		int offset = 0;
		
		for(int i=0; i<params.size(); i++){
			Param pm = params.get(params.size()-1-i);

			if(pm.type.equals("int")){
				offset += SizeOfInt;
			}
			
			else if(pm.type.equals("char")){
				offset += SizeOfChar;
			}
		}
		
		return offset;
	}

	public int findSize(LinkedList<LinkedList<Node>> scopesLocal, ScopeTemp temps){
		
		int offset = 0;
		int curscope = temps.scope;
		int lastType=0;

		for(int i=0; i<scopesLocal.get(curscope-1).size(); i++){						//Find out local variables's size
			Node myNode = scopesLocal.get(curscope-1).get(i);
			String type = myNode.type;
			
			int arraysize = 1;
			if(myNode.arraylist != null){
				for(int j=0; j<myNode.arraylist.size(); j++){
					arraysize *= myNode.arraylist.get(j);								//None of these values will be zero
				}
			}
			
			if(type.equals("int")){
				offset += SizeOfInt*arraysize;
				lastType = SizeOfInt;
			}
			
			else if(type.equals("char")){
				offset += SizeOfChar*arraysize;
				lastType = SizeOfChar;
			}
		}
		
		for(int i=0; i<temps.temps.size(); i++){
			Temp temp = temps.temps.get(i);

			if(temp.type.equals("int")){
				offset += SizeOfInt;
				lastType = SizeOfInt;
			}
			
			else if(temp.type.equals("char")){
				offset += SizeOfChar;
				lastType = SizeOfChar;
			}
		}
		
		return offset-lastType;
	}
	
	public int findOffset(String a, LinkedList<LinkedList<Node>> scopesLocal, ScopeTemp temps){
		
		//Figure out whether is is a local variable or a temporary one
		//Temporary variables begin with '$'
		int offset = SizeOfInt;
		
		if(a.charAt(0) == '$'){			//Temporary
			
			int curscope = temps.scope;
			
			for(int i=0; i<scopesLocal.get(curscope-1).size(); i++){						//Find out local variables's size
				Node myNode = scopesLocal.get(curscope-1).get(i);
				String type = myNode.type;
				
				int arraysize = 1;
				if(myNode.arraylist != null){
					for(int j=0; j<myNode.arraylist.size(); j++){
						arraysize *= myNode.arraylist.get(j);					//None of these values will be zero
					}
				}
				
				if(type.equals("int")){
					offset += SizeOfInt*arraysize;
				}
				
				else if(type.equals("char")){
					offset += SizeOfChar*arraysize;
				}
			}
			
			for(int i=0; i<temps.temps.size(); i++){
				Temp temp = temps.temps.get(i);

				if(!temp.tempname.equals(a)){
					if(temp.type.equals("int")){
						offset += SizeOfInt;
					}
					
					else if(temp.type.equals("char")){
						offset += SizeOfChar;
					}
				}
				
				else return offset;
			}
			
		}
		
		else{							//Local			
			Node myNode = symtable.lookup(new Key(a));				//myNode will not be null at this point
			
			int curscope = myNode.scope;
			
			LinkedList<Node> nodes = scopesLocal.get(curscope-1);
			
			for(int i=0; i<nodes.size(); i++){
				
				Node nd = nodes.get(i);
				
				int arraysize = 1;
				if(nd.arraylist != null){
					for(int j=0; j<nd.arraylist.size(); j++){
						arraysize *= nd.arraylist.get(j);					//None of these values will be zero
					}
				}
				
				if(!nd.name.name.equals(a)){
					if(nd.type.equals("int")){
						offset += SizeOfInt*arraysize;
					}
					
					else if(nd.type.equals("char")){
						offset += SizeOfChar*arraysize;
					}
				}
				
				else return offset;
			}
		}
		
		return offset;
	}

	private boolean isParam(String a, LinkedList<Param> params){
		
		if(params!=null){
			
			for(int i=0; i<params.size(); i++){
				if(params.get(i).idname.name.equals(a))
					return true;
			}
		}
		return false;
	}
	
	
	
	
	/**************************************************************************************************************************************/
	public void createAssembly(Quad quad, Integer i, LinkedList<LinkedList<Node>> scopesLocal, ScopeTemp temps, LinkedList<Param> params){
		
		try{	
			
			Integer scope;
			Node nd;
			
    		writer.append("\n_".concat(i.toString().concat(":")));	
			
			
			switch(quad.opcode) {
	        
			case "<-":
	
	        	load("eax", quad.op1, scopesLocal, temps, params);
	        	store("eax", quad.dest, scopesLocal, temps, params);
	
	            break;
	            
	
	        case "array":
	        	
	        	String type;
	        	if(quad.op1.charAt(0) == '$'){
	        		Temp tmp = temps.findElement(quad.op1);
	        		type = tmp.type;
	        	}
	        	
	        	else{
	        		nd = symtable.lookup(new Key(quad.op1));
	        		type = nd.type;
	        	}
	        	
	        	Integer size;
	        	if(type.equals("int"))
	        		size = SizeOfInt;
	        	else size = SizeOfChar;
	        	
	        	
	    		load("eax", quad.op2, scopesLocal, temps, params);
	        	writer.append("				mov ecx, ".concat(size.toString().concat("\n")));
	        	writer.append("				imul ecx\n");
	        	loadAddr("ecx", quad.op1, scopesLocal, temps, params);
	        	writer.append("				add eax, ecx\n");
	        	store("eax", quad.dest, scopesLocal, temps, params);
	            	
	            break;
	
	            
	            
	            
	            
	            /**************** Expressions *****************/
	            
	        case "+":

        		load("eax", quad.op1, scopesLocal, temps, params);
        		load("ecx", quad.op2, scopesLocal, temps, params);
        		writer.append("				add eax, ecx\n");
        		store("eax", quad.dest, scopesLocal, temps, params);
        	
	            break;
	        

	        case "-":

        		load("eax", quad.op1, scopesLocal, temps, params);
        		load("ecx", quad.op2, scopesLocal, temps, params);
        		writer.append("				sub eax, ecx\n");
        		store("eax", quad.dest, scopesLocal, temps, params);

	            break;
	
	
	        case "*":

        		load("eax", quad.op1, scopesLocal, temps, params);
        		load("ebx", quad.op2, scopesLocal, temps, params);
        		writer.append("				imul eax ebx\n");
        		store("eax", quad.dest, scopesLocal, temps, params);

	            break;
	            
	        
	        case "/":

        		load("eax", quad.op1, scopesLocal, temps, params);
        		writer.append("				cdq\n");
        		load("ebx", quad.op2, scopesLocal, temps, params);
        		writer.append("				idiv ebx\n");
        		store("eax", quad.dest, scopesLocal, temps, params);
        	
	            break;
	        
	            
	        case "%":

        		load("eax", quad.op1, scopesLocal, temps, params);
        		writer.append("				cdq\n");
        		load("ebx", quad.op2, scopesLocal, temps, params);
        		writer.append("				idiv ebx\n");
        		store("edx", quad.dest, scopesLocal, temps, params);
	        	
	            break;
	            

	            
	            

	            /**************** Conditions *****************/
	            
	        case "=":

	        	load("eax", quad.op1, scopesLocal, temps, params);
	        	load("edx", quad.op2, scopesLocal, temps, params);
	        	writer.append("				cmp eax, edx\n");
	        	writer.append("				je "); label(quad.dest);
	
	            break;
	        
	        case "#":
	            
	        	load("eax", quad.op1, scopesLocal, temps, params);
	        	load("edx", quad.op2, scopesLocal, temps, params);
	        	writer.append("				cmp eax, edx\n");
	        	writer.append("				jne "); label("_".concat(quad.dest));
	        	
	            break;
	            
	            
	        case "<":

	        	load("eax", quad.op1, scopesLocal, temps, params);
	        	load("edx", quad.op2, scopesLocal, temps, params);
	        	writer.append("				cmp eax, edx\n");
	        	writer.append("				jl "); label("_".concat(quad.dest));

	            break;
	        
	        case ">":
	            
	        	load("eax", quad.op1, scopesLocal, temps, params);
	        	load("edx", quad.op2, scopesLocal, temps, params);
	        	writer.append("				cmp eax, edx\n");
	        	writer.append("				jg "); label("_".concat(quad.dest));

	            break;
	            
	        case "<=":
	            
	        	load("eax", quad.op1, scopesLocal, temps, params);
	        	load("edx", quad.op2, scopesLocal, temps, params);
	        	writer.append("				cmp eax, edx\n");
	        	writer.append("				jle "); label("_".concat(quad.dest));
	        	
	            break;
	        
	        case ">=":

	        	load("eax", quad.op1, scopesLocal, temps, params);
	        	load("edx", quad.op2, scopesLocal, temps, params);
	        	writer.append("				cmp eax, edx\n");
	        	writer.append("				 jge "); label("_".concat(quad.dest));
        	
	            break;
	     

	            /**************************************************/
	        
	        
	        
	        
	        case "jump":
	
		        writer.append("				jmp "); label("_".concat(quad.dest));
		        
	            break;
	            
	            
	        case ":-":
	        	
		        writer.append("				jmp "); label("_".concat(quad.dest));
		        
	            break;
	            
	        
	            
	            /**************************************************/
	            
	        case "par":
	        	
	        	if(quad.op2.equals("V")){
	        		
	        		load("eax", quad.op1, scopesLocal, temps, params);
	        		writer.append("				push eax\n");
	        	}
	        	
	        	else{

	        		loadAddr("esi", quad.op1, scopesLocal, temps, params);
	        		writer.append("				push esi\n");
	        	}        	

	            break;
	            
	        case "call":
	        	
	        	nd = symtable.lookup(new Key(quad.dest));
	        	scope = nd.scope;
	        	Integer offset = SizeOfInt*2;
	        	size = findSizeParams(params);
	        	
	        	updateAL(nd.scope);
	        	writer.append("				call near ptr _");
	        	writer.append(nd.name.name.concat("_".concat(scope.toString())));
	        	writer.append("\n");
	        	writer.append("				add esp, ".concat(size.toString()));
	        	writer.append("+".concat(offset.toString()));
	        	writer.append("\n");
	        	
	        	break;
	            
	        	
	        case "ret":
	            /*
	        	nd = symtable.lookup(new Key(quad.dest));
	        	scope = nd.scope;
	        	writer.append("				jmp _".concat(quad.dest));
	        	writer.append("_".concat(scope.toString()));
	        	*/
	        	
	        	writer.append("				ret\n");
	        	break;
	        	
	        	
	        	
	        case "unit":
	        	
        		nd = symtable.lookup(new Key(quad.op1));
        		scope = nd.scope;
        		name(nd.name.name.concat("_".concat(scope.toString())));
        		
        		size = findSize(scopesLocal, temps);
        		
        		writer.append("			proc near\n");
	        	writer.append("				push ebp\n");
	        	writer.append("				mov ebp, esp\n");
	        	writer.append("				sub esp ".concat(size.toString().concat("\n")));
        	

	            break;
	
	
	        case "endu":
	
	        	
        		nd = symtable.lookup(new Key(quad.op1));
        		scope = nd.scope;
        		endof(nd.name.name.concat("_".concat(scope.toString())));

	        	writer.append("			mov esp, ebp\n");
	        	writer.append("				pop ebp\n");
	        	writer.append("				ret\n");
        		
	        	//endof(nd.name.name.concat("_".concat(scope.toString())));
	        	
	        	writer.append("				endp\n");
	        	
	            break;
	            
	            /**************************************************/	
	            
	            
	        default: 
	        	writer.append("			Not ready yet\n");
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
	    	writer.append("\n@".concat(name.concat(":")));
	    }
	    
	    catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(1);
		}	
	}
	
	private void name(String name){
	    try{
	    	writer.append("\n_".concat(name.concat(":")));
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


}