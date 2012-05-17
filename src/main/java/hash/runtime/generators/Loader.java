package hash.runtime.generators;

public class Loader extends ClassLoader {
	public static final Loader instance;

	static {
		instance = new Loader();
	}

	private Loader() {

	}

	public Class<?> defineClass(String name, byte[] classData) {
		return defineClass(null, classData, 0, classData.length);
	}
}
