package hash.parsing.visitors.evaluators;

@SuppressWarnings("serial")
public class ReturnStatement extends RuntimeException {

	private Object value;

	public ReturnStatement(Object value){
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}
}
