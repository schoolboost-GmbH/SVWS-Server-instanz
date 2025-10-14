package de.svws_nrw.base.email;

import jakarta.validation.constraints.NotNull;

/**
 * Die Klasse beinhaltet die Informationen zu einem Attachment einer EMail-Empfängers.
 */
public class EmailJobAttachment {

	/** Der Dateiname des Anhangs */
	public final @NotNull String filename;

	/** Die Daten des Attachements als Byte-Array. */
	public final @NotNull byte[] data;

	/** Der Mime-Type des Dateianhangs (z.B. "application/pdf" oder "image/png") */
	public final @NotNull String mimetype;


	/**
	 * Erstellt einen neuen Anhang für den Email-Job
	 *
	 * @param filename   der Dateiname
	 * @param data       die Daten
	 * @param mimetype   der Mime-Type
	 */
	public EmailJobAttachment(final @NotNull String filename, final @NotNull byte[] data, final @NotNull String mimetype) {
		this.filename = filename;
		this.data = data;
		this.mimetype = mimetype;
	}

}
