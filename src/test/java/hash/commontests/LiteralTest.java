package hash.commontests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import hash.parsing.HashParser;
import hash.parsing.HashParser.literal_return;
import hash.parsing.ParserFactory;
import hash.parsing.exceptions.ParsingException;
import hash.parsing.visitors.evaluators.ExpressionResult;
import hash.parsing.visitors.evaluators.LiteralEvaluator;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;
import org.junit.Before;
import org.junit.Test;

public class LiteralTest {

	private LiteralEvaluator target;

	@Before
	public void setup() {
		target = new LiteralEvaluator();
	}

	private Object evaluate(String code) {
		ANTLRStringStream source = new ANTLRStringStream(code);
		HashParser parser = ParserFactory.createParser(source);
		literal_return psrReturn = null;
		try {
			psrReturn = parser.literal();
			Tree t = (Tree) psrReturn.getTree();
			return ((ExpressionResult) target.visit(t)).getNodeData();
		} catch (RecognitionException e) {
			e.printStackTrace();
			fail(e.getMessage());
			return null;
		}
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

	@Test(expected = ParsingException.class)
	public void unfinishedHereDoc() {
		assertEquals("  Testing\n  Here\nDoc\n  String\n  Indentation\n  ",
				evaluate("  <<|EOF\n  Testing\n  Here\nDoc\n  String\n"
						+ "  Indentation\n  "));
	}
	
	@Test(expected = ParsingException.class)
	public void unfinishedIndentedHereDoc() {
		evaluate("  <<] EOF\n  Testing\n  Here\nDoc\n  String\n"
				+ "    Indentation\n  OF");
	}
}
