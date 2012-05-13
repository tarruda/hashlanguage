package hash.testutils;

import static org.junit.Assert.assertEquals;
import hash.runtime.Factory;
import hash.runtime.generators.HashAdapter;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public abstract class PrimaryExpressionTest {

	protected Map context;

	protected abstract Object evaluate(String expression);

	@Before
	public void setup() {
		context = Factory.createExecutionScope();
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
	public void stringMethods() {
		evaluate("s='Key=value'.split('=')");
		assertEquals("Key", evaluate("s[0]"));
		assertEquals("value", evaluate("s[1]"));
	}

	@Test
	public void stringSlice() {
		evaluate("s='abcdefgh'");
		assertEquals("abcd", evaluate("s[:3]"));
		assertEquals("efgh", evaluate("s[-4:]"));
		assertEquals("aceg", evaluate("s[::2]"));
	}

	@Test
	public void listMethods() {
		evaluate("l=[]");
		evaluate("l.add(5)");
		evaluate("l.add(10)");
		assertEquals("[5, 10]", evaluate("l.toString()"));
		evaluate("l[0]= 'Test'");
		assertEquals("[Test, 10]", evaluate("l.toString()"));
	}

	@Test
	public void listIndex() {
		evaluate("l=[5,10]");
		evaluate("l[-1]= 'Test'");
		assertEquals("[5, Test]", evaluate("l.toString()"));
	}

	@Test(expected = ClassCastException.class)
	public void nullArgument() {
		evaluate("l=[5,10]");
		evaluate("l[1.2]");
	}

	@Test
	public void listSlice() {
		evaluate("l=[1,2,3,4,5,6,7,8,9,10]");
		assertEquals("[1, 2, 3, 4, 5]", evaluate("l[:4].toString()"));
		assertEquals("[6, 7, 8, 9, 10]", evaluate("l[5:].toString()"));
		assertEquals("[3, 4, 5, 6, 7, 8]", evaluate("l[2:7].toString()"));
		assertEquals("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]",
				evaluate("l[:].toString()"));
		assertEquals("[1, 3, 5, 7, 9]", evaluate("l[::2].toString()"));
		assertEquals("[3, 6, 9]", evaluate("l[2::3].toString()"));
		assertEquals("[3, 6, 9]", evaluate("l[2::3].toString()"));
		assertEquals("[9, 10]", evaluate("l[-2:].toString()"));
		assertEquals("[9, 6, 3]", evaluate("l[-2:0:3].toString()"));
		assertEquals("[10, 9, 8, 7, 6, 5, 4, 3, 2, 1]",
				evaluate("l[-1:0].toString()"));
	}

	@Test
	public void arraySlice() {
		evaluate("a='1,2,3,4,5,6,7,8,9,10'.split(',')[-2:0:3]");
		assertEquals("9", evaluate("a[0]"));
		assertEquals("6", evaluate("a[1]"));
		assertEquals("3", evaluate("a[2]"));
	}

	@Test
	public void objectAttributes() {
		evaluate("m={name:'mongo','type':'db'}");
		assertEquals("mongo", evaluate("m.name"));
		assertEquals("db", evaluate("m.type"));
		assertEquals("mongo", evaluate("m.put('name', 'Thiago')"));
		assertEquals("db", evaluate("m.put('type', 'Programmer')"));
		assertEquals("{name=Thiago, type=Programmer}", evaluate("m.toString()"));
	}

	@Test
	public void constructingInstances() {
		context.put("Integer", HashAdapter.getHashClass(Integer.class));
		assertEquals(5, evaluate("new Integer('5')"));
	}
}
