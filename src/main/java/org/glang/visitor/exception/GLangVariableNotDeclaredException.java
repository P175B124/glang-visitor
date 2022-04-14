package org.glang.visitor.exception;

public class GLangVariableNotDeclaredException extends GLangException {
    public GLangVariableNotDeclaredException(String variableNme) {
        super(String.format("Variable '%s' is not declared.", variableNme));
    }
}
