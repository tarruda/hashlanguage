package hash.vm;

public class Constant extends Expression {
	private Object value;

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		Class c = value.getClass();
		assert isPrimitive(c) || c == String.class;
		this.value = value;
	}

	@Override
	public Class getDataType() {
		Class rv = value.getClass();
		if (rv == Boolean.class)
			rv = Boolean.TYPE;
		else if (rv == Character.class)
			rv = Character.TYPE;
		else if (rv == Byte.class)
			rv = Byte.TYPE;
		else if (rv == Short.class)
			rv = Short.TYPE;
		else if (rv == Integer.class)
			rv = Integer.TYPE;
		else if (rv == Long.class)
			rv = Long.TYPE;
		else if (rv == Float.class)
			rv = Float.TYPE;
		else if (rv == Double.class)
			rv = Double.TYPE;
		return rv;
	}
}
