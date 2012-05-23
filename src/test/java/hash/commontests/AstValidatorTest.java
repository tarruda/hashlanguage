package hash.commontests;

import static org.junit.Assert.fail;
import hash.parsing.HashParser;
import hash.parsing.HashParser.program_return;
import hash.parsing.ParserFactory;
import hash.parsing.exceptions.TreeValidationException;
import hash.parsing.tree.HashNode;
import hash.parsing.visitors.AstValidator;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;
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
		HashParser parser = ParserFactory.createParser(source);
		program_return psrReturn = null;
		try {
			psrReturn = parser.program();
			HashNode t = (HashNode) psrReturn.getTree();
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
