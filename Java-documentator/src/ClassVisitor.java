import java.util.StringTokenizer;

public class ClassVisitor extends Java8ParserBaseVisitor<String> {
    private String className;
    public ClassVisitor(String className)
    {
        this.className = className;
    }
    private String[] Atributes;

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
        //--------------------------------
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
}
