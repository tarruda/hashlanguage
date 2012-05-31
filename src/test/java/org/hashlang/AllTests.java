package org.hashlang;


import org.hashlang.commontests.AstValidatorTest;
import org.hashlang.commontests.LiteralTest;
import org.hashlang.simplevm.ExpressionSimpleVmTest;
import org.hashlang.simplevm.FunctionSimpleVmTest;
import org.hashlang.simplevm.PrimaryExpressionSimpleVmTest;
import org.hashlang.simplevm.StatementSimpleVmTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ExpressionSimpleVmTest.class, LiteralTest.class,
		StatementSimpleVmTest.class, PrimaryExpressionSimpleVmTest.class,
		AstValidatorTest.class, FunctionSimpleVmTest.class })
public class AllTests {

}
