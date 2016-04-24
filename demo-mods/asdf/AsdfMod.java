package asdf;

import terminalGL.ModMarker;
import terminalGL.TerminalGL;

@ModMarker(name = "Mod of Asdf")
public class AsdfMod {

	public static void preInit() {

	}

	public static void main() {
		drawCircle(170, 40, 22);
	}

	public static void drawCircle(int x0, int y0, int radius) {
		int x = radius;
		int y = 0;
		int radiusError = 1 - x;

		while (x >= y) {
			TerminalGL.putPixel(x + x0, y + y0);
			TerminalGL.putPixel(y + x0, x + y0);
			TerminalGL.putPixel(-x + x0, y + y0);
			TerminalGL.putPixel(-y + x0, x + y0);
			TerminalGL.putPixel(-x + x0, -y + y0);
			TerminalGL.putPixel(-y + x0, -x + y0);
			TerminalGL.putPixel(x + x0, -y + y0);
			TerminalGL.putPixel(y + x0, -x + y0);
			++y;

			if (radiusError < 0)
				radiusError += 2 * y + 1;
			else {
				--x;
				radiusError += 2 * (y - x) + 1;
			}
		}
	}
}
