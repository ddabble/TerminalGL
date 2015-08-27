package test.drawing;

import terminalGL.TerminalGL;
import terminalGL.TerminalGL.EnumMessageType;

public class Drawing {

	public static void drawLine(int x0, int y0, int x1, int y1) {

		if (x1 - x0 < 0) {
			// Swap x0 and x1
			x0 = x0 ^ x1;
			x1 = x0 ^ x1;
			x0 = x0 ^ x1;
			// Swap y0 and y1
			y0 = y0 ^ y1;
			y1 = y0 ^ y1;
			y0 = y0 ^ y1;
		}

		float deltaX = x1 - x0;
		float deltaY = y1 - y0;
		float signumY = Math.signum(deltaY);
		float error = 0;
		float deltaError = (deltaX == 0) ? Math.abs(deltaY) + 1 : Math.abs(deltaY / deltaX);
		int y = y0;

		// Finds and fills all pixels between the two points
		for (int x = x0; x <= x1; ++x) {
			TerminalGL.putPixel(x, y);
			error += deltaError;

			while (error >= 0.5 && signumY * y <= signumY * y1) {
				TerminalGL.putPixel(x, y);
				y += signumY;
				--error;
			}
		}
	}

	public static void drawLine(int x, int y, int length, double angle) {
		boolean even = length % 2 == 0;

		if (even)
			++length;

		angle = Math.toRadians(angle);
		int extendedX = (int) (Math.cos(angle) * (length / 2f));
		int extendedY = (int) (Math.sin(angle) * (length / 2f));
		int x0 = x - (extendedX - ((even) ? (int) Math.signum(extendedX) : 0));
		int y0 = y - (extendedY - ((even) ? (int) Math.signum(extendedY) : 0));
		int x1 = x + extendedX;
		int y1 = y + extendedY;
		drawLine(x0, y0, x1, y1);
	}

	public static void drawRectangle(int x, int y, int width, int height, double angle) {

		// if (x || y % 2 == 0)
		// TODO: ... add 0.5 to the relevant variable

		double hypotenuse = Math.hypot(width, height);

		angle = Math.toRadians(angle) + Math.asin(height / hypotenuse);
		int extendedX = (int) (Math.cos(angle) * (hypotenuse / 2));
		int extendedY = (int) (Math.sin(angle) * (hypotenuse / 2));
		int x0 = x - extendedX;
		int y0 = y - extendedY;
		int x2 = x + extendedX;
		int y2 = y + extendedY;

		angle += Math.PI / 2;
		extendedX = (int) (Math.cos(angle) * (hypotenuse / 2));
		extendedY = (int) (Math.sin(angle) * (hypotenuse / 2));
		int x1 = x - extendedX;
		int y1 = y - extendedY;
		int x3 = x + extendedX;
		int y3 = y + extendedY;

		drawLine(x0, y0, x1, y1);
		drawLine(x1, y1, x2, y2);
		drawLine(x2, y2, x3, y3);
		drawLine(x3, y3, x0, y0);
	}

	public static void drawPolygon(int... cornerCoordinates) {

		if (cornerCoordinates.length < 6) {
			TerminalGL.displayMessage("Tried to draw a polygon with less than 3 x- and y-coordinate pairs.", EnumMessageType.ERROR);
			return;
		}

		if (cornerCoordinates.length % 2 != 0) {
			TerminalGL.displayMessage("Tried to draw a polygon without a matching number of x- and y-coordinates.", EnumMessageType.ERROR);
			return;
		}

		for (int i = 0; i < cornerCoordinates.length; i += 2) {

			if (i == cornerCoordinates.length - 2) {
				int x0 = cornerCoordinates[0], y0 = cornerCoordinates[1], x1 = cornerCoordinates[i], y1 = cornerCoordinates[i + 1];
				drawLine(x1, y1, x0, y0);
			} else {
				int x0 = cornerCoordinates[i], y0 = cornerCoordinates[i + 1], x1 = cornerCoordinates[i + 2], y1 = cornerCoordinates[i + 3];
				drawLine(x0, y0, x1, y1);
			}
		}
	}
}
