
public class Quad {

	String opcode;
	String op1;
	String op2;
	String dest;
	
	public Quad(String opcd, String o1, String o2, String dst){
		
		opcode = new String(opcd);
		
		if(o1!=null)
			op1 = new String(o1);
		else op1 = null;
				
		if(o2!=null)
			op2 = new String(o2);
		else op2 = null;
		
		if(dst!=null)
			dest = new String(dst);
		else dest = null;
	}
	
	
	public void print(){
		System.out.println(opcode + ", " + op1 + ", " + op2 + ", " + dest);
	}
}
