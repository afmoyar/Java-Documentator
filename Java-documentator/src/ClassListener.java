import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ClassListener extends Java8ParserBaseListener {

    private StringBuilder toFile = new StringBuilder();
    private boolean isPublicClass = false;
    private boolean isInsideConstructor = false;
    private boolean isAsignmentInsideConstructor = false;
    private String currentClass;
    private ClassVisitor visitor;
    private HashMap<String, ArrayList<HashMap<String, String>>> relations = new HashMap<String, ArrayList<HashMap<String, String>>>();
    private HashMap<String,ArrayList<String>> constructorAsigments = new HashMap<>();


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
    public static String getClassFromBrackets(String relation)
    {
        if(relation.contains("<")&&relation.contains(">"))
        {
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
                return "-up-|>";
            case "implements":
                return ".up.|>";
            case "asociation":
                return "-up->";
            case "innerClass":
                return "+--";
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

        //System.out.println("Clase detectada");
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
            if(ctx.superinterfaces()!=null)
            {
                for(Java8Parser.InterfaceTypeContext itctx: ctx.superinterfaces().interfaceTypeList().interfaceType())
                {
                    this.relations.get(className).add(new HashMap<String,String>());
                    int lastRelationIndex =  this.relations.get(className).size()-1;
                    relations.get(className).get(lastRelationIndex).put(itctx.classType().Identifier().getText(),"implements");
                }
            }

        }
        isPublicClass=false;
    }




    @Override
    public void exitNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
        isPublicClass = false;
    }

    @Override
    public void enterNormalInterfaceDeclaration(Java8Parser.NormalInterfaceDeclarationContext ctx) {
        //System.out.println("Interface detectada");
        StringBuilder modifiers = new StringBuilder();

        for(Java8Parser.InterfaceModifierContext mofifierctx: ctx.interfaceModifier())
        {
            String modifier = mofifierctx.getText();
            //if modifier is public means this class is the main class, so its not included
            if(modifier.equals("public"))
                isPublicClass = true;
            if(modifier.equals("abstract"))
                modifiers.append(modifier+" ");
        }
        String interfaceName = ctx.Identifier().getText();
        relations.put(interfaceName, new ArrayList<HashMap<String,String>>());
        visitor = new ClassVisitor(interfaceName, relations);
        toFile.append(modifiers.toString());
        toFile.append("Interface "+interfaceName);
        toFile.append("{\n");
        for(Java8Parser.InterfaceMemberDeclarationContext imdcctx:ctx.interfaceBody().interfaceMemberDeclaration())
            toFile.append(visitor.visitInterfaceMemberDeclaration(imdcctx));
        toFile.append("}\n");

        //checks if class has father
        if(ctx.extendsInterfaces()!=null)
        {
            //HashMap<String, ArrayList<HashMap<String, String>>>
            for(Java8Parser.InterfaceTypeContext itctx: ctx.extendsInterfaces().interfaceTypeList().interfaceType())
            {
                this.relations.get(interfaceName).add(new HashMap<String,String>());
                int lastRelationIndex =  this.relations.get(interfaceName).size()-1;
                relations.get(interfaceName).get(lastRelationIndex).put(itctx.classType().Identifier().getText(),"implements");
            }

        }
    }

    @Override
    public void enterConstructorDeclaration(Java8Parser.ConstructorDeclarationContext ctx) {
        //System.out.println("Entra a constructor");
        currentClass = ctx.constructorDeclarator().simpleTypeName().Identifier().getText();
        isInsideConstructor = true;
    }

    @Override
    public void enterAssignment(Java8Parser.AssignmentContext ctx) {
        if(isInsideConstructor)
        {
            //System.out.println("constructor class is "+ currentClass);
            isAsignmentInsideConstructor = true;
        }

    }

    @Override
    public void enterClassInstanceCreationExpression_lfno_primary(Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx) {
        if(isAsignmentInsideConstructor)
        {
            //System.out.println("entra a new ...");
            String identifier = ctx.Identifier(0).getText();
            if(!constructorAsigments.containsKey(currentClass))
            {
                constructorAsigments.put(currentClass,new ArrayList<String>());
            }
            constructorAsigments.get(currentClass).add(identifier);
            //System.out.println(identifier);
        }
    }

    @Override
    public void exitConstructorDeclaration(Java8Parser.ConstructorDeclarationContext ctx) {
        isInsideConstructor = false;
        isAsignmentInsideConstructor = false;
        //System.out.println("sale de constructor");
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
                    //System.out.println("relation: "+relation+", inside class: "+getClassFromBrackets(relation));
                    String symbol = getRelationSymbol(classRelations.get(relation));
                    String classInsideBrackets =getClassFromBrackets(relation);
                    //System.out.println("relation: "+relation);
                    //System.out.println("keySet: "+classess);
                    if(classess.contains(relation)||classess.contains(classInsideBrackets))
                    {
                        //relation betwen two classes found. Means that class relation is an atribute of classKey
                        String relatedClass;
                        if(classess.contains(classInsideBrackets))
                            relatedClass = classInsideBrackets;//related class is something like List<....>
                        else
                            relatedClass = relation;


                        System.out.println(classKey+" "+classRelations.get(relation)+" "+relation);
                        if(classRelations.get(relation).equals("unknown")&&
                                constructorAsigments.containsKey(classKey)&&
                                constructorAsigments.get(classKey).contains(relatedClass)) {
                            //composition detected, there is an object of relatedclass created inside constructor
                            //of classkey
                            toFile.append(classKey+" "+getRelationSymbol("composition")+" "+relatedClass+"\n");
                        }else if(!classRelations.get(relation).equals("unknown"))
                            //agragation detected, objetc of relatedClass is passed as argument of classKey constructor
                            toFile.append(classKey+" "+symbol+" "+relatedClass+"\n");
                        else{
                            //Nor agregation nor composition, but there is a relation, must be asosiation
                            toFile.append(classKey+" "+getRelationSymbol("asociation")+" "+relatedClass+"\n");
                        }

                    }
                }
            }
        }
        toFile.append("@enduml\n");
        write(toFile.toString(),"Class_diagram");
    }
}
