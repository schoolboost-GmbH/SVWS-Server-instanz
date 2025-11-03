package de.svws_nrw.asd.utils;

/**
 * Eine Exception, welche beim Laden von Core-Type-Ressourcen erzeugt wird
 */
public class CoreTypeRessourceException extends RuntimeException {

	private static final long serialVersionUID = 878123458912431L;

	/**
	 * Erzeugt eine neue Exception mit der übergebenen Nachricht und dem übergebenen Grund.
	 *
	 * @param message   die Nachricht
	 * @param cause     der Grund
	 */
	public CoreTypeRessourceException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
