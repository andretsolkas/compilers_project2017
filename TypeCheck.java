import java.util.LinkedList;

public class TypeCheck {

	String type;
	LinkedList <String> arraylist;
	String idname;
	String num;
	LinkedList <Integer> declarraylist;
	
	public TypeCheck(String tp, LinkedList <String> arlist, String n, String name, LinkedList <Integer> declarlist)
	{
		type = tp;
		arraylist = arlist;
		idname = name;
		num = n;
		declarraylist = declarlist;
	}

	public void print(){
		int i;
		System.out.printf("Type = %s\n", type);
		
		if(arraylist != null && !arraylist.isEmpty()){
			
			System.out.printf("Arraylist = ");
			
			for(i=0; i<arraylist.size()-1; i++){
				System.out.printf("[%s], ",arraylist.get(i));
			}
			
			System.out.printf("[%s]\n",arraylist.get(i));
		}
		
		else System.out.printf("Arraylist = null\n");
		
		System.out.printf("idname = %s\n", idname);
		System.out.printf("Num = %s\n", num);
	
		if(declarraylist != null && !declarraylist.isEmpty()){
			
			System.out.printf("DeclArraylist = ");
			
			for(i=0; i<declarraylist.size()-1; i++){
				System.out.printf("[%d], ",declarraylist.get(i).intValue());
			}
			
			System.out.printf("[%d]\n",declarraylist.get(i).intValue());
		}
	
		else System.out.printf("DeclArraylist = null\n");
	}
}
