
import java.util.*;

public class Node {

	Key name;											//Identifier
	String type;										//Variable's type
	int scope;											//Scope
	Boolean reference;									//reference
	LinkedList <Param> params; 							//If not function, gets null- holds a function's parameters
	String retvalue;									//Return Value
	
	Node prevNode;

	
	public Node(Key idName, String tp, int sp, Boolean ref, LinkedList <Param> pm, String ret, Node prevnode){
		
		name = idName;
		type = tp;
		scope = sp;
		reference = ref;
		params = pm;
		retvalue = ret;
		
		prevNode = prevnode;
		
	}

}
