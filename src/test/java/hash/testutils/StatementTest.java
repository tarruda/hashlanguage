package hash.testutils;

import static org.junit.Assert.assertEquals;
import hash.lang.Scope;
import hash.runtime.Factory;

import org.junit.Before;
import org.junit.Test;

public abstract class StatementTest {

	protected Scope context;

	protected abstract Object evaluate(String expression);

	@Before
	public void setup() {
		context = Factory.createExecutionScope();
	}

	@Test
	public void importJavaClasses() {
		evaluate("import java.lang.Integer; import java.lang.StringBuilder");		
		assertEquals(10, evaluate("new Integer('10')"));
		evaluate("sb = new StringBuilder('Testing')");
		evaluate("sb.append('\\nTesting2')");
		assertEquals("Testing\nTesting2", evaluate("sb.toString()"));
	}

}
