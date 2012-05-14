package hash.evaluation;

import hash.testutils.Evaluator;
import hash.testutils.StatementTest;

public class StatementEvaluationTest extends StatementTest {

	@Override
	protected Object evaluate(String code) {
		return Evaluator.eval(code, context);
	}
}
