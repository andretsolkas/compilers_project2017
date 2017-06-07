fun main():nothing

	var res,x:int;

	fun prime(number:int):int

		var i:int;
	{
		if(number mod 2 = 0) then
			return 2;

		i<-3;

		while(i < number) do
		{
			if(number mod i = 0) then
				return i;

			i<-i+2;
		}

		return -1;
		
	}
{

	x <- geti();
	puti(prime(x));

}

