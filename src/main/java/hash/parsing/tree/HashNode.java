package hash.parsing.tree;


import org.antlr.runtime.tree.Tree;

public interface HashNode extends Tree {

	public static final String IS_METHOD = "isMethod";
	
	Object getNodeData(Object key);

	void setNodeData(Object key, Object value);

	void setNodeData(Object value);

	Object getNodeData();
}
