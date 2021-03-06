import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class ClassVisitor extends Java8ParserBaseVisitor<String> {
    private String className;
    private HashMap<String, ArrayList<HashMap<String, String>>> relations;
    private ArrayList<String> paramaterList = new ArrayList<String>();
    public ClassVisitor(String className, HashMap<String, ArrayList<HashMap<String, String>>> relations)
    {
        this.className = className;
        this.relations = relations;
    }

    public HashMap<String, ArrayList<HashMap<String, String>>> getRelations()
    {
        return this.relations;
    }

    public String getModifier(String modifier)
    {
        String modifierSymbol="";
        switch (modifier)
        {
            case "public":
                modifierSymbol = "+";
                break;
            case "private":
                modifierSymbol = "-";
                break;
            case "protected":
                modifierSymbol = "#";
                break;
            case "static":
                modifierSymbol = "{static}";
                break;
            default:
                modifierSymbol = "";
        }
        return modifierSymbol;
    }

    public boolean checkType(String type)
    {
        return (
                !type.equals("String")&&!type.equals("String[]")&& !type.contains("<String>") &&
                !type.equals("Integer")&&!type.equals("Integer[]")&&!type.contains("<String>") &&
                !type.equals("Double")&&!type.equals("Double[]")&&!type.contains("<Double>") &&
                !type.equals("Boolean")&&!type.equals("Boolean[]")&&!type.contains("<Boolean>") &&
                !type.equals("Long")&&!type.equals("Long[]")&&!type.contains("<Long>")
        );

    }

    @Override
    public String visitClassBodyDeclaration(Java8Parser.ClassBodyDeclarationContext ctx) {
        StringBuilder classBuilder = new StringBuilder();
        if(ctx.constructorDeclaration()!= null &&
                ctx.constructorDeclaration().constructorDeclarator().formalParameterList()!=null)
        {
            //class has constructor with parameters
            //System.out.println("cheking constructor parameters");
            this.visitFormalParameterList(ctx.constructorDeclaration().constructorDeclarator().formalParameterList());
        }
        if(ctx.classMemberDeclaration()!=null)
        {
            if(ctx.classMemberDeclaration().fieldDeclaration()!=null)
            {
                //System.out.println("getting variables");
                //get variables
                String variables = this.visitFieldDeclaration(ctx.classMemberDeclaration().fieldDeclaration());
                classBuilder.append(variables);
            }
            if(ctx.classMemberDeclaration().methodDeclaration()!=null)
            {
                //get methods
                String method = this.visitMethodDeclaration(ctx.classMemberDeclaration().methodDeclaration());
                classBuilder.append(method);
            }
            if(ctx.classMemberDeclaration().classDeclaration()!=null)
            {
                //there is an inner class
                visitClassDeclaration(ctx.classMemberDeclaration().classDeclaration());
            }
            return classBuilder.toString();
        }

        return  "";
    }

    @Override
    public String visitFieldDeclaration(Java8Parser.FieldDeclarationContext ctx) {

        String modifier = "";
        String modifierSymbol ="";
        StringBuilder toFile = new StringBuilder();
        for(Java8Parser.FieldModifierContext modifierctx: ctx.fieldModifier())
        {
            modifier = modifierctx.getText();
            modifierSymbol += getModifier(modifier);

        }
        String type = ctx.unannType().getText();
        if(ctx.unannType().unannReferenceType()!=null)
        {
            //type is a class, this can mean that there is some relation between classes
            if(checkType(type))
            {
                this.relations.get(this.className).add(new HashMap<String,String>());
                int lastRelationIndex =  this.relations.get(this.className).size()-1;
                this.relations.get(this.className).get(lastRelationIndex).put(type,"unknown");
                //System.out.println("class name: "+this.className+" unknown "+type);
            }

            //System.out.println(this.relations);
        }
        String[] variableNames = this.visitVariableDeclaratorList(ctx.variableDeclaratorList()).split(",");
        for(String variable:variableNames)
        {
            toFile.append("\t"+modifierSymbol+variable+": "+type+"\n");
        }
        return toFile.toString();
    }

    @Override
    public String visitVariableDeclaratorList(Java8Parser.VariableDeclaratorListContext ctx) {
        StringBuilder variables = new StringBuilder();
        for(Java8Parser.VariableDeclaratorContext vdctx: ctx.variableDeclarator())
        {
            variables.append(vdctx.variableDeclaratorId().Identifier().getText()+",");
        }
        return variables.toString();

    }

    @Override
    public String visitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
        String modifier = "";
        String modifierSymbol ="";
        StringBuilder toFile = new StringBuilder();
        for(Java8Parser.MethodModifierContext modifierctx: ctx.methodModifier())
        {
            modifier = modifierctx.getText();
            modifierSymbol += getModifier(modifier);
        }


        String method_name = ctx.methodHeader().methodDeclarator().Identifier().getText();
        return "\t"+modifierSymbol+method_name+"()"+"\n";
    }

    @Override
    public String visitFormalParameterList(Java8Parser.FormalParameterListContext ctx) {

        if(ctx.receiverParameter()!=null)
        {
            paramaterList.add(ctx.receiverParameter().unannType().getText());

        }else if(ctx.formalParameters()!=null)
        {
            //|	formalParameters ',' lastFormalParameter
            visitFormalParameters(ctx.formalParameters());
            if(ctx.lastFormalParameter().unannType()!=null)
            {
                paramaterList.add(ctx.lastFormalParameter().unannType().getText());
            }
            else
            {
                paramaterList.add(ctx.lastFormalParameter().formalParameter().unannType().getText());
            }

        }else {
            //lastFormalParameter
            if(ctx.lastFormalParameter().unannType()!=null)
            {
                paramaterList.add(ctx.lastFormalParameter().unannType().getText());
            }
            else
            {
                paramaterList.add(ctx.lastFormalParameter().formalParameter().unannType().getText());
            }
        }
        //System.out.println(paramaterList);
        //System.out.println(relations);
        ArrayList<HashMap<String ,String>> currentRelations = relations.get(this.className);
        //System.out.println(currentRelations);
        for(HashMap<String,String> map: currentRelations)
        {
            for(String paramater: paramaterList)
            {
                if(map.containsKey(paramater))
                {
                    map.put(paramater,"agregation");
                }
            }
        }
        //System.out.println(relations);
        return null;
    }

    @Override
    public String visitFormalParameters(Java8Parser.FormalParametersContext ctx) {
        for(Java8Parser.FormalParameterContext fpctx: ctx.formalParameter())
        {
            paramaterList.add(fpctx.unannType().getText());
        }
        if(ctx.receiverParameter()!=null)
        {
            paramaterList.add(ctx.receiverParameter().unannType().getText());
        }
        return null;
    }

    @Override
    public String visitClassDeclaration(Java8Parser.ClassDeclarationContext ctx) {
        if(ctx.normalClassDeclaration()!=null)
        {
            String innerClass = ctx.normalClassDeclaration().Identifier().getText();
            //System.out.println(this.className +" has an inner class named: "+innerClass);
            HashMap<String, String> map = new HashMap<>();
            map.put(innerClass,"innerClass");
            this.relations.get(this.className).add(map);

        }

        return null;
    }

    @Override
    public String visitInterfaceMemberDeclaration(Java8Parser.InterfaceMemberDeclarationContext ctx) {
        StringBuilder classBuilder = new StringBuilder();

        if(ctx.constantDeclaration()!=null)
        {
            //System.out.println("getting variables");
            //get variables
            String variables = this.visitConstantDeclaration(ctx.constantDeclaration());
            classBuilder.append(variables);
        }
        if(ctx.interfaceMethodDeclaration()!=null)
        {
            //get methods
            String method = this.visitInterfaceMethodDeclaration(ctx.interfaceMethodDeclaration());
            classBuilder.append(method);
        }
        return classBuilder.toString();
    }

    @Override
    public String visitConstantDeclaration(Java8Parser.ConstantDeclarationContext ctx) {
        String modifier = "";
        String modifierSymbol ="";
        StringBuilder toFile = new StringBuilder();
        for(Java8Parser.ConstantModifierContext modifierctx: ctx.constantModifier())
        {
            modifier = modifierctx.getText();
            modifierSymbol += getModifier(modifier);

        }
        String type = ctx.unannType().getText();
        if(ctx.unannType().unannReferenceType()!=null)
        {
            //type is a class, this can mean that there is some relation between classes
            if(checkType(type))
            {
                this.relations.get(this.className).add(new HashMap<String,String>());
                int lastRelationIndex =  this.relations.get(this.className).size()-1;
                this.relations.get(this.className).get(lastRelationIndex).put(type,"asociation");
            }

            //System.out.println(this.relations);
        }
        String[] variableNames = this.visitVariableDeclaratorList(ctx.variableDeclaratorList()).split(",");
        for(String variable:variableNames)
        {
            toFile.append("\t"+modifierSymbol+variable+": "+type+"\n");
        }
        return toFile.toString();
    }

    @Override
    public String visitInterfaceMethodDeclaration(Java8Parser.InterfaceMethodDeclarationContext ctx) {
        String modifier = "";
        String modifierSymbol ="";
        StringBuilder toFile = new StringBuilder();
        for(Java8Parser.InterfaceMethodModifierContext modifierctx: ctx.interfaceMethodModifier())
        {
            modifier = modifierctx.getText();
            modifierSymbol += getModifier(modifier);
        }


        String method_name = ctx.methodHeader().methodDeclarator().Identifier().getText();
        return "\t"+modifierSymbol+method_name+"()"+"\n";

    }
}
