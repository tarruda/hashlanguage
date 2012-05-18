package hash.basetests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import hash.lang.Context;
import hash.runtime.Factory;

import org.junit.Before;
import org.junit.Test;

public abstract class FunctionTest {

	protected Context context;

	protected abstract Object evaluate(String expression);

	@Before
	public void setup() {
		context = Factory.createContext();
	}

	@Test
	public void simpleFunctionReturn() {
		evaluate("f=(){g=(){return 15.5}\nreturn g()}");
		assertEquals(15.5, evaluate("f()"));
	}


	@Test
	public void explicitlyAccessingOuterScopes() {
		evaluate("function outer() { function inner() { @@y=10 }\n @x=5\n "
				+ "return inner}\n i = outer()");
		assertEquals(5, context.get("x"));
		assertFalse(context.containsKey("y"));
		evaluate("i()");
		assertEquals(10, context.get("y"));
	}

}
