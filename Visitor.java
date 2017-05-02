import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;

import java.util.Collections;


	class Visitor extends DepthFirstAdapter
	{
		
		SymbolTable symtable;
	
		
		public Visitor(){
			symtable = new SymbolTable();
		}
		
		
		//-------------------------------------------
		@Override
		public void defaultIn(Node node)
		{
		}

		@Override
		public void defaultOut(Node node)
		{
		}
		
		

	
		
		
		
		
		
		
		

if(node == null){
	System.out.println("Error: variable " + name + "has not been declared before\n");
	exit(1);
}

	

		
	}