import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;


class SymbolTable {

    final LinkedList<Struct> offsets = new LinkedList<>();
    private final Hashtable<Key, Node> hashtable;
    private final LinkedList<Node> list;
    private final int SizeOfInt = 4;
    int scope;
    boolean param;
    int paramoffset = 3 * SizeOfInt;

    SymbolTable() {
        hashtable = new Hashtable<>();
        list = new LinkedList<>();
        scope = 0;
    }


    void enter() {
        scope++;
    }


    void insert(Key name, String type, Boolean ref, LinkedList<Integer> arraylist, LinkedList<Param> params, Boolean defined, String retvalue) {

        Node newNode, node = hashtable.get(name);

        int ofs;
        if (type != null) {

            if (!param) {            //not a function
                findOffset(type, arraylist, ref);
                ofs = offsets.getLast().offset;
            } else {
                paramoffset += 4;
                ofs = paramoffset;
            }
        } else ofs = -1;

        if (node == null) {                                                       //id 's first appearance in the hash table
            newNode = new Node(name, type, scope, ref, params, arraylist, retvalue, defined, ofs, param, null);
            hashtable.put(name, newNode);
        } else {                                                                   //id will shadow an already existing one
            newNode = new Node(name, type, scope, ref, params, arraylist, retvalue, defined, ofs, param, node);
            hashtable.replace(name, newNode);
        }

        list.addLast(newNode);
        //hashtable.get(name).print();
    }


    private void findOffset(String type, LinkedList<Integer> arraylist, Boolean reference) {

        Struct struct = offsets.getLast();

        int arraysize = 1;
        if (arraylist != null) {
            for (Integer anArraylist : arraylist) {
                arraysize *= anArraylist;                                //None of these values will be zero
            }
        }

        if (reference) {
            if (struct.numChars != 0) {
                struct.offset += SizeOfInt - struct.numChars;            //padding
                struct.numChars = 0;
            }
            struct.offset += SizeOfInt;
        } else if (type.equals("int")) {

            if (struct.numChars != 0) {
                struct.offset += SizeOfInt - struct.numChars;            //padding
                struct.numChars = 0;
            }
            struct.offset += SizeOfInt * arraysize;
        } else if (type.equals("char")) {
            struct.numChars = (struct.numChars + arraysize) % 4;
            int sizeOfChar = 1;
            struct.offset += sizeOfChar * arraysize;
        }

    }


    Node lookup(Key name) {
        return hashtable.get(name);
    }

    void exit() {                                                         //Destroy last scope

        Node node;
        Iterator<Node> iter = list.descendingIterator();
        while (iter.hasNext()) {
            node = iter.next();

            if (node.scope == scope) {
                if (node.prevNode == null)
                    hashtable.remove(node.name);
                else
                    hashtable.replace(node.name, node.prevNode);

                iter.remove();

            }
        }

        scope--;
    }

    void alteredExit(int lineError) {                                                         //Destroys last scope, meanwhile it searched for declared but undefined functions

        Node node;
        Iterator<Node> iter = list.descendingIterator();
        while (iter.hasNext()) {
            node = iter.next();

            if (node.scope == scope) {

                if (node.defined != null && !node.defined) {                                            //Found an undefined function
                    System.out.println("Error: Line " + lineError + ": Function " + node.name.name + " never matches with a definiton\n");
                    System.exit(1);
                }

                if (node.prevNode == null)
                    hashtable.remove(node.name);
                else
                    hashtable.replace(node.name, node.prevNode);

                iter.remove();

            }
        }

        scope--;
    }

    int SearchKey(Key key) {

        Node node = hashtable.get(key);
        while (node != null) {
            if (node.scope == scope)
                return 1;
            node = node.prevNode;
        }
        return 0;

    }

    void increase_scope() {
        scope++;
    }

    void decrease_scope() {
        scope--;
    }

    void insertLibfuncs() {

        LinkedList<Param> params;
        LinkedList<Integer> arraylist;

        params = new LinkedList<>();
        params.addLast(new Param("int", new Key("n"), false, null));
        insert(new Key("puti"), null, null, null, params, true, "nothing");


        params = new LinkedList<>();
        params.addLast(new Param("char", new Key("c"), false, null));
        insert(new Key("putc"), null, null, null, params, true, "nothing");


        params = new LinkedList<>();
        arraylist = new LinkedList<>();
        arraylist.addLast(0);
        params.addLast(new Param("char", new Key("s"), true, arraylist));
        insert(new Key("puts"), null, null, null, params, true, "nothing");


        insert(new Key("geti"), null, null, null, null, true, "int");

        insert(new Key("getc"), null, null, null, null, true, "char");


        params = new LinkedList<>();
        params.addLast(new Param("int", new Key("n"), false, null));
        arraylist = new LinkedList<>();
        arraylist.addLast(0);
        params.addLast(new Param("char", new Key("s"), true, arraylist));
        insert(new Key("gets"), null, null, null, params, true, "nothing");


        params = new LinkedList<>();
        params.addLast(new Param("int", new Key("n"), false, null));
        insert(new Key("abs"), null, null, null, params, true, "int");


        params = new LinkedList<>();
        params.addLast(new Param("char", new Key("c"), false, null));
        insert(new Key("ord"), null, null, null, params, true, "int");


        params = new LinkedList<>();
        params.addLast(new Param("int", new Key("n"), false, null));
        insert(new Key("chr"), null, null, null, params, true, "char");


        params = new LinkedList<>();
        arraylist = new LinkedList<>();
        arraylist.addLast(0);
        params.addLast(new Param("char", new Key("s"), true, arraylist));
        insert(new Key("strlen"), null, null, null, params, true, "int");


        params = new LinkedList<>();
        arraylist = new LinkedList<>();
        arraylist.addLast(0);
        params.addLast(new Param("char", new Key("s1"), true, arraylist));

        arraylist = new LinkedList<>();
        arraylist.addLast(0);
        params.addLast(new Param("char", new Key("s2"), true, arraylist));
        insert(new Key("strcmp"), null, null, null, params, true, "int");


        params = new LinkedList<>();
        arraylist = new LinkedList<>();
        arraylist.addLast(0);
        params.addLast(new Param("char", new Key("trg"), true, arraylist));

        arraylist = new LinkedList<>();
        arraylist.addLast(0);
        params.addLast(new Param("char", new Key("src"), true, arraylist));
        insert(new Key("strcpy"), null, null, null, params, true, "nothing");


        params = new LinkedList<>();
        arraylist = new LinkedList<>();
        arraylist.addLast(0);
        params.addLast(new Param("char", new Key("trg"), true, arraylist));

        arraylist = new LinkedList<>();
        arraylist.addLast(0);
        params.addLast(new Param("char", new Key("src"), true, arraylist));
        insert(new Key("strcat"), null, null, null, params, true, "nothing");
    }


    public void print() {

        System.out.println("List:\n");
        for (Node aList : list) {
            aList.print();
        }
    }

}
