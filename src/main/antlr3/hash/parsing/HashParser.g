parser grammar HashParser;

options {  
    language  = Java;
    output    = AST;
    superClass = AbstractHashParser;
    tokenVocab = HashLexer;
}

tokens {
    // imaginary tokens for the AST
    INCR;    
    BINARY;
    UNARY;
    MAP;
    STRING;
    FLOAT;
    INTEGER;
    BOOLEAN;
    INVOCATION;
    NEWINSTANCE;
    LIST;
    ATTRIBUTE;
    INDEX;
    SLICE;
    BLOCK;
    FUNCTIONBLOCK;
    FUNCTION;
    FOREACH; 
    NAMEREF; 
}

@header {
    package hash.parsing;
}


program
  : statements EOF!      
  ;
        
block
  : LCURLY statements? RCURLY -> statements
  ;
    
statements
  : statementSeparator*
    s+=statement 
    (
      (statementSeparator statement) => statementSeparator s+=statement
    | statementSeparator
    )*   
    -> ^(BLOCK["Statements"] $s+)
  ;
  
statementSeparator
  : (LINE|SCOLON)
  ;

statement
  : importStatement
  | functionStatement
  | classStatement
  | ifStatement  
  | forStatement
  | whileStatement
  | doWhileStatement
  | tryStatement
  | throwStatement
  | returnStatement
  | continueStatement
  | breakStatement
  | expression    
  ;
  
importStatement
  : t=IMPORT parts+=IDENTIFIER (DOT parts+=IDENTIFIER)*
    -> ^(ASSIGN[$t, "="] NAMEREF[getImportTargetId($parts)]
        ^(INVOCATION["Invocation"] NAMEREF[getImportFunctionId()]
         ^(LIST["Arguments"] STRING[getImportString($parts)])))
  ;
    
functionStatement
  : t=FUNCTION name=IDENTIFIER f=functionExpression
    -> ^(ASSIGN[$t, "Function Declaration"] NAMEREF[$name] $f)
  ;
  
classStatement
  : t=CLASS name=IDENTIFIER
      (EXTENDS superClass=nameRef)? map=mapExpression
    -> ^(ASSIGN[$t, "Class Declaration"] NAMEREF[$name]
        ^(INVOCATION["Invocation"] NAMEREF[getClassFunctionId()]
         ^(LIST["Arguments"] $map {nodeOrNull(superClass)})))
  ;
  
ifStatement
  : ((IF LROUND expression RROUND (block|statement statementSeparator) ELSE) =>
    IF LROUND cond=expression RROUND
      (
        (LCURLY) => tb=block
      | ts=statement statementSeparator
      )        
    ELSE 
      (
        (LCURLY) => fb=block
      | fs=statement
      )
  | IF LROUND cond=expression RROUND
      (
        (LCURLY) => tb=block
      | ts=statement
      )
  )
    -> ^(IF["if"] $cond {select(tb,ts)} {select(fb,fs)})  
  ;
    
forStatement
 : (FOR LROUND IDENTIFIER IN) =>
    t=FOR LROUND id=IDENTIFIER IN iterable=expression RROUND
      ((LCURLY) => b=block|s=statement)
    -> ^(FOREACH[$t, "for each"] $id $iterable {select(b,s)})   
 |  FOR LROUND 
    (init=expression)? SCOLON (cond=expression)? SCOLON (u=expression)?
    RROUND
      ((LCURLY) => b=block|s=statement)
    -> ^(FOR["for"] {nodeOrNull(init)} {nodeOrNull(cond)} {nodeOrNull(u)} {select(b,s)})
 ;
  
whileStatement
  : WHILE LROUND cond=expression RROUND
      ((LCURLY) => b=block|s=statement)
    -> ^(WHILE["while"] $cond {select(b,s)})
  ;  

doWhileStatement
  : DO ((LCURLY) => b=block|s=statement)
    statementSeparator* WHILE LROUND cond=expression RROUND
    -> ^(DO["do while"] $cond {select(b,s)})
  ;
            
tryStatement
  : t=TRY tb=block
    ((CATCH)=>
      (catches+=catchBlock)+
      (FINALLY fb=block)?
      -> ^(TRY["Try Statement"] 
            {tryBlock(tb)} 
            {catchBlocks($catches)} 
            {finallyBlock(fb)})
    | FINALLY fb=block
      -> ^(TRY["Try Statement"] 
            {tryBlock(tb)}  
            {catchBlocks()}  
            {finallyBlock(fb)})
    )    
  ;    
 
catchBlock
  : CATCH 
    (LROUND 
    ((IDENTIFIER IDENTIFIER) =>
      extype=IDENTIFIER exid=IDENTIFIER
    | exid=IDENTIFIER
    ) 
    RROUND)
      cb=block
    -> ^(CATCH["Catch"] {nameRefOrNull(extype)} NAMEREF[$exid] $cb)
  ;
    
throwStatement
  : THROW t=expression
    -> ^(THROW $t) 
  ;
           
returnStatement
  : RETURN (r=expression)? -> ^(RETURN {nodeOrNull(r)}) 
  ;
  
continueStatement
  : CONTINUE
  ;
  
breakStatement
  : BREAK
  ;    
                    
expression
  : resumeExpression
  | yieldExpression
  | functionExpression
  | incOrDecExpression     
  | (l=disjunction -> $l)
    ( 
      o=ASSIGN r=expression -> ^($o $l $r)
    | o=PLUS_ASSIGN r=expression -> ^(ASSIGN[$o, "="] $l ^(BINARY[$o, "+"] $l $r))
    | o=MINUS_ASSIGN r=expression -> ^(ASSIGN[$o, "="] $l ^(BINARY[$o, "-"] $l $r))
    | o=MUL_ASSIGN r=expression -> ^(ASSIGN[$o, "="] $l ^(BINARY[$o, "*"] $l $r))
    | o=DIV_ASSIGN r=expression -> ^(ASSIGN[$o, "="] $l ^(BINARY[$o, "/"] $l $r))
    | o=MOD_ASSIGN r=expression -> ^(ASSIGN[$o, "="] $l ^(BINARY[$o, "\%"] $l $r))
    | o=POW_ASSIGN r=expression -> ^(ASSIGN[$o, "="] $l ^(BINARY[$o, "**"] $l $r))
    | o=AND_ASSIGN r=expression -> ^(ASSIGN[$o, "="] $l ^(BINARY[$o, "&"] $l $r))
    | o=OR_ASSIGN r=expression -> ^(ASSIGN[$o, "="] $l ^(BINARY[$o, "|"] $l $r))
    | o=XOR_ASSIGN r=expression -> ^(ASSIGN[$o, "="] $l ^(BINARY[$o, "^"] $l $r))
    | o=SHL_ASSIGN r=expression -> ^(ASSIGN[$o, "="] $l ^(BINARY[$o, "<<"] $l $r))
    | o=USHR_ASSIGN r=expression -> ^(ASSIGN[$o, "="] $l ^(BINARY[$o, ">>>"] $l $r))
    | o=SHR_ASSIGN r=expression -> ^(ASSIGN[$o, "="] $l ^(BINARY[$o, ">>"] $l $r))
    | o=INC-> ^(INCR[$o] $l ^(ASSIGN[$o,"="] $l ^(BINARY[$o, "+"] $l INTEGER["1"])))
    | o=DEC-> ^(INCR[$o] $l ^(ASSIGN[$o,"="] $l ^(BINARY[$o, "-"] $l INTEGER["1"])))
    )? 
  ;
  
resumeExpression
  : RESUME name=nameRef (arg=expression)? 
    -> ^(RESUME $name {nodeOrNull(arg)})
  ;
  
yieldExpression
  : YIELD (arg=expression)?
    -> ^(YIELD {nodeOrNull(arg)})
  ;

functionExpression
  : l=LROUND (params+=IDENTIFIER (COMMA params+=IDENTIFIER)*)? RROUND b=block 
      -> ^(FUNCTION[$l, "Function"] {stringList($params)} {functionBlock(b)} )
  ;
  
incOrDecExpression
  : o=INC l=expression -> ^(ASSIGN[$o, "="] $l ^(BINARY[$o, "+"] $l INTEGER["1"]))   
  | o=DEC l=expression -> ^(ASSIGN[$o, "="] $l ^(BINARY[$o, "-"] $l INTEGER["1"]))   
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
  : (l=relation -> $l) 
    (o=BIT_AND r=relation -> ^(BINARY[$o] $bitwiseAnd $r))*
  ;

relation
  : (l=comparison -> $l) 
    (
      (o=EQ|o=NEQ|o=MATCHES|o=IS) r=comparison -> ^(BINARY[$o] $relation $r)
    | (o=IN) r=comparison -> ^(BINARY[$o, getContainsId()] $r $relation)
    )*   
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
  : (o=PLUS|o=MINUS) op=plusOrMinus -> ^(UNARY[$o] $op)
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
        {args=null;}(args=expressionList)?       
        RROUND
        -> ^(INVOCATION[$s, "Invocation"] $primary ^(LIST["Arguments"] $args?))
      )
    | (
        s=DOT name=IDENTIFIER 
        -> ^(ATTRIBUTE[$s, "Attribute"] $primary STRING[$name])
      )            
    | ( (LSQUARE expression RSQUARE) =>
        (
          s=LSQUARE key=expression RSQUARE
          -> ^(INDEX[$s, "Item"] $primary $key)
        )
      | (
          s=LSQUARE 
          {lb=null;}(lb=expression)? 
          COLON 
          {ub=null;}(ub=expression)? 
          {step=null;}(COLON step=expression)? 
          RSQUARE
          -> ^(SLICE[$s, "Slice"] $primary 
              ^(LIST["Args"] {nodeOrNull(lb)} {nodeOrNull(ub)} {nodeOrNull(step)}))
        )
	    )     
    )*
  ;

expressionList
  :
  expression (COMMA expression)* 
  -> expression+  
  ;
  
atom
  : parenthesisExpression  
  | constructorExpression
  | mapExpression
  | listExpression
  | nameRef
  | thisExpression
  | literal 
  ;
   
parenthesisExpression
  : LROUND expression RROUND -> expression
  ;
  
constructorExpression
  : t=NEW klass=nameRef LROUND (args=expressionList)? RROUND
    -> ^(INVOCATION[$t, "New Instance"] 
          ^(ATTRIBUTE["Attribute"] $klass STRING[getConstructorId()])
          ^(LIST["Arguments"] $args?))    
  ;
  
mapExpression
  : t=LCURLY
    ( 
      keyValuePair
      (COMMA keyValuePair)*
      COMMA?
    )?
    RCURLY
    -> ^(MAP[$t, "Map"] keyValuePair+)
  ;
  
listExpression
  : t=LSQUARE (expressionList COMMA?)? RSQUARE
    -> ^(LIST[$t, "List"] expressionList?)
  ;
  
keyValuePair
  : i=IDENTIFIER COLON v=expression -> ^(STRING[$i] $v)
  | l=literal COLON v=expression -> ^($l $v)   
  ;
    
nameRef
@init {
  int level = 0;
}
  : (AT {level++;})* t=IDENTIFIER
    -> ^(NAMEREF[$t] {level})
  ;

thisExpression
  : THIS
  ;
  
literal
  : regexLiteral
  | stringLiteral
  | floatLiteral
  | integerLiteral
  | booleanLiteral
  | NULL
  ;

regexLiteral
  : REGEX
  ;

stringLiteral
  : (t=SQ_STRING|t=DQ_STRING|t=HEREDOC|t=INDENTED_HEREDOC) -> STRING[$t]
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