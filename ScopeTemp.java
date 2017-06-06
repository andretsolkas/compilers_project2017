
import java.util.LinkedList;

public class ScopeTemp{

	int scope;
	LinkedList<Temp> temps;
	
	
	public ScopeTemp(int scp, LinkedList<Temp> tmps){
		scope = scp;
		temps = tmps;
	}
	
	
	public Temp findElement(String a){
		
		for(int i=0; i<temps.size(); i++){
			
			if(temps.get(i).tempname.equals(a)){
				return temps.get(i);
			}
		}
		return null;
	}
	
	public void print(){
		System.out.println("Scope: " + scope);
		for(int i=0; i<temps.size(); i++)
			temps.get(i).print();
	}
}
