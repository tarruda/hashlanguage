package hash.runtime;

public class AdapterLoader extends ClassLoader {
	public static final AdapterLoader instance;

	static {
		instance = new AdapterLoader();
	}

	private AdapterLoader() {

	}

	public Class<?> defineClass(String name, byte[] classData) {
		return defineClass(null, classData, 0, classData.length);
	}
}
