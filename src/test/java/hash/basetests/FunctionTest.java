package hash.basetests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import hash.runtime.Context;
import hash.runtime.Factory;

import java.util.List;

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
		assertEquals(1, evaluate("g.resume(10)"));
		assertEquals(13, evaluate("g.resume(5)"));
		assertEquals(5, evaluate("g.resume()"));
		evaluate("g.resume()", IllegalStateException.class);
	}

	@Test
	public void sequenceGenerator() {
		evaluate("gen=() { for (i=1;i<=5;i++)yield i }; g=gen()");
		assertEquals(1, evaluate("g.resume()"));
		assertEquals(2, evaluate("g.resume()"));
		assertEquals(3, evaluate("g.resume()"));
		assertEquals(4, evaluate("g.resume()"));
		assertEquals(5, evaluate("g.resume()"));
		assertEquals(false, evaluate("g.isAlive()"));
		evaluate("g.resume()", IllegalStateException.class);
	}

	@Test
	public void iterateContinuation() {
		evaluate("gen=() { for (i=1;i<=5;i++)yield i };z=0;for(e in gen())z+=e");
		assertEquals(15, context.get("z"));
	}

	@Test
	public void coroutines() {
		evaluate("import java.util.LinkedList;l=[];q=new LinkedList()");
		evaluate("p=(){i=0;while(true){while(q.size()<20)q.add(i++);"
				+ "jumpto consumer}}");
		evaluate("c=(){while(l.size()<1000){while(q.size()>0)l.add(-q.remove());"
				+ "jumpto producer}}");
		evaluate("producer=p();consumer=c();producer.resume()");
		List l = (List) context.get("l");
		assertEquals(1000, l.size());
		assertEquals(0, l.get(0));
		assertEquals(-1, l.get(1));
		assertEquals(-2, l.get(2));
		assertEquals(-999, l.get(999));
	}

}
