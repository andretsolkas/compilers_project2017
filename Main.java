
import compiler.parser.*;
import compiler.lexer.*;
import compiler.node.*;
import java.io.*;


public class Main{

    public static void main(String[] args){
        
		Start tree = null;
	
		
			
		
		try {
			
			File file = new File("myAssembly.s");
			
	        Lexer lex = new Lexer(new PushbackReader(new FileReader(args[0]), 1024));
	
	        Parser p = new Parser(lex);
	        tree = p.parse();
	
			file.createNewFile();
			FileWriter writer = new FileWriter(file);

			Visitor visitor = new Visitor(writer);
			visitor.optimizer = (args.length == 2) && args[1].equals("-f");
	        tree.apply(visitor);
			
			writer.close();
		}
		
		catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(1);
		}
		
    }
}
