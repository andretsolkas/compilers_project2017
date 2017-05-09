import java.util.LinkedList;

public class TypeCheck {

	String type;
	LinkedList <String> arraylist;
	String num;
	String idname;
	LinkedList <Integer> declarraylist;

	
	public TypeCheck(String tp, LinkedList <String> arlist, String n, String name, LinkedList <Integer> declarlist)
	{
		if(tp!=null)													//Copying all values
			type = new String(tp);
		else type = null;
		

		arraylist = new LinkedList<>();
		
		if(arlist!=null){
			arraylist = new LinkedList<>();
			
			for(int i=0; i<arlist.size(); i++)
				arraylist.addLast(new String(arlist.get(i)));
		}
		else arraylist = null;

		
		
		if(n!=null)
			num =  new String(n);
		else num = null;
			
		if(name!=null)
			idname = new String(name);
		else idname = null;
		
		
		
		declarraylist = new LinkedList<>();
		
		if(declarlist!=null){
			declarraylist = new LinkedList<>();
			
			for(int i=0; i<declarlist.size(); i++)
				declarraylist.addLast(new Integer(declarlist.get(i)));
		}
		else declarraylist = null;

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
