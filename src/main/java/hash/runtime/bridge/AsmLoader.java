package hash.runtime.bridge;

public class AsmLoader extends ClassLoader {
	public static final AsmLoader instance;

	static {
		instance = new AsmLoader();
	}

	private AsmLoader() {

	}

	public Class<?> defineClass(String name, byte[] classData) {
		return defineClass(null, classData, 0, classData.length);
	}
}
