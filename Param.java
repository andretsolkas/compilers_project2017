
import java.util.*;

public class Param {

	String type;
	LinkedList <Integer> arraylist;
	
	
	public Param(String tp, LinkedList <Integer> arlist)
	{
		type = tp;
		arraylist = arlist;
	}
	
	public void print(){
		int i;
		System.out.printf("Type = %s", type);
		
		if(arraylist != null && !arraylist.isEmpty()){
				
			for(i=0; i<arraylist.size()-1; i++){
				System.out.printf("[%d], ",arraylist.get(i).intValue());
			}
			
			System.out.printf("[%d]",arraylist.get(i).intValue());
	
		}
	}
}
