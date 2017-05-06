
import java.util.*;

public class Node {

	Key name;											//Identifier
	String type;										//Variable's type
	int scope;											//Scope
	Boolean reference;									//reference
	LinkedList <Param> params; 							//If not function, gets null- holds a function's parameters
	LinkedList <Integer> arraylist;
	String retvalue;									//Return Value
	Boolean defined;									//In case of function - True if it's been defined, false if not
	
	Node prevNode;

	
	public Node(Key idName, String tp, int sp, Boolean ref, LinkedList <Param> pm, LinkedList <Integer> arlist, String ret, Boolean def, Node prevnode){
		
		name = idName;
		type = tp;
		scope = sp;
		reference = ref;
		params = pm;
		arraylist = arlist;
		retvalue = ret;
		defined = def;
		
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
