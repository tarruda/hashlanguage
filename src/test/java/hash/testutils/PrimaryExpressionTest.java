package hash.testutils;

import static org.junit.Assert.assertEquals;

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
		assertEquals(true, evaluate("5.equals(5)"));
		assertEquals(true, evaluate("'ABCDE'.equals(\"AB\"+\"CDE\")"));
	}

	@Test
	public void stringSplit() {
		assertEquals("Key", evaluate("'Key=value'.split('=')[0]"));
		assertEquals("value", evaluate("'Key=value'.split('=')[1]"));
	}
}
