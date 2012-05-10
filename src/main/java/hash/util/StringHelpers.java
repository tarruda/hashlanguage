package hash.util;

public class StringHelpers {

	public static String multiplicateString(String s, int n) {
		StringBuilder rv = new StringBuilder();
		for (int i = 0; i < n; i++)
			rv.append(s);
		return rv.toString();
	}
}
