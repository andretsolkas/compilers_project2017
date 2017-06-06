
import compiler.parser.*;
import compiler.lexer.*;
import compiler.node.*;
//import compiler.analysis.*;
import java.io.*;


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
		System.exit(1);
	}
	
		File file = new File("myAssembly.s");
		
		try{
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			
	        tree.apply(new Visitor(writer));
			
			writer.close();
		}
		
		catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(1);
		}
		

    }
}
