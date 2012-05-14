package hash.evaluation;

import hash.testutils.Evaluator;
import hash.testutils.FunctionTest;

public class FunctionEvaluationTest extends FunctionTest {

	@Override
	protected Object evaluate(String code) {
		return Evaluator.eval(code, context);
	}
}
