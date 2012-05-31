package org.hashlang.parsing.tree;

import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

public class CommonHashNode extends CommonTree implements HashNode {

	private Map nodeInfo;
	private Object data;
	private String text;

	public CommonHashNode(CommonHashNode node) {
		super(node);
	}

	public CommonHashNode(Token t) {
		super(t);
	}

	public CommonHashNode(int type) {
		super(new CommonToken(type));
	}
	
	public CommonHashNode(int type, String text) {
		super(new CommonToken(type));
		setText(text);
	}


	public Object getNodeData(Object key) {
		if (nodeInfo == null)
			return null;
		return nodeInfo.get(key);
	}

	public void setNodeData(Object key, Object value) {
		if (nodeInfo == null)
			nodeInfo = new HashMap();
		nodeInfo.put(key, value);
	}

	public void setNodeData(Object value) {
		data = value;
	}

	public Object getNodeData() {
		return data;
	}

	public Tree dupNode() {
		CommonHashNode rv = new CommonHashNode(this);
		if (nodeInfo != null)
			for (Object key : nodeInfo.keySet())
				rv.setNodeData(key, nodeInfo.get(key));
		rv.setNodeData(data);
		return rv;
	}

	@Override
	public String getText() {
		if (text != null)
			return text;
		return super.getText();
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean contains(Object key) {
		return nodeInfo != null && nodeInfo.containsKey(key);
	}
}
