package hash.basetests;

public abstract class AbstractCodeTest {
	protected abstract Object evaluate(String expression);

	protected abstract Object evaluate(String expression,
			Class expectedException);
}
