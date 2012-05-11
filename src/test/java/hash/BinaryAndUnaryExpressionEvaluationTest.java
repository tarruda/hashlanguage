package hash;

import hash.testutils.BinaryAndUnaryExpressionTest;
import hash.testutils.Evaluator;

public class BinaryAndUnaryExpressionEvaluationTest extends
		BinaryAndUnaryExpressionTest {

	@Override
	protected Object evaluate(String code) {
		return Evaluator.eval(code, context);
	}

}
