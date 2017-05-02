
public class Node {

	Key name;
	String type;
	int scope;
	Node prevNode;
	
	public Node(Key idName, String tp, int sp, Node prevnode){
		
		name = idName;
		type = tp;
		scope = sp;
		prevNode = prevnode;
		
	}

}
