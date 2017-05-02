
import java.util.*;




public class SymbolTable {

	Hashtable <Key, Node> hashtable;
	LinkedList <Node> list;
	

	public SymbolTable(){
		hashtable = new Hashtable <Key, Node>();
		list = new LinkedList <Node>();
	}
	
	
	//public void enter(){}
	
	public void insert(Key key){
		
		Node newNode, node = hashtable.get(key);
		
		if(node == null){								//id 's first appearance 
			newNode = new Node(key, null);
			hashtable.put(key, newNode);
		}
		
		else{											//id is shadowing an already existing one
			newNode = new Node(key, node);
			hashtable.replace(key, newNode);
		}
		
		list.add(newNode);
		
	}
	
	public int lookup(Key name){
		
		return 0;
	}
	
	
	public void exit(){
		
		
	}
	
	
}
