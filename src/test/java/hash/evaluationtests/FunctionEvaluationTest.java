package hash.evaluationtests;

import hash.basetests.FunctionTest;
import hash.testutils.Evaluator;

public class FunctionEvaluationTest extends FunctionTest {

	@Override
	protected Object evaluate(String code) {
		return Evaluator.eval(code, context);
	}
}
