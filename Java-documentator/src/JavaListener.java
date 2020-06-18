public class JavaListener extends Java8ParserBaseListener {
    @Override
    public void enterNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
        System.out.println("Clase detectada");


    }
}
