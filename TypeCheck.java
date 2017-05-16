import java.util.LinkedList;

public class TypeCheck {

	String type;
	LinkedList <String> indices;			//Holds the current indices-- if not null the element given is an array
	String num;								//In case i can have the real value during static checking i keep it for array's index bounds checking 
	String idname;							//It is not null only in case the element corresponds to an identifier, existing in the symbol table
	int dimensions = 0;						// In case of array this is >0
	
	public TypeCheck(String tp, LinkedList <String> arlist, String n, String name, int dim)
	{
		if(tp!=null)													//Copying all values
			type = new String(tp);
		else type = null;
		

		indices = new LinkedList<>();
		
		if(arlist!=null){
			indices = new LinkedList<>();
			
			for(int i=0; i<arlist.size(); i++)
				indices.addLast(new String(arlist.get(i)));
		}
		else indices = null;
		
		if(n!=null)
			num =  new String(n);
		else num = null;
			
		if(name!=null)
			idname = new String(name);
		else idname = null;
		
		dimensions = dim;

	}

	public void print(){
		int i;
		System.out.printf("Type = %s\n", type);

		if(indices != null && !indices.isEmpty()){
			
			System.out.printf("Arraylist = ");
			
			for(i=0; i<indices.size()-1; i++){
				System.out.printf("[%s], ",indices.get(i));
			}
			
			System.out.printf("[%s]\n",indices.get(i));
		}
		
		else System.out.printf("Arraylist = null\n");
		
		
		System.out.printf("idname = %s\n", idname);
		System.out.printf("Num = %s\n", num);


	}
}
