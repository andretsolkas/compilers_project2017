
public class Temp {

	String tempname;
	String type;
	int strlen;					//in case of a string it holds it's length + 1
	String strname;
	int offset;
	

	public Temp(String tmp, String tp, int ofs, int len, String str){
		if(tmp!=null)
			tempname = new String(tmp);
		else tempname = null;
		
		if(tp!=null)
			type = new String(tp);
		else type = null;
		
		strlen = len;
		
		offset = ofs;
		
		if(str!=null)
			strname = new String(str);
		else strname = null;
		
	}
	
	public void print(){
		System.out.println(tempname + " - " + type + " - " + offset + " - " + strlen + " - " + strname);
	}
}
