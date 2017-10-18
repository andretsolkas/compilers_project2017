class Temp {

    final String type;
    final int strlen;                    //in case of a string it holds it's length + 1
    final String strname;
    final int offset;
    String tempname;


    Temp(String tmp, String tp, int ofs, int len, String str) {
        if (tmp != null)
            tempname = tmp;
        else tempname = null;

        if (tp != null)
            type = tp;
        else type = null;

        strlen = len;

        offset = ofs;

        if (str != null)
            strname = str;
        else strname = null;

    }

    void print() {
        System.out.println(tempname + " - " + type + " - " + offset + " - " + strlen + " - " + strname);
    }
}
