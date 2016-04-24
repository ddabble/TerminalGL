package party.dabble.terminalgl;

import java.util.Scanner;

import party.dabble.terminalgl.util.OutputHandler;

public class TerminalGL
{
	Scanner scanner = new Scanner(System.in);
	int width = 2;
	int height = 2;
	int xMax;
	int yMax;
	private char[][] screen;

	private boolean keepRunning = true;
	private String userInput;

	public void run()
	{
		boolean setup = FileSystem.init(this);
		FileSystem.findMods();

		if (setup)
			FileSystem.setup(this);
		else
			FileSystem.setupPrompt(this);

		ModManager.initMods(this);
		initScreen();

		while (this.keepRunning)
		{
			ModManager.registerModMessages(this);
			flushScreen();
			ModManager.drawMods(this);
			OutputHandler.print(makeScreen());

			EnumCommand command = parseUserInput();
			if (command != null)
				obey(command);
		}

		closeResources();
	}

	private void initScreen()
	{
		this.screen = new char[this.height][this.width];

		// Border corners
		this.screen[this.height - 1][0] = '╔';
		this.screen[this.height - 1][this.width - 1] = '╗';
		this.screen[0][0] = '╚';
		this.screen[0][this.width - 1] = '╝';

		// Upper and lower borders
		for (int col = 1; col < this.width - 1; col++)
		{
			this.screen[this.height - 1][col] = '═';
			this.screen[0][col] = '═';
		}

		// Left and right borders
		for (int row = 1; row < this.height - 1; row++)
		{
			this.screen[row][0] = '║';
			this.screen[row][this.width - 1] = '║';
		}

		flushScreen();
	}

	/**
	 * Fills the screen with spaces.
	 */
	private void flushScreen()
	{
		for (int row = 1; row < this.height - 1; row++)
		{
			for (int col = 1; col < this.width - 1; col++)
				this.screen[row][col] = ' ';
		}
	}

	private String makeScreen()
	{
		StringBuilder screenBuffer = new StringBuilder((this.width + 1) * this.height);

		for (int row = this.height - 1; row >= 0; row--)
		{
			for (int col = 0; col < this.width; col++)
				screenBuffer.append(this.screen[row][col]);

			screenBuffer.append('\n');
		}

		return screenBuffer.toString();
	}

	private void closeResources()
	{
		this.scanner.close();
		FileSystem.closeResources();
		ModManager.closeResources(this);
	}

	private EnumCommand parseUserInput()
	{
		this.userInput = this.scanner.nextLine();

		if (this.userInput.toUpperCase().equals("QUIT"))
			return EnumCommand.QUIT;

		return null;
	}

	private void obey(EnumCommand command)
	{
		if (command != null)
		{
			switch (command)
			{
				case QUIT:
					this.keepRunning = false;
					break;
			}
		}
	}

	/**
	 * @return the user's input from after the last frame.
	 */
	public String getUserInput()
	{
		return this.userInput;
	}

	/**
	 * @return the screen's maximum x-coordinate for pixels to be drawn.
	 */
	public int getXMax()
	{
		return this.xMax;
	}

	/**
	 * @return the screen's maximum y-coordinate for pixels to be drawn.
	 */
	public int getYMax()
	{
		return this.yMax;
	}

	/**
	 * Draws the provided character at the specified x and y coordinates of the screen.
	 * If the coordinates are outside of the designated screen area, nothing will be drawn.
	 * 
	 * @param x The x coordinate of the pixel.
	 * @param y The y coordinate of the pixel.
	 * @param c The {@code char} to be drawn.
	 */
	public void putPixel(int x, int y, char c)
	{
		if (x < 0 || x > this.xMax || y < 0 || y > this.yMax)
			return;

		this.screen[++y][++x] = c;
	}

	/**
	 * Same as {@link #putPixel(int, int, char)}, but wraps the pixel coordinates around the edges of the screen.
	 * 
	 * @param x The x coordinate of the pixel.
	 * @param y The y coordinate of the pixel.
	 * @param c The {@code char} to be drawn.
	 */
	public void putWrappedPixel(int x, int y, char c)
	{
		int width = this.xMax + 1;
		int height = this.yMax + 1;

		if (width == 0 || height == 0)
			return;

		x %= width;
		y %= height;

		if (x < 0)
			x += width;
		if (y < 0)
			y += height;

		this.screen[++y][++x] = c;
	}

	/**
	 * Returns the character at the specified x and y coordinates of the screen.
	 * If the coordinates are outside of the designated screen area, {@code null} will be returned.
	 * 
	 * @param x The x coordinate of the pixel.
	 * @param y The y coordinate of the pixel.
	 * 
	 * @return the targeted pixel wrapped in a {@link Character} object.
	 */
	public Character getPixel(int x, int y)
	{
		if (x < 0 || x > this.xMax || y < 0 || y > this.yMax)
			return null;

		return this.screen[++y][++x];
	}

	public static enum EnumCommand
	{
		QUIT
	}
}
