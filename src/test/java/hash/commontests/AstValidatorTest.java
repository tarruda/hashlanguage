package hash.commontests;

import static org.junit.Assert.fail;
import hash.parsing.ConcreteHashLexer;
import hash.parsing.ConcreteHashParser;
import hash.parsing.HashParser.program_return;
import hash.parsing.exceptions.TreeValidationException;
import hash.parsing.visitors.AstValidator;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;
import org.junit.Before;
import org.junit.Test;

public class AstValidatorTest {

	private AstValidator target;

	@Before
	public void setup() {
		target = new AstValidator();
	}

	private void validate(String code) {
		ANTLRStringStream source = new ANTLRStringStream(code);
		ConcreteHashLexer lexer = new ConcreteHashLexer(source);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ConcreteHashParser parser = new ConcreteHashParser(tokens);
		program_return psrReturn = null;
		try {
			psrReturn = parser.program();
			Tree t = (Tree) psrReturn.getTree();
			target.visit(t);
		} catch (RecognitionException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test(expected = TreeValidationException.class)
	public void assignmentToInvocation() {
		validate("someFunction() = 'someString'");
	}

	@Test(expected = TreeValidationException.class)
	public void assignmentToLiteral() {
		validate("5 = 'someString'");
	}

	@Test(expected = TreeValidationException.class)
	public void returnOutsideFunction() {
		validate("return null");
	}
}
