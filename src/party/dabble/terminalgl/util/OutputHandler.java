package party.dabble.terminalgl.util;

public class OutputHandler
{
	public static final long STD_DELAY = 250;

	/**
	 * Suspends the current thread for a standard {@value #STD_DELAY} ms, then prints a newline.
	 */
	public static void ln()
	{
		ln(STD_DELAY);
	}

	/**
	 * Suspends the current thread for the specified number of milliseconds, then prints a newline.
	 * 
	 * @param delayMillis the length of time to keep the thread suspended in milliseconds.
	 */
	public static void ln(long delayMillis)
	{
		delay(delayMillis);
		System.out.println();
	}

	/**
	 * Suspends the current thread for a standard {@value #STD_DELAY} ms, then prints a newline and the provided string, in that order.
	 * 
	 * @param x The <code>String</code> to be printed.
	 */
	public static void lnprint(String x)
	{
		delay();
		System.out.println();
		System.out.print(x);
	}

	/**
	 * Suspends the current thread for a standard {@value #STD_DELAY} ms, then prints a newline and the provided Object, in that order.
	 * 
	 * @param x The <code>Object</code> to be printed.
	 */
	public static void lnprint(Object x)
	{
		lnprint(String.valueOf(x));
	}

	/**
	 * Prints the provided string.
	 * 
	 * @param s The <code>String</code> to be printed.
	 */
	public static void print(String s)
	{
		System.out.print(s);
	}

	/**
	 * Prints the provided Object.
	 * 
	 * @param obj The <code>Object</code> to be printed.
	 */
	public static void print(Object obj)
	{
		print(String.valueOf(obj));
	}

	/**
	 * Suspends the current thread for a standard {@value #STD_DELAY} ms.
	 */
	public static void delay()
	{
		delay(STD_DELAY);
	}

	/**
	 * Suspends the current thread for the specified number of milliseconds, then prints a newline.
	 * 
	 * @param millis the length of the delay in milliseconds.
	 */
	public static void delay(long millis)
	{
		try
		{
			Thread.sleep(millis);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
