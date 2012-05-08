package hash.runtime.exceptions;

public class AttributeNotFoundException extends HashException {

	private static final long serialVersionUID = -7279853122361757870L;

	public AttributeNotFoundException(String attributeName) {
		super(String.format("Attribute '%s' is not defined", attributeName));
	}
}
