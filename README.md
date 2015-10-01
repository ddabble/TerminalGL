# TerminalGL
An experiment with making a graphics library from scratch using characters in a terminal window as pixels.

### Usage
* Download **TerminalGL.jar** from this repository's root directory and place it somewhere warm and dry.
* Put all your jarred mods in a folder named *mods* located in the same directory as the above-mentioned jar file.
	* If you want to put them elsewhere, create a text file named *properties.txt*, again, in the same directory, and write a line starting with `mod-directory:` followed by the path to the desired folder.
* Run the program from a command prompt with the command `java -jar "<path>"`, where &lt;path&gt; is replaced with the full path to the **TerminalGL.jar** file.

### Modding
* Include **TerminalGL.jar** in your project's referenced libraries.
* Create a main class and name it whatever you'd like. This will be your mod's starting point. Give it the annotation `@ModMarker`, which can take two elements:
	* `name` - The full name of the mod (*required*).
	* `version` - The version number of the mod.
* Also, give your main class these two methods:
	* `public static void preInit()` - Called during the program's start-up.
	* `public static void main()` - Called just before each frame is drawn.
* Package your class files (with packages) in a jar and place it as mentioned earlier, or just place them directly in your *mods* folder for easy development.
