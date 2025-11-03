package de.svws_nrw.asd.validate.lehrer;

import java.util.Set;

import de.svws_nrw.asd.data.lehrer.LehrerPersonalabschnittsdaten;
import de.svws_nrw.asd.types.lehrer.LehrerEinsatzstatus;
import de.svws_nrw.asd.validate.Validator;
import de.svws_nrw.asd.validate.ValidatorKontext;
import jakarta.validation.constraints.NotNull;

/**
 * Dieser Validator führt eine Statistikprüfung auf das Pflichtstundensoll der Abschnittsdaten
 * eines Lehrers einer Schule aus.
 */
public final class ValidatorLehrerPersonalabschnittsdatenPflichtstundensoll extends Validator {

	/** Die Lehrer-Personalabschnittsdaten */
	private final @NotNull LehrerPersonalabschnittsdaten daten;

	/**
	 * Erstellt einen neuen Validator mit den übergebenen Daten und dem übergebenen Kontext
	 *
	 * @param daten     die Daten des Validators
	 * @param kontext   der Kontext des Validators
	 */
	public ValidatorLehrerPersonalabschnittsdatenPflichtstundensoll(final @NotNull LehrerPersonalabschnittsdaten daten,
			final @NotNull ValidatorKontext kontext) {
		super(kontext);
		this.daten = daten;
	}


	@Override
	protected boolean pruefe() {
		boolean success = true;
		final Double pflichtstundensoll = daten.pflichtstundensoll;
		final LehrerEinsatzstatus einsatzstatus = LehrerEinsatzstatus.getBySchluessel(daten.einsatzstatus);

		success = exec(0, () -> pflichtstundensoll == null, "Kein Wert im Feld 'pflichtstundensoll'.");
		if (!success)
			return false;

		success = exec(1, () -> (pflichtstundensoll == null || pflichtstundensoll < 0.0) || (pflichtstundensoll > 41.0),
				"Unzulässiger Wert im Feld 'pflichtstundensoll'. Zulässig sind im Stundenmodell Werte im Bereich von 0,00 bis 41,00 Wochenstunden. "
						+ "Im Minutenmodell zwischen 0,00 und 1845,00 Minuten.");

		if (!exec(2, () -> (einsatzstatus == LehrerEinsatzstatus.B) && (pflichtstundensoll == 0.0),
				"Bei Lehrkräften, die von einer anderen Schule abgeordnet wurden (Einsatzstatus = 'B'), darf das Pflichtstundensoll"
						+ " nicht 0,00 betragen."))
			success = false;

		final String beschaeftigungsart = daten.beschaeftigungsart;
		final @NotNull Set<String> setBeschaeftigungsart = Set.of("WV", "WT");
		final String fehlertext3 = "Ist bei einer Lehrkraft im Feld 'Pflichtstundensoll' der Wert = 0.00 eingetragen, so muss das Feld 'Einsatzstatus' den Schlüssel"
				+ " 'Stammschule, ganz oder teilweise auch an anderen Schulen tätig' oder die 'Beschäftigungsart' den Schlüssel 'Beamte auf"
				+ " Widerruf (LAA) in Vollzeit' bzw. 'Beamte auf Widerruf (LAA) in Teilzeit' aufweisen.";
		if (!exec(3, () -> (pflichtstundensoll == 0)
				&& (!LehrerEinsatzstatus.A.equals(einsatzstatus)
				&& !setBeschaeftigungsart.contains(beschaeftigungsart)),
				fehlertext3))
			success = false;

		return success;
	}

}
