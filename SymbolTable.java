
import java.util.*;
 

public class SymbolTable {

	int scope;
	Hashtable <Key, Node> hashtable;
	LinkedList <Node> list;

	int SizeOfInt = 4;
	int SizeOfChar = 1;

	int paramoffset = 3*SizeOfInt;
	
	boolean param;
	
	LinkedList<Struct> offsets = new LinkedList<Struct>();
	
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

		int ofs;
		if(type != null){
			
			if(param != true){			//not a function
				findOffset(type, arraylist, ref);
				ofs = offsets.getLast().offset;
			}
			else{
				paramoffset += 4;
				ofs = paramoffset;
			}
		}
		else ofs = -1;
			
		if(node == null){                                                       //id 's first appearance in the hash table
			newNode = new Node(name, type, scope, ref, params, arraylist, retvalue, defined, ofs, param, null);
			hashtable.put(name, newNode);
		}
		
		else{                                                                   //id will shadow an already existing one
			newNode = new Node(name, type, scope, ref, params, arraylist, retvalue, defined, ofs, param, node);
			hashtable.replace(name, newNode);
		}
		
		list.addLast(newNode);
		hashtable.get(name).print();
	}
	
	
	private void findOffset(String type, LinkedList<Integer> arraylist, Boolean reference){
		
		Struct struct = offsets.getLast();
		
		int arraysize = 1;
		if(arraylist != null){
			for(int j=0; j<arraylist.size(); j++){
				arraysize *= arraylist.get(j);								//None of these values will be zero
			}
		}

		if(reference == true){
			if(struct.numChars != 0){
				struct.offset += SizeOfInt-struct.numChars;			//padding
				struct.numChars = 0;
			}
			struct.offset += SizeOfInt;
		}
		else if(type.equals("int")){
			
			if(struct.numChars != 0){
				struct.offset += SizeOfInt-struct.numChars;			//padding
				struct.numChars = 0;
			}
			struct.offset += SizeOfInt*arraysize;
		}
		
		else if(type.equals("char")){
			struct.numChars = (struct.numChars+arraysize)%4;
			struct.offset += SizeOfChar*arraysize;
		}
		
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
	
	public void alteredExit(int lineError){                                                         //Destroys last scope, meanwhile it searched for declared but undefined functions
		
		Node node;
		Iterator<Node> iter = list.descendingIterator();
        while(iter.hasNext()){
				node = iter.next();

				if(node.scope == scope){
					
					if(node.defined != null && node.defined == false){											//Found an undefined function
						System.out.println("Error: Line " + lineError + ": Function " + node.name.name + " never matches with a definiton\n");
	                    System.exit(1);
					}
					
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
	
	public void insertLibfuncs(){
		
		LinkedList<Param> params;
		LinkedList<Integer> arraylist;
		
		params = new LinkedList<Param>();
		params.addLast(new Param("int", new Key("n"), false, null));
		insert(new Key("puti"), null, null, null, params, true, "nothing");
		
		
		params = new LinkedList<Param>();
		params.addLast(new Param("char", new Key("c"), false, null));
		insert(new Key("putc"), null, null, null, params, true, "nothing");
		
		
		params = new LinkedList<Param>();
		arraylist = new LinkedList<Integer>();
		arraylist.addLast(0);
		params.addLast(new Param("char", new Key("s"), true, arraylist));
		insert(new Key("puts"), null, null, null, params, true, "nothing");
	
		
		insert(new Key("geti"), null, null, null, null, true, "int");
	
		insert(new Key("getc"), null, null, null, null, true, "char");
	
		
		params = new LinkedList<Param>();
		params.addLast(new Param("int", new Key("n"), false, null));
		arraylist = new LinkedList<Integer>();
		arraylist.addLast(0);
		params.addLast(new Param("char", new Key("s"), true, arraylist));
		insert(new Key("gets"), null, null, null, params, true, "nothing");
	
		
		params = new LinkedList<Param>();
		params.addLast(new Param("int", new Key("n"), false, null));
		insert(new Key("abs"), null, null, null, params, true, "int");
		
		
		params = new LinkedList<Param>();
		params.addLast(new Param("char", new Key("c"), false, null));
		insert(new Key("ord"), null, null, null, params, true, "int");
		
		
		params = new LinkedList<Param>();
		params.addLast(new Param("int", new Key("n"), false, null));
		insert(new Key("chr"), null, null, null, params, true, "char");

	
		params = new LinkedList<Param>();
		arraylist = new LinkedList<Integer>();
		arraylist.addLast(0);
		params.addLast(new Param("char", new Key("s"), true, arraylist));
		insert(new Key("strlen"), null, null, null, params, true, "int");
		
		
		params = new LinkedList<Param>();
		arraylist = new LinkedList<Integer>();
		arraylist.addLast(0);
		params.addLast(new Param("char", new Key("s1"), true, arraylist));
		
		arraylist = new LinkedList<Integer>();
		arraylist.addLast(0);
		params.addLast(new Param("char", new Key("s2"), true, arraylist));
		insert(new Key("strcmp"), null, null, null, params, true, "int");
		
		
		params = new LinkedList<Param>();
		arraylist = new LinkedList<Integer>();
		arraylist.addLast(0);
		params.addLast(new Param("char", new Key("trg"), true, arraylist));
		
		arraylist = new LinkedList<Integer>();
		arraylist.addLast(0);
		params.addLast(new Param("char", new Key("src"), true, arraylist));
		insert(new Key("strcpy"), null, null, null, params, true, "nothing");
		
		
		params = new LinkedList<Param>();
		arraylist = new LinkedList<Integer>();
		arraylist.addLast(0);
		params.addLast(new Param("char", new Key("trg"), true, arraylist));
		
		arraylist = new LinkedList<Integer>();
		arraylist.addLast(0);
		params.addLast(new Param("char", new Key("src"), true, arraylist));
		insert(new Key("strcat"), null, null, null, params, true, "nothing");
	}

	
	public void print(){
		
		System.out.println("List:\n");
		for(int i=0; i<list.size(); i++){
			
			list.get(i).print();
		}
    }
	
}
