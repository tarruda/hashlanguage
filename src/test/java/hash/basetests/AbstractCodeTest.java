package hash.basetests;

import hash.runtime.AppRuntime;
import hash.runtime.Factory;
import hash.runtime.Module;
import hash.util.Constants;

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
