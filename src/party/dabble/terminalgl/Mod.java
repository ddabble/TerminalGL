package party.dabble.terminalgl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import party.dabble.terminalgl.util.ModMarker;

public class Mod
{
	private String name;
	private String version;
	private Class<?> clazz;
	private Method init;
	private Method main;
	private Method message;
	private Method quit;

	/**
	 * @return the name of the mod.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @return the version number of the mod.
	 */
	public String getVersion()
	{
		return this.version;
	}

	Mod(Class<?> clazz)
	{
		this.clazz = clazz;
		ModMarker annotation = clazz.getAnnotation(ModMarker.class);
		this.name = annotation.name();
		this.version = annotation.version();

		try
		{
			Method main = clazz.getMethod(EnumMethod.MAIN.name, TerminalGL.class);

			if (isStatic(main, clazz, EnumMethod.MAIN))
				this.main = main;
		} catch (NoSuchMethodException e)
		{
			System.err.println("Could not find the method \"" + EnumMethod.MAIN.name + "\" in " + clazz + ". Does it have the correct signature?");
			e.printStackTrace();
		} catch (SecurityException e)
		{
			System.err.println("Could not access the method \"" + EnumMethod.MAIN.name + "\" in " + clazz);
			e.printStackTrace();
		}

		this.init = getOptionalMethod(EnumMethod.INIT, clazz, TerminalGL.class);
		this.message = getOptionalMethod(EnumMethod.MESSAGE, clazz, TerminalGL.class);
		this.quit = getOptionalMethod(EnumMethod.QUIT, clazz, TerminalGL.class);
	}

	private static Method getOptionalMethod(EnumMethod enumMethod, Class<?> clazz, Class<?>... parameterTypes)
	{
		try
		{
			Method method = clazz.getMethod(enumMethod.name, parameterTypes);

			if (isStatic(method, clazz, enumMethod))
				return method;
		} catch (NoSuchMethodException e)
		{} catch (SecurityException e)
		{
			System.err.println("Could not access the method \"" + enumMethod.name + "\" in " + clazz);
			e.printStackTrace();
		}

		return null;
	}

	private static boolean isStatic(Method method, Class<?> clazz, EnumMethod enumMethod)
	{
		if (Modifier.isStatic(method.getModifiers()))
			return true;

		System.err.println("The method " + enumMethod.name + " in " + clazz + " is not static and will as a result not get invoked.");
		return false;
	}

	Object invokeMethod(EnumMethod method, TerminalGL terminalGL)
	{
		try
		{
			switch (method)
			{
				case INIT:
					if (this.init != null)
						return this.init.invoke(null, terminalGL);
					else
						break;

				case MAIN:
					return this.main.invoke(null, terminalGL);

				case MESSAGE:
					if (this.message != null)
						return this.message.invoke(null, terminalGL);
					else
						break;

				case QUIT:
					if (this.quit != null)
						return this.quit.invoke(null, terminalGL);
					else
						break;
			}
		} catch (IllegalAccessException e)
		{
			System.err.println("Could not access the method " + method.name + " in " + this.clazz);
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			System.err.println("The method " + method.name + " in " + this.clazz + " does not accept the required arguments.");
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			System.err.println("Could not invoke the method " + method.name + " in " + this.clazz);
			e.printStackTrace();
		}

		return null;
	}

	public static enum EnumMethod
	{
		INIT("init"),
		MAIN("main"),
		MESSAGE("registerMessage"),
		QUIT("quit");

		public final String name;

		private EnumMethod(String name)
		{
			this.name = name;
		}
	}
}
