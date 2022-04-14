grammar GLang;

program
 : statement+ EOF
 ;

statement
 : variableDeclaration
 | assignment
 | systemFunctionCall
 | ifElseStatement
 ;

variableDeclaration
 : 'var' IDENTIFIER '=' expression
 ;

assignment
 : IDENTIFIER '=' expression
 ;

systemFunctionCall
 : PRINT '(' expression ')'                             #printFunctionCall
 ;

ifElseStatement : 'if' '(' expression ')' block 'else' block ;

block : '{' statement* '}' ;

constant: INTEGER | DECIMAL | BOOLEAN |STRING ;

expression
 : constant                                             #constantExpression
 | IDENTIFIER                                           #identifierExpression
 | '(' expression ')'                                   #parenthesesExpression
 | booleanUnaryOp expression                            #booleanUnaryOpExpression
 | expression booleanBinaryOp expression                #booleanBinaryOpExpression
 | expression numericMultiOp expression                 #numericMultiOpExpression
 | expression numericAddOp expression                   #numericAddOpExpression
 | expression stringBinaryOp expression                 #stringBinaryOpExpression
 ;

booleanUnaryOp : '!' ;

booleanBinaryOp : '||' | '&&' ;

numericMultiOp : '*' | '/' | '%' ;

numericAddOp : '+' | '-' ;

stringBinaryOp : '..' ; //concat

PRINT : 'print';

INTEGER : [0-9]+ ; //TODO - support negative numbers
DECIMAL : [0-9]+ '.' [0-9]+ ;
BOOLEAN : 'true' | 'false' ;
STRING : ["] ( ~["\r\n\\] | '\\' ~[\r\n] )* ["] ;

IDENTIFIER : [a-zA-Z_][a-zA-Z_0-9]* ;

COMMENT : ( '//' ~[\r\n]* | '/*' .*? '*/' ) -> skip ;

WS : [ \t\f\r\n]+ -> skip ;