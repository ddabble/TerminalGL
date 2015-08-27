package terminalGL;

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
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import terminalGL.Mod.EnumMethod;

public class TerminalGL {
	private static boolean keepRunning = true;
	private static Scanner scanner = new Scanner(System.in);
	private static String path;
	private static File modDirectory;
	private static File propertyFile;
	private static ArrayList<String> propertyFileLines;
	private static URLClassLoader classLoader;
	private static ArrayList<Mod> mods = new ArrayList<Mod>();
	private static char[][] screen;
	private static int width;
	private static int height;
	private static int xMax;
	private static int yMax;

	public static void main(String[] args) {
		fileSystemInit();
		findMods(modDirectory.getAbsolutePath());
		propertyInit();
		modPreInit();
		prepareScreen();

		while (keepRunning) {
			flushScreen();
			System.out.print(makeScreen());
			inputActionHandler();
		}

		closeResources();
	}

	private static void fileSystemInit() {

		try {
			path = TerminalGL.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().substring(1).replace('/', '\\');
			if (path.endsWith(".jar"))
				path = path.substring(0, path.lastIndexOf('\\') + 1);
		} catch (URISyntaxException e) {
			System.err.println("Something went wrong during the conversion of this class' URL to a URI.");
			e.printStackTrace();
			System.exit(0);
		}

		if ((propertyFile = new File(path + "properties.txt")).exists()) {
			try {
				modDirectory = new File(getModPath());
			} catch (Exception e) {
				System.exit(0);
			}
		} else
			modDirectory = new File(path + "mods");

		System.out.println("\nPlease place your mod jar files in " + modDirectory.getAbsolutePath() + "\\");
		System.out.println("You can modify this location in the property file.");
	}

	private static String getModPath() throws Exception {

		try (BufferedReader reader = new BufferedReader(new FileReader(propertyFile))) {
			propertyFileLines = new ArrayList<String>();
			String line = null;
			while ((line = reader.readLine()) != null)
				propertyFileLines.add(line);

			try {
				return (String) getPropertyValue(EnumProperty.MOD_DIRECTORY);
			} catch (NullPointerException e) {
				return path + "mods";
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + propertyFile.toString());
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			System.err.println("Unable to read file: " + propertyFile.toString());
			e.printStackTrace();
			throw e;
		}
	}

	private static void findMods(String path) {

		if (!modDirectory.exists()) {
			modDirectory.mkdirs();
			return;
		}

		for (File file : new File(path).listFiles()) {

			if (file.isDirectory())
				findMods(path + "/" + file.getName());
			else if (file.getName().endsWith(".jar"))
				registerModInJar(file);
		}
	}

	private static void registerModInJar(File jarFile) {
		JarFile jar = null;
		try {
			jar = new JarFile(jarFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Enumeration<JarEntry> jarEntries = jar.entries();
		while (jarEntries.hasMoreElements()) {
			JarEntry entry = jarEntries.nextElement();

			if (entry.getName().endsWith(".class")) {
				String className = entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.');
				Class<?> clazz = null;

				try {
					classLoader = new URLClassLoader(new URL[] { jarFile.toURI().toURL() });
					clazz = classLoader.loadClass(className);
				} catch (Exception e) {
					System.err.println("Could not register the mod inside " + jarFile.getName());
					e.printStackTrace();
					return;
				}

				if (clazz.isAnnotationPresent(ModMarker.class)) {
					Mod mod = new Mod(clazz);
					mods.add(mod);
					System.out.print("\nLoaded " + mod.name + mod.version);
					return;
				}
			}
		}
	}

	private static void propertyInit() {

		if (!mods.isEmpty())
			System.out.println();

		if (propertyFile.exists()) {
			System.out.println("\nDo you want to load the previous screen settings? If so, press enter. Otherwise, write something and then press enter.");

			if (scanner.nextLine().isEmpty()) {
				setupFromFile();
				return;
			}
		}

		setup();
	}

	private static void setup() {

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(propertyFile))) {
			System.out.println("\nPlease note that this program will create a text file called \"properties.txt\" in " + path);
			System.out.println("\nDo with it as you please after the program has terminated.");

			System.out.println("Please maximise your terminal window and press enter.");
			scanner.nextLine();

			System.out.println("\nPlease SPAM YOUR KEYBOARD as calmly as possible until your rambling covers the entire length of your terminal, or simply write the length if you already know it.");
			System.out.println("\nWhen you're finished with either; press enter.");
			String length = scanner.next();
			System.out.println();

			if (length.length() <= 4)
				width = Integer.parseInt(length);
			else
				width = length.length();

			xMax = width - 3;

			for (int i = 0; i <= 256; ++i)
				System.out.println(i);

			System.out.println("\nNow, please write the number that's shown on the top line of your terminal window and press enter.");
			height = 259 - scanner.nextInt();
			yMax = height - 3;

			System.out.println("\nSetup is now finished and the main screen will be shown.");
			System.out.println("\nFrom now on, press enter to show the next frame (keep it pressed to enter Superfast Mode™) or enter \"q\" to quit.");
			System.out.println("\nPlease press enter one last time.");
			scanner.nextLine();
			// I have no idea why this is (seemingly) necessary
			scanner.nextLine();

			writer.write(EnumProperty.MOD_DIRECTORY.name + ":" + modDirectory.getAbsolutePath());
			writer.newLine();
			writer.write(EnumProperty.WIDTH.name + ":" + width);
			writer.newLine();
			writer.write(EnumProperty.HEIGHT.name + ":" + height);
			writer.newLine();
		} catch (IOException e) {
			System.err.println("Unable to write to file: " + propertyFile.toString());
			e.printStackTrace();
			System.exit(0);
		}
	}

	private static void setupFromFile() {

		try (BufferedReader reader = new BufferedReader(new FileReader(propertyFile))) {

			if (propertyFileLines == null) {
				propertyFileLines = new ArrayList<String>();
				String line = null;
				while ((line = reader.readLine()) != null)
					propertyFileLines.add(line);
			}

			try {
				width = (int) getPropertyValue(EnumProperty.WIDTH);
				xMax = width - 3;
				height = (int) getPropertyValue(EnumProperty.HEIGHT);
				yMax = height - 3;
				propertyFileLines.clear();
			} catch (NullPointerException e) {
				System.out.println(e.getMessage()
						+ "\nCreating new property file.");
				setup();
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + propertyFile.toString());
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			System.err.println("Unable to read file: " + propertyFile.toString());
			e.printStackTrace();
			System.exit(0);
		}
	}

	private static Object getPropertyValue(EnumProperty property) {

		for (String line : propertyFileLines) {
			int valueIndex = line.indexOf(':');
			String key = line.substring(0, valueIndex);
			String value = line.substring(valueIndex + 1).trim();

			switch (property) {
				case MOD_DIRECTORY:
					if (key.equals(EnumProperty.MOD_DIRECTORY.name))
						return value;
					else
						break;

				case WIDTH:
					if (key.equals(EnumProperty.WIDTH.name))
						return Integer.valueOf(value);
					else
						break;

				case HEIGHT:
					if (key.equals(EnumProperty.HEIGHT.name))
						return Integer.valueOf(value);
					else
						break;
			}
		}

		throw new NullPointerException("Found property file, but could not find the property \"" + property.name + "\".");
	}

	private static void modPreInit() {

		for (Mod mod : mods)
			mod.visitMethod(EnumMethod.PREINIT);
	}

	private static void prepareScreen() {
		screen = new char[height][width];

		// Border corners
		screen[height - 1][0] = '╔';
		screen[height - 1][width - 1] = '╗';
		screen[0][0] = '╚';
		screen[0][width - 1] = '╝';

		// Upper and lower borders
		for (int col = 1; col < width - 1; ++col) {
			screen[height - 1][col] = '═';
			screen[0][col] = '═';
		}

		// Left and right borders
		for (int row = 1; row < height - 1; ++row) {
			screen[row][0] = '║';
			screen[row][width - 1] = '║';
		}

		flushScreen();
	}

	/**
	 * Fills the screen with spaces.
	 */
	private static void flushScreen() {

		for (int row = 1; row < height - 1; ++row) {

			for (int col = 1; col < width - 1; ++col)
				screen[row][col] = ' ';
		}
	}

	private static StringBuffer makeScreen() {

		for (Mod mod : mods)
			mod.visitMethod(EnumMethod.MAIN);

		StringBuffer screenBuffer = new StringBuffer();

		for (int row = height - 1; row >= 0; --row) {

			for (int col = 0; col < width; ++col)
				screenBuffer.append(screen[row][col]);

			screenBuffer.append('\n');
		}

		return screenBuffer;
	}

	private static void inputActionHandler() {
		String input = scanner.nextLine();
		char action = (input.isEmpty()) ? ' ' : input.charAt(0);
		actionQueueHandler(action);

		if (action == 'q')
			stop();
	}

	private static void actionQueueHandler(char action) {
		// TODO: Yeah, just write, like, ANYTHING here, then that'd be great
	}

	private static void closeResources() {
		try {
			if (classLoader != null)
				classLoader.close();
		} catch (IOException e) {
			System.err.println("Could not close the URLClassLoader " + classLoader);
			e.printStackTrace();
		}

		scanner.close();
	}

	private static void stop() {
		keepRunning = false;
	}

	/**
	 * @return The screen's maximum x-coordinate for pixels to be drawn.
	 */
	public static int getXMax() {
		return xMax;
	}

	/**
	 * @return The screen's maximum y-coordinate for pixels to be drawn.
	 */
	public static int getYMax() {
		return yMax;
	}

	public static void putPixel(int x, int y) {
		putPixel(x, y, '*');
	}

	public static void putPixel(int x, int y, char c) {

		try {
			if (x < 0 || x > xMax || y < 0 || y > yMax)
				throw new ArrayIndexOutOfBoundsException();

			screen[++y][++x] = c;
		} catch (ArrayIndexOutOfBoundsException e) {
			displayMessage("Tried to draw a pixel outside of the designated screen area (x: " + x + ", y: " + y + ")", EnumMessageType.WARNING);
		}
	}

	public static boolean isPixelEmpty(int x, int y) {
		Character pixel = getPixel(x, y);
		if (pixel == null)
			return false;

		return (char) pixel == ' ';
	}

	public static Character getPixel(int x, int y) {
		try {
			if (x < 0 || x > xMax || y < 0 || y > yMax)
				throw new ArrayIndexOutOfBoundsException();

			return screen[++y][++x];
		} catch (ArrayIndexOutOfBoundsException e) {
			displayMessage("Tried to read a pixel from outside of the designated screen area (x: " + x + ", y: " + y + ")", EnumMessageType.ERROR);
			return null;
		}
	}

	/**
	 * Displays a message at the bottom left corner of the screen.
	 * 
	 * @param message
	 * @param messageType
	 */
	public static void displayMessage(String message, EnumMessageType messageType) {
		// TODO: Support for multiple and multi-line messages, string wrapping, and messages expiring after a certain amount of time
		message = messageType + ": " + message;

		for (int i = 0; i < message.length(); ++i)
			putPixel(i, 0, message.charAt(i));

		if (messageType == EnumMessageType.FATAL)
			stop();
	}

	private static enum EnumProperty {
		MOD_DIRECTORY("mod-directory"),
		WIDTH("width"),
		HEIGHT("height");

		private final String name;

		private EnumProperty(String name) {
			this.name = name;
		}
	}

	public static enum EnumMessageType {
		DEBUG("DEBUG"),
		INFO("INFO"),
		WARNING("WARNING"),
		ERROR("ERROR"),
		/**
		 * Stops the program after the message has been written.
		 */
		FATAL("FATAL");

		private String name;

		private EnumMessageType(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}
}
