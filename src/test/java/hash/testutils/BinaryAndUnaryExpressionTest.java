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

	@Test
	public void power() throws Exception {
		assertEquals(32, evaluate("2**5"));
	}

	@Test
	public void powerRightAssociativity() {
		assertEquals(256, evaluate("2**2**3"));
	}

	@Test
	public void multiplication() {
		assertEquals(2048, evaluate("4**5*2"));
	}

	@Test
	public void division() {
		assertEquals(512, evaluate("4**5/2"));
	}

	@Test
	public void modulo() {
		assertEquals(4, evaluate("4%5**2"));
	}

	@Test
	public void sum() {
		assertEquals(78734, evaluate("2+4*3**9"));
	}

	@Test
	public void subtraction() {
		assertEquals(119, evaluate("2*4**3-9"));
	}

	@Test
	public void shl() {
		assertEquals(4, evaluate("1<<2"));
		assertEquals(8, evaluate("1<<3"));
	}

	@Test
	public void shr() {
		assertEquals(1, evaluate("2>>1"));
		assertEquals(0, evaluate("2>>2"));
		assertEquals(0, evaluate("2>>3"));
		assertEquals(0, evaluate("2>>4"));
	}

	@Test
	public void shifts() {
		assertEquals(2, evaluate("1<<3>>2"));
		assertEquals(0, evaluate("1<<2>>+4*3"));
	}

	@Test
	public void bit_and() {
		assertEquals(0, evaluate("3&1<<2>>+4*3**9"));
	}

	@Test
	public void bit_xor() {
		assertEquals(7, evaluate("7^3&1<<2>>+4*3**9"));
	}

	@Test
	public void bit_or() {
		assertEquals(15, evaluate("8|7^3&1<<2>>+4*3**9"));
	}

	@Test
	public void stringConcatenation() {
		assertEquals("String concatenation",
				evaluate("'String' + ' ' + 'concatenation'"));
	}

	@Test
	public void stringMultiplication() {
		assertEquals("=====", evaluate("'='*5"));
		assertEquals("+++++", evaluate("5*'+'"));
	}
}
