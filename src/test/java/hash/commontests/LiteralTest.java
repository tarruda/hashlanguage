package hash.commontests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import hash.parsing.exceptions.ParsingException;
import hash.testutils.SimpleVmTester;

import org.junit.Test;

public class LiteralTest {

	private Object evaluate(String code) {
		return SimpleVmTester.eval(code, null);
	}

	private Object evaluate(String code, Class expectedException) {
		return SimpleVmTester.eval(code, null, expectedException);
	}

	@Test
	public void integerNumbers() {
		assertEquals(25, evaluate("25"));
		assertEquals(534342344334534322l, evaluate("534342344334534322"));
	}

	@Test
	public void floatNumbers() {
		assertEquals(10.5, evaluate("10.5"));
		assertEquals(3.0e101, evaluate("30e100"));
		assertTrue(Double.isInfinite((Double) evaluate("30e1000")));
	}

	@Test
	public void booleans() {
		assertTrue((Boolean) evaluate("true"));
		assertFalse((Boolean) evaluate("false"));
	}

	@Test
	public void strings() {
		assertEquals(" SQ 	string\n ", evaluate("' SQ \\tstring\\n '"));
		assertEquals(" DQ string ", evaluate("\" DQ string \""));
	}

	@Test
	public void hereDoc() {
		assertEquals("  Testing\n  Here\nDoc\n  String\n  Indentation\n  ",
				evaluate("  <<|EOF\n  Testing\n  Here\nDoc\n  String\n"
						+ "  Indentation\n  EOF"));
	}

	@Test
	public void indentedHereDoc() {
		assertEquals("Testing\nHere\nc\nString\n  Indentation\n",
				evaluate("  <<]EOF\n  Testing\n  Here\nDoc\n  String\n"
						+ "    Indentation\n  EOF"));
	}

	public void unfinishedHereDoc() {
		evaluate("  <<|EOF\n  Testing\n  Here\nDoc\n  String\n"
				+ "  Indentation\n  ", ParsingException.class);
	}

	public void unfinishedIndentedHereDoc() {
		evaluate("  <<] EOF\n  Testing\n  Here\nDoc\n  String\n"
				+ "    Indentation\n  OF", ParsingException.class);
	}
}
