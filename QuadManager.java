
import java.util.LinkedList;

public class QuadManager{

	int quadnum = 1;
	Integer temp = 0;
	
	ScopeTemp temps;
	SymbolTable symtable;
	
	int offset;
	int numChars;
	int SizeOfInt = 4;
	int SizeOfChar = 1;
	
	LinkedList<IRelement> stack = new LinkedList<>();
	LinkedList<String> places = new LinkedList<>();             //It will never be null. 
	LinkedList<Quad> quads = new LinkedList<>();
	
	
	public QuadManager(SymbolTable st){
		temps = new ScopeTemp(0, new LinkedList<Temp>());
		symtable = st;
	}
	
	public int nextQuad(){
		return quadnum;
	}
	
	public Quad genQuad(String opcode, String op1, String op2, String dest){
		
		Quad quad = new Quad(opcode, op1, op2, dest);
		quads.addLast(quad);

		quadnum++;
		
		return quad;
	}
	
	
	public String newtemp(String type){
		temp++;
		String str, tmp = "$";
		
		str = tmp.concat(temp.toString());
	
		if(type.equals("int")){
			
			if(numChars != 0){
				offset += SizeOfInt-numChars;		//padding
				numChars = 0;
			}	
			offset += SizeOfInt;
		}
		
		else if(type.equals("char")){
			numChars = (numChars+1)%4;
			offset += SizeOfChar;
		}
		
		
		
		
		temps.temps.addLast(new Temp(str, type, offset));
	
		return str;
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

	public void clearTemps(){
		temps.temps = new LinkedList<Temp>();
	}


	public void printQuads(){

		for(int i=0; i<quads.size(); i++){
		
			System.out.printf("%d: ", i+1);
			quads.get(i).print();
		}
	}

	
	public void printTemps(){
		
		temps.print();
	}
	
}
