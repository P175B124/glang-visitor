package org.glang.visitor;

import org.antlr.v4.runtime.CharStreams;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionCallTest {

    @Test
    void function_with_return() {
        String program = """
                func test(a,b) {
                    var c = a + b
                    return c
                }
                
                print(test(2,3))
                """;

        String expected =
                """
                5
                """;

        String actual = (String)GLang.execute(CharStreams.fromString(program));

        assertEquals(expected, actual);
    }

    @Test
    void function_without_return() {
        String program = """
                func printFive() {
                    print(5)
                }
                printFive()
                """;

        String expected =
                """
                5
                """;

        String actual = (String)GLang.execute(CharStreams.fromString(program));

        assertEquals(expected, actual);
    }

    @Test
    void function_with_empty_return() {
        String program = """
                func printFive() {
                    print(5)
                    return
                    print(7)
                }
                printFive()
                """;

        String expected =
                """
                5
                """;

        String actual = (String)GLang.execute(CharStreams.fromString(program));

        assertEquals(expected, actual);
    }

    @Test
    void functions_nested() {
        String program = """
                func sum(a,b) {
                    return a+b
                }
                print(sum(1, sum(2, sum(3,4))))
                """;

        String expected =
                """
                10
                """;

        String actual = (String)GLang.execute(CharStreams.fromString(program));

        assertEquals(expected, actual);
    }
}