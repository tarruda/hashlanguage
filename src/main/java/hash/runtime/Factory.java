package hash.runtime;

import hash.lang.Context;
import hash.lang.HashContext;
import hash.runtime.functions.ClassFactory;
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

	public static Context createContext() {
		HashContext rv = new HashContext();
		rv.installBuiltin(new Import());
		rv.installBuiltin(new ClassFactory());
		return rv;
	}

	public static Context createContext(Context parent) {
		return new HashContext(parent);
	}
}
