
import java.util.*;

public class Node {

	Key name;											//Identifier
	String type;										//Variable's type
	int scope;											//Scope
	Boolean reference;									//reference
	LinkedList <Param> params; 							//If not function, gets null- holds a function's parameters
	LinkedList <Integer> arraylist;						//In case it's an array
	String retvalue;									//Return Value
	Boolean defined;									//In case of function - True if it's been defined, false if not
	
	Node prevNode;

	
	public Node(Key idName, String tp, int sp, Boolean ref, LinkedList <Param> pm, LinkedList <Integer> arlist, String ret, Boolean def, Node prevnode){
		
		name = new Key(idName.name);
		
		if(tp != null){															//Copying all values
			type = new String(tp);
		}
		else type = null;
		
		scope = sp;
		
		if(ref != null){
			reference = new Boolean(ref);
		}
		else reference = null;
		
		if(pm != null){
			params = new LinkedList<>();
			
			for(int i=0; i<pm.size(); i++){
				params.addLast(new Param(pm.get(i).type, pm.get(i).idname, pm.get(i).arraylist));
			}
		}
		else params = null;
		
		if(arlist!=null && !arlist.isEmpty()){
			arraylist = new LinkedList<>();
			
			for(int i=0; i<arlist.size(); i++)
				arraylist.addLast(new Integer(arlist.get(i)));
		}
		else arraylist = null;

		if(ret != null){
			retvalue = new String(ret);
		}
		else retvalue = null;
		
		if(def != null){
			defined = new Boolean(def);
		}
		else defined = null;
		
		prevNode = prevnode;
		
	}

	
	public void print(){
		
		System.out.println("Name = " + name.name);
		
		System.out.println("Type = " + type);
		
		System.out.println("Scope = " + scope);
		
		System.out.println("Reference = " + reference);
		
		System.out.println("Return Value = " + retvalue);
		
		System.out.println("Defined = " + defined);
		
		int i;
		
		if(params != null){
			
			System.out.printf("Parameters = ");
			
			for(i=0; i<params.size()-1; i++){
			
				params.get(i).print();
				System.out.printf(", ");
			}
			params.get(i).print();
			System.out.printf("\n");
		}
		
		else{
			
			System.out.println("Parameters = null");

			
			if(retvalue == null){								//Not a function

				if(arraylist != null && !arraylist.isEmpty()){
					
					System.out.printf("ArrayList = ");
					
					for(i=0; i<arraylist.size()-1; i++)
						System.out.printf("%d, ",arraylist.get(i));
					System.out.printf("%d\n",arraylist.get(i));
				}
			}	
		}
		
		System.out.print("\n\n");
		
	}
}
