import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Stack;

public class MethodListener extends Java8ParserBaseListener{
    private String methodName;
    private StringBuilder toFile = new StringBuilder();
    private boolean isInsideMethod = false;
    private boolean isInsideForSetUp = false;
    private static void write(String data,String fileName) {
        try {
            //Guardar imagen
            SourceStringReader reader = new SourceStringReader(data);
            final ByteArrayOutputStream os = new ByteArrayOutputStream();

            /*
            reader.outputImage(os , new FileFormatOption(FileFormat.PNG));
            // The XML is stored into svg
            final String png_image = new String(os.toByteArray());
            Files.write(Paths.get("Documentation/"+fileName+".png"), png_image.getBytes());
            */
            reader.outputImage(os , new FileFormatOption(FileFormat.SVG));
            // The XML is stored into svg
            final String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));
            //uml.java
            //System.out.println(svg);

            Files.write(Paths.get("Documentation/images/"+fileName+".svg"), svg.getBytes());

            Files.write(Paths.get("Documentation/"+fileName+".puml"), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void enterMethodDeclarator(Java8Parser.MethodDeclaratorContext ctx) {
        methodName = ctx.Identifier().getText();
        System.out.println("entra a "+methodName);
        toFile.append("@startuml\n");
        toFile.append("title ").append(methodName).append("\n");
    }


    @Override
    public void enterMethodBody(Java8Parser.MethodBodyContext ctx) {
        isInsideMethod = true;
        toFile.append("start\n");
    }

    @Override
    public void enterVariableDeclaratorList(Java8Parser.VariableDeclaratorListContext ctx) {
        ArrayList<String> variables = new ArrayList<>();
        String assignation = null;
        for(Java8Parser.VariableDeclaratorContext varctx: ctx.variableDeclarator())
        {
            System.out.println("Declaracion de variable "+varctx.getText());
            variables.add(varctx.variableDeclaratorId().Identifier().getText());
            if(varctx.variableInitializer()!=null)
            {
                assignation = varctx.variableInitializer().getText();
            }
        }
        for (int i = 0; i <variables.size() ; i++) {
            toFile.append(":New variable: "+variables.get(i)+";\n");
            if(assignation!=null)
            {
                toFile.append(":Assign "+assignation+" to "+variables.get(i)+";\n");
            }

        }
    }

    @Override
    public void enterAssignment(Java8Parser.AssignmentContext ctx) {
        if(isInsideForSetUp)
            return;
        String expresion = ctx.expression().getText();
        String lefHandSide = ctx.leftHandSide().getText();
        switch (ctx.assignmentOperator().getText())
        {
            case "=":
                toFile.append(":Assign "+expresion+" to "+lefHandSide+";\n");
                break;
            case "+=":
                toFile.append(":Assign "+expresion+"+"+lefHandSide+" to "+lefHandSide+";\n");
                break;
            case "*=":
                toFile.append(":Assign "+expresion+"*"+lefHandSide+" to "+lefHandSide+";\n");
                break;
            case "/=":
                toFile.append(":Assign "+lefHandSide+"/"+expresion+" to "+lefHandSide+";\n");
                break;
            case "%=":
                toFile.append(":Assign "+lefHandSide+"%"+expresion+" to "+lefHandSide+";\n");
                break;
        }

    }

    @Override
    public void enterIfThenStatement(Java8Parser.IfThenStatementContext ctx) {
        toFile.append("if ("+ctx.expression().getText()+") then (yes)\n");

    }

    @Override
    public void exitIfThenStatement(Java8Parser.IfThenStatementContext ctx) {
        toFile.append("else (no)\n");
        toFile.append("endif\n");
    }

    @Override
    public void enterIfThenElseStatement(Java8Parser.IfThenElseStatementContext ctx) {
        toFile.append("if ("+ctx.expression().getText()+") then (yes)\n");
    }

    @Override
    public void enterElseStatement(Java8Parser.ElseStatementContext ctx) {
        toFile.append("else (no)\n");
    }

    @Override
    public void exitIfThenElseStatement(Java8Parser.IfThenElseStatementContext ctx) {
        toFile.append("endif\n");
    }

    @Override
    public void enterIfThenElseStatementNoShortIf(Java8Parser.IfThenElseStatementNoShortIfContext ctx) {
        toFile.append("if ("+ctx.expression().getText()+") then (yes)\n");
    }

    @Override
    public void exitElseStatementNoShortIf(Java8Parser.ElseStatementNoShortIfContext ctx) {
        toFile.append("else (no)\n");
    }

    @Override
    public void exitIfThenElseStatementNoShortIf(Java8Parser.IfThenElseStatementNoShortIfContext ctx) {
        toFile.append("endif\n");
    }


    @Override
    public void enterForSetUp(Java8Parser.ForSetUpContext ctx) {
        isInsideForSetUp = true;
    }

    @Override
    public void exitForSetUp(Java8Parser.ForSetUpContext ctx) {
        if(ctx.expression()!=null)
            toFile.append("while ("+ctx.expression().getText()+") is (Yes)\n");
        else
            toFile.append("while () is (Yes)\n");
        if(ctx.forUpdate()!=null)
            toFile.append(":"+ctx.forUpdate().getText()+";\n");
        isInsideForSetUp = false;
    }

    @Override
    public void exitBasicForStatement(Java8Parser.BasicForStatementContext ctx) {
        toFile.append("endwhile (No)\n");
    }

    @Override
    public void exitBasicForStatementNoShortIf(Java8Parser.BasicForStatementNoShortIfContext ctx) {
        toFile.append("endwhile (No)\n");
    }


    @Override
    public void enterWhileStatement(Java8Parser.WhileStatementContext ctx) {
        toFile.append("while ("+ctx.expression().getText()+") is (Yes)\n");
    }

    @Override
    public void exitWhileStatement(Java8Parser.WhileStatementContext ctx) {
        toFile.append("endwhile (No)\n");
    }

    @Override
    public void enterWhileStatementNoShortIf(Java8Parser.WhileStatementNoShortIfContext ctx) {
        toFile.append("while ("+ctx.expression().getText()+") is (Yes)\n");
    }

    @Override
    public void exitWhileStatementNoShortIf(Java8Parser.WhileStatementNoShortIfContext ctx) {
        toFile.append("endwhile (No)\n");
    }

    @Override
    public void enterDoStatement(Java8Parser.DoStatementContext ctx) {
        toFile.append("repeat\n");
    }

    @Override
    public void exitDoStatement(Java8Parser.DoStatementContext ctx) {
        toFile.append("repeat while ("+ctx.expression().getText()+")\n");
    }

    @Override
    public void exitMethodBody(Java8Parser.MethodBodyContext ctx) {
        toFile.append("stop\n");
        toFile.append("@enduml\n");
        write(toFile.toString(),"Method_"+methodName+"_diagram");
        isInsideMethod = false;
        System.out.println("sale de "+methodName);
    }
}
