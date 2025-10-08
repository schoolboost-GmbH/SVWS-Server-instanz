package de.svws_nrw.module.reporting.types.schueler;

/**
 * Eine Enum, um den Status der aus der DB geladenen Daten eine ReportingSchuelers zu speichern und schnell darauf zuzugreifen.
 */
public enum ReportingSchuelerDatenstatus {

	/** Erzieherdaten wurden bereits geladen. */
	ERZIEHER,

	/** Lernabschnittsdaten wurden bereits geladen. */
	LERNABSCHNITTE,

	/** Leistungsdaten wurden bereits geladen. */
	LEISTUNGSDATEN,

	/** Leistungsdaten wurden bereits geladen. */
	SPRACHBELEGUNGEN;

}
