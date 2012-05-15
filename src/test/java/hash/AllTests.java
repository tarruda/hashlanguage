package hash;

import hash.commontests.AstValidatorTest;
import hash.commontests.LiteralTest;
import hash.evaluationtests.BinaryAndUnaryExpressionEvaluationTest;
import hash.evaluationtests.FunctionEvaluationTest;
import hash.evaluationtests.PrimaryExpressionEvaluationTest;
import hash.evaluationtests.StatementEvaluationTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BinaryAndUnaryExpressionEvaluationTest.class,
		LiteralTest.class, PrimaryExpressionEvaluationTest.class,
		StatementEvaluationTest.class, FunctionEvaluationTest.class,
		AstValidatorTest.class })
public class AllTests {

}
