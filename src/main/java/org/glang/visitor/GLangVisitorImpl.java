package org.glang.visitor;

import java.util.HashMap;
import java.util.Map;

public class GLangVisitorImpl extends GLangBaseVisitor<Object> {

    private final Map<String, Object> symbols = new HashMap<>();

    @Override
    public Object visitProgram(GLangParser.ProgramContext ctx) {
        return super.visitProgram(ctx).toString();
    }

    @Override
    public Object visitPrintFunctionCall(GLangParser.PrintFunctionCallContext ctx) {
        String text = visit(ctx.expression()).toString();
        System.out.println(text);
        return text + "\n";
    }

    @Override
    public Object visitConstantExpression(GLangParser.ConstantExpressionContext ctx) {
        return visit(ctx.constant());
    }

    @Override
    public Object visitConstant(GLangParser.ConstantContext ctx) {
        if (ctx.INTEGER() != null) {
            return Integer.parseInt(ctx.INTEGER().getText());
        }
        //TODO implement other types
        return null;
    }

    @Override
    public Object visitAssignment(GLangParser.AssignmentContext ctx) {
        String varName = ctx.IDENTIFIER().getText();
        Object value = visit(ctx.expression());
        this.symbols.put(varName, value);
        return null;
    }

    @Override
    public Object visitIdentifierExpression(GLangParser.IdentifierExpressionContext ctx) {
        //TODO validate (maybe not defined)
        return this.symbols.get(ctx.IDENTIFIER().getText());
    }

    @Override
    protected Object defaultResult() {
        return new StringBuilder();
    }

    @Override
    protected Object aggregateResult(Object aggregate, Object nextResult) {
        if (nextResult != null) {
            ((StringBuilder) aggregate).append(nextResult);
        }
        return aggregate;
    }
}
