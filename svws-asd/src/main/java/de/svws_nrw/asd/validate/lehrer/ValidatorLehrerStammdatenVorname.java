package de.svws_nrw.asd.validate.lehrer;

import de.svws_nrw.asd.data.lehrer.LehrerStammdaten;
import de.svws_nrw.asd.validate.Validator;
import de.svws_nrw.asd.validate.ValidatorKontext;
import jakarta.validation.constraints.NotNull;

/**
 * Dieser Validator führt eine Statistikprüfung auf den Vornamen bei den Stammdaten
 * eines Lehrers einer Schule aus.
 */
public final class ValidatorLehrerStammdatenVorname extends Validator {

	/** Die Lehrer-Stammdaten */
	private final @NotNull LehrerStammdaten daten;

	/**
	 * Erstellt einen neuen Validator mit den übergebenen Daten und dem übergebenen Kontext
	 *
	 * @param daten     die Daten des Validators
	 * @param kontext   der Kontext des Validators
	 */
	public ValidatorLehrerStammdatenVorname(final @NotNull LehrerStammdaten daten,
			final @NotNull ValidatorKontext kontext) {
		super(kontext);
		this.daten = daten;
	}


	@Override
	protected boolean pruefe() {
		boolean success = true;
		final String vorname = daten.vorname;

		success = exec(0, () -> (vorname == null) || (vorname.length() == 0), "Vorname der Lehrkraft: Kein Wert vorhanden.");
		if (!success)
			return false;

		success = exec(1, () -> vorname.trim().isBlank(), "Vorname der Lehrkraft: Der Vorname darf nicht nur aus Leerzeichen bestehen.");
		if (!success)
			return false;

		success = exec(2, () -> daten.vorname.length() == 1,
				"Vorname der Lehrkraft: Der Vorname besteht aus nur einem Zeichen. Bitte überprüfen sie ihre Angaben.");

		if (!exec(3, () -> vorname.startsWith(" ") || vorname.startsWith("\t"),
				"Vorname der Lehrkraft: Die Eintragung des Nachnamens muss linksbündig erfolgen (ohne vorangestellte Leerzeichen oder Tabs)."))
			success = false;

		if (!exec(4, () -> !Character.isUpperCase(vorname.charAt(0)),
				"Vorname der Lehrkraft: Die erste Stelle des Vornamens muss mit einem Großbuchstaben besetzt sein."))
			success = false;

		if (!exec(5, () -> (vorname.length() > 1) && Character.isUpperCase(vorname.charAt(1)),
				"Vorname der Lehrkraft: Die zweite Stelle des Vornamens ist mit einem Großbuchstaben besetzt. Bitte stellen sie sicher, dass nur der erste Buchstabe des Vornamens ein Großbuchstabe ist. Bitte schreiben Sie auf ihn folgende Buchstaben klein."))
			success = false;


		if (!exec(6, () -> (vorname.length() > 2) && Character.isUpperCase(vorname.charAt(2)),
				"Vorname der Lehrkraft: Die dritte Stelle des Vornamens ist mit einem Großbuchstaben besetzt. Bitte stellen sie sicher, dass nur der erste Buchstabe des Vornamens ein Großbuchstabe ist. Bitte schreiben Sie auf ihn folgende Buchstaben klein."))
			success = false;

		if (!exec(7, () -> vorname.contains(" -") || vorname.contains("- "),
				"Vorname der Lehrkraft: Der Vorname enthält überflüssige Leerzeichen vor und/oder nach dem Bindestrich."))
			success = false;

		if (!exec(8, () -> {
			final String nLower = vorname.toLowerCase();
			return nLower.startsWith("frau ") || nLower.startsWith("herr ");
		}, "Vorname der Lehrkraft: Die Anrede (Frau oder Herr) gehört nicht in den Vornamen."))
			success = false;

		return success;
	}

}
