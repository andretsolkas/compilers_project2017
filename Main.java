import compiler.lexer.Lexer;
import compiler.node.Start;
import compiler.parser.Parser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PushbackReader;


public class Main {

    public static void main(String[] args) {

        try {

            File file = new File("myAssembly.s");

            Lexer lex = new Lexer(new PushbackReader(new FileReader(args[0]), 1024));

            Parser p = new Parser(lex);
            Start tree = p.parse();

            file.createNewFile();
            FileWriter writer = new FileWriter(file);

            Visitor visitor = new Visitor(writer);
            visitor.optimizer = (args.length == 2) && args[1].equals("-f");
            tree.apply(visitor);

            writer.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

    }
}
