package hash.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Factory {

	public static Map createObject() {
		return new HashMap();
	}

	public static List createList() {
		return new ArrayList();
	}

	public static Map createExecutionContext() {
		return new HashMap();
	}
}
