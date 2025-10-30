package de.svws_nrw.core.abschluss.bk.d;

import de.svws_nrw.core.exceptions.DeveloperNotificationException;
import jakarta.validation.constraints.NotNull;

/**
 * Eine Aufzählung zur Unterscheidung der Arten der Prüfbedingungen.
 */
public enum BKGymPruefbedingungArt {

	/** Bedingungen, die für bestimmte Kursarten gelten */
	KURSART("KURSART"),

	/** Bedingungen, die für konkrete Fächer gelten */
	FACH("FACH"),

	/** Bedingungen, die für Fremdsprachen gelten */
	FREMDSPRACHE("FREMDSPRACHE"),

	/** Bedingungen, die für die Aufgabenfelder I, II und III gelten */
	AUFGABENFELD("AUFGABENFELD"),

	/** Bedingungen, die für die Gesamtanzahl von eingebrachten Kursen gelten */
	ANZAHLKURSE("ANZAHLKURSE"),

	/** Bedingungen, die für eine bestimmte Fachgruppe gilt */
	FACHGRUPPE("FACHGRUPPE"),

	/** Bedingungen, die für erreichte Punkte gilt */
	PUNKTE("PUNKTE"),

	/** Bedingungen, die für Gesamtanzahl von Defiziten gilt */
	DEFIZITE("DEFIZITE"),

	/** Bedingungen, die für Anzahl von Defiziten im Leistungskursbereich gilt */
	DEFIZITE_LK("DEFIZITE_LK");

	/** Das Kürzel für die Belegungsfehlerart */
	public final @NotNull String kuerzel;


	/**
	 * Erzeugt ein neues Objekt der PrüfbedingungsArt
	 *
	 * @param kuerzel        das Kürzel der PrüfbedingungArt
	 */
	BKGymPruefbedingungArt(final @NotNull String kuerzel) {
		this.kuerzel = kuerzel;
	}


	/**
	 * Gibt die Prüfbedingungsart anhand des übergebenen Kürzels zurück.
	 *
	 * @param kuerzel    das Kürzel der Prüfbedingungsart
	 *
	 * @return die Prüfbedingungsart
	 */
	public static @NotNull BKGymPruefbedingungArt fromKuerzel(final @NotNull String kuerzel) {
		return switch (kuerzel) {
			case "KURSART" -> KURSART;
			case "FACH" -> FACH;
			case "FREMDSPRACHE" -> FREMDSPRACHE;
			case "AUFGABENFELD" -> AUFGABENFELD;
			case "ANZAHLKURSE" -> ANZAHLKURSE;
			case "FACHGRUPPE" -> FACHGRUPPE;
			default -> throw new DeveloperNotificationException("Die Prüfungbedingungsart " + kuerzel + " gibt es nicht.");
		};
	}


	@Override
	public @NotNull String toString() {
		return kuerzel;
	}

}
