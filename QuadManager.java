import java.util.LinkedList;

class QuadManager {

    final ScopeTemp temps;
    final LinkedList<IRelement> stack = new LinkedList<>();
    final LinkedList<Quad> quads = new LinkedList<>();
    int offset;
    int numChars;
    LinkedList<String> places = new LinkedList<>();             //It will never be null.
    private int quadnum = 1;
    private Integer temp = 0;


    QuadManager() {
        temps = new ScopeTemp(new LinkedList<>());
    }

    int nextQuad() {
        return quadnum;
    }

    Quad genQuad(String opcode, String op1, String op2, String dest) {

        Quad quad = new Quad(opcode, op1, op2, dest);
        quads.addLast(quad);

        quadnum++;

        return quad;
    }


    String newtemp(String type, int len, String strname) {
        temp++;
        String str = "$" + temp.toString();

        if (type.equals("int") || len == -1) {

            int sizeOfInt = 4;
            if (numChars != 0) {
                offset += sizeOfInt - numChars;        //padding
                numChars = 0;
            }
            offset += sizeOfInt;
        } else if (type.equals("char")) {

            int sizeOfChar = 1;
            if (len != 0) {                            //String temp
                numChars = (numChars + len) % 4;
                offset += sizeOfChar * len;
            } else {
                numChars = (numChars + 1) % 4;
                offset += sizeOfChar;
            }
        }

        temps.temps.addLast(new Temp(str, type, offset, len, strname));

        return str;
    }


    LinkedList<Quad> merge(LinkedList<Quad> list1, LinkedList<Quad> list2) {

        list1.addAll(list2);

        return list1;
    }


    void backpatch(LinkedList<Quad> stack, Integer nextquad) {

        for (Quad aStack : stack) {
            aStack.dest = nextquad.toString();
        }
    }

    void clearTemps() {
        temps.temps = new LinkedList<>();
    }

    public void printQuads() {
        for (int i = 0; i < quads.size(); i++) {
            System.out.printf("%d: ", i + 1);
            quads.get(i).print();
        }
    }

    public void printTemps() {
        temps.print();
    }

}
