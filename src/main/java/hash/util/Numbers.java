package hash.util;

public class Numbers {

	public static boolean isIntegerResult(Object arg1, Object arg2) {
		return (arg1.getClass() == Long.class || arg1.getClass() == Integer.class)
				&& (arg2.getClass() == Long.class || arg2.getClass() == Integer.class);
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
}
