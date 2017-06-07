
public class Temp {

	String tempname;
	String type;
	int offset;
	

	public Temp(String tmp, String tp, int ofs){
		if(tmp!=null)
			tempname = new String(tmp);
		else tempname = null;
		
		if(tp!=null)
			type = new String(tp);
		else type = null;
		
		offset = ofs;
	}
	
	public void print(){
		System.out.println(tempname + " - " + type + " - " + offset);
	}
}
