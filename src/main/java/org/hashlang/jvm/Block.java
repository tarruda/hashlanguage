package org.hashlang.jvm;

import java.util.ArrayList;
import java.util.List;

public class Block extends Statement {
	private List<Statement> statements = new ArrayList<Statement>();

	public void addStatement(Statement statement) {
		this.statements.add(statement);
	}

	public Iterable<Statement> getStatements() {
		return statements;
	}
}
