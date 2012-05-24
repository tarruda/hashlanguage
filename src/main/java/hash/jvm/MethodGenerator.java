package hash.jvm;

import java.util.ArrayList;
import java.util.List;

public class MethodGenerator {

	private String name;
	private Class returnType;
	private Class[] parameterTypes;
	private List<Statement> statements = new ArrayList<Statement>();

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Class getReturnType() {
		return returnType;
	}

	public void setReturnType(Class returnType) {
		this.returnType = returnType;
	}

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
