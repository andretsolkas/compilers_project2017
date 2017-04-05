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
	    Lexer lex = new Lexer(new PushbackReader(new FileReader(args[0]), 1024));
      Parser p = new Parser(lex);
	    tree = p.parse();
     }
	   catch(Exception e){
	    System.out.println(e.getMessage());
	   }
    System.out.println("Now printing the statement tree:\n\n");
    tree.apply(new Visitor());
  }
}
