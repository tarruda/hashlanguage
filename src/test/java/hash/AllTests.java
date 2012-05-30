package hash;

import hash.commontests.AstValidatorTest;
import hash.commontests.LiteralTest;
import hash.simplevm.ExpressionSimpleVmTest;
import hash.simplevm.FunctionSimpleVmTest;
import hash.simplevm.PrimaryExpressionSimpleVmTest;
import hash.simplevm.StatementSimpleVmTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ExpressionSimpleVmTest.class, LiteralTest.class,
		StatementSimpleVmTest.class, PrimaryExpressionSimpleVmTest.class,
		AstValidatorTest.class, FunctionSimpleVmTest.class })
public class AllTests {

}
