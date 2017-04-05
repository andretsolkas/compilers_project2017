import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;

import java.util.Collections;


	class Visitor extends DepthFirstAdapter
	{
		int indent;

	    private void printIndent() {
       		 System.out.print(String.join("", Collections.nCopies(indent, " ")));
    		}


		private void printNode(Node node)
		{
			printIndent();

			System.out.println("  " + node.toString());

		}

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
	}

