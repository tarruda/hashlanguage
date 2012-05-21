package hash.vm;

import java.util.ArrayList;
import java.util.List;

public class ConstructorGenerator {

	private List<Statement> statements = new ArrayList<Statement>();
	private Class[] parameterTypes;

	public Class[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class... parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public void addStatement(Statement statement) {
		statements.add(statement);
	}

	public Statement get(int i) {
		return statements.get(i);
	}

	public Iterable<Statement> getStatements() {
		return statements;
	}
}
