# TerminalGL
An experiment with making a graphics library from scratch using characters in a terminal window as pixels.

### Usage
* Download **TerminalGL.jar** from this repository's root directory and place it somewhere warm and dry.
* Put all your mods in a folder named `mods` located in the same directory as the above-mentioned jar file.
	* If you want to put them elsewhere, create a text file named `settings.txt` in the same directory as the jar file, and write a line starting with `mod-directory:` followed by the full path to the desired mod folder.
* Run the program from a command prompt by navigating to the directory of the jar file and using the command `java -jar TerminalGL.jar`.

### Modding
* Include **TerminalGL.jar** in your project's referenced libraries.
* Create a main class and name it whatever you'd like. This will be your mod's starting point. Give it the annotation `@ModMarker`, which takes two elements:
	* `name` &ndash; The full name of the mod.
	* `version` &ndash; The version number of the mod. *(optional)*
* Also, give your main class these four methods:
	* `public static void main(TerminalGL)` &ndash; Invoked just before each frame is drawn.
	* `public static void init(TerminalGL)` &ndash; Invoked during the program's start-up. *(optional)*
	* `public static Object registerMessage(TerminalGL)` &ndash; Invoked before `main`. Use this to communicate with other mods between each frame. *(optional)*
	* `public static void quit(TerminalGL)` &ndash; Invoked when the program is about to terminate. *(optional)*
* Package your class files (with packages, i.e. folder structure) in a jar and place it as mentioned earlier, or just change your mod directory in the setting file to their containing folder, for easier development.
