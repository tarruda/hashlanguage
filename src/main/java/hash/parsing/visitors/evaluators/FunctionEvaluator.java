package hash.parsing.visitors.evaluators;

import hash.lang.Function;
import hash.lang.Context;
import hash.runtime.Factory;
import hash.util.Check;
import hash.util.Constants;

import java.util.List;

import org.antlr.runtime.tree.Tree;

public class FunctionEvaluator implements Function {

	private Context definingContext;
	private List parameters;
	private Tree block;

	public FunctionEvaluator(Context definingContext, List parameters, Tree block) {
		this.definingContext = definingContext;
		this.parameters = parameters;
		this.block = block;	
	}

	public Object invoke(Object... args) {
		Check.numberOfArgs(args, parameters.size() + 1);
		Context context = Factory.createContext(definingContext);
		Object self = args[0];
		if (self != null)
			context.put(Constants.THIS, self);
		for (int i = 0; i < parameters.size(); i++)
			context.put(parameters.get(i), args[i + 1]);
		ProgramEvaluator walker = new ProgramEvaluator(context);
		try {
			walker.visit(block);
		} catch (ReturnStatement r) {
			return r.getValue();
		}
		return null;
	}

}
