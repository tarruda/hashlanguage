package hash.simplevm;

import hash.basetests.BinaryAndUnaryExpressionTest;
import hash.testutils.SimpleVmTester;

public class BinaryAndUnaryExpressionSimpleVmTest extends
		BinaryAndUnaryExpressionTest {

	@Override
	protected Object evaluate(String code) {
		return SimpleVmTester.eval(testRuntime, code, context);
	}

	@Override
	protected Object evaluate(String code, Class expectedException) {
		return SimpleVmTester.eval(testRuntime, code, context, expectedException);
	}

}
