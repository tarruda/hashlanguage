package hash.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Factory {

	public static Map createMap() {
		return new HashMap();
	}

	public static List createList() {
		return new ArrayList();
	}
}
