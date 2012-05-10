lexer grammar HashLexer;

options {
   language = Java;
   superClass = AbstractHashLexer;
}

@header {
package hash.parsing;
}

//keywords
TRUE: 'true'; 
FALSE: 'false';

//
SCOLON: ';';
COLON: ':';
COMMA: ',';
DOT: '.';
LROUND: '(';
RROUND: ')';
LCURLY: '{';
RCURLY: '}';
LSQUARE: '[';
RSQUARE: ']';
POW: '**';
MUL: '*';
DIV: '/';
MOD: '%';
INC: '++';
DEC: '--';
PLUS: '+';
MINUS: '-';
SHR: '>>';
SHL: '<<';
BIT_NOT: '~';
BIT_AND: '&';
BIT_XOR: '^';
BIT_OR: '|';
LE: '<=';
LT: '<';
GE: '>=';
GT: '>';
EQ: '==';
NEQ: '!=';
ASSIGN: '=';
OR: '||';
AND: '&&';
NOT: '!';
IN: 'in';
IS: 'is'; 

IDENTIFIER: LETTER (LETTER|DEC_DIGIT)*;

// Literal tokens
FLOAT_NORMAL
  : { !isNumberAttributeAccess() }? =>   
    DEC_DIGIT+ '.' DEC_DIGIT* EXPONENT? 
  ;

DOT_FLOAT
  : '.' ('0'..'9')+ EXPONENT? 
  ;
  
FLOAT_EXP
  : ('0'..'9')+ EXPONENT  
  ;
   
HEX_INT
  : '0' ('x'|'X') HEX_DIGIT+
    { validateInteger(16, getText().substring(2)); }
  ;
  
DEC_INT
  : DEC_DIGIT+
    { validateInteger(10, getText()); }
  ;
  
OCT_INT
  : '0' ('o'|'O') OCT_DIGIT+
    { validateInteger(8, getText().substring(2)); }
  ;
  
BIN_INT 
  : '0' ('b'|'B') BIN_DIGIT+
    { validateInteger(2, getText().substring(2)); }
  ; 

DQ_STRING
@init { 
StringBuilder sb = new StringBuilder(); 
}
  :
  '"'
  ( options {greedy=false;}:
    (
	    ESC_SEQ[sb]
	  | DQCHAR[sb]	   
    )
  )* 
  '"'
  {setText(sb.toString());}
  ;

SQ_STRING
@init { 
StringBuilder sb = new StringBuilder(); 
}
  :
  '\''
  ( options {greedy=false;}:
    (
      ESC_SEQ[sb]
    | SQCHAR[sb]     
    )
  )* 
  '\''
  {setText(sb.toString());}
  ;

fragment DQCHAR[StringBuilder sb]
  :
  c=~('\\'|'"'|'\n') {sb.appendCodePoint($c);}  
  ;

fragment SQCHAR[StringBuilder sb]
  :
  c=~('\\'|'\''|'\n') {sb.appendCodePoint($c);}  
  ;

fragment ESC_SEQ[StringBuilder sb]
  :  
    '\\' 
    ( 'b' {sb.append("\b");}
    | 't' {sb.append("\t");}
	  | 'n' {sb.append("\n");}
	  | 'f' {sb.append("\f");}
	  | 'r' {sb.append("\r");}
	  | '\"' {sb.append("\"");}
	  | '\'' {sb.append("'");}
	  | '\\' {sb.append("\\");}
	  | 'u' i=HEX_DIGIT j=HEX_DIGIT k=HEX_DIGIT l=HEX_DIGIT 
	    {sb.appendCodePoint(convertFromHexDigits($i.text, $j.text, $k.text, $l.text));}
	  )
  ;

fragment EXPONENT: ('e'|'E') ('+'|'-')? ('0'..'9')+;  
fragment HEX_DIGIT: ('0'..'9'|'a'..'f'|'A'..'F');  
fragment DEC_DIGIT: '0'..'9';  
fragment OCT_DIGIT: '0'..'7';  
fragment BIN_DIGIT: '0'..'1';
fragment LETTER: ('a'..'z'|'A'..'Z'|'$'|'_');  

// Ignored tokens
COMMENT
  : '//' ~('\n'|'\r')* '\r'? '\n'
  | '/*' ( options {greedy=false;} : . )* '*/' 
    {$channel = HIDDEN;}
  ;    
WS  
  : 
  (' '|'\r'|'\t'|'\n') {$channel=HIDDEN;}
  ;
