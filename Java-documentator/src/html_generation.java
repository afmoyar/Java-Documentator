import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class html_generation extends Java8ParserBaseListener{
    private StringBuilder struct = new StringBuilder(); //general html structure
    private StringBuilder sections_menu = new StringBuilder(); //left menu
    private StringBuilder sections = new StringBuilder(); //actual section content

    //Helpers
    private boolean isPublicClass = false;
    private String glob_classname = "";
    private ClassVisitor visitor;
    private HashMap<String, ArrayList<HashMap<String, String>>> relations = new HashMap<String, ArrayList<HashMap<String, String>>>();

    private List<String> attributes = new ArrayList<String>();
    private List<String> methods = new ArrayList<String>();

    private boolean selected_first_on_menu = false;

    @Override
    public void enterCompilationUnit(Java8Parser.CompilationUnitContext ctx) {
        //initial html structure
        struct.append("<!doctype html>\n" +
                "<html>\n" +
                "  <head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"description\" content=\"\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "    <title>Documentation</title>\n" +
                "    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">\n" +
                "    <link href=\"https://fonts.googleapis.com/css?family=Nunito+Sans:300,400,600,700,800,900\" rel=\"stylesheet\">\n" +
                "    <link rel=\"stylesheet\" href=\"style/style.css\">\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <div class=\"doc__bg\"></div>\n" +
                "    <nav class=\"header\">\n" +
                "      <h1 class=\"logo\">Generated<span class=\"logo__thin\"> Documentation</span></h1>\n" +
                "      <ul class=\"menu\">\n" +
                "        <div class=\"menu__item toggle\"><span></span></div>\n" +
                "        <li class=\"menu__item\"><a href=\"\" class=\"link link--dark\"><i class=\"fa fa-github\"></i> Github</a></li>\n" +
                "        <li class=\"menu__item\"><a href=\"index.html\" class=\"link link--dark\"><i class=\"fa fa-home\"></i> Home</a></li>\n" +
                "      </ul>\n" +
                "    </nav>\n" +
                "    <div class=\"wrapper\">\n" +
                "   <aside class=\"doc__nav\">\n" +
                "        <ul>");
    }

    @Override
    public void enterNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {

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
            //start section
            String className = ctx.Identifier().getText();
            glob_classname = className;
            relations.put(className, new ArrayList<HashMap<String,String>>());
            visitor = new ClassVisitor(className,relations);
            //sections_menu.append(modifiers.toString());
            if(selected_first_on_menu){
                sections_menu.append("<li class=\"js-btn\">"+className.replace("+","")+"</li>");
            }
            else{
                sections_menu.append("<li class=\"js-btn selected\">"+className.replace("+","")+"</li>");
                selected_first_on_menu = true;
            }

            sections_menu.append("\n");

            sections.append(
                    "<section class=\"js-section\">\n" +
                    "<h3 class=\"section__title\"> " + className+"</h3>" +
                    //"<p>Constructor</p>" +
                    "<table id=\""+"attributes_"+ className+"\">"+
                    "<tr>\n" +
                    "<th>Attribute</th>\n" +
                    "<th>Type</th>\n" +
                    "<th>Access</th>\n" +
                    "</tr>"
                    );
            for(Java8Parser.ClassBodyDeclarationContext class_body_ctx:ctx.classBody().classBodyDeclaration()){
                try {
                    //System.out.println("!!!! "+visitor.visitClassBodyDeclaration(class_body_ctx));
                    /*
                    sections.append("<tr>\n" +
                            "<td>encryption</td>\n" +
                            "<td>" + visitor.visitClassBodyDeclaration(class_body_ctx) + "</td>\n" +
                            "<td>false</td>\n" +
                            "</tr>"
                    );*/
                    String tmp = visitor.visitClassBodyDeclaration(class_body_ctx);

                    //check if its method or attribute
                    if(tmp.length() != 0) {
                        tmp = tmp.trim(); //Remove whitespaces
                        /*
                        System.out.println("--------- ");
                        System.out.println(tmp);
                        System.out.println("at -1: "+tmp.charAt(tmp.length() - 1));
                        System.out.println("at -2: "+tmp.charAt(tmp.length() - 2));
                        System.out.println("--------- ");*/
                        if (tmp.charAt(tmp.length() - 1) == ')' &&
                                tmp.charAt(tmp.length() - 2) == '(') {
                            methods.add(tmp);
                        } else {
                            attributes.add(tmp);
                        }
                    }
                }catch(Exception e){
                    System.out.println(e);
                }
            }

            for(String attr:attributes){
                String type = "";
                String attribute_name = "";
                String modifier = "";
                Character leading = attr.charAt(0);
                if(leading == '+'){
                    modifier = "public";
                }
                if(leading == '-'){
                    modifier = "private";
                }
                if(leading == '#'){
                    modifier = "protected";
                }

                String[] parts = attr.split(":");

                if(modifier != "") {
                    sections.append("<tr>\n" +
                            "<td>" + parts[0].substring(1, parts[0].length()).replace("{static}","") +
                            "</td>\n" +
                            "<td>" + parts[1] + "</td>\n" +
                            "<td>" + modifier + "</td>\n" +
                            "</tr>"
                    );
                }

            }

            //close attributes table
            sections.append("</table>\n" );

            //initialize methods table
            sections.append(
                            "<table id=\""+"methods_"+className+"\">"+
                            "<tr>\n" +
                            "<th>Method</th>\n" +
                            "<th>Access</th>\n" +
                            "</tr>"
                    );

            for(String method:methods){
                String modifier = "";
                Character leading = method.charAt(0);
                if(leading == '+'){
                    modifier = "public";
                }
                if(leading == '-'){
                    modifier = "private";
                }
                if(leading == '#'){
                    modifier = "protected";
                }

                if(modifier != "") {
                    sections.append("<tr>\n" +
                            "<td>" + method.substring(1, method.length()).replace("{static}","") +
                            "</td>\n" +
                            "<td>" + modifier + "</td>\n" +
                            "</tr>"
                    );
                }
            }

            sections.append("</table>\n" );
            //close the section
            sections.append(
                    "<p>End section</p>\n" +
                    "<hr />\n" +
                    "</section>");

        }
        isPublicClass = false;
        glob_classname = "";
    }


    @Override
    public void exitNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {

    }


    @Override
    public void exitCompilationUnit(Java8Parser.CompilationUnitContext ctx) {

        struct.append(sections_menu);
        struct.append("</ul>\n" +
                      "</aside>"+
                      "<article class=\"doc__content\">");

        sections.append("<img src=\"images/" + "Class_diagram" + ".svg\">"); //change to the classname
        struct.append(sections);
        String ending = "</article>\n" +
                        "</div>" +
                        "<script src=\"style/handle_select.js\"></script>\n" +
                        "</body>\n" +
                        "</html>";
        struct.append(ending);
        write(struct.toString(),"index.html");
    }

    private static void write(String data,String fileName) {
        try {
            Files.write(Paths.get("Documentation/"+fileName), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}