package org.hashlang.parsing.tree;


import java.util.List;

import org.antlr.runtime.tree.Tree;
import org.hashlang.util.Err;

/**
 * Provides default implementation for the 'Tree' interface
 * 
 * @author Thiago de Arruda
 * 
 */
public class EmptyNode implements HashNode {

	public Tree getChild(int i) {
		throw Err.notImplemented();
	}

	public int getChildCount() {
		throw Err.notImplemented();
	}

	public Tree getParent() {
		throw Err.notImplemented();
	}

	public void setParent(Tree t) {
		throw Err.notImplemented();
	}

	public boolean hasAncestor(int ttype) {
		throw Err.notImplemented();
	}

	public Tree getAncestor(int ttype) {
		throw Err.notImplemented();
	}

	public List getAncestors() {
		throw Err.notImplemented();
	}

	public int getChildIndex() {
		throw Err.notImplemented();
	}

	public void setChildIndex(int index) {
		throw Err.notImplemented();
	}

	public void freshenParentAndChildIndexes() {
		throw Err.notImplemented();
	}

	public void addChild(Tree t) {
		throw Err.notImplemented();
	}

	public void setChild(int i, Tree t) {
		throw Err.notImplemented();
	}

	public Object deleteChild(int i) {
		throw Err.notImplemented();
	}

	public void replaceChildren(int startChildIndex, int stopChildIndex,
			Object t) {
		throw Err.notImplemented();
	}

	public boolean isNil() {
		throw Err.notImplemented();
	}

	public int getTokenStartIndex() {
		throw Err.notImplemented();
	}

	public void setTokenStartIndex(int index) {
		throw Err.notImplemented();
	}

	public int getTokenStopIndex() {
		throw Err.notImplemented();
	}

	public void setTokenStopIndex(int index) {
		throw Err.notImplemented();
	}

	public Tree dupNode() {
		throw Err.notImplemented();
	}

	public int getType() {
		throw Err.notImplemented();
	}

	public String getText() {
		throw Err.notImplemented();
	}

	public int getLine() {
		throw Err.notImplemented();
	}

	public int getCharPositionInLine() {
		throw Err.notImplemented();
	}

	public String toStringTree() {
		throw Err.notImplemented();
	}

	public Object getNodeData(Object key) {
		throw Err.notImplemented();
	}

	public void setNodeData(Object key, Object value) {
		throw Err.notImplemented();
	}

	public void setNodeData(Object value) {
		throw Err.notImplemented();
	}

	public Object getNodeData() {
		return null;
	}

	public void setText(String text) {
		throw Err.notImplemented();
	}

	public boolean contains(Object key) {
		throw Err.notImplemented();
	}

	public void insertChild(int i, Object t) {
		throw Err.notImplemented();
	}
}
