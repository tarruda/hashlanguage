package org.hashlang.runtime;

import java.util.HashMap;
import java.util.Map;

/**
 * Special map object that 'knows' its type. This is used to implement classes
 * in the Hash language. Instances of Hash-defined classes are instances of this
 * class.
 * 
 * @author Thiago de Arruda
 * 
 */
public class HashObject extends HashMap {

	private static final long serialVersionUID = -6256302598579545880L;
	private HashObject isa;

	public HashObject() {

	}

	public HashObject(Map map) {
		super(map);
	}

	public HashObject getIsa() {
		return isa;
	}

	public void setIsa(HashObject isa) {
		this.isa = isa;
	}

	@Override
	public Object clone() {
		HashObject rv = (HashObject) super.clone();
		if (this.isa != null)
			rv.setIsa((HashObject) this.isa.clone());
		return rv;
	}
}
