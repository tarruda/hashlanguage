package hash.simplevm;

import hash.basetests.StatementTest;
import hash.testutils.SimpleVmTester;

public class StatementSimpleVmTest extends StatementTest {

	@Override
	protected Object evaluate(String code) {
		return SimpleVmTester.eval(code, context);
	}
	
	@Override
	protected Object evaluate(String code, Class expectedException) {
		return SimpleVmTester.eval(code, context, expectedException);
	}
}
