package hash.vm;

public abstract class Expression extends Statement {
	public abstract Class getDataType();

	protected boolean isPrimitive(Class c) {
		return c.isPrimitive() || c == Boolean.class || c == Byte.class
				|| c == Short.class || c == Character.class
				|| c == Integer.class || c == Long.class || c == Float.class
				|| c == Double.class;
	}

	protected boolean isPrimitive() {
		return isPrimitive(getDataType());
	}	
}
