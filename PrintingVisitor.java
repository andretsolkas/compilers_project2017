//package compiler;

import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;

import java.util.Collections;

public class PrintingVisitor extends DepthFirstAdapter {
	int i;
    int indentation = 0;
    private void addIndentationLevel() {
        indentation++;
    }

    private void removeIndentationLevel() {
        indentation--;
    }

    private void printIndentation() {
        System.out.print(String.join("", Collections.nCopies(indentation, " ")));
    }



    public void outAFuncDecl(AFuncDecl node)
    {
	System.out.println(";\n");
    }

    public void inAVarDef(AVarDef node)
    {
	System.out.println("variable definition\n");
    }

    public void inAHeader(AHeader node)
    {
	printIndentation();
	System.out.printf("%s Name: %s", node.getFun().toString(), node.getId().toString());
    }


    public void inAHelper(AHelper node)
    {	if(node.getFparDef() != null)	i=1; else i=0;
	if(node.getHelper0() != null)	i += node.getHelper0().size();
	System.out.printf(" <%d : arguments> ", i);
    }

    public void inADataRetType(ADataRetType node)
    {	System.out.printf("%s\n", node.getDataType().toString());
    }

    public void inANoneRetType(ANoneRetType node)
    {	System.out.printf("%s\n", node.getNothing().toString());
    }

    public void inABlock(ABlock node)
    {
	printIndentation();
	System.out.printf("{ ");
    }


    public void outABlock(ABlock node)
    {
	printIndentation();
	System.out.printf("}\n");
    }



    @Override
    public void inANoelseIfStmt(ANoelseIfStmt node) {
        addIndentationLevel();

        printIndentation();
        System.out.println("Entering if statement");
        printIndentation();
        System.out.printf("Then branch has %d statements and else branch has %d statements\n",
                node.getThen().size(), node.getElse().size());
    }

    @Override
    public void outAIfStmt(AIfStmt node) {
        printIndentation();
        System.out.println("Exiting if statement");

        removeIndentationLevel();
    }




/*
public void inAElseIfstmt(AElseIfstmt node)
{		
	printIndentation();
	System.out.printf("Ifelse ");
	addIndentationLevel(); 
}

public void outAElseIfstmt(AElseIfstmt node)
{		
	printIndentation();
	System.out.printf("End Ifelse ");
	removeIndentationLevel(); 
}

public void inAWithelse(AWithelse node)
{		
	printIndentation();
	System.out.printf("IfWithelse ");
	addIndentationLevel(); 
}

public void outAWithelse(AWithelse node)
{		
	printIndentation();
	System.out.printf("End IfWithelse ");
	removeIndentationLevel(); 
}
*/

}

