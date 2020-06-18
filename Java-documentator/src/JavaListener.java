import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JavaListener extends Java8ParserBaseListener {

    private StringBuilder toFile = new StringBuilder();

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
        boolean isPublicClass = false;
        for(Java8Parser.ClassModifierContext mofifierctx: ctx.classModifier())
        {
            String modifier = mofifierctx.getText();
            //if modifier is public means this class is the main class, so its not included
            if(modifier=="public")
                isPublicClass = true;
            if(modifier=="abstract")
                modifiers.append(modifier+" ");
        }
        if(!isPublicClass)
        {
            toFile.append(modifiers.toString());
            toFile.append("class "+ctx.Identifier().getText());
            toFile.append("{}\n");
        }
    }

    @Override
    public void exitCompilationUnit(Java8Parser.CompilationUnitContext ctx) {
        toFile.append("@enduml\n");
        write(toFile.toString(),"Class_diagram");
    }
}
