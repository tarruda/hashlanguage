package hash;

import hash.testutils.Evaluator;
import hash.testutils.PrimaryExpressionTest;

public class PrimaryExpressionEvaluationTest extends PrimaryExpressionTest {

	@Override
	protected Object evaluate(String code) {
		return Evaluator.eval(code, context);
	}
}
