package hash.evaluationtests;

import hash.basetests.StatementTest;
import hash.testutils.Evaluator;

public class StatementEvaluationTest extends StatementTest {

	@Override
	protected Object evaluate(String code) {
		return Evaluator.eval(code, context);
	}
}
