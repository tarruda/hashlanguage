package hash.basetests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import hash.lang.Context;
import hash.runtime.Factory;

import org.junit.Before;
import org.junit.Test;

public abstract class FunctionTest extends AbstractCodeTest {

	protected Context context;

	@Before
	public void setup() {
		context = Factory.createContext();
	}

	@Test
	public void simpleFunctionReturn() {
		evaluate("f=()\n{\ng=()\n{;return 15.5;}\nreturn g()}");
		assertEquals(15.5, evaluate("f()"));
	}

	@Test
	public void explicitlyAccessingOuterScopes() {
		evaluate("function \n\n outer\n() { \nfunction\n inner\n()"
				+ " { @@y=10 }\n @x=5\n " + "return inner}\n i = outer()");
		assertEquals(5, context.get("x"));
		assertFalse(context.containsKey("y"));
		evaluate("i()");
		assertEquals(10, context.get("y"));
	}

	@Test
	public void yieldingValues() {
		evaluate("function gen() { x=yield 1; y=yield x +3; yield y }; g=gen()");
		assertEquals(1, evaluate("resume g 10"));
		assertEquals(13, evaluate("resume g 5"));
		assertEquals(5, evaluate("resume g"));
		evaluate("resume g", IllegalStateException.class);
	}

	@Test
	public void sequenceGenerator() {
		evaluate("gen=() { for (i=1;i<=5;i++)yield i }; g=gen()");
		assertEquals(1, evaluate("resume g"));
		assertEquals(2, evaluate("resume g"));
		assertEquals(3, evaluate("resume g"));
		assertEquals(4, evaluate("resume g"));
		assertEquals(5, evaluate("resume g"));
		assertEquals(false, evaluate("g.isAlive()"));
		evaluate("resume g", IllegalStateException.class);
	}

}
