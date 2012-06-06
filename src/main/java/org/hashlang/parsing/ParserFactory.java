package org.hashlang.parsing;

import java.io.IOException;
import java.io.InputStream;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenSource;
import org.antlr.runtime.UnbufferedTokenStream;
import org.hashlang.util.Err;

public class ParserFactory {

    public static TokenSource createReplLexer(CharStream input) {
        return new LinefeedFilter(new UnbufferedTokenStream(
                new ConcreteHashLexer(input)), true);
    }

    public static TokenSource createLexer(CharStream input) {
        return new LinefeedFilter(new UnbufferedTokenStream(
                new ConcreteHashLexer(input)), false);
    }

    public static HashParser createParser(TokenSource source) {
        return new ConcreteHashParser(new CommonTokenStream(source));
    }

    public static HashParser createParser(CharStream input) {
        return createParser(createLexer(input));
    }

    public static HashParser createParser(String input) {
        return createParser(new ANTLRStringStream(input));
    }

    public static HashParser createParser(InputStream input) {
        try {
            return createParser(new ANTLRInputStream(input));
        } catch (IOException e) {
            throw Err.ex(e);
        }
    }
}
