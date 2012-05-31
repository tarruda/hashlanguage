package org.hashlang.runtime;

import java.util.Map;

public interface Context extends Map {
	
	Context getParent();

	Object restore();

	void save(Object value);	
}
