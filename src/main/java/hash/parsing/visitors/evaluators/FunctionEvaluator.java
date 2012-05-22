package hash.parsing.visitors.evaluators;

import hash.lang.Context;
import hash.lang.Function;
import hash.parsing.tree.HashNode;
import hash.runtime.Factory;
import hash.util.Check;
import hash.util.Constants;
import hash.util.Err;

import java.util.List;

import org.antlr.runtime.tree.Tree;

public class FunctionEvaluator implements Function {

	private Context definingContext;
	private List parameters;
	private Tree block;
	private boolean isMethod;

	public FunctionEvaluator(Context definingContext, List parameters,
			Tree block, boolean isMethod) {
		this.definingContext = definingContext;
		this.parameters = parameters;
		this.block = block;
		this.isMethod = isMethod;
	}

	public Object invoke(Object... args) {
		Check.numberOfArgs(args, parameters.size() + 1);
		Context context = Factory.createContext(definingContext);
		Object self = args[0];
		if (self == null && isMethod)
			throw Err.functionIsMethod();
		context.put(Constants.THIS, self);
		for (int i = 0; i < parameters.size(); i++)
			context.put(parameters.get(i), args[i + 1]);
		ProgramEvaluator walker = new ProgramEvaluator(context);
		Tree result = walker.visit(block);
		if (result instanceof ReturnStatement)
			return ((HashNode) result).getNodeData();
		else
			return null;
		// HashNode node = (HashNode) block;
		// int len = node.getChildCount();
		// for (int i = 0; i < len &&
		// !node.contains(ProgramEvaluator.RETURN_KEY); i++)
		// walker.visit(node.getChild(i));
		// return node.getNodeData(ProgramEvaluator.RETURN_KEY);
	}

}
