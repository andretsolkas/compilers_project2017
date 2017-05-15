
import java.util.*;

public class Param {

	String type;
	Key idname;
	LinkedList <Integer> arraylist;
	
	
	public Param(String tp, Key name, LinkedList <Integer> arlist)
	{	
		if(tp!=null)										//Copying all values
			type = new String(tp);
		else type = null;
		
		
		if(name != null)
			idname = new Key(name.name);
		else idname = null;
		
			
		arraylist = new LinkedList<>();
	
		if(arlist!=null && !arlist.isEmpty()){
			for(int i=0; i<arlist.size(); i++){
				arraylist.addLast(new Integer(arlist.get(i)));
			}
		}
		else arraylist = null;
	}
	
	public void print(){
		int i;
		System.out.printf("Id: %s, ", idname.name);
		
		System.out.printf("Type: %s", type);
		
		if(arraylist != null && !arraylist.isEmpty()){
				
			for(i=0; i<arraylist.size(); i++){
				System.out.printf("[%d]",arraylist.get(i).intValue());
			}
			//System.out.printf("\n");
		}
	}
	
}
