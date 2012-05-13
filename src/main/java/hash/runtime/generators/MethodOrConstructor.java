package hash.runtime.generators;

import hash.util.Asm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.MethodVisitor;

public class MethodOrConstructor {

	private static final String[] ignoredMethodNames = { "getClass" };

	public static List<MethodOrConstructor> getDeclaredMethods(Class<?> klass) {
		ArrayList<MethodOrConstructor> rv = new ArrayList<MethodOrConstructor>();
		for (Method method : klass.getDeclaredMethods()) {
			int mod = method.getModifiers();
			if (Modifier.isAbstract(mod) || Modifier.isPrivate(mod)
					|| Modifier.isProtected(mod) || isIgnored(method))
				continue;
			rv.add(new MethodOrConstructor(method));
		}
		return rv;
	}

	public static List<MethodOrConstructor> getDeclaredConstructors(
			Class<?> klass) {
		ArrayList<MethodOrConstructor> rv = new ArrayList<MethodOrConstructor>();
		for (Constructor constructor : klass.getDeclaredConstructors()) {
			int mod = constructor.getModifiers();
			if (Modifier.isPublic(mod))
				rv.add(new MethodOrConstructor(constructor));
		}
		return rv;
	}

	private static boolean isIgnored(Method method) {
		for (String mName : ignoredMethodNames)
			if (mName.equals(method.getName()))
				return true;
		return false;
	}

	private Method method;
	private Constructor constructor;

	private MethodOrConstructor(Method method) {
		this.method = method;
	}

	private MethodOrConstructor(Constructor constructor) {
		this.constructor = constructor;
	}

	public Class<?> getDeclaringClass() {
		if (method == null)
			return constructor.getDeclaringClass();
		return method.getDeclaringClass();
	}

	public Class<?> getReturnType() {
		if (method == null)
			return constructor.getDeclaringClass();
		return method.getReturnType();
	}

	public Class<?>[] getParameterTypes() {
		if (method == null)
			return constructor.getParameterTypes();
		return method.getParameterTypes();
	}

	public void implementInvocation(MethodVisitor mv) {
		if (method == null)
			Asm.invokeInit(mv, constructor);
		else if (!Modifier.isStatic(method.getModifiers()))
			Asm.invokeVirtual(mv, method);
		else
			Asm.invokeStatic(mv, method);
	}

	public String getName() {
		return method.getName();
	}

	public boolean isConstructor() {
		return method == null;
	}

	public boolean isStatic() {
		return method != null && Modifier.isStatic(method.getModifiers());
	}

}
