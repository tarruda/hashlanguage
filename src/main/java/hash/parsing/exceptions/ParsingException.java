package hash.parsing.exceptions;


@SuppressWarnings("serial")
public class ParsingException extends RuntimeException {

	public ParsingException(){	
	}
	
	public ParsingException(String msg){
		super(msg);
	}
	
	public ParsingException(String msg, Throwable cause){
		super(msg, cause);
	}
	
	public ParsingException(Throwable cause){
		super(cause);
	}
}
