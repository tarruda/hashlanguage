parser grammar HashParser;

options {  
    language  = Java;
    output    = AST;
    superClass = AbstractHashParser;
    tokenVocab = HashLexer;
}

tokens {
    // imaginary tokens for the AST
    BINARY;
    UNARY;
    STRING;
    FLOAT;
    INTEGER;
    BOOLEAN;
    INVOCATION;
    ARGS;
    ATTRIBUTE;
    ITEM;
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
  : (l=conjunction -> $l) 
    (o=OR r=conjunction -> ^(BINARY[$o] $disjunction $r))* 
  ;

conjunction
  : (l=bitwiseOr -> $l) 
    (o=AND r=bitwiseOr -> ^(BINARY[$o] $conjunction $r))*
  ;
  
bitwiseOr
  : (l=bitwiseXor -> $l)
    (o=BIT_OR r=bitwiseXor -> ^(BINARY[$o] $bitwiseOr $r))*
  ;  
  
bitwiseXor
  : (l=bitwiseAnd -> $l) 
    (o=BIT_XOR r=bitwiseAnd -> ^(BINARY[$o] $bitwiseXor $r))*
  ;  
  
bitwiseAnd
  : (l=equality -> $l) 
    (o=BIT_AND r=equality -> ^(BINARY[$o] $bitwiseAnd $r))*
  ;

equality
  : (l=comparison -> $l) 
    ((o=EQ|o=NEQ) r=comparison -> ^(BINARY[$o] $equality $r))*   
  ;
  
comparison
  : (l=shift -> $l)
    ((o=LE|o=LT|o=GE|o=GT) r=shift -> ^(BINARY[$o] $l $r))?
  ;
  
shift
  : (l=addition -> $l)
    ((o=SHR|o=SHL) r=addition -> ^(BINARY[$o] $shift $r))*  
  ;
  
addition
  : (l=multiplication -> $l)
    ((o=PLUS|o=MINUS) r=multiplication -> ^(BINARY[$o] $addition $r))*
  ;
  
multiplication
  : (l=power -> $l)
    ((o=MUL|o=DIV|o=MOD) r=power -> ^(BINARY[$o] $multiplication $r))*  
  ;
  
power
  : (l=plusOrMinus -> $l) 
    ((o=POW) r=power -> ^(BINARY[$o] $l $r))?
  ;

plusOrMinus
  : (o=INC|o=DEC|o=PLUS|o=MINUS) op=plusOrMinus -> ^(UNARY[$o] $op)
  | invert 
  ;
  
invert
  : (o=NOT|o=BIT_NOT) op=invert -> ^(UNARY[$o] $op)
  | primary
  ;
  
primary
  : (atom -> atom)
    (
      (
        s=LROUND
        (args+=expression (COMMA args+=expression)*)? 
        RROUND
        -> ^(INVOCATION[$s, "Invocation"] $primary ^(ARGS $args*))
      )
    | (
        s=DOT name=identifier 
        -> ^(ATTRIBUTE[$s, "Attribute"] $primary STRING[$name.start, $name.text])
      )
    | (
        s=LSQUARE key=expression RSQUARE
        -> ^(ITEM[$s, "Item"] $primary $key)
      )
    )*
  ;
  
atom
  : enclosure
  | literal
  | identifier
  ;
  
enclosure
  : LROUND expression RROUND -> expression
  ;
  
identifier
  : IDENTIFIER
  ;
  
literal
  : stringLiteral
  | floatLiteral
  | integerLiteral
  | booleanLiteral
  ;

stringLiteral
  : (t=SQ_STRING|t=DQ_STRING) -> STRING[$t]
  ;

floatLiteral
  : (t=FLOAT_NORMAL|t=DOT_FLOAT|t=FLOAT_EXP) -> FLOAT[$t]
  ;
  
integerLiteral
  : (t=HEX_INT|t=DEC_INT|t=OCT_INT|t=BIN_INT) -> INTEGER[$t]
  ;
  
booleanLiteral
  : (t=TRUE|t=FALSE) -> BOOLEAN[$t]
  ;