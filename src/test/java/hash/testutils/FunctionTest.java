package hash.testutils;

import static org.junit.Assert.assertEquals;
import hash.lang.Scope;
import hash.parsing.exceptions.TreeWalkException;
import hash.runtime.Factory;

import org.junit.Before;
import org.junit.Test;

public abstract class FunctionTest {

	protected Scope context;

	protected abstract Object evaluate(String expression);

	@Before
	public void setup() {
		context = Factory.createExecutionScope();
	}

	@Test
	public void simpleFunctionReturn() {
		evaluate("f = () { return 15.5 }");
		assertEquals(15.5f, evaluate("f()"));
	}
	
	@Test(expected=TreeWalkException.class)
	public void returnOutsideFunction() {
		evaluate("return null");		
	}
}
