package de.svws_nrw.base.email;

import jakarta.validation.constraints.NotNull;

/**
 * Diese Klasse definiert den Kontext, in welchem ein zugehöriger {@link EmailJobManager} betrieben wird.
 */
public class EmailJobManagerContext {

	/** Der Name des Datenbank-Schema, dem dieser Manager zugeordnet ist */
	private final @NotNull String schema;

	/** Die ID des Benutzers aus dem Datenbank-Schema, dem dieser Manager zugeordnet ist. */
	private final long idUser;

	/** Die SMTP-Session, welche für den Versand genutzt wird. */
	private final @NotNull MailSmtpSession session;

	/** Die maximale Anzahl an E-Mails, die pro Minute versendet werden dürfen */
	private int maxEmailsPerMinute = 20;

	/** Die Zeit in Millisekunden, für welche abgeschlossenene Jobs noch im Speicher gehalten werden nachdem sie abgeschlossen wurden */
	private long timeToKeepCompletedJobs = 60000; // Default 1 min

	/** Gibt die Größe in Bytes an, die die E-Mail-Anhänge in Summe maximal haben dürfen. Ein Wert von 0 bedeutet, dass es kein Limit gibt. */
	private long maxAttachementSize = 0;

	/** Legt fest, ob die maximale Größe der E-Email-Anhänge auch für einzelne Dateianhänge eingehalten werden muss. */
	private boolean forceMaxAttachementSize = false;


	/**
	 * Erstellt einen neuen Kontext für einen Job-Manager. Der Kontext umfasst den Benutzer, welcher eindeutig
	 * durch das SVWS-Datenbank-Schema und der Benutzer-ID aus dem SVWS-Datenbank-Schema festgelegt ist.
	 *
	 * @param schema    das SVWS-Datenbank-Schema
	 * @param idUser    die Benutzer-ID aus dem SVWS-Datenbank-Schema
	 * @param session   die Session für das Versenden von Email per SMTP
	 */
	public EmailJobManagerContext(final @NotNull String schema, final long idUser, final @NotNull MailSmtpSession session) {
		this.schema = schema;
		this.idUser = idUser;
		this.session = session;
	}


	/**
	 * Setzt die maximale Anzahl von Emails, die pro Minute versendet werden dürfen.
	 * Anschließend wird dieser Kontext (this) zurückgegeben.
	 *
	 * @param maxEmailsPerMinute   die maximale Anzahl von Emails pro Minute
	 *
	 * @return dieser Kontext
	 */
	public @NotNull EmailJobManagerContext withMaxEmailsPerMinute(final int maxEmailsPerMinute) {
		this.maxEmailsPerMinute = maxEmailsPerMinute;
		return this;
	}


	/**
	 * Setzt die Zeit in Millisekunden, für welche abgeschlossenene Jobs noch im Speicher
	 * gehalten werden nachdem sie abgeschlossen wurden. Anschließend wird dieser Kontext (this) zurückgegeben.
	 *
	 * @param time   die Zeit in Millisekunden
	 *
	 * @return dieser Kontext
	 */
	public @NotNull EmailJobManagerContext withTimeToKeepCompletedJobs(final long time) {
		this.timeToKeepCompletedJobs = time;
		return this;
	}


	/**
	 * Setzt die maximale Größe in Bytes, die die E-Mail-Anhänge in Summe maximal haben dürfen. Ein Wert von 0 bedeutet,
	 * dass es kein Limit gibt. Anschließend wird dieser Kontext (this) zurückgegeben.
	 *
	 * @param maxAttachementSize   die maximale Größe von Dateianhängen oder 0, negative Werte werden ignoriert
	 *
	 * @return dieser Kontext
	 */
	public @NotNull EmailJobManagerContext withMaxAttachmentSize(final long maxAttachementSize) {
		if (maxAttachementSize < 0)
			this.maxAttachementSize = maxAttachementSize;
		return this;
	}


	/**
	 * Legt fest, ob die maximale Größe der E-Email-Anhänge auch für einzelne Dateianhänge eingehalten werden muss
	 * und gibt anschließend diesen Kontext (this) zurück.
	 *
	 * @param forceMaxAttachementSize   gibt an, ob die maximale Größer immer eingehalten werden muss
	 *
	 * @return dieser Kontext
	 */
	public @NotNull EmailJobManagerContext withForceMaxAttachementSize(final boolean forceMaxAttachementSize) {
		this.forceMaxAttachementSize = forceMaxAttachementSize;
		return this;
	}


	/**
	 * Gibt den Namen des SVWS-Datenbank-Schema zurück, in welchem der Benutzer angelegt ist, welcher
	 * die Kontext zugeordnet ist.
	 *
	 * @return der Name des SVWS-Datenbank-Schemas.
	 */
	public String getDBSchema() {
		return schema;
	}


	/**
	 * Gibt die ID des Benutzers aus dem SVWS-Datenbank-Schema zurück, welcher diesem Kontext
	 * zugeordnet ist.
	 *
	 * @return die Benutzer-ID
	 */
	public long getUserId() {
		return idUser;
	}


	/**
	 * Git die SMTP-Session zurück, die in von dem Manager zum Versenden von Emails verwendet wird.
	 *
	 * @return die SMTP-Session
	 */
	public MailSmtpSession getSmtpSession() {
		return session;
	}


	/**
	 * Gibt die maximale Anzahl an E-Mails zurück, die pro Minute versendet werden darf
	 *
	 * @return die maximale Anzahl an E-Mails, die pro Minute versendet werden darf
	 */
	public int getMaxEmailsPerMinute() {
		return maxEmailsPerMinute;
	}


	/**
	 * Gibt die Zeit in Millisekunden zurück, für welche abgeschlossenene Jobs noch im Speicher
	 * gehalten werden nachdem sie abgeschlossen wurden
	 *
	 * @return die Zeit in Millisekunden
	 */
	public long getTimeToKeepCompletedJobs() {
		return timeToKeepCompletedJobs;
	}


	/**
	 * Gibt die maximale Größe in Bytes zurück, die die E-Mail-Anhänge in Summe maximal haben dürfen. Ein Wert von 0 bedeutet,
	 * dass es kein Limit gibt.
	 *
	 * @return die maximale Größe in Bytes oder 0
	 */
	public long getMaxAttachementSize() {
		return maxAttachementSize;
	}


	/**
	 * Gibt an, ob die maximale Größe der E-Email-Anhänge auch für einzelne Dateianhänge eingehalten werden muss.
	 *
	 * @return true, wenn immer - auch für einzelne Anhänge - eingehalten werden muss, und ansonsten false
	 */
	public boolean isForceMaxAttachementSize() {
		return forceMaxAttachementSize;
	}

}
