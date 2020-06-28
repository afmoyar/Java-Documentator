import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

public class MethodListener extends Java8ParserBaseListener{
    private String methodName;
    private StringBuilder toFile = new StringBuilder();
    private boolean isInsideMethod = false;
    private boolean isInsideForSetUp = false;
    private HashSet<String> methods = new HashSet<>();

    private void write(String data,String fileName) {
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
            new File("Documentation/images").mkdirs();
            Files.write(Paths.get("Documentation/images/"+fileName+".svg"), svg.getBytes());
            new File("Documentation/puml_code").mkdirs();
            Files.write(Paths.get("Documentation/puml_code/"+fileName+".puml"), data.getBytes());
            toFile = new StringBuilder();
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
        methods.add(methodName);
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
            toFile.append(":New variable: <b>"+variables.get(i)+"</b>;\n");
            if(assignation!=null)
            {
                toFile.append(":Assign <color:darkblue><i>"+assignation+"</i></color> to <b>"+variables.get(i)+"</b>;\n");
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
                toFile.append(":Assign <color:darkblue><i>"+expresion+"</i></color> to <b>"+lefHandSide+"</b>;\n");
                break;
            case "+=":
                toFile.append(":Assign <color:darkblue><i>"+expresion+"+"+lefHandSide+"</i></color> to <b>"+lefHandSide+"</b>;\n");
                break;
            case "*=":
                toFile.append(":Assign <color:darkblue><i>"+expresion+"*"+lefHandSide+"</i></color> to <b>"+lefHandSide+"</b>;\n");
                break;
            case "/=":
                toFile.append(":Assign <color:darkblue><i>"+lefHandSide+"/"+expresion+"</i></color> to <b>"+lefHandSide+"</b>;\n");
                break;
            case "%=":
                toFile.append(":Assign <color:darkblue><i>"+lefHandSide+"%"+expresion+"</i></color> to <b>"+lefHandSide+"</b>;\n");
                break;
        }

    }

    @Override
    public void enterMethodInvocation(Java8Parser.MethodInvocationContext ctx) {

        String methodName = null;
        if(ctx.getText().contains("System.out.print") && ctx.argumentList()!=null)
        {
            toFile.append(":Print: <color:darkblue><i>"+ctx.argumentList().getText()+"</i></color>;\n");
            return;
        }
        else if(ctx.methodName()!=null)
        {
            System.out.println("ctx.methodName()!=null");
           methodName = ctx.methodName().Identifier().getText();
        }else {
            System.out.println("else");
            methodName = ctx.Identifier().getText();
        }
        if(methods.contains(methodName))
        {
            toFile.append(":Enter subroutine: <color:darkred><b>"+methodName+"</b></color>;\n");
        }
    }

    @Override
    public void exitMethodInvocation(Java8Parser.MethodInvocationContext ctx) {
        super.exitMethodInvocation(ctx);
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
    public void enterReturnStatement(Java8Parser.ReturnStatementContext ctx) {
        if(ctx.expression()!=null)
        {
            toFile.append(":<color:INDIGO><i>return</i></color> <color:INDIGO><b>"+ctx.expression().getText()+"</b></color>;\n");
        }
        else {
            toFile.append(":<color:INDIGO><b>Exit subroutine</b></color>;\n");
        }

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
