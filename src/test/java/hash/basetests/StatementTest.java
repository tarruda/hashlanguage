package hash.basetests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import hash.lang.Context;
import hash.lang.Function;
import hash.runtime.Factory;

import org.junit.Before;
import org.junit.Test;

public abstract class StatementTest {

	protected Context context;

	protected abstract Object evaluate(String expression);

	@Before
	public void setup() {
		context = Factory.createContext();
	}

	@Test
	public void functionStatement() {
		evaluate("function\n f1\n  (\nn){return n}");
		assertEquals("name", evaluate("f1('name')"));
		assertEquals(2147483648l, evaluate("f1(1<<31)"));
	}

	@Test
	public void importJavaClasses() {
		evaluate("import java.lang.Integer; import java.lang.StringBuilder");
		assertEquals(10, evaluate("new Integer('10')"));
		evaluate("sb = new StringBuilder('Testing')");
		evaluate("sb.append('\\nTesting2')");
		assertEquals("Testing\nTesting2", evaluate("sb.toString()"));
	}

	@Test
	public void classStatement() {
		evaluate("class \n\nAccount {balance:0,add:(amount){this.balance+=1.0*amount},"
				+ "remove:(amount){this.balance-=amount}}");
		evaluate("class BonusAccount extends Account{balance:1,"
				+ "add:(amount){this.balance+=1.05*amount}}");
		evaluate("acc = new Account();acc.add(100)");
		evaluate("bonusAcc = new BonusAccount();bonusAcc.add(100)");
		assertEquals(100f, evaluate("acc.balance"));
		assertEquals(106f, evaluate("bonusAcc.balance"));
	}

	@Test
	public void tryCatchFinally() {
		String message = "TryCatchFinally";
		final RuntimeException ex = new RuntimeException(message);
		context.put("throwingAction", new Function() {
			public Object invoke(Object... args) {
				throw ex;
			}
		});
		evaluate("try {\n  throwingAction()\n} catch(ex) {\n  x = ex\n"
				+ "y=ex.getMessage()} finally {\n  z = 'abc'\n}");
		assertTrue(ex == context.get("x"));
		assertEquals(message, context.get("y"));
		assertEquals("abc", context.get("z"));
	}

	@Test
	public void tryFinally() {
		String message = "TryCatchFinally";
		final RuntimeException ex = new RuntimeException(message);
		context.put("throwingAction", new Function() {
			public Object invoke(Object... args) {
				throw ex;
			}
		});
		try {
			evaluate("try {throwingAction()}  \n\nfinally \n"
					+ "{\ny = 'finally'\n}");
			fail("Should have thrown");
		} catch (Exception e) {
			assertEquals("finally", context.get("y"));
		}
	}

	@Test
	public void tryCatchRightExceptionType() {
		String message = "TryCatchFinally";
		final UnsupportedOperationException ex = new UnsupportedOperationException(
				message);
		context.put("throwingAction", new Function() {
			public Object invoke(Object... args) {
				throw ex;
			}
		});
		evaluate("import java.lang.UnsupportedOperationException");

		evaluate("try {throwingAction()} catch(UnsupportedOperationException e){a=1}"
				+ "catch(e){b=2}");
		assertEquals(1, context.get("a"));
		assertFalse(context.containsKey("b"));
	}

	@Test
	public void tryCatchWrongExceptionType() {
		String message = "TryCatchFinally";
		final UnsupportedOperationException ex = new UnsupportedOperationException(
				message);
		context.put("throwingAction", new Function() {
			public Object invoke(Object... args) {
				throw ex;
			}
		});
		evaluate("import java.lang.IllegalArgumentException");
		evaluate("try {throwingAction()} catch(IllegalArgumentException e){a=1}"
				+ "catch(e){b=2}");
		assertFalse(context.containsKey("a"));
		assertEquals(2, context.get("b"));
	}

	@Test
	public void throwing() {
		try {
			evaluate("throw 'SomeString'");
			fail("should have thrown");
		} catch (RuntimeException e) {
			assertEquals("SomeString", e.getMessage());
		}
	}

	@Test
	public void ifs() {
		evaluate("f=(x){if(x>10)return x else if(x>5) return x+5 else return x+10}");
		assertEquals(11, evaluate("f(11)"));
		assertEquals(15, evaluate("f(10)"));
		assertEquals(14, evaluate("f(9)"));
		assertEquals(15, evaluate("f(5)"));
	}
}
