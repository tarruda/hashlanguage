package hash.parsing.walkers.evaluators;

import java.util.List;

import org.antlr.runtime.tree.Tree;

/**
 * Provides default implementation for the 'Tree' interface 
 * @author Thiago de Arruda
 *
 */
public class EmptyNode implements Tree {

	public Tree getChild(int i) {
		return null;
	}

	public int getChildCount() {
		return 0;
	}

	public Tree getParent() {
		return null;
	}

	public void setParent(Tree t) {
	}

	public boolean hasAncestor(int ttype) {
		return false;
	}

	public Tree getAncestor(int ttype) {
		return null;
	}

	@SuppressWarnings("rawtypes")
	public List getAncestors() {
		return null;
	}

	public int getChildIndex() {
		return 0;
	}

	public void setChildIndex(int index) {
	}

	public void freshenParentAndChildIndexes() {
	}

	public void addChild(Tree t) {
	}

	public void setChild(int i, Tree t) {
	}

	public Object deleteChild(int i) {
		return null;
	}

	public void replaceChildren(int startChildIndex, int stopChildIndex,
			Object t) {
	}

	public boolean isNil() {
		return false;
	}

	public int getTokenStartIndex() {
		return 0;
	}

	public void setTokenStartIndex(int index) {
	}

	public int getTokenStopIndex() {
		return 0;
	}

	public void setTokenStopIndex(int index) {
	}

	public Tree dupNode() {
		return null;
	}

	public int getType() {
		return 0;
	}

	public String getText() {
		return null;
	}

	public int getLine() {
		return 0;
	}

	public int getCharPositionInLine() {
		return 0;
	}

	public String toStringTree() {
		return null;
	}
}
