import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ClassListener extends Java8ParserBaseListener {

    private StringBuilder toFile = new StringBuilder();
    private boolean isPublicClass = false;
    private ClassVisitor visitor;
    private HashMap<String, ArrayList<HashMap<String, String>>> relations = new HashMap<String, ArrayList<HashMap<String, String>>>();

    private static void write(String data,String fileName) {
        try {
            Files.write(Paths.get("Documentation/"+fileName+".puml"), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getClassFromBrackets(String relation)
    {
        if(relation.contains("<")&&relation.contains(">"))
        {
            System.out.println("<>");
            try {
                return relation.substring(relation.indexOf('<')+1,relation.indexOf('>'));
            }catch (Exception e)
            {
                System.out.println(e);
                return "";
            }

        }
        if(relation.contains("[")&&relation.contains("]"))
        {
            try {
                return relation.substring(0,relation.indexOf('[')-1);
            }catch (Exception e)
            {
                return "";
            }

        }
        return "";
    }

    public static String getRelationSymbol(String relation)
    {
        switch (relation)
        {
            case "agregation":
                return "o--";
            case "composition":
                return "*--";
            case "Inheritance":
                return "<|--";
        }
        return "";
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
            if(modifier.equals("abstract"))
                modifiers.append(modifier+" ");
        }
        if(!isPublicClass)
        {
            //if it has non public classes, then it has uml diagram
            String className = ctx.Identifier().getText();
            relations.put(className, new ArrayList<HashMap<String,String>>());
            visitor = new ClassVisitor(className,relations);
            toFile.append(modifiers.toString());
            toFile.append("class "+className);
            toFile.append("{\n");
            for(Java8Parser.ClassBodyDeclarationContext cbdctx:ctx.classBody().classBodyDeclaration())
                toFile.append(visitor.visitClassBodyDeclaration(cbdctx));
            toFile.append("}\n");
            relations = visitor.getRelations();
            //checks if class has father
            if(ctx.superclass()!=null)
            {
                //HashMap<String, ArrayList<HashMap<String, String>>>
                this.relations.get(className).add(new HashMap<String,String>());
                int lastRelationIndex =  this.relations.get(className).size()-1;
                relations.get(className).get(lastRelationIndex).put(ctx.superclass().classType().Identifier().getText(),"Inheritance");
            }

        }
        isPublicClass=false;
    }




    @Override
    public void exitNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
        isPublicClass = false;
    }

    @Override
    public void exitCompilationUnit(Java8Parser.CompilationUnitContext ctx) {
        HashSet<String> classess = new HashSet<String>(relations.keySet());
        for(String classKey:relations.keySet())
        {
            //System.out.println(relations.get(classKey));
            for(HashMap<String,String> classRelations:relations.get(classKey))
            {
                //System.out.println(classRelations);
                for(String relation: classRelations.keySet())
                {
                    System.out.println("relation: "+relation+", inside class: "+getClassFromBrackets(relation));
                    String symbol="";
                    String classInsideBrackets =getClassFromBrackets(relation);
                    System.out.println("relation: "+relation);
                    System.out.println("keySet: "+classess);
                    if(classess.contains(relation))
                    {
                        System.out.println("clas esta");

                        System.out.println(classRelations.get(relation));
                        symbol = getRelationSymbol(classRelations.get(relation));
                        System.out.println(classKey+" "+classRelations.get(relation)+" "+relation);
                        if(symbol.equals("<|--"))
                        {
                            toFile.append(relation+" "+symbol+" "+classKey+"\n");
                        }
                        else {
                            toFile.append(classKey+" "+symbol+" "+relation+"\n");
                        }

                    }else if(classess.contains(classInsideBrackets))
                    {
                        System.out.println("inside esta");
                        System.out.println(classRelations);
                        System.out.println(classRelations.get(classInsideBrackets));
                        symbol = getRelationSymbol(classRelations.get(relation));
                        System.out.println(classKey+" "+classRelations.get(classInsideBrackets)+" "+classInsideBrackets);
                        toFile.append(classKey+" "+symbol+" "+classInsideBrackets+"\n");
                    }
                }
            }
        }
        toFile.append("@enduml\n");
        write(toFile.toString(),"Class_diagram");
    }
}
