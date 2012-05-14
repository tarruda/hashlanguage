package hash;

import hash.evaluation.BinaryAndUnaryExpressionEvaluationTest;
import hash.evaluation.FunctionEvaluationTest;
import hash.evaluation.PrimaryExpressionEvaluationTest;
import hash.evaluation.StatementEvaluationTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BinaryAndUnaryExpressionEvaluationTest.class,
		LiteralTest.class, PrimaryExpressionEvaluationTest.class,
		StatementEvaluationTest.class, FunctionEvaluationTest.class })
public class AllTests {

}
