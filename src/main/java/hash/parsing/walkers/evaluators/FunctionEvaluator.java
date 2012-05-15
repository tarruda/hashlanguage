package hash.parsing.walkers.evaluators;

import hash.lang.Function;
import hash.lang.Scope;
import hash.runtime.Factory;
import hash.util.Check;
import hash.util.Constants;

import java.util.List;

import org.antlr.runtime.tree.Tree;

public class FunctionEvaluator implements Function {

	private Scope parentScope;
	private List parameters;
	private Tree block;

	public FunctionEvaluator(Scope parentScope, List parameters, Tree block) {
		this.parentScope = parentScope;
		this.parameters = parameters;
		this.block = block;	
	}

	public Object invoke(Object... args) {
		Check.numberOfArgs(args, parameters.size() + 1);
		Scope scope = Factory.createExecutionScope(parentScope);
		Object self = args[0];
		if (self != null)
			scope.put(Constants.THIS, self);
		for (int i = 0; i < parameters.size(); i++)
			scope.put(parameters.get(i), args[i + 1]);
		ProgramEvaluator walker = new ProgramEvaluator(scope);
		try {
			walker.visit(block);
		} catch (ReturnStatement r) {
			return r.getValue();
		}
		return null;
	}

}
