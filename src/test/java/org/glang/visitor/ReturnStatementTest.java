package org.glang.visitor;

import org.antlr.v4.runtime.CharStreams;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReturnStatementTest {

    @Test
    void simple_return() {
        String program = """
                print(1)
                return 5
                print(2)
                """;

        String expected =
                """
                1
                """;

        String actual = (String)GLang.execute(CharStreams.fromString(program));

        assertEquals(expected, actual);
    }

    @Test
    void nested_return() {
        String program = """
                print(1)
                
                if(true) {
                    print(2)
                    if(true) {
                        print(3)
                        return 5
                        print(4)
                    } else {}
                    print(5)
                } else {}
                
                print(6)
                """;

        String expected =
                """
                1
                2
                3
                """;

        String actual = (String)GLang.execute(CharStreams.fromString(program));

        assertEquals(expected, actual);
    }
}