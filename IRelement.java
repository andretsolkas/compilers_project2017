
import java.util.LinkedList;


public class IRelement {

	String type;
	String place;
	LinkedList<Quad> next;			//Label lists
	LinkedList<Quad> True;
	LinkedList<Quad> False;
	
	
	public IRelement(String tp, String pl, LinkedList<Quad> nxt, LinkedList<Quad> tr, LinkedList<Quad> fls){
		
		place = pl;					//PASS BY REFERENCE
		type = tp;
		next = nxt;
		True = tr;
		False = fls;
	
	
	}
	
}
