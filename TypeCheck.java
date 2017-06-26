import java.util.LinkedList;

class TypeCheck {

    final String type;
    final String idname;                            //It is not null only in case the element corresponds to an identifier, existing in the symbol table
    LinkedList<String> indices;            //Holds the current indices-- if not null the element given is an array
    String num;                                //In case i can have the real value during static checking i keep it for array's index bounds checking
    int dimensions = 0;                        // In case of array this is >0

    TypeCheck(String tp, LinkedList<String> arlist, String n, String name, int dim) {
        if (tp != null)                                                    //Copying all values
            type = tp;
        else type = null;

        if (arlist != null) {
            indices = new LinkedList<>();

            for (String anArlist : arlist) indices.addLast(anArlist);
        } else indices = null;

        if (n != null)
            num = n;
        else num = null;

        if (name != null)
            idname = name;
        else idname = null;

        dimensions = dim;

    }

    public void print() {
        int i;
        System.out.printf("Type = %s\n", type);

        if (indices != null && !indices.isEmpty()) {

            System.out.printf("Arraylist = ");

            for (i = 0; i < indices.size() - 1; i++) {
                System.out.printf("[%s], ", indices.get(i));
            }

            System.out.printf("[%s]\n", indices.get(i));
        } else System.out.printf("Arraylist = null\n");


        System.out.printf("idname = %s\n", idname);
        System.out.printf("Num = %s\n", num);


    }
}
