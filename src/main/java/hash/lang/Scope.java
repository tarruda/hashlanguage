package hash.lang;

import java.util.Map;

public interface Scope extends Map {
	Scope getParent();
}
