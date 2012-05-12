package hash.testutils;

import static org.junit.Assert.assertEquals;
import hash.lang.Factory;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public abstract class PrimaryExpressionTest {

	protected Map context;

	protected abstract Object evaluate(String expression);

	@Before
	public void setup() {
		context = Factory.createExecutionContext();
	}

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

	@Test
	public void listOperations() {
		evaluate("l=[]");
		evaluate("l.add(5)");
		evaluate("l.add(10)");
		assertEquals("[5, 10]", evaluate("l.toString()"));
		evaluate("l[0]= 'Test'");
		assertEquals("[Test, 10]", evaluate("l.toString()"));
	}

	@Test
	public void listSlice() {
		evaluate("l=[1,2,3,4,5,6,7,8,9,10]");
		assertEquals("[1, 2, 3, 4, 5]", evaluate("l[0:4].toString()"));

	}
}
