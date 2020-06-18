import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {
        try{
            //Lexer
            Java8Lexer lexer = null;
            if(args.length > 0)
                lexer = new Java8Lexer(CharStreams.fromFileName(args[0]));

            else
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                boolean validOption = false;
                while (validOption==false)
                {
                    System.out.println("Press 0 if you want to write your code on the console" +
                            ", 1 if you want to use code from file");
                    String option =  reader.readLine();
                    switch (option)
                    {
                        case "0":
                            System.out.println("Write your code! When you are done press ctrl d");
                            System.out.println("-----------------------------------------------");
                            lexer = new Java8Lexer(CharStreams.fromStream(System.in));
                            validOption = true;
                            break;
                        case "1":
                            System.out.println("Write the name of your file, your file MUST be inside the Input folder");
                            System.out.println("Example: example.txt");
                            option = reader.readLine();
                            System.out.println("-----------------------------------------------");
                            try{
                                lexer = new Java8Lexer(CharStreams.fromFileName("Input/"+option));
                                validOption = true;
                            }catch (Exception e){
                                System.out.println("Enter valid file name");
                                validOption = false;
                            }

                            break;
                        default:
                            System.out.println("Enter valid option");

                    }

                }

            }

            CommonTokenStream tokens = new CommonTokenStream(lexer);

            //Parser
            Java8Parser parser = new Java8Parser(tokens);
            ParseTree tree = parser.compilationUnit();
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(new JavaListener(),tree);

            /*
            //System.out.println(tokens.getTokens());
            //System.out.println(tree.toStringTree(parser));
            //Walker
            //Create generic parse tree walker
            ParseTreeWalker walker = new ParseTreeWalker();
            //Walk the tree
            CheckForImports importCheck = new CheckForImports();
            walker.walk(importCheck,tree);
            walker.walk(new Translator(importCheck.getImports()), tree);
            System.out.println();//print ln after translation
            */


        }
        catch(Exception e){
            System.err.println("Error: "+e.getStackTrace()[0].getLineNumber());
        }
    }
}