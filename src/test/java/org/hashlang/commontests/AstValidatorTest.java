package org.hashlang.commontests;

import static org.junit.Assert.fail;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;
import org.hashlang.parsing.HashParser;
import org.hashlang.parsing.HashParser.program_return;
import org.hashlang.parsing.ParserFactory;
import org.hashlang.parsing.exceptions.TreeValidationException;
import org.hashlang.parsing.tree.HashNode;
import org.hashlang.parsing.visitors.AstValidator;
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
