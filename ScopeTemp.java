
import java.util.LinkedList;

public class ScopeTemp{

	int scope;
	LinkedList<Temp> temps;
	
	
	public ScopeTemp(int scp, LinkedList<Temp> tmps){
		scope = scp;
		temps = tmps;
	}
	
	
	public void print(){
		System.out.println(scope);
		for(int i=0; i<temps.size(); i++)
			temps.get(i).print();
	}
}
