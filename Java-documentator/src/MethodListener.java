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
    private String variableType = "variable";

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

    public String expressionTranslate(String expression)
    {
        expression = expression.replace("||"," or ").replace("&&"," and ")
                .replace("=="," equals ").replace(">"," > ")
                .replace(">="," >= ").replace("<="," <= ")
                .replace("<"," < ").replace("!="," not equals ");
        return expression;
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
    public void enterUnannType(Java8Parser.UnannTypeContext ctx) {
        String type = ctx.getText();
        if(type.contains("[]"))
        {
            variableType = "array";
            return;
        }
        if(ctx.unannReferenceType()!=null)
        {
            if(!type.equals("String") && !type.equals("Integer")
                    &&!type.equals("Double")&&!type.equals("Boolean")&& !type.equals("Long"))
            {
                if(type.contains("List<")){
                    variableType = "list";
                }else if(type.contains("Map<")) {
                    variableType = "map";
                }else if(type.contains("Set<")) {
                    variableType = "set";
                }else {
                    variableType = "object";
                }
            }
        }
    }

    @Override
    public void enterVariableDeclaratorList(Java8Parser.VariableDeclaratorListContext ctx) {
        ArrayList<String> variables = new ArrayList<>();
        String assignation = null;
        for(Java8Parser.VariableDeclaratorContext varctx: ctx.variableDeclarator())
        {
            System.out.println("Declaracion de variable "+varctx.getText());
            variables.add(varctx.variableDeclaratorId().Identifier().getText());
            if(varctx.variableDeclaratorId().dims()!=null)
            {
                variableType = "array";
            }
            if(varctx.variableInitializer()!=null)
            {
                assignation = varctx.variableInitializer().getText();
            }
        }
        for (int i = 0; i <variables.size() ; i++) {
            toFile.append(":New "+variableType+": <b>"+variables.get(i)+"</b>;\n");
            if(assignation!=null)
            {
                toFile.append(":Assign <color:darkblue><i>"+assignation+"</i></color> to <b>"+variables.get(i)+"</b>;\n");
            }

        }
        variableType = "variable";
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
            //System.out.println("ctx.methodName()!=null");
           methodName = ctx.methodName().Identifier().getText();
        }else {
            //System.out.println("else");
            methodName = ctx.Identifier().getText();
        }
        if(methods.contains(methodName))
        {
            //calling a method created in the program
            if(ctx.typeName()!=null)
            {
                //method belongs to an objetct
                String objectName = ctx.typeName().Identifier().getText();
                toFile.append(":Enter <b>"+objectName+"'s</b> subroutine: <color:darkred><b>"+methodName+"</b></color>;\n");
                return;
            }else{
                toFile.append(":Enter subroutine: <color:darkred><b>"+methodName+"</b></color>;\n");
                return;
            }

        }else{
            toFile.append(":"+ctx.getText()+";\n");
        }
        /*
        if(ctx.typeName()!=null)
        {
            //method of some list or other data structure
            String method = ctx.Identifier().getText();
            String structure = ctx.typeName().Identifier().getText();
            switch (method)
            {
                case "add":


            }

        }*/

    }

    @Override
    public void exitMethodInvocation(Java8Parser.MethodInvocationContext ctx) {
        super.exitMethodInvocation(ctx);
    }

    @Override
    public void enterIfThenStatement(Java8Parser.IfThenStatementContext ctx) {
        toFile.append("if ("+expressionTranslate(ctx.expression().getText())+ " ?) then (yes)\n");

    }

    @Override
    public void exitIfThenStatement(Java8Parser.IfThenStatementContext ctx) {
        toFile.append("else (no)\n");
        toFile.append("endif\n");
    }

    @Override
    public void enterIfThenElseStatement(Java8Parser.IfThenElseStatementContext ctx) {
        toFile.append("if ("+expressionTranslate(ctx.expression().getText())+" ?) then (yes)\n");
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
        toFile.append("if ("+expressionTranslate(ctx.expression().getText())+" ?) then (yes)\n");
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
            toFile.append("while ("+expressionTranslate(ctx.expression().getText())+" ?) is (Yes)\n");
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
        toFile.append("while ("+expressionTranslate(ctx.expression().getText())+" ?) is (Yes)\n");
    }

    @Override
    public void exitWhileStatement(Java8Parser.WhileStatementContext ctx) {
        toFile.append("endwhile (No)\n");
    }

    @Override
    public void enterWhileStatementNoShortIf(Java8Parser.WhileStatementNoShortIfContext ctx) {
        toFile.append("while ("+expressionTranslate(ctx.expression().getText())+" ?) is (Yes)\n");
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
        toFile.append("repeat while ("+expressionTranslate(ctx.expression().getText())+" ?)\n");
    }

    @Override
    public void enterForEachSetUp(Java8Parser.ForEachSetUpContext ctx) {

        toFile.append("while(For each <b>"+ctx.variableDeclaratorId().Identifier()+"</b> of <b>"+ctx.expression().getText()+"</b>);\n");
    }

    @Override
    public void exitForEachSetUp(Java8Parser.ForEachSetUpContext ctx) {

    }

    @Override
    public void exitEnhancedForStatement(Java8Parser.EnhancedForStatementContext ctx) {
        toFile.append("endWhile (No more elements)\n");
    }

    @Override
    public void exitEnhancedForStatementNoShortIf(Java8Parser.EnhancedForStatementNoShortIfContext ctx) {
        toFile.append("endWhile\n");
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
