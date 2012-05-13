package hash.runtime;

import java.util.HashMap;

public class HashObject extends HashMap {

	private static final long serialVersionUID = -6256302598579545880L;
	private HashObject isa;

	public HashObject getIsa() {
		return isa;
	}

	public void setIsa(HashObject value) {
		isa = value;
	}
}
