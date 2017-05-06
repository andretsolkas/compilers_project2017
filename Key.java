
//import java.util.*;



public class Key {
	String name;
	
	public Key(String str){
		
		name = new String(str);
	}

	public String getKey(){
		return name;
	}

	
	public int hashCode(){
		int hashkey=0;
		for(int i=0; i<name.length(); i++)
			hashkey += (int)name.charAt(i);

		return hashkey;
	}

	@Override
	public boolean equals(Object o){
		
		Key key = (Key) o;
		if(name.equals(key.name))
			return true;
		return false;
	}

}

