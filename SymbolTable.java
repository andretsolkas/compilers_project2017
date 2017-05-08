
import java.util.*;
 

public class SymbolTable {

	int scope;
	Hashtable <Key, Node> hashtable;
	LinkedList <Node> list;

	public SymbolTable(){
		hashtable = new Hashtable <>();
		list = new LinkedList <>();
		scope = 0;
	}
	
	
	public void enter(){
		scope++;
	}
	
	
	public void insert(Key name, String type, Boolean ref, LinkedList <Integer> arraylist, LinkedList <Param> params, Boolean defined, String retvalue){
		
		Node newNode, node = hashtable.get(name);

		if(node == null){                                                       //id 's first appearance in the hash table
			newNode = new Node(name, type, scope, ref, params, arraylist, retvalue, defined, null);
			hashtable.put(name, newNode);
		}
		
		else{                                                                   //id will shadow an already existing one
			newNode = new Node(name, type, scope, ref, params, arraylist, retvalue, defined, node);
			hashtable.replace(name, newNode);
		}
		
		list.addLast(newNode);
		//hashtable.get(name).print();
	}
	
	
	public Node lookup(Key name){
		return hashtable.get(name);
	}
	
	
	
	public void exit(){                                                         //Destroy last scope
		
		Node node;
		Iterator<Node> iter = list.descendingIterator();
        while(iter.hasNext()){
				node = iter.next();

				if(node.scope == scope){
					if(node.prevNode == null)
						hashtable.remove(node.name);
					else
						hashtable.replace(node.name, node.prevNode);
				
					iter.remove();

				}
		}
		
		scope--;	
	}
    
    int SearchKey(Key key){
        
        Node node = hashtable.get(key);
        while(node != null){
            if(node.scope == scope)
                return 1;
            node = node.prevNode;
        }
        return 0;
        
    }
    
	public void increase_scope(){
		scope++;
	}
	
	public void decrease_scope(){
		scope--;
	}
	
	public void print(){
		
		System.out.println("List:\n");
		for(int i=0; i<list.size(); i++){
			
			list.get(i).print();
		}
    }
	
}
