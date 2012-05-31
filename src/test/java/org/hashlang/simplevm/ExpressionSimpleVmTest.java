package org.hashlang.simplevm;

import org.hashlang.basetests.ExpressionTest;
import org.hashlang.testutils.SimpleVmTester;

public class ExpressionSimpleVmTest extends
		ExpressionTest {

	@Override
	protected Object evaluate(String code) {
		return SimpleVmTester.eval(testRuntime, code, context);
	}

	@Override
	protected Object evaluate(String code, Class expectedException) {
		return SimpleVmTester.eval(testRuntime, code, context, expectedException);
	}

}
