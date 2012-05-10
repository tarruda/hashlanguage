package hash.testutils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public abstract class BinaryAndUnaryExpressionTest {
	protected abstract Object evaluate(String expression);

	@Test
	public void bitInvert() {
		assertEquals(-2, evaluate("~1"));
		assertEquals(-3, evaluate("~2"));
	}

	@Test
	public void invert() {
		assertEquals(-18.0f, evaluate("-18.0"));
		assertEquals(-18, evaluate("-18"));
	}

	@Test
	public void integerDivison() {
		assertEquals(32, evaluate("10+15*3/2"));
	}

	@Test
	public void floatDivison() {
		assertEquals(32.5f, evaluate("10+15*3/2.0"));
	}

}
