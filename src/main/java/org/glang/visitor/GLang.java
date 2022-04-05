package org.glang.visitor;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class GLang {
    public static void main(String[] args) {
        try {
            execute(CharStreams.fromFileName(args[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object execute(CharStream stream) {
        GLangLexer lexer = new GLangLexer(stream);
        GLangParser parser = new GLangParser(new CommonTokenStream(lexer));
        parser.setBuildParseTree(true);
        ParseTree tree = parser.program();

        GLangVisitorImpl visitor = new GLangVisitorImpl();
        return visitor.visit(tree);
    }
}
