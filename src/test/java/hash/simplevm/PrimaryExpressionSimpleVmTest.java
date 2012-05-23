package hash.simplevm;

import hash.basetests.PrimaryExpressionTest;
import hash.testutils.SimpleVmTester;

public class PrimaryExpressionSimpleVmTest extends PrimaryExpressionTest {

	@Override
	protected Object evaluate(String code) {
		return SimpleVmTester.eval(code, context);
	}
	
	@Override
	protected Object evaluate(String code, Class expectedException) {
		return SimpleVmTester.eval(code, context, expectedException);
	}
}
