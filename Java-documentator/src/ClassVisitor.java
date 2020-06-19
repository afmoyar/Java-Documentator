import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class ClassVisitor extends Java8ParserBaseVisitor<String> {
    private String className;
    private HashMap<String, ArrayList<HashMap<String, String>>> relations;
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

    @Override
    public String visitClassBodyDeclaration(Java8Parser.ClassBodyDeclarationContext ctx) {
        StringBuilder classBuilder = new StringBuilder();
        if(ctx.constructorDeclaration()!= null &&
                ctx.constructorDeclaration().constructorDeclarator().formalParameterList()!=null)
        {
            //class has constructor with parameters
            this.visitFormalParameterList(ctx.constructorDeclaration().constructorDeclarator().formalParameterList());
        }
        if(ctx.classMemberDeclaration()!=null)
        {
            if(ctx.classMemberDeclaration().fieldDeclaration()!=null)
            {
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
            this.relations.get(this.className).add(new HashMap<String,String>());
            int lastRelationIndex =  this.relations.get(this.className).size()-1;
            this.relations.get(this.className).get(lastRelationIndex).put(type,"relation unknown");
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
        ArrayList<String> paramaterList = new ArrayList<String>();
        if(ctx.receiverParameter()!=null)
        {
            paramaterList.add(ctx.receiverParameter().unannType().getText());

        }else if(ctx.formalParameters()!=null)
        {

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
        System.out.println(paramaterList);
        return null;
    }
}
