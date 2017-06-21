
import java.util.*;

public class Param {

	String type;
	Key idname;
	boolean reference;
	LinkedList <Integer> arraylist;
	int offset;
	
	
	public Param(String tp, Key name, boolean ref, LinkedList <Integer> arlist)
	{	
		if(tp!=null)										//Copying all values
			type = new String(tp);
		else type = null;
		
		
		if(name != null)
			idname = new Key(name.name);
		else idname = null;
		
		reference = ref;

		arraylist = new LinkedList<>();
	
		if(arlist!=null && !arlist.isEmpty()){
			for(int i=0; i<arlist.size(); i++){
				arraylist.addLast(new Integer(arlist.get(i)));
			}
		}
		else arraylist = null;
	
	
		offset = 0;
	
	}
	
	public void print(){
		int i;
		System.out.printf("Id: %s, ", idname.name);
		
		System.out.printf("Offset: %d ", offset);
		
		System.out.printf("Type: %s", type);
		
		if(arraylist != null && !arraylist.isEmpty()){
				
			for(i=0; i<arraylist.size(); i++){
				System.out.printf("[%d]",arraylist.get(i).intValue());
			}
			//System.out.printf("\n");
		}
		//System.out.printf("\n");
	}

	
	public boolean differ(Param pm){
		
		if(!type.equals(pm.type))
			return true;
		
		if(!idname.name.equals(pm.idname.name))
			return true;
		
		if(reference != pm.reference)
			return true;
		
		if(arraylist != null && pm.arraylist != null){
			if(arraylist.size() != pm.arraylist.size())
				return true;
			
			else{
				for(int i=0; i<arraylist.size(); i++){
					if(arraylist.get(i).intValue() != pm.arraylist.get(i).intValue()){
						return true;
					}
				}
			}
		}
		
		else if(!(arraylist == null && pm.arraylist == null))
			return true;
	
		return false;
	}
	
}
