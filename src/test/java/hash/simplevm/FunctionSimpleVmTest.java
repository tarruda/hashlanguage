package hash.simplevm;

import hash.basetests.FunctionTest;
import hash.testutils.SimpleVmTester;

public class FunctionSimpleVmTest extends FunctionTest {

	@Override
	protected Object evaluate(String code) {
		return SimpleVmTester.eval(testRuntime, code, context);
	}

	@Override
	protected Object evaluate(String code, Class expectedException) {
		return SimpleVmTester.eval(testRuntime, code, context, expectedException);
	}
}
