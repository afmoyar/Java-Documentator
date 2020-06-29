# Java-Documentator


Final project for the Programming languages class at Universidad Nacional de Colombia.

This project generates documentation from Java source code. Source code to analyze should be inside only one file.
####Documentation supported:
- Information about class attributes
- Information about every method
- Class diagram (the outer public class is not included)
- Flow diagram for every method
### Set up:

- install plantuml plugin for intelij https://plugins.jetbrains.com/plugin/7017-plantuml-integration.
- go to https://bobswift.atlassian.net/wiki/spaces/GVIZ/pages/20971549/How+to+install+Graphviz+software.
to install graphviz and follow tutorial to add new system variable.
- go to https://plantuml.com/es/download to download PlantUML.jar. Then add this file as a dependency of IntelliJ IDEA.
- Configure IntelliJ (ctrl + alt + shift + s) like this:
    - src and gen directories must be sources.
    - Add antlr and plantuml jar files as dependencies.
### Use instructions

There are three ways of using this translator:
- **Execute with program arguments:** Configure as program argument the file path of the code you want to translate.
For example Input/program.txt
- **Run code on the console:** Run the program without arguments, a set of instructions will appear. Press 0
- **Put name of file on the console:** Run the program without arguments, a set of instructions will appear. Press 1

Once the analysis is complete, open in your browser index.html, wich is located in the Documentation folder.

if you want to check or modify the code that generates the uml graphs, check the puml_code int the Documentation folder.

###Contact:
- Michael Estiven Guerrero
- Andrés Felipe Moya
- Esteban Jaramillo Sarmiento