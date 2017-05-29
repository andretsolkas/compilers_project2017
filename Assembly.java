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
		
		if(np < nx){
			
		}
		
	}
	
	
	
	public void load(String R, String a){
		
	}
	
	
	public void loadAddr(String R, String a){
		
	}
	
	public void store(String R, String a){
		
	}


	public int findOffset(String a, LinkedList<LinkedList<Node>> scopes, ScopeTemp temps){
		
		//Figure out whether is is a local variable or a temporary one
		//Temporary variables begin with '$'
		int offset = 0;
		
		if(a.charAt(0) == '$'){			//Temporary
			
			int curscope = temps.scope;
			
			for(int i=0; i<scopes.get(curscope-1).size(); i++){						//Find out local variables's size
				String type = scopes.get(curscope-1).get(i).type;
				
				if(type.equals("int")){
					offset += SizeOfInt;
				}
				
				else if(type.equals("char")){
					offset += SizeOfChar;
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
			
			LinkedList<Node> nodes = scopes.get(curscope-1);
			
			for(int i=0; i<nodes.size(); i++){
				
				Node nd = nodes.get(i);
				
				if(!nd.name.name.equals(a)){
					if(nd.type.equals("int")){
						offset += SizeOfInt;
					}
					
					else if(nd.type.equals("char")){
						offset += SizeOfChar;
					}
				}
				
				else return offset;
			}
		}
		
		return offset;
	}
}
