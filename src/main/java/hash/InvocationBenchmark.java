package hash;

import hash.lang.Function;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Simple benchmark for comparing the speed of invoking with static, hash lookup
 * and reflection
 * 
 */
public class InvocationBenchmark {
	public static void main(String[] args) throws Exception {
		doRegular(10);
		doHash(10);
		doReflectionCachedMethod(10);
		doFullReflection(10);
	}

	public static void doRegular(Object arg) throws Exception {
		Stub stub = new Stub();
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++)
			stub.factorial((Integer) arg);
		System.out.println(String.format("Static invoke : %s",
				System.currentTimeMillis() - start));
	}

	public static void doHash(Object arg) throws Exception {
		HashMap hm = createHashClass();
		Object stub = new Stub();
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++)
			((Function) hm.get("factorial")).invoke(stub, 10);
		System.out.println(String.format("Hashtable lookup invoke : %s",
				System.currentTimeMillis() - start));
	}

	public static void doReflectionCachedMethod(Object arg) throws Exception {
		Object stub = new Stub();
		Method method = stub.getClass().getMethod("factorial", arg.getClass());
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++)
			method.invoke(stub, arg);
		System.out.println(String.format("Cached method reflection : %s",
				System.currentTimeMillis() - start));
	}

	public static void doFullReflection(Object arg) throws Exception {
		Object stub = new Stub();
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++)
			stub.getClass().getMethod("factorial", arg.getClass())
					.invoke(stub, arg);
		System.out.println(String.format("Full reflection : %s",
				System.currentTimeMillis() - start));
	}

	private static class Stub {
		public int factorial(Integer n) {
			if (n == 1)
				return 1;
			return n * factorial(n - 1);
		}
	}

	private static HashMap createHashClass() {
		HashMap rv = new HashMap();
		rv.put("factorial", new Function() {
			public Object invoke(Object... args) {
				Stub self = (Stub) args[0];
				return self.factorial((Integer) args[1]);
			}
		});
		return rv;
	}
}
