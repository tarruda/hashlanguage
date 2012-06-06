package org.hashlang.runtime;

import java.util.Map;

public interface Context extends Map {
	
    void setParent(Context context);
    
	Context getParent();

	Object restore();

	void save(Object value);	
}
