import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;

import java.util.Collections;


	class Visitor extends DepthFirstAdapter
	{
		
		SymbolTable symtable;
	
		
		public Visitor(){
			symtable = new SymbolTable();
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
/*
		int i;
		int indent;
		String[] parts;

	    private void printIndent() {
       		 System.out.print(String.join("", Collections.nCopies(indent, " ")));
    		}


		private void printNode(Node node)
		{
			parts = node.getClass().getName().split("\\.");
			System.out.printf("%s",parts[2]);
			printIndent();
			System.out.println("  		" + node.toString());

		}
//-------------------------------------------
		@Override
		public void defaultIn(Node node)
		{
			printNode(node);
			indent++;
		}

		@Override
		public void defaultOut(Node node)
		{
			indent--;
		}

	
*/

		
	}