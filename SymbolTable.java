
import java.util.*;
 

public class SymbolTable {

	int scope;
	Hashtable <Key, Node> hashtable;
	LinkedList <Node> list;

	public SymbolTable(){
		hashtable = new Hashtable <Key, Node>();
		list = new LinkedList <Node>();
		scope = 0;
	}
	
	
	public void enter(){
		scope++;
	}
	
	
	public void insert(Key name, String type, Boolean ref, LinkedList <Param> params, String retvalue){
		
		Node newNode, node = hashtable.get(name);
		
		if(node == null){											//id 's first appearance in the hashtable
			newNode = new Node(name, type, scope, ref, params, retvalue, null);
			hashtable.put(name, newNode);
		}
		
		else{														//id will shadow an already existing one
			newNode = new Node(name, type, scope, ref, params, retvalue, node);
			hashtable.replace(name, newNode);
		}
		
		list.addLast(newNode);
		
	}
	
	
	public Node lookup(Key name){
		return hashtable.get(name);
	}
	
	
	
	public void exit(){												//Destroy last scope
		
		Node node;
		
		for(int i=0; i < list.size(); i++){
				
				node = list.get(i);
				if(node.scope == scope){
					
					if(node.prevNode == null)
						hashtable.remove(node.name);

					else
						hashtable.replace(node.name, node.prevNode);
				
					list.remove(node);
				}
		}
		
		scope--;	
	}
	
	public void increase_scope(){
		scope++;
	}
	
	public void decrease_scope(){
		scope--;
	}
	
	
}
