//package compiler;

import compiler.parser.*;
import compiler.lexer.*;
import compiler.node.*;
import compiler.analysis.*;

import java.io.*;
import java.util.Set;

public class Main{

    public static void main(String[] args){
        
	Start tree = null;

	try {

	Lexer lex = new Lexer(new PushbackReader(new FileReader(args[0])	/*new InputStreamReader(System.in)*/, 1024));
       
    	Parser p = new Parser(lex);
	tree = p.parse();

	}
	catch(Exception e){
		System.out.println(e.getMessage());
	}
       
/*	 catch (LexerException e) {
            System.err.printf("Lexing error: %s\n", e.getMessage());
        }
	catch (IOException e) {
            System.err.printf("I/O error: %s\n", e.getMessage());
            e.printStackTrace();
	}        
	catch (ParserException e) {
            System.err.printf("Parsing error: %s\n", e.getMessage());
        }
*/
        System.out.println("Now printing the statement tree:");
        tree.apply(new PrintingVisitor());
    }
}
