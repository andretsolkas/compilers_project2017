import java.util.LinkedList;


class IRelement {

    final String type;
    final String place;
    final LinkedList<Quad> next;            //Label lists
    LinkedList<Quad> True;
    LinkedList<Quad> False;


    IRelement(String tp, String pl, LinkedList<Quad> nxt, LinkedList<Quad> tr, LinkedList<Quad> fls) {
        place = pl;                    //PASS BY REFERENCE
        type = tp;
        next = nxt;
        True = tr;
        False = fls;
    }

}
