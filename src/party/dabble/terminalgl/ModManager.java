package party.dabble.terminalgl;

import java.util.ArrayList;
import java.util.HashMap;

import party.dabble.terminalgl.Mod.EnumMethod;
import party.dabble.terminalgl.util.OutputHandler;

public class ModManager
{
	static ArrayList<Mod> mods;
	private static HashMap<Mod, Object> modMessages = new HashMap<>();

	private static boolean isRegisteringMessages;

	static void loadMods(ArrayList<Mod> modsFound)
	{
		mods = modsFound;

		for (Mod mod : modsFound)
			OutputHandler.lnprint("Loaded " + mod.getName() + " " + mod.getVersion());

		if (!modsFound.isEmpty())
			OutputHandler.ln();
	}

	static void initMods(TerminalGL terminalGL)
	{
		OutputHandler.ln();

		for (Mod mod : mods)
			mod.invokeMethod(EnumMethod.INIT, terminalGL);

		OutputHandler.ln();
	}

	/**
	 * @return an array of all the {@code Mods} loaded.
	 */
	public static ArrayList<Mod> getModList()
	{
		@SuppressWarnings("unchecked")
		ArrayList<Mod> shallowCopy = (ArrayList<Mod>)mods.clone();
		return shallowCopy;
	}

	/**
	 * Returns a map of messages registered by mods; up to one message each.
	 * 
	 * @return a {@code HashMap} with {@code Mods} as keys and {@code Objects} as messages.
	 */
	public static HashMap<Mod, Object> getModMessages()
	{
		if (isRegisteringMessages)
			throw new IllegalStateException("Cannot provide messages while registering messages.");

		@SuppressWarnings("unchecked")
		HashMap<Mod, Object> shallowCopy = (HashMap<Mod, Object>)modMessages.clone();
		return shallowCopy;
	}

	// TODO: Is this really useful at all?
	static void registerModMessages(TerminalGL terminalGL)
	{
		isRegisteringMessages = true;

		modMessages.clear();
		for (Mod mod : mods)
		{
			Object message = mod.invokeMethod(Mod.EnumMethod.MESSAGE, terminalGL);
			if (message != null)
				modMessages.put(mod, message);
		}

		isRegisteringMessages = false;
	}

	static void drawMods(TerminalGL terminalGL)
	{
		for (Mod mod : mods)
			mod.invokeMethod(EnumMethod.MAIN, terminalGL);
	}

	static void closeResources(TerminalGL terminalGL)
	{
		for (Mod mod : mods)
			mod.invokeMethod(EnumMethod.QUIT, terminalGL);
	}
}
