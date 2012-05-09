package hash.runtime.exceptions;

public class AttributeNotFoundException extends HashException {

	private static final long serialVersionUID = -2949038823066781895L;

	public AttributeNotFoundException(String attributeName) {
		super(String.format("Attribute '%s' is not defined", attributeName));
	}
}
