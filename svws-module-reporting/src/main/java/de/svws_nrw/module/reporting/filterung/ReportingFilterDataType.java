package de.svws_nrw.module.reporting.filterung;

/**
 * Aufzählung zur Definition der Datentypen, nach denen in HTML-Contexts auf Basis der idsDetaildaten gefiltert werden kann.
 * Dies wird in der Regel nur dann verwendet, wenn die Hauptdatenquelle ein Ergebnisobjekt mit vielen Informationen ist, wie die Blockungsergebnisse oder
 * die Klausurplanung.
 */
public enum ReportingFilterDataType {
	/**
	 * Undefinierte Filterung
	 */
	UNDEFINED,

	/**
	 * Filterung nach Schülern.
	 */
	SCHUELER,

	/**
	 * Filterung nach Schülern.
	 */
	LEHRER,

	/**
	 * Filterung nach Schülern.
	 */
	KLASSEN,

	/**
	 * Filterung nach Schülern.
	 */
	KURSE
}
