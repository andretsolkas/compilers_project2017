
public class Key {
	String name;
	
	public Key(String str){
		
		name = str;
	}

	public String getKey(){
		return name;
	}

	
    @Override
	public int hashCode(){
		int hashkey=0;
		for(int i=0; i<name.length(); i++)
			hashkey += (int)name.charAt(i);

		return hashkey;
	}

	@Override
	public boolean equals(Object o){
		
		Key key = (Key) o;
		return name.equals(key.name);
	}

}

