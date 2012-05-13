package hash;

import hash.testutils.BuiltinFunctionsTest;
import hash.testutils.Evaluator;

public class BuiltinFunctionsEvaluationTest extends BuiltinFunctionsTest {

	@Override
	protected Object evaluate(String code) {
		return Evaluator.eval(code, context);
	}
}
