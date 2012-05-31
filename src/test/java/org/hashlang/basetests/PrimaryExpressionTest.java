package org.hashlang.basetests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public abstract class PrimaryExpressionTest extends AbstractCodeTest {

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

	public void nullArgument() {
		evaluate("l=[5,10]");
		evaluate("l[1.2]", ClassCastException.class);
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
		evaluate("import java.lang.Integer");
		assertEquals(5, evaluate("new Integer('5')"));
	}

	@Test
	public void methodInvocation() {
		evaluate("account={balance:0,add:(amount){this.balance+=amount}}");
		assertEquals(0, evaluate("account.balance"));
		evaluate("account.add(43.2)");
		assertEquals(43.2, evaluate("account.balance"));
		evaluate("account.add(50.5)");
		assertEquals(93.7, evaluate("account.balance"));
	}

	@Test
	public void methodInvocationValidation() {
		// A function is classified as a method if it makes any reference
		// to 'this'.
		evaluate("person={name:'user',getName:(){return this.name},"
				+ "getRandom:(){return 15*4+2}}");
		assertEquals("user", evaluate("person.getName()"));
		assertEquals(62, evaluate("person.getRandom()"));
		evaluate("f = person.getRandom");
		evaluate("m = person.getName");
		assertEquals(62, evaluate("f()"));
		// Throws since m is a method(must be invoked as an attribute or index
		// of an object)
		evaluate("m()", RuntimeException.class);
	}

	@Test
	public void newlinesAsStatementTerminators() {
		/*
		 * TODO Move move this test somewhere else Also testing how the lexer
		 * ignores newlines based on the nesting/scoping level(newlines inside
		 * curly, square or round braces are ignored. For curly braces it can
		 * determine if it inside a code block. If it it, then newlines are
		 * passed to the parser, which treats them as statement
		 * terminatorstokens)
		 */
		evaluate("o = {\n  duplicateWord: \n  (name) {\n\n    calc = (n\n) {\n"
				+ "\n      rv = n * 1\n      return rv * name\n    }    \n"
				+ "    return calc(1) * 2\n\n\n  }\n}");
		assertEquals("abcabc", evaluate("o.duplicateWord('abc')"));
	}

	@Test
	public void conditionalExpressions() {
		assertEquals(5, evaluate("true ? 5 : 10"));
		assertEquals(10, evaluate("false ? 5 : 10"));
	}
}
