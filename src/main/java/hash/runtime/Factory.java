package hash.runtime;

import hash.lang.HashScope;
import hash.lang.Scope;
import hash.runtime.functions.Import;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Factory {

	public static HashObject createObject() {
		return new HashObject();
	}

	public static Map createMap() {
		return new HashMap();
	}

	public static List createList() {
		return new ArrayList();
	}

	public static List createList(int initialCapacity) {
		return new ArrayList(initialCapacity);
	}

	public static Scope createExecutionScope() {
		HashScope rv = new HashScope();
		rv.installBuiltin(new Import());
		return rv;
	}

	public static Scope createExecutionScope(Scope parent) {
		return new HashScope(parent);
	}
}
