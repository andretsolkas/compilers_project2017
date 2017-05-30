import java.io.FileWriter;
import java.util.LinkedList;

public class Assembly{

	int SizeOfInt = 4;
	int SizeOfChar = 1;
	
	int runtimeScope = 0;
	
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
			writer.append("mov si, word ptr [bp+".concat(of).concat("]\n"));
			
			for(int i=0; i < runtimeScope-np-1; i++){	
				writer.append("mov si, word ptr [si+".concat(of).concat("]\n"));
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
	
	public void updateAL(int np, int nx){
		
		Integer size = 2*SizeOfInt;
		String of = size.toString(); 
		
		try{
			if(np < nx){
				writer.append("push bp\n");
			}
			
			else if(np == nx){
				writer.append("push word ptr [bp+".concat(of).concat("]\n"));
			}
			
			else{
				writer.append("mov si, word ptr [bp+".concat(of).concat("]\n"));
				for(int i=0; i < np-nx; i++){
					writer.append("push word ptr [si+".concat(of).concat("]\n"));
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
				writer.append("mov R, a\n");
			}
			
			else if(a.charAt(0) == '['){								//array element
				char[] str = new char[a.length()-2];
				a.getChars(1, a.length()-1, str, 0);
			}
			
			else if(a.charAt(0) == '\''){								// a is a character
				Integer ch = (int)'\'';
				String str = ch.toString();
				writer.append("mov R, ".concat(str).concat("\n"));
			}
			

			else if(a.charAt(0) == '$'){										//temporary variable -- by value
				Integer offset = findOffset(a, scopesLocal, temps);
				Temp tmp = temps.findElement(a);
				
				if(tmp.type.equals("int")){
					writer.append("mov R, word ptr [bp-".concat(offset.toString().concat("]\n")));
				}
			
				else writer.append("mov R, byte ptr [bp-".concat(offset.toString().concat("]\n")));
			}
				
			else{
					Node myNode = symtable.lookup(new Key(a));	//It won't be null
					Integer offset;
					
					if(myNode.scope == temps.scope){		//LOCAL
		
						if(isParam(a, params)){
							
							offset = findOffsetParam(a, params);
							
							if(myNode.reference == false){											//LOCAL PARAMETER - BY VALUE
									
								if(myNode.type.equals("int"))
									writer.append("mov R, word ptr [bp+".concat(offset.toString().concat("]\n")));
								else writer.append("mov R, byte ptr [bp+".concat(offset.toString().concat("]\n")));
							}
						
							else{																	//LOCAL PARAMETER - BY REFERENCE
								writer.append("mov si, word ptr [bp+".concat(offset.toString().concat("]\n"))); 
								writer.append("mov R, word ptr [si]\n");
							}
						}

						else{																		//LOCAL VARIABLE
							offset = findOffset(a, scopesLocal, temps);			
							if(myNode.type.equals("int"))
								writer.append("mov R, word ptr [bp-".concat(offset.toString().concat("]\n")));
							else writer.append("mov R, byte ptr [bp-".concat(offset.toString().concat("]\n")));
						}
					}
					else{									//NON LOCAL
						getAR(a);
						
						if(isParam(a, params)){

							offset = findOffsetParam(a, params);
							
							if(myNode.reference == false){											//LOCAL PARAMETER - BY VALUE
									
								if(myNode.type.equals("int"))
									writer.append("mov R, word ptr [si+".concat(offset.toString().concat("]\n")));
								else writer.append("mov R, byte ptr [si+".concat(offset.toString().concat("]\n")));
							}
						
							else{																	//LOCAL PARAMETER - BY REFERENCE
								writer.append("mov si, word ptr [si+".concat(offset.toString().concat("]\n"))); 
								writer.append("mov R, word ptr [si]\n");
							}
						}	
						else{																		//NON LOCAL VARIABLE
							offset = findOffset(a, scopesLocal, temps);
							if(myNode.type.equals("int"))
								writer.append("mov R, word ptr [si-".concat(offset.toString().concat("]\n")));
							else writer.append("mov R, byte ptr [si-".concat(offset.toString().concat("]\n")));
						}
					}
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
	

	
	
	public void loadAddr(String R, String a){	
	}
	
	public void store(String R, String a){
	}
	
	public int findOffsetParam(String a, LinkedList<Param> params){
	
		int offset = 0;
		
		for(int i=0; i<params.size(); i++){
			Param pm= params.get(params.size()-1-i);
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

	public int findOffset(String a, LinkedList<LinkedList<Node>> scopesLocal, ScopeTemp temps){
		
		//Figure out whether is is a local variable or a temporary one
		//Temporary variables begin with '$'
		int offset = 0;
		
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
	
	
}
