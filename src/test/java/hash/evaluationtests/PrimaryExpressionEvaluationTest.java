package hash.evaluationtests;

import hash.basetests.PrimaryExpressionTest;
import hash.testutils.Evaluator;

public class PrimaryExpressionEvaluationTest extends PrimaryExpressionTest {

	@Override
	protected Object evaluate(String code) {
		return Evaluator.eval(code, context);
	}
}
