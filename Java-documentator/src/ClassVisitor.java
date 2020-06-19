import java.util.StringTokenizer;

public class ClassVisitor extends Java8ParserBaseVisitor<String> {

    @Override
    public String visitClassBodyDeclaration(Java8Parser.ClassBodyDeclarationContext ctx) {

        if(ctx.classMemberDeclaration()!=null)
        {
            //get variables
            String variables = this.visitFieldDeclaration(ctx.classMemberDeclaration().fieldDeclaration());
            //System.out.println(variables);
            return variables;
        }
        return  "";
    }

    @Override
    public String visitFieldDeclaration(Java8Parser.FieldDeclarationContext ctx) {

        String modifier = "+";
        StringBuilder toFile = new StringBuilder();
        for(Java8Parser.FieldModifierContext modifierctx: ctx.fieldModifier())
        {
            modifier = modifierctx.getText();
            switch (modifier)
            {
                case "public":
                    modifier = "+";
                    break;
                case "private":
                    modifier = "-";
                    break;
                case "protected":
                    modifier = "#";
                    break;
                default:
                    modifier = "";
            }
        }
        String[] variableNames = this.visitVariableDeclaratorList(ctx.variableDeclaratorList()).split(",");
        for(String variable:variableNames)
        {
            toFile.append("\t"+modifier+variable+"\n");
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
}
