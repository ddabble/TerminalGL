package party.dabble.terminalgl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import party.dabble.terminalgl.util.ModMarker;
import party.dabble.terminalgl.util.OutputHandler;

public class FileSystem
{
	private static String path;
	private static File settingFile;
	static File modDirectory;
	private static URLClassLoader classLoader;

	static boolean init(TerminalGL terminalGL)
	{
		try
		{
			path = TerminalGL.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replace('/', '\\');
			if (path.endsWith(".jar"))
				path = path.substring(1, path.lastIndexOf('\\') + 1);
			else if (path.endsWith("\\"))
				path = path.substring(1, path.length() - 1);
		} catch (URISyntaxException e)
		{
			System.err.println("Something went wrong during the conversion of TerminalGL's URL to a URI.");
			e.printStackTrace();
			System.exit(0);
		}

		modDirectory = new File(path + "\\mods");

		boolean setup = false;
		settingFile = new File(path + "\\settings.txt");
		if (settingFile.exists())
		{
			if (!parseSettings(terminalGL))
			{
				OutputHandler.lnprint("Scheduling a re-setup.");
				OutputHandler.ln();
				setup = true;
			}
		} else
			setup = true;

		return setup;
	}

	private static boolean parseSettings(TerminalGL terminalGL)
	{
		ArrayList<String> settingFileLines = new ArrayList<>(3);
		try (BufferedReader reader = new BufferedReader(new FileReader(settingFile)))
		{
			String line = null;
			while ((line = reader.readLine()) != null)
				settingFileLines.add(line);
		} catch (FileNotFoundException e)
		{
			System.err.println("File not found: " + settingFile.toString());
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e)
		{
			System.err.println("Unable to read file: " + settingFile.toString());
			e.printStackTrace();
			System.exit(0);
		}

		ArrayList<String> missingSettings = new ArrayList<>();

		String modPath = getSettingValue(Settings.MOD_DIRECTORY, settingFileLines);
		if (modPath != null)
			modDirectory = new File(modPath);
		else
			missingSettings.add("\"" + Settings.MOD_DIRECTORY + "\"");

		String width = getSettingValue(Settings.WIDTH, settingFileLines);
		if (width != null)
		{
			terminalGL.width = Integer.parseInt(width);
			terminalGL.xMax = terminalGL.width - 3;
		} else
			missingSettings.add("\"" + Settings.WIDTH + "\"");

		String height = getSettingValue(Settings.HEIGHT, settingFileLines);
		if (height != null)
		{
			terminalGL.height = Integer.parseInt(height);
			terminalGL.yMax = terminalGL.height - 3;
		} else
			missingSettings.add("\"" + Settings.HEIGHT + "\"");

		if (!missingSettings.isEmpty())
		{
			OutputHandler.lnprint("Could not find the setting" + ((missingSettings.size() > 1) ? "s " : " ") + formatWordList(missingSettings) + " in the setting file.");
			return false;
		} else
			return true;
	}

	private static String getSettingValue(String setting, ArrayList<String> settingFileLines)
	{
		for (String line : settingFileLines)
		{
			int valueIndex = line.indexOf(':');
			String key = line.substring(0, valueIndex).trim();
			String value = line.substring(valueIndex + 1).trim();

			if (key.equals(setting))
				return value;
		}

		return null;
	}

	private static String formatWordList(ArrayList<String> words)
	{
		StringBuilder list = new StringBuilder();

		for (int i = 0; i < words.size(); i++)
		{
			if (i > 0)
			{
				if (i == words.size() - 1)
					list.append(" and ");
				else
					list.append(", ");
			}

			list.append(words.get(i));
		}

		return list.toString();
	}

	static void findMods()
	{
		ArrayList<Mod> modsFound = new ArrayList<>();

		findMods(modDirectory.getAbsolutePath(), modsFound);

		OutputHandler.lnprint((modsFound.isEmpty() ? "Please place your" : "Found") + " mods in " + modDirectory.getAbsolutePath());
		OutputHandler.lnprint("You can modify this location in the setting file.");
		OutputHandler.ln();

		ModManager.loadMods(modsFound);
	}

	private static void findMods(String path, ArrayList<Mod> modsFound)
	{
		if (!modDirectory.exists())
		{
			modDirectory.mkdirs();
			return;
		}

		for (File file : new File(path).listFiles())
		{
			if (file.isDirectory())
				findMods(path + "/" + file.getName(), modsFound);
			else if (file.getName().endsWith(".jar"))
				registerModInJar(file, modsFound);
			else if (file.getName().endsWith(".class"))
				registerModFromClass(file, modsFound);
		}
	}

	private static void registerModInJar(File jarFile, ArrayList<Mod> modsFound)
	{
		try (JarFile jar = new JarFile(jarFile))
		{
			classLoader = new URLClassLoader(new URL[] { jarFile.toURI().toURL() });
			Enumeration<JarEntry> jarEntries = jar.entries();
			while (jarEntries.hasMoreElements())
			{
				JarEntry entry = jarEntries.nextElement();
				if (!entry.getName().endsWith(".class"))
					continue;

				String classPath = entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.');
				Class<?> clazz = null;
				clazz = classLoader.loadClass(classPath);

				if (clazz.isAnnotationPresent(ModMarker.class))
				{
					Mod mod = new Mod(clazz);
					modsFound.add(mod);
				}
			}
		} catch (IOException e)
		{
			System.err.println("Could not open the jar " + jarFile.getName());
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			System.err.println("Could not register the mod inside " + jarFile.getName());
			e.printStackTrace();
			return;
		}
	}

	private static void registerModFromClass(File file, ArrayList<Mod> modsFound)
	{
		String filePath = file.getAbsolutePath();
		String fileName = file.getName();

		String packageName = filePath.substring(modDirectory.getAbsolutePath().length() + 1, filePath.length() - (fileName.length() + 1)).replace('\\', '.');
		String className = fileName.substring(0, fileName.length() - 6);
		Class<?> clazz = null;

		try
		{
			classLoader = new URLClassLoader(new URL[] { modDirectory.toURI().toURL() });
			clazz = classLoader.loadClass(packageName + "." + className);
		} catch (Exception e)
		{
			System.err.println("Could not load the class " + className + " inside package " + packageName);
			e.printStackTrace();
			return;
		} catch (Throwable e)
		{
			System.err.println("Could not load the class " + className + " inside package " + packageName);
			System.err.println("This is probably caused by the class being in the wrong package relative to your mods folder.");
			System.err.println("Try moving the class' top package to " + modDirectory.getAbsolutePath() + " or give " + className + " the package name " + packageName);
			e.printStackTrace();
			return;
		}

		if (clazz.isAnnotationPresent(ModMarker.class))
		{
			Mod mod = new Mod(clazz);
			modsFound.add(mod);
		}
	}

	static void setup(TerminalGL terminalGL)
	{
		if (!settingFile.exists())
		{
			OutputHandler.lnprint("Please note that this program will create a file called \"settings.txt\" in " + path);
			OutputHandler.lnprint("Do with it as you please after the program has terminated.");
			OutputHandler.ln();
		}

		OutputHandler.lnprint("Please maximise your terminal window and press enter.");
		terminalGL.scanner.nextLine();

		OutputHandler.lnprint("Keep pressing enter until the arrow below touches the very right edge of this terminal window. If you went too far, just input < to go back one character.");
		OutputHandler.lnprint("Press space once, followed by enter to proceed.");
		OutputHandler.ln();
		OutputHandler.lnprint("╔" + produceRepeatingChar('═', terminalGL.width - 2) + "╗->");
		while (true)
		{
			String input = terminalGL.scanner.nextLine();
			if (input.equals(" "))
				break;
			else if (input.contains("<"))
			{
				if (terminalGL.width > 2)
					terminalGL.width--;
			} else
				terminalGL.width++;

			OutputHandler.print("╔" + produceRepeatingChar('═', terminalGL.width - 2) + "╗->");
		}
		terminalGL.xMax = terminalGL.width - 3;

		boolean restart = false;
		do
		{
			OutputHandler.lnprint(restart ? "K" : "Now, k");
			OutputHandler.print("eep pressing enter until the arrow below touches the very top edge of this terminal window. If you went too far, input < to start over.");
			OutputHandler.lnprint("Press space once, followed by enter to proceed.");
			OutputHandler.ln();
			OutputHandler.lnprint("^");
			OutputHandler.lnprint("|");
			OutputHandler.lnprint("╔" + produceRepeatingChar('═', terminalGL.width - 2) + "╗");

			for (int visibleHeight = 1; visibleHeight < terminalGL.height - 1; visibleHeight++)
				OutputHandler.print("\n║" + produceRepeatingChar('*', terminalGL.width - 2) + "║");

			while (true)
			{
				String input = terminalGL.scanner.nextLine();
				if (input.equals(" "))
				{
					restart = false;
					break;
				} else if (input.contains("<"))
				{
					terminalGL.height = 2;
					restart = true;
					break;
				} else
					terminalGL.height++;

				OutputHandler.print("║" + produceRepeatingChar('*', terminalGL.width - 2) + "║");
			}
		} while (restart);
		terminalGL.yMax = terminalGL.height - 3;
		OutputHandler.delay();
		OutputHandler.print("╚" + produceRepeatingChar('═', terminalGL.width - 2) + "╝");

		OutputHandler.ln();
		OutputHandler.lnprint("Setup is now finished and the main screen will be shown.");
		OutputHandler.ln();
		OutputHandler.lnprint("From now on, press enter to show the next frame (keep it pressed to enter Superfast Mode(TM)) or input QUIT to quit.");
		OutputHandler.ln();
		OutputHandler.lnprint("Please press enter one last time to finalise the setup process.");
		terminalGL.scanner.nextLine();

		writeSettings(terminalGL);
	}

	private static String produceRepeatingChar(char c, int amount)
	{
		return new String(new char[amount]).replace('\0', c);
	}

	private static void writeSettings(TerminalGL terminalGL)
	{
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(settingFile)))
		{
			writer.write(Settings.MOD_DIRECTORY + ":" + modDirectory.getAbsolutePath());
			writer.newLine();
			writer.write(Settings.WIDTH + ":" + terminalGL.width);
			writer.newLine();
			writer.write(Settings.HEIGHT + ":" + terminalGL.height);
			writer.newLine();
		} catch (IOException e)
		{
			System.err.println("Unable to write to file: " + settingFile.toString());
			e.printStackTrace();
			System.exit(0);
		}
	}

	static void setupPrompt(TerminalGL terminalGL)
	{
		OutputHandler.lnprint("Do you want to use the previous screen settings? If so, press enter. Otherwise, type something and then press enter.");
		OutputHandler.ln();

		if (!terminalGL.scanner.nextLine().isEmpty())
		{
			terminalGL.width = 2;
			terminalGL.height = 2;
			setup(terminalGL);
		}
	}

	static void closeResources()
	{
		try
		{
			classLoader.close();
		} catch (IOException e)
		{
			System.err.println("Could not close the URLClassLoader.");
			e.printStackTrace();
		}
	}

	private static class Settings
	{
		private static final String MOD_DIRECTORY = "mod-directory";
		private static final String WIDTH = "width";
		private static final String HEIGHT = "height";
	}
}
