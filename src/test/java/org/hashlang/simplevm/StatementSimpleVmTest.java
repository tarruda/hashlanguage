package org.hashlang.simplevm;

import org.hashlang.basetests.StatementTest;
import org.hashlang.testutils.SimpleVmTester;

public class StatementSimpleVmTest extends StatementTest {

	@Override
	protected Object evaluate(String code) {
		return SimpleVmTester.eval(testRuntime, code, context);
	}

	@Override
	protected Object evaluate(String code, Class expectedException) {
		return SimpleVmTester.eval(testRuntime, code, context, expectedException);
	}
}
