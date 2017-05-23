
import java.util.LinkedList;

public class QuadManager{

	int quadnum = 1;
	Integer temp = 0;;
	
	LinkedList<IRelement> stack = new LinkedList<>();
	
	LinkedList<Quad> quads = new LinkedList<>();
	
	
	
	public int nextQuad(){
		return quadnum;
	}
	
	public Quad genQuad(String opcode, String op1, String op2, String dest){
		
		Quad quad = new Quad(opcode, op1, op2, dest);
		quads.addLast(quad);

		quadnum++;
		
		return quad;
	}
	
	
	public String newtemp(String type){			//!!!!!! whats type for???????????????????????
		temp++;
		String tmp = "$";
		
		return tmp.concat(temp.toString());
	}

	
	public LinkedList<Quad> merge(LinkedList<Quad> list1, LinkedList<Quad> list2){
		
		for(int i=0; i<list2.size(); i++){
			list1.addLast(list2.get(i));
		}
		
		return list1;
	}
	
	
	public void backpatch(LinkedList<Quad> stack, Integer nextquad){
		
		for(int i=0; i<stack.size(); i++){
			stack.get(i).dest = new String(nextquad.toString());
		}
	}
	
	public void printQuads(){

		for(int i=0; i<quads.size(); i++){
		
			System.out.printf("%d: ", i+1);
			quads.get(i).print();
		}
	}
	
}
