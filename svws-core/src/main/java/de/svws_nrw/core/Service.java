package de.svws_nrw.core;

import de.svws_nrw.core.logger.LogConsumerList;
import de.svws_nrw.core.logger.Logger;
import jakarta.validation.constraints.NotNull;

/**
 * Diese generische Klasse dient als Basisklasse für einfache Dienste bzw. Algorithmen,
 * welche einen Listen-basierten Logger verwenden.
 */
public abstract class Service {

	/** Die Instanz des Logger, der von diesem Service genutzt wird */
	protected @NotNull Logger logger = new Logger();

	/** Die Instanz des Consumers von Log-Informationen. In diesem Fall eine einfache ArrayList */
	protected @NotNull LogConsumerList log = new LogConsumerList();

	/**
	 * Erstellt einen neuen Service, dessen Logger automatisch in einen ArrayList loggt.
	 */
	protected Service() {
		this.logger.addConsumer(log);
	}

	/**
	 * Gibt die Logger-Instanz von diesem Service zurück.
	 *
	 * @return die Logger-Instanz.
	 */
	public @NotNull Logger getLogger() {
		return logger;
	}


	/**
	 * Gibt das Log dieses Services zurück.
	 *
	 * @return das Log dieses Services
	 */
	public @NotNull LogConsumerList getLog() {
		return log;
	}

}
