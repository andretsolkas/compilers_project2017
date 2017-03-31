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

    public void inAProgram(AProgram node)
    {
       	System.out.println("Entering AST");
	addIndentationLevel();    
}

    public void outAProgram(AProgram node)
    {
        System.out.println("Exiting AST");
	removeIndentationLevel();
    }

    public void inAFuncDef(AFuncDef node)
    {
	printIndentation();
	System.out.println("Function Declaration");
    }

    public void outAFuncDef(AFuncDef node)
    {
	printIndentation();
	System.out.println("End Of Function Declaration");
    }


    public void inAHeader(AHeader node)
    {
	printIndentation();
	System.out.printf("%s Name: %s", node.getFun().toString(), node.getId().toString());
    }

    public void outAHeader(AHeader node)
    {
	printIndentation();
	System.out.println("Header End");
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
}
