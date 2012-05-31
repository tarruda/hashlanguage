package org.hashlang.basetests;


import org.hashlang.runtime.AppRuntime;
import org.hashlang.runtime.Factory;
import org.hashlang.runtime.Module;
import org.hashlang.util.Constants;
import org.junit.Before;

public abstract class AbstractCodeTest {

	public static final AppRuntime testRuntime;

	static {
		testRuntime = new AppRuntime();
	}

	protected Module context;

	protected abstract Object evaluate(String expression);

	protected abstract Object evaluate(String expression,
			Class expectedException);

	@Before
	public void setup() {
		context = Factory.createModule();
		context.put(Constants.CLASS, testRuntime.getClassHandler());
		context.put(Constants.IMPORT, testRuntime.getImportHandler());
	}
}
