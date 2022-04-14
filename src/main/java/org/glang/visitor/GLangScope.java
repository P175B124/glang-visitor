package org.glang.visitor;

import org.glang.visitor.exception.GLangVariableAlreadyDeclaredException;
import org.glang.visitor.exception.GLangVariableNotDeclaredException;

import java.util.HashMap;
import java.util.Map;

public class GLangScope {

    private final GLangScope parent;
    private final Map<String, Object> symbols = new HashMap<>();

    public GLangScope() {
        this.parent = null;
    }

    public GLangScope(GLangScope parent) {
        this.parent = parent;
    }

    public void declareVariable(String variableName, Object value) {
        if (isDeclared(variableName)) {
            throw new GLangVariableAlreadyDeclaredException(variableName);
        }
        symbols.put(variableName, value);
    }

    private boolean isDeclared(String variableName) {
        if (symbols.containsKey(variableName)) {
            return true;
        }
        return parent != null && parent.isDeclared(variableName);
    }

    public void changeVariable(String variableName, Object value) {
        if (!isDeclared(variableName)) {
            throw new GLangVariableNotDeclaredException(variableName);
        }
        if (symbols.containsKey(variableName)) {
            symbols.put(variableName, value);
        } else {
            assert parent != null;
            parent.changeVariable(variableName, value);
        }
    }

    public Object resolveVariable(String variableName) {
        if (!isDeclared(variableName)) {
            throw new GLangVariableNotDeclaredException(variableName);
        }
        if (symbols.containsKey(variableName)) {
            return symbols.get(variableName);
        } else {
            assert parent != null;
            return parent.resolveVariable(variableName);
        }
    }
}
