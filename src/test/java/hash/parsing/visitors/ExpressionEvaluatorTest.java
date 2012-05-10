package hash.parsing.visitors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import hash.parsing.HashLexer;
import hash.parsing.HashParser;
import hash.parsing.HashParser.expression_return;
import hash.parsing.visitors.ExpressionEvaluator;
import hash.parsing.visitors.nodes.Result;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;
import org.junit.Before;
import org.junit.Test;

public class ExpressionEvaluatorTest {

	private ExpressionEvaluator target;

	@Before
	public void setup() {
		target = new ExpressionEvaluator();
	}

	private Object evaluate(String code) {
		ANTLRStringStream source = new ANTLRStringStream(code);
		HashLexer lexer = new HashLexer(source);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		HashParser parser = new HashParser(tokens);
		expression_return psrReturn = null;
		try {
			psrReturn = parser.expression();
			Tree t = (Tree) psrReturn.getTree();
			return ((Result) target.visit(t)).getEvaluationResult();
		} catch (RecognitionException e) {
			e.printStackTrace();
			fail(e.getMessage());
			return null;
		}
	}

	@Test
	public void arithmetic1() {
		assertEquals(25l, evaluate("10+15"));
	}

}
