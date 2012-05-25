package hash.runtime;

import hash.runtime.functions.BuiltinFunction;

import java.util.Map;

public interface Context extends Map {
	Context getParent();
	
	void installBuiltin(BuiltinFunction f);
	
	Object getLastEvaluationResult();
	
	void setLastEvaluationResult(Object value);
}
