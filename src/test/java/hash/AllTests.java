package hash;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BinaryAndUnaryExpressionEvaluationTest.class,
		LiteralTest.class, PrimaryExpressionEvaluationTest.class })
public class AllTests {

}
