lexer grammar HashLexer;

options {
   language = Java;
   superClass = AbstractHashLexer;
}

tokens {
  WS;
  STERM;
}

@header {
package hash.parsing;
}

//keywords
RETURN: 'return';
IMPORT: 'import';
FUNCTION: 'function';
CLASS: 'class';
NEW: 'new';
TRUE: 'true'; 
FALSE: 'false';
NULL: 'null';
THIS: 'this';
IN: 'in';
IS: 'is';

REGEX
@init { 
StringBuilder sb = new StringBuilder(); 
}
  : {regexTokenAllowed()}? =>
  '/'{sb.append("/");}
  ( options {greedy=false;}:
    (
      '\\/' {sb.append("/");}
    | REGEXCHAR[sb]     
    )
  )* 
  '/'{sb.append("/");}
  ('i'{sb.append("i");})?  
  {setText(sb.toString());}
  ;

fragment REGEXCHAR[StringBuilder sb]
  :
  c=~('\\'|'/'|'\n') {sb.appendCodePoint($c);}  
  ;

//
INDENTED_HEREDOC: '<<]' {indentedHereDoc()}?;
HEREDOC: '<<|' {hereDoc()}?;
COLON: ':';
COMMA: ',';
DOT: '.';
LROUND: '('{incNesting();};
RROUND: ')'{decNesting();};
LCURLY: '{'{enterBlock();};
RCURLY: '}'{leaveBlock();};
LSQUARE: '['{incNesting();};
RSQUARE: ']'{decNesting();};
PLUS_ASSIGN: '+=';
MINUS_ASSIGN: '-=';
MUL_ASSIGN: '*=';
DIV_ASSIGN: '/=';
MOD_ASSIGN: '%=';
POW_ASSIGN: '**=';
AND_ASSIGN: '&=';
OR_ASSIGN: '|=';
XOR_ASSIGN: '^=';
SHL_ASSIGN: '<<=';
USHR_ASSIGN: '>>>=';
SHR_ASSIGN: '>>=';
POW: '**';
MUL: '*';
DIV: '/';
MOD: '%';
INC: '++';
DEC: '--';
PLUS: '+';
MINUS: '-';
USHR: '>>>';
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
MATCHES: '=~';
NEQ: '!=';
ASSIGN: '=';
OR: '||';
AND: '&&';
NOT: '!';


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

TERM_OR_WS
  : ((';')+
  | ('\n'|'\r'|' '|'\t')+)
  {emitTerminatorOrWhitespace();}
  ;

LINE_CONT
  :
  '\\\n'
  {$channel = HIDDEN;}
  ; 

// Ignored tokens
COMMENT
  : '//' ~('\n'|'\r')* '\r'? '\n'
  | '/*' ( options {greedy=false;} : . )* '*/' 
  {$channel = HIDDEN;}
  ;  
