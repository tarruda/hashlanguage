package hash.parsing.tree;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTreeAdaptor;

public class CommonHashAdaptor extends CommonTreeAdaptor {
	public Object create(Token token) {
        return new CommonHashNode(token);
    }
}
