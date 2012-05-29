package hash.runtime;

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

	public static Module createModule() {
		return new HashModule();
	}

	public static Context createContext() {
		return new HashContext();
	}

	public static Context createContext(Context parent) {
		HashContext rv = new HashContext();
		rv.setParent(parent);
		return rv;
	}
}
