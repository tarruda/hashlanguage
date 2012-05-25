package hash.runtime;

public interface Function {
	Object invoke(Object... args) throws Throwable;
}
