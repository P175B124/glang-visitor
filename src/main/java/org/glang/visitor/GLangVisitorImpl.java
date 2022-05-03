package org.glang.visitor;

import org.antlr.v4.runtime.tree.RuleNode;

import java.util.*;

//TODO - ValueType enum
//TODO class Value (wraps value+type), return type accepts Value
//TODO VOID return type
//TODO Function class, with validation and invoke methods
//TODO small visitor classes
public class GLangVisitorImpl extends GLangBaseVisitor<Object> {

    private final StringBuilder SYSTEM_OUT = new StringBuilder();

    private final Stack<GLangScope> scopeStack = new Stack<>();

    private GLangScope currentScope = new GLangScope();

    private final Map<String, GLangParser.FunctionDeclarationContext> functions = new HashMap<>();

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
            return visit(ctx.block(0));
        } else {
            return visit(ctx.block(1));
        }
    }

    @Override
    public Object visitBlock(GLangParser.BlockContext ctx) {
        scopeStack.push(currentScope);
        currentScope = new GLangScope(currentScope);
        Object value = super.visitBlock(ctx);
        currentScope = scopeStack.pop();
        return value;
    }

    @Override
    public Object visitParenthesesExpression(GLangParser.ParenthesesExpressionContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Object visitReturnStatement(GLangParser.ReturnStatementContext ctx) {
        if (ctx.expression() == null) {
            return new ReturnValue(null);
        } else {
            return new ReturnValue(this.visit(ctx.expression()));
        }
    }

    @Override
    protected boolean shouldVisitNextChild(RuleNode node, Object currentResult) {
        return !(currentResult instanceof ReturnValue);
    }

    @Override
    public Object visitFunctionDeclaration(GLangParser.FunctionDeclarationContext ctx) {
        String functionName = ctx.IDENTIFIER().getText();

        //TODO create Function class that has constructor(FunctionDeclarationContext), invoke method
        //TODO validate if does not exist
        //TODO probably something else
        this.functions.put(functionName, ctx);
        return null;
    }

    @Override
    public Object visitFunctionCall(GLangParser.FunctionCallContext ctx) {

        String functionName = ctx.IDENTIFIER().getText();
        //TODO validate if exists
        GLangParser.FunctionDeclarationContext function = this.functions.get(functionName);

        //TODO validate args count

        List<Object> arguments = new ArrayList<>();
        if (ctx.expressionList() != null) {
            for (var expr : ctx.expressionList().expression()) {
                arguments.add(this.visit(expr));
            }
        }

        //TODO validate args types

        GLangScope functionScope = new GLangScope();

        if (function.paramList() != null) {
            for (int i = 0; i < function.paramList().IDENTIFIER().size(); i++) {
                String paramName = function.paramList().IDENTIFIER(i).getText();
                functionScope.declareVariable(paramName, arguments.get(i));
            }
        }

        scopeStack.push(currentScope);
        currentScope = functionScope;
        ReturnValue value = (ReturnValue) this.visitFunctionBody(function.functionBody());
        currentScope = scopeStack.pop();

        return value.getValue();
    }

    @Override
    public Object visitFunctionBody(GLangParser.FunctionBodyContext ctx) {
        Object value = super.visitFunctionBody(ctx);
        if (value instanceof ReturnValue) {
            return value;
        }
        return new ReturnValue(null);
    }
}
