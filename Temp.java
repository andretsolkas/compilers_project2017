
public class Temp {

	String tempname;
	String type;
	

	public Temp(String tmp, String tp){
		if(tmp!=null)
			tempname = new String(tmp);
		else tempname = null;
		
		if(tp!=null)
			type = new String(tp);
		else type = null;
	}
	
	public void print(){
		System.out.println(tempname + " - " + type);
	}
}
