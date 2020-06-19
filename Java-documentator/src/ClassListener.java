import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClassListener extends Java8ParserBaseListener {

    private StringBuilder toFile = new StringBuilder();
    private boolean isPublicClass = false;
    private ClassVisitor visitor = new ClassVisitor();

    private static void write(String data,String fileName) {
        try {
            Files.write(Paths.get("Documentation/"+fileName+".puml"), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void enterCompilationUnit(Java8Parser.CompilationUnitContext ctx) {
        //program starts, add startuml to begin with uml file
        toFile.append("@startuml\n");
    }

    @Override
    public void enterNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {

        System.out.println("Clase detectada");
        StringBuilder modifiers = new StringBuilder();

        for(Java8Parser.ClassModifierContext mofifierctx: ctx.classModifier())
        {
            String modifier = mofifierctx.getText();
            //if modifier is public means this class is the main class, so its not included
            if(modifier.equals("public"))
                isPublicClass = true;
            if(modifier=="abstract")
                modifiers.append(modifier+" ");
        }
        if(!isPublicClass)
        {
            //if it has non public classes, then it has uml diagram
            toFile.append(modifiers.toString());
            toFile.append("class "+ctx.Identifier().getText());
            toFile.append("{\n");
            for(Java8Parser.ClassBodyDeclarationContext cbdctx:ctx.classBody().classBodyDeclaration())
                toFile.append(visitor.visitClassBodyDeclaration(cbdctx));
            toFile.append("}\n");
        }
        isPublicClass=false;
    }




    @Override
    public void exitNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
        isPublicClass = false;
    }

    @Override
    public void exitCompilationUnit(Java8Parser.CompilationUnitContext ctx) {
        toFile.append("@enduml\n");
        write(toFile.toString(),"Class_diagram");
    }
}