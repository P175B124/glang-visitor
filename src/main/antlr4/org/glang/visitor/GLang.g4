grammar GLang;

program
 : line+ EOF
 ;

line
 : functionDeclaration
 | statement
 ;

statement
 : variableDeclaration
 | assignment
 | functionCall
 | systemFunctionCall
 | ifElseStatement
 | returnStatement
 ;

functionDeclaration
 : 'func' IDENTIFIER '(' paramList? ')' functionBody
 ;

paramList : IDENTIFIER (',' IDENTIFIER)* ;

functionBody : '{' statement* '}' ; //TODO cannot return from the midle of function

variableDeclaration
 : 'var' IDENTIFIER '=' expression
 ;

assignment
 : IDENTIFIER '=' expression
 ;

functionCall
 : IDENTIFIER '(' expressionList? ')'
 ;

systemFunctionCall
 : PRINT '(' expression ')'                             #printFunctionCall
 ;

ifElseStatement : 'if' '(' expression ')' block 'else' block ;

block : '{' statement* '}' ;

returnStatement : 'return' expression? ;

constant: INTEGER | DECIMAL | BOOLEAN |STRING ;

expressionList
 : expression (',' expression)*
 ;

expression
 : constant                                             #constantExpression
 | IDENTIFIER                                           #identifierExpression
 | '(' expression ')'                                   #parenthesesExpression
 | booleanUnaryOp expression                            #booleanUnaryOpExpression
 | expression booleanBinaryOp expression                #booleanBinaryOpExpression
 | expression numericMultiOp expression                 #numericMultiOpExpression
 | expression numericAddOp expression                   #numericAddOpExpression
 | expression stringBinaryOp expression                 #stringBinaryOpExpression
 | functionCall                                         #functionCallExpression
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