package org.glang.visitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class GLangVisitorImpl extends GLangBaseVisitor<Object> {

    private final Map<String, Object> globalSymbols = new HashMap<>();

    private final Stack<Map<String, Object>> blockSymbolStack = new Stack<>();

    private Map<String, Object> currentBlockSymbol = null;

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
        if (ctx.BOOLEAN() != null) {
            return Boolean.parseBoolean(ctx.BOOLEAN().getText());
        }
        //TODO implement other types
        return null;
    }

    @Override
    public Object visitAssignment(GLangParser.AssignmentContext ctx) {
        String varName = ctx.IDENTIFIER().getText();
        Object value = visit(ctx.expression());
        if (currentBlockSymbol == null) {
            this.globalSymbols.put(varName, value);
        } else {
            if (this.globalSymbols.containsKey(varName)) {
                this.globalSymbols.put(varName, value);
            } else {
                this.currentBlockSymbol.put(varName, value);
            }
        }
        return null;
    }

    @Override
    public Object visitIdentifierExpression(GLangParser.IdentifierExpressionContext ctx) {
        //TODO validate (maybe not defined)
        String varName = ctx.IDENTIFIER().getText();
        if (this.globalSymbols.containsKey(varName)) {
            return this.globalSymbols.get(varName);
        } else {
            return this.currentBlockSymbol.get(varName);
        }
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
        if (currentBlockSymbol != null) {
            blockSymbolStack.push(currentBlockSymbol);
        }
        currentBlockSymbol = new HashMap<>();
        super.visitBlock(ctx);
        if (blockSymbolStack.empty()) {
            currentBlockSymbol = null;
        } else {
            currentBlockSymbol = blockSymbolStack.pop();
        }
        return null;
    }

    @Override
    public Object visitParenthesesExpression(GLangParser.ParenthesesExpressionContext ctx) {
        return visit(ctx.expression());
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
