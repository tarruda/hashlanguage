package hash.testutils;

import static org.junit.Assert.assertEquals;

import org.antlr.runtime.RecognitionException;
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
	public void bitwiseAnd() {
		assertEquals(0, evaluate("3&1<<2>>+4*3**9"));
	}

	@Test
	public void bitwiseXor() {
		assertEquals(7, evaluate("7^3&1<<2>>+4*3**9"));
	}

	@Test
	public void bitOr() {
		assertEquals(15, evaluate("8|7^3&1<<2>>+4*3**9"));
	}

	@Test
	public void conjunction() throws RecognitionException {
		assertEquals(false, evaluate(" 5>=4 && 4>10"));
	}

	@Test
	public void disjunction() throws RecognitionException {
		assertEquals(true, evaluate("5>=4 && 4>10 || true "));
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

	// a few more testbeds

	@Test
	public void boolValues() {
		assertEquals(true, evaluate("'str'.boolValue()"));
		assertEquals(false, evaluate("''.boolValue()"));
		assertEquals(true, evaluate("4.boolValue()"));
		assertEquals(false, evaluate("0.boolValue()"));
		assertEquals(5, evaluate("'' || 5"));
		assertEquals(0, evaluate("''&&0"));
		assertEquals(10, evaluate("''|| 10 || 5"));
		assertEquals(5, evaluate("''|| 10 && 5"));
	}

	@Test
	public void booleanComparison() {
		assertEquals(true, evaluate("true==true"));
		assertEquals(true, evaluate("false==false"));
		assertEquals(true, evaluate("true!=false"));
		assertEquals(true, evaluate("false!=true"));
		assertEquals(false, evaluate("true!=true"));
		assertEquals(false, evaluate("false!=false"));
		assertEquals(false, evaluate("true==false"));
		assertEquals(false, evaluate("false==true"));
	}

	@Test
	public void numberComparison() throws RecognitionException {
		assertEquals(false, evaluate("5>=6"));
		assertEquals(true, evaluate("5>=5"));
		assertEquals(true, evaluate("5>=4"));
		assertEquals(false, evaluate("3>4"));
		assertEquals(false, evaluate("3>3"));
		assertEquals(true, evaluate("3>2"));
		assertEquals(true, evaluate("5<6"));
		assertEquals(false, evaluate("5<5"));
		assertEquals(false, evaluate("5<4"));
		assertEquals(true, evaluate("5<=6"));
		assertEquals(true, evaluate("5<=5"));
		assertEquals(false, evaluate("5<=4"));
		assertEquals(true, evaluate("5.4 < 6"));
		assertEquals(true, evaluate("25==25"));
		assertEquals(false, evaluate("25==26"));
		assertEquals(true, evaluate("25==25.0"));
		assertEquals(true, evaluate("25.0==25"));
		assertEquals(true, evaluate("25!=26"));
		assertEquals(false, evaluate("25!=25"));
		assertEquals(false, evaluate("25!=25.0"));
		assertEquals(false, evaluate("25.0!=25"));
	}

	@Test
	public void mixedComparisons() throws RecognitionException {
		assertEquals(false, evaluate("'1'== 1"));
		assertEquals(true, evaluate("(((1+1)==3)==false)"));
		assertEquals(true, evaluate("1+1==3==false"));
		assertEquals(true, evaluate("false==true==(1-2==3))"));
		assertEquals(true, evaluate("false==false==true==false==false"));
		assertEquals(false, evaluate("1<2==(3>4)==false==(12<=3*4)==false"));

	}
}
