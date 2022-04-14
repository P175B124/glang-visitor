package org.glang.visitor;

import java.util.Stack;

public class GLangVisitorImpl extends GLangBaseVisitor<Object> {

    private final StringBuilder SYSTEM_OUT = new StringBuilder();

    private final Stack<GLangScope> scopeStack = new Stack<>();

    private GLangScope currentScope = new GLangScope();

    @Override
    public Object visitProgram(GLangParser.ProgramContext ctx) {
        super.visitProgram(ctx);
        return SYSTEM_OUT.toString();
    }

    @Override
    public Object visitPrintFunctionCall(GLangParser.PrintFunctionCallContext ctx) {
        String text = visit(ctx.expression()).toString();
        System.out.println(text);
        SYSTEM_OUT.append(text).append("\n");
        return null;
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
        if (ctx.BOOLEAN() != null) {
            return Boolean.parseBoolean(ctx.BOOLEAN().getText());
        }
        //TODO implement other types
        return null;
    }

    @Override
    public Object visitVariableDeclaration(GLangParser.VariableDeclarationContext ctx) {
        String varName = ctx.IDENTIFIER().getText();
        Object value = visit(ctx.expression());
        this.currentScope.declareVariable(varName, value);
        return null;
    }

    @Override
    public Object visitAssignment(GLangParser.AssignmentContext ctx) {
        String varName = ctx.IDENTIFIER().getText();
        Object value = visit(ctx.expression());
        this.currentScope.changeVariable(varName, value);
        return null;
    }

    @Override
    public Object visitIdentifierExpression(GLangParser.IdentifierExpressionContext ctx) {
        String varName = ctx.IDENTIFIER().getText();
        return this.currentScope.resolveVariable(varName);
    }

    @Override
    public Object visitNumericAddOpExpression(GLangParser.NumericAddOpExpressionContext ctx) {
        Object val1 = visit(ctx.expression(0));
        Object val2 = visit(ctx.expression(1));
        //TODO - validation etc
        return switch (ctx.numericAddOp().getText()) {
            case "+" -> (Integer) val1 + (Integer) val2;
            case "-" -> (Integer) val1 - (Integer) val2;
            default -> null;
        };
    }

    @Override
    public Object visitNumericMultiOpExpression(GLangParser.NumericMultiOpExpressionContext ctx) {
        Object val1 = visit(ctx.expression(0));
        Object val2 = visit(ctx.expression(1));
        //TODO - validation etc
        return switch (ctx.numericMultiOp().getText()) {
            case "*" -> (Integer) val1 * (Integer) val2;
            case "/" -> (Integer) val1 / (Integer) val2;
            case "%" -> (Integer) val1 % (Integer) val2;
            default -> null;
        };
    }

    @Override
    public Object visitIfElseStatement(GLangParser.IfElseStatementContext ctx) {
        boolean value = (Boolean) visit(ctx.expression());
        if (value) {
            visit(ctx.block(0));
        } else {
            visit(ctx.block(1));
        }
        return null;
    }

    @Override
    public Object visitBlock(GLangParser.BlockContext ctx) {
        scopeStack.push(currentScope);
        currentScope = new GLangScope(currentScope);
        super.visitBlock(ctx);
        currentScope = scopeStack.pop();
        return null;
    }

    @Override
    public Object visitParenthesesExpression(GLangParser.ParenthesesExpressionContext ctx) {
        return visit(ctx.expression());
    }
}
