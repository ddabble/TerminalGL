package test;

import party.dabble.terminalgl.TerminalGL;
import party.dabble.terminalgl.util.ModMarker;
import party.dabble.terminalgl.util.OutputHandler;
import test.drawing.Drawing;

@ModMarker(name = "Test Mod", version = "1.0")
public class TestMod
{
	private static float angle = 0;

	public static void init(@SuppressWarnings("unused") TerminalGL terminalGL)
	{
		OutputHandler.lnprint("Test Mod initializing.");
	}

	public static void main(TerminalGL terminalGL)
	{
		displayMessage("Line and rectangle angle: " + angle, terminalGL);
		Drawing.drawLine(40, 40, 5, angle, terminalGL);
		Drawing.drawRectangle(60, 20, 16, 16, angle, terminalGL);
		Drawing.drawPolygon(terminalGL, 15, 15, 22, 21, 33, 17, 20, 30);
		angle = (angle + 15) % 360;
	}

	private static void displayMessage(String message, TerminalGL terminalGL)
	{
		for (int i = 0; i < message.length(); i++)
			terminalGL.putPixel(i, 0, message.charAt(i));
	}

	public static void quit(@SuppressWarnings("unused") TerminalGL terminalGL)
	{
		OutputHandler.lnprint("Test Mod terminating.");
	}
}
