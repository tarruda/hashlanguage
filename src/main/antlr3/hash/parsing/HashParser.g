parser grammar HashParser;

options {  
    language  = Java;
    output    = AST;
    superClass = AbstractHashParser;
    tokenVocab = HashLexer;
}

tokens {
    // virtual tokens for the AST
    STRING;
    BOOLEAN;
    INTEGER;
}

@header {
    package hash.parsing;
}


program
  :
  statement*
  ;
  
statement
  :
  expression SCOLON -> expression
  ;
  
expression
  : disjunction
  ;

disjunction
  : conjunction (OR^ conjunction)* 
  ;

conjunction
  : bitwiseOr (AND^ bitwiseOr)*
  ;
  
bitwiseOr
  : bitwiseAnd (BIT_OR^ bitwiseAnd)*
  ;
  
bitwiseAnd
  : equality (BIT_AND^ equality)*
  ;
  
equality
  : comparison ((EQ|NEQ)^ comparison)*   
  ;
  
comparison
  : shift ((LE|LT|GE|GT)^ shift)?
  ;
  
shift
  : addition ((SHR|SHL)^ addition)*  
  ;
  
addition
  : multiplication ((PLUS|MINUS)^ multiplication)*
  ;
  
multiplication
  : plusOrMinus ((MUL|DIV|MOD)^ plusOrMinus)* 
  ;

plusOrMinus
  : (INC|DEC|PLUS|MINUS)^ plusOrMinus
  | invert 
  ;
  
invert
  : (NOT|BIT_NOT)^ invert
  | primary
  ;
  
primary
  : atom
  ;

atom
  : enclosure
  | literal  
  ;
  
enclosure
  : LROUND disjunction RROUND -> disjunction
  ;
  
literal
  : STRING
  | FLOAT
  | INT
  | BOOLEAN
  ;

identifier
  : IDENTIFIER
  ;
