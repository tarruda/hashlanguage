package hash.util;

public class Numbers {

	public static boolean isIntegerResult(Object arg1, Object arg2) {
		return isInteger(arg1) && isInteger(arg2);
	}

	public static Object floatNumber(Double d) {
		float fVal = d.floatValue();
		if (d.doubleValue() == fVal)
			return fVal;
		return d;
	}

	public static Object integerNumber(Long l) {
		int iVal = l.intValue();
		if (l.longValue() == iVal)
			return iVal;
		return l;
	}

	public static boolean isInteger(Object n) {
		return n.getClass() == Long.class || n.getClass() == Integer.class;
	}
}
