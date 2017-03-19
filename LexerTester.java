//package comp;

//import compiler.parser.*;
import compiler.lexer.*;
import compiler.node.*;
import compiler.analysis.*;
import java.io.*;

public class LexerTester{

	protected static Token token;
	
	public static void main(String[] args) {
		try
		{
System.out.println("start\n");
			
			// Create a Lexer instance.
			Lexer lex = new Lexer(new PushbackReader(new FileReader(args[0])	/*new InputStreamReader(System.in)*/, 1024));

			while(true){
				
				token = lex.next();
				if (new String(token.getText()).equals("") )
					break;
				
				token.apply(new AnalysisAdapter()
				{	public void caseTId(TId node)
					{	System.out.println("Sexy identifier" + "			" + node.getText());	}

					public void caseTWhiteSpace(TWhiteSpace node){}
						//System.out.println("Sexy whitespace");	
					
					public void caseTNumber(TNumber node)
					{	System.out.println("Sexy integer" + "			" + node.getText());	}
					
					public void caseTConchars(TConchars node)
					{	System.out.println("Sexy constant" + "			" + node.getText());	}
					
					public void caseTLineComment(TLineComment node)
					{	System.out.println("Sexy comment" + "			" + node.getText());	}
					
					public void caseTMultilineComment(TMultilineComment node)
					{	System.out.println("Sexy multiline comment" + "			" + node.getText());	}
					
					public void caseTStrings(TStrings node)
					{	System.out.println("Sexy string" + "			" + node.getText());	}
					
					public void caseTMult(TMult node)
					{	System.out.println("Sexy Operator" + "			" + node.getText());	}
					
					public void caseTLess(TLess node)
					{	System.out.println("Sexy Operator" + "			" + node.getText());	}
					
					public void caseTInteger(TInteger node)
					{	System.out.println("Sexy Key" + "			" + node.getText());	}
					
					public void caseTIf(TIf node)
					{	System.out.println("Sexy Key" + "			" + node.getText());	}
					
					public void caseTThen(TThen node)
					{	System.out.println("Sexy Key" + "			" + node.getText());	}
					
					public void caseTAnd(TAnd node)
					{	System.out.println("Sexy Key" + "			" + node.getText());	}
					
					public void caseTDo(TDo node)
					{	System.out.println("Sexy Key" + "			" + node.getText());	}
					
					public void caseTLeftPar(TLeftPar node)
					{	System.out.println("Sexy Separator" + "			" + node.getText());	}
					
					public void caseTRightPar(TRightPar node)
					{	System.out.println("Sexy Separator" + "			" + node.getText());	}
					
					public void caseTTags(TTags node)
					{	System.out.println("Sexy Separator" + "			" + node.getText());	}
					
					public void caseTMinus(TMinus node)
					{	System.out.println("Sexy Operator" + "			" + node.getText());	}
					
					public void caseTDots(TDots node)
					{	System.out.println("Sexy Separator" + "			" + node.getText());	}
					
					public void defaultCase(Node node)
					{	System.out.println("boom boom");	}
				});


			}

System.out.println("end\n");
		}
		
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}	
}

