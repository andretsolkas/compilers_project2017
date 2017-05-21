
import java.util.LinkedList;

public class QuadManager{

	int quad = 0;
	Integer temp = 0;;
	
	LinkedList<IRelement> stack = new LinkedList<>();
	LinkedList<IRelement> trueStack = new LinkedList<>();				//Using it as a stack
	LinkedList<IRelement> falseStack = new LinkedList<>();				//Using it as a stack
	
	LinkedList<Quad> quads = new LinkedList<>();
	
	
	
	public int nextQuad(){
		return ++quad;
	}
	
	public Quad genQuad(String opcode, String op1, String op2, String dest){
		
		Quad quad = new Quad(opcode, op1, op2, dest);
		quads.addLast(quad);

		return quad;
	}
	
	
	public String newtemp(String type){			//!!!!!!
		temp++;
		String tmp = "$";
		
		return tmp.concat(temp.toString());
	}
	
	public void emptylist(){
		
	}
	
	public void makelist(String x){
		
	}
	
	public void merge(){
		
	}
	
	public void backpatch(){
		
	}
	
	/*
	void insertQuad(Quad quad){
		quads.addLast(quad);
	}
	*/
	
	
	public void printQuads(){

		for(int i=0; i<quads.size(); i++){
		
			System.out.printf("%d: ", i+1);
			quads.get(i).print();
		}
	}
	
}
