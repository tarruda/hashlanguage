package hash.basetests;

import static org.junit.Assert.assertEquals;
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
		evaluate("f = () { return 15.5 }");
		assertEquals(15.5f, evaluate("f()"));
	}
	
}
