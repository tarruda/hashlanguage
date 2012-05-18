package hash.basetests;

import static org.junit.Assert.assertEquals;
import hash.lang.Context;
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
		evaluate("function\n f1\n  (n){return n}");
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

}
