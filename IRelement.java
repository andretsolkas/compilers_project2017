
import java.util.LinkedList;


public class IRelement {

	String type;
	String place;
	LinkedList<Quad> next;			//Label lists
	LinkedList<Quad> True;
	LinkedList<Quad> False;
	
	
	public IRelement(String tp, String pl, LinkedList<Quad> nxt, LinkedList<Quad> tr, LinkedList<Quad> fls){
		
		if(pl!=null){
			place = new String(pl);
		}
		else place = null;
	
		
		if(tp!=null){
			type = new String(tp);
		}
		else type = null;
	
	/*
		if(nxt!=null){
			next = new LinkedList<>();
			
			for(int i=0; i<nxt.size(); i++)
				next.addLast(new Quad(nxt.get(i)));
		}
		else next = null;
	*/
		next = nxt;				//REFERENCE
		True = tr;
		False = fls;
	
	
	}
	
}
