package party.dabble.terminalGL;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.TYPE)
public @interface ModMarker {
	/**
	 * The full name of the mod.
	 */
	String name();

	/**
	 * The version number of the mod (optional).
	 */
	String version() default "";
}
