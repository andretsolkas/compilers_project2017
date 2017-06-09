fun main():nothing

	var len:int;
	
	fun strlen(ref str:char[]):int
		var i:int;

		{	
			i<-0;
		
			while(str[i] # '\0') do{
				i<-i+1;
			}
		
			return i;
		}

{

	len <- strlen("boo");

	puti(len);

}
