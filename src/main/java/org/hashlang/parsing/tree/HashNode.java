package org.hashlang.parsing.tree;

import org.antlr.runtime.tree.Tree;

public interface HashNode extends Tree {

	public static final String RETURNS_CONTINUATION = "returnsContinuation";
	public static final String IS_METHOD = "isMethod";
	public static final String CONTEXT_LEVEL = "contextLevel";

	Object getNodeData(Object key);

	void setNodeData(Object key, Object value);

	void setNodeData(Object value);

	Object getNodeData();

	void setText(String text);

	boolean contains(Object key);
	
	void insertChild(int i, Object t);
}
