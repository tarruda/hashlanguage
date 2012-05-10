package hash.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public abstract class PrimaryExpressionTest {
	protected abstract Object evaluate(String expression);

	@Test
	public void numberToString() {
		assertEquals("5", evaluate("5.toString()"));
		assertEquals("101", evaluate("5.toBinaryString(5)"));
	}

	@Test
	public void equality() {
		assertTrue((Boolean) evaluate("5.equals(5)"));
		assertTrue((Boolean) evaluate("'ABCDE'.equals(\"AB\"+\"CDE\")"));
	}

}
