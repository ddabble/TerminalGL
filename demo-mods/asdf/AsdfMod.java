package asdf;

import party.dabble.terminalgl.TerminalGL;
import party.dabble.terminalgl.util.ModMarker;

@ModMarker(name = "Mod of Asdf")
public class AsdfMod
{
	private static final char c = '*';

	private static int x0 = 110;
	private static int y0 = 24;

	public static void main(TerminalGL terminalGL)
	{
		drawCircle(x0++, y0++, 22, terminalGL);
	}

	public static void drawCircle(int x0, int y0, int radius, TerminalGL terminalGL)
	{
		int x = radius;
		int y = 0;
		int radiusError = 1 - x;

		while (x >= y)
		{
			terminalGL.putWrappedPixel(x + x0, y + y0, c);
			terminalGL.putWrappedPixel(y + x0, x + y0, c);
			terminalGL.putWrappedPixel(-x + x0, y + y0, c);
			terminalGL.putWrappedPixel(-y + x0, x + y0, c);
			terminalGL.putWrappedPixel(-x + x0, -y + y0, c);
			terminalGL.putWrappedPixel(-y + x0, -x + y0, c);
			terminalGL.putWrappedPixel(x + x0, -y + y0, c);
			terminalGL.putWrappedPixel(y + x0, -x + y0, c);
			y++;

			if (radiusError < 0)
				radiusError += 2 * y + 1;
			else
			{
				x--;
				radiusError += 2 * (y - x) + 1;
			}
		}
	}
}
