package org.glang.visitor;

public record ReturnValue(Object value) {

    public Object getValue() {
        return value;
    }
}
