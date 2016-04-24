package test;

import terminalGL.ModMarker;
import terminalGL.TerminalGL;
import terminalGL.TerminalGL.EnumMessageType;
import test.drawing.Drawing;

@ModMarker(name = "Test Mod", version = "1.0")
public class TestMod {
	private static float angle;

	public static void preInit() {
		angle = 0;
	}

	public static void main() {
		TerminalGL.displayMessage("Line and rectangle angle: " + angle, EnumMessageType.INFO);
		Drawing.drawLine(80, 60, 5, angle);
		Drawing.drawRectangle(100, 20, 16, 16, angle);
		Drawing.drawPolygon(15, 15, 22, 21, 33, 17, 20, 30);
		angle = (angle + 15) % 360;
	}
}
