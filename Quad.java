class Quad {

    String op1;
    String op2;
    String opcode;
    String dest;

    Quad(String opcd, String o1, String o2, String dst) {

        opcode = opcd;

        op1 = o1;

        op2 = o2;

        dest = dst;
    }

    void print() {
        System.out.println(opcode + ", " + op1 + ", " + op2 + ", " + dest);
    }
}
