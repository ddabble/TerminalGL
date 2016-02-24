package party.dabble.terminalGL;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Mod {
	public String name;
	public String version;
	private Class<?> clazz;
	private Method preInit;
	private Method main;

	public Mod(Class<?> clazz) {
		this.clazz = clazz;
		ModMarker annotation = clazz.getAnnotation(ModMarker.class);
		this.name = annotation.name();
		String version = annotation.version().trim();
		this.version = (version.isEmpty()) ? ""
				: ((version.charAt(0) == 'v') ? " " + version : " v" + version);

		try {
			Method preInit = clazz.getMethod("preInit");

			if (Modifier.isStatic(preInit.getModifiers()))
				this.preInit = preInit;
			else
				System.err.println("The method preInit in " + clazz + " is not static and will as a result not get invoked.");
		} catch (NoSuchMethodException e) {
			System.err.println("Could not find the method \"preInit\" in " + clazz);
			e.printStackTrace();
		} catch (SecurityException e) {
			System.err.println("Could not access the method \"preInit\" in " + clazz);
			e.printStackTrace();
		}

		try {
			Method main = clazz.getMethod("main");

			if (Modifier.isStatic(main.getModifiers()))
				this.main = main;
			else
				System.err.println("The method main in " + clazz + " is not static and will as a result not get invoked.");
		} catch (NoSuchMethodException e) {
			System.err.println("Could not find the method \"main\" in " + clazz);
			e.printStackTrace();
		} catch (SecurityException e) {
			System.err.println("Could not access the method \"main\" in " + clazz);
			e.printStackTrace();
		}
	}

	public void visitMethod(EnumMethod method) {

		try {
			switch (method) {
				case PREINIT:
					this.preInit.invoke(null);
					break;

				case MAIN:
					this.main.invoke(null);
					break;
			}
		} catch (IllegalAccessException e) {
			System.err.println("Could not access the method " + method.name + " in " + this.clazz);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.err.println("The method " + method.name + " in " + this.clazz + " does not accept the required arguments.");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			System.err.println("Could not invoke the method " + method.name + " in " + this.clazz);
			e.printStackTrace();
		}
	}

	public static enum EnumMethod {
		PREINIT("preInit"),
		MAIN("main");

		public final String name;

		private EnumMethod(String name) {
			this.name = name;
		}
	}
}
