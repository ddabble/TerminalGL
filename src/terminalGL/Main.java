package terminalGL;

import java.util.Scanner;

public class Main {
	private static Scanner scanner = new Scanner(System.in);
	private static char[][] screen;
	private static boolean isLinux;
	private static int width = 162;
	private static int height = 42;
	private static int xMax;
	private static int yMax;

	public static void main(String[] args) {
		setup();

		while (true) {
			String input = scanner.nextLine();
			char action = (input.isEmpty()) ? ' ' : input.charAt(0);

			if (action == 'q')
				break;

			prepareScreen();
			System.out.print(makeScreen());
		}

		scanner.close();
	}

	private static void setup() {
		System.out.println("Please maximise your terminal window and press enter.");
		scanner.nextLine();
		System.out.println("Please input '0' if you're running Windows or '1' if you're running Linux.");
		isLinux = scanner.nextInt() == 1;
		System.out.println("\nPlease SPAM YOUR KEYBOARD as calmly as possible until your rambling"
				+ " covers the entire length of your terminal, or simply write the length if"
				+ " you already know it.\nWhen you're finished with either; press enter.");
		String length = scanner.next();

		if (length.length() < 5 && Integer.parseInt(length) > 50)
			width = Integer.parseInt(length);
		else
			width = length.length();

		xMax = width - 3;

		System.out.println("\nThe width of your terminal window is: " + width + ". Press enter.");
		scanner.nextLine();

		for (int i = 0; i < 101; i++)
			System.out.println(i);

		System.out.println("Now, please write the number that's shown on the top line of your"
				+ " terminal and press enter.");
		height = 102 - scanner.nextInt();
		yMax = height - 3;
		System.out.println("\nSetup is now finished and the main screen will be shown.\n"
				+ "From now on, press enter to show the next frame (keep it pressed to enter"
				+ " Superfast Mode™) or enter 'q' to quit.\nPlease press enter one last time.");
		scanner.nextLine();
	}

	private static void prepareScreen() {
		screen = new char[height][width];

		// Fills the screen with spaces
		for (int row = 0; row < height; row++) {

			for (int col = 0; col < width; col++) {
				screen[row][col] = ' ';
			}
		}

		// Border corners
		screen[height - 1][0] = '╔';
		screen[height - 1][width - 1] = '╗';
		screen[0][0] = '╚';
		screen[0][width - 1] = '╝';

		// Upper and lower border
		for (int col = 1; col < width - 1; col++) {
			screen[height - 1][col] = '═';
			screen[0][col] = '═';
		}

		// Left and right border
		for (int row = 1; row < height - 1; row++) {
			screen[row][0] = '║';
			screen[row][width - 1] = '║';
		}
	}

	private static StringBuffer makeScreen() {
		drawSquare(10, 5, 10);
		drawRotatingSquare(70, 11, 20, 5);
		drawQuadrilateral(15, 20, 22, 23, 40, 19, 20, 30);
		drawCircle(170, 40, 22);

		StringBuffer screenBuffer = new StringBuffer();

		for (int row = height - 1; row >= 0; row--) {

			for (int col = 0; col < width; col++) {
				screenBuffer.append(screen[row][col]);
			}

			if (isLinux)
				screenBuffer.append("\n");
		}

		return screenBuffer;
	}

	private static void putPixel(int x, int y) {
		putPixel(x, y, '*');
	}

	private static void putPixel(int x, int y, char c) {

		try {

			if (x < 0 || x > xMax || y < 0 || y > yMax)
				throw new ArrayIndexOutOfBoundsException();

			screen[++y][++x] = c;
		} catch (ArrayIndexOutOfBoundsException e) {
			writeMessage("Tried to write outside of the designated screen area: (x: " + x + ", y: " + y + ")");
		}
	}

	private static void drawLine(int startX, int startY, int endX, int endY) {
		float deltaX = endX - startX;
		float deltaY = endY - startY;

		if (deltaX < 0 || (deltaX < 0 && deltaY < 0)) {
			// Swap startX and endX
			startX = startX ^ endX;
			endX = startX ^ endX;
			startX = startX ^ endX;
			// Swap startY and endY
			startY = startY ^ endY;
			endY = startY ^ endY;
			startY = startY ^ endY;
		}

		float error = 0;
		float deltaError = (deltaX == 0) ? Math.abs(deltaY) + 1 : Math.abs(deltaY / deltaX);
		int y = startY;

		// Find and fill all pixels between the two points
		for (int x = startX; x <= endX; x++) {
			putPixel(x, y);
			error += deltaError;

			while (error >= 0.5) {
				putPixel(x, y);
				y += Math.signum(endY - startY);
				error -= 1;
			}
		}
	}

	private static void drawSquare(int bottomLeftX, int bottomLeftY, int size) {
		size--;
		// Bottom left point (starting point)
		int x0 = bottomLeftX;
		int y0 = bottomLeftY;
		// Bottom right point
		int x1 = x0 + size * 2 + 1;
		int y1 = y0;
		// Top right point
		int x2 = x1;
		int y2 = y1 + size;
		// Top left point
		int x3 = x2 - size * 2 - 1;
		int y3 = y2;

		drawLine(x0, y0, x1, y1);
		drawLine(x1, y1, x2, y2);
		drawLine(x2, y2, x3, y3);
		drawLine(x3, y3, x0, y0);
	}

	private static void drawRotatingSquare(int bottomLeftX, int bottomLeftY, int size, float rotationSpeed) {
		// Does not actually rotate.. yet..

		size--;
		// Bottom left point (starting point)
		int x0 = bottomLeftX;
		int y0 = bottomLeftY;
		// Bottom right point
		int x1 = x0 + size * 2 + 1;
		int y1 = y0;
		// Top right point
		int x2 = x1;
		int y2 = y1 + size;
		// Top left point
		int x3 = x2 - size * 2 - 1;
		int y3 = y2;

		drawLine(x0, y0, x1, y1);
		drawLine(x1, y1, x2, y2);
		drawLine(x2, y2, x3, y3);
		drawLine(x3, y3, x0, y0);
	}

	private static void drawQuadrilateral(int x0, int y0, int x1, int y1, int x2, int y2, int x3, int y3) {
		drawLine(x0, y0, x1, y1);
		drawLine(x1, y1, x2, y2);
		drawLine(x2, y2, x3, y3);
		drawLine(x3, y3, x0, y0);
	}

	public static void drawCircle(int x0, int y0, int radius) {
		int x = radius;
		int y = 0;
		int radiusError = 1 - x;

		while (x >= y) {
			putPixel(x + x0, y + y0);
			putPixel(y + x0, x + y0);
			putPixel(-x + x0, y + y0);
			putPixel(-y + x0, x + y0);
			putPixel(-x + x0, -y + y0);
			putPixel(-y + x0, -x + y0);
			putPixel(x + x0, -y + y0);
			putPixel(y + x0, -x + y0);
			y++;

			if (radiusError < 0) {
				radiusError += 2 * y + 1;
			} else {
				x--;
				radiusError += 2 * (y - x) + 1;
			}
		}
	}

	private static void writeMessage(String message) {
		// TODO Support for multiple and multi-line messages, and messages expiring after a certain amount of time
		
		for (int i = 0; i < message.length(); i++)
			putPixel(i, 0, message.charAt(i));
	}
}