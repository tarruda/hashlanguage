package hash.parsing.visitors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import hash.parsing.HashLexer;
import hash.parsing.HashParser;
import hash.parsing.HashParser.expression_return;
import hash.parsing.visitors.Evaluator;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;
import org.junit.Before;
import org.junit.Test;

public class EvaluatorTest {

	private Evaluator target;

	@Before
	public void setup() {
		target = new Evaluator();
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
			return target.evaluate(t);
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
