package de.svws_nrw.core.abschluss.bk.d;

import jakarta.validation.constraints.NotNull;

/**
 * Diese Aufzählung beinhaltet die Prüfungsbedingungen entsprechend der Anlage D.
 *
 */
public enum BKGymPruefbedingung {

	/** Prüfe, dass vier erste Leistungskurse eingebracht sind */
	KA_1("KA_1", BKGymPruefungsArt.ZULASSUNG, BKGymPruefbedingungArt.KURSART, "LK1", 4, "", null, "APO-BK AnlageD § 15 Abs. 2 Nr. 1",
			"Es sind weniger als acht Leistungskurse eingebracht worden."),

	/** Prüfe, dass vier zweite Leistungskurse eingebracht sind */
	KA_2("KA_2", BKGymPruefungsArt.ZULASSUNG, BKGymPruefbedingungArt.KURSART, "LK2", 4, "", null, "APO-BK AnlageD § 15 Abs. 2 Nr. 1 ",
			"Es sind weniger als acht Leistungskurse eingebracht worden."),

	/** Prüfe, dass vier Grundkurse des 3. Abifachs eingebracht sind */
	KA_3("KA_3", BKGymPruefungsArt.ZULASSUNG, BKGymPruefbedingungArt.KURSART, "AB3", 4, "", null, "APO-BK AnlageD § 15 Abs. 3 Nr. 1",
			"Es sind weniger als vier Kurse des dritten Abiturfachs eingebracht worden."),

	/** Prüfe, dass vier Grundkurse des 4. Abifachs eingebracht sind */
	KA_4("KA_4", BKGymPruefungsArt.ZULASSUNG, BKGymPruefbedingungArt.KURSART, "AB4", 4, "", null, "APO-BK AnlageD § 15 Abs. 3 Nr. 1",
			"Es sind weniger als vier Kurse des vierten Abiturfachs eingebracht worden."),

	/** Prüfe, dass vier Kurse des Fachs Deutsch eingebracht sind */
	FA_D("FA_D", BKGymPruefungsArt.ZULASSUNG, BKGymPruefbedingungArt.FACH, "Deutsch", 4, "", null, "APO-BK AnlageD § 15 Abs. 3 Nr. 2 a)",
			"Es sind weniger als vier Kurse Deutsch eingebracht worden."),

	/** Prüfe, dass vier Kurse des Fachs Mathematik eingebracht sind */
	FA_M("FA_M", BKGymPruefungsArt.ZULASSUNG, BKGymPruefbedingungArt.FACH, "Mathematik", 4, "", null, "APO-BK AnlageD § 15 Abs. 3 Nr. 2 c)",
			"Es sind weniger als vier Kurse Mathematik eingbracht worden."),

	/** Prüfe, dass zwei Kurse Gesellschaftslehre mit Geschichte eingebracht sind */
	FA_GMG("FA_GMG", BKGymPruefungsArt.ZULASSUNG, BKGymPruefbedingungArt.FACH, "Gesellschaftslehre mit Geschichte", 2, "", null, "APO-BK AnlageD § 15 Abs. 3 Nr. 2 e)",
			"Es sind weniger als zwei Kurse Gesellschaftslehre mit Geschichte eingbracht worden."),

	/** Prüfe, dass vier Kurse aus Aufgabenfeld II eingebracht sind */
	AF_II("AF_II", BKGymPruefungsArt.ZULASSUNG, BKGymPruefbedingungArt.AUFGABENFELD, "II", 4, "", null, "APO-BK AnlageD § 15 Abs. 3 Nr. 2 e)",
			"Es sind weniger als vier Kurse aus dem gesellschaftswissenschaftlichen Aufgabenbereich eingbracht worden."),

	/** Prüfe, dass vier Kurse der Fachgruppe Naturwissenschaft eingebracht sind */
	FG_NW("FG_NW", BKGymPruefungsArt.ZULASSUNG, BKGymPruefbedingungArt.FACHGRUPPE, "Naturwissenschaft", 4, "EF.1", null, "APO-BK AnlageD § 15 Abs. 3 Nr. 2 d)",
			"Es sind weniger als vier Kurse Mathematik eingbracht worden."),

	/** Prüfe, dass zweite Fremdsprache belegt wurde seit 11.1 */
	FS_1("FS_1", BKGymPruefungsArt.ZULASSUNG, BKGymPruefbedingungArt.FREMDSPRACHE, "FORTGEFUEHRT_NEU", 4, "", null, "APO-BK AnlageD § 15 Abs. 3 Nr. 2 b)",
			"Es sind weniger als vier Kurse einer Fremdsprache eingbracht worden."),

	/** Prüfe, dass mindestens zwei Kurse einer neu einsetzenden Fremdsprache eingebracht sind */
	FS_2("FS_2", BKGymPruefungsArt.ZULASSUNG, BKGymPruefbedingungArt.FREMDSPRACHE, "NEU_NICHT SI", 2, "", null, "APO-BK AnlageD § 15 Abs. 3",
			"Es sind weniger als zwei Kurse der neu einsetzenden Fremdsprache eingebracht worden, obwohl kein vier Jahre durchgängier Unterricht in der SI belegt wurde."),

	/** Prüfe, dass mindestens 4 Kurse der neu einsetzenden Fremdsprache belegt wurden, wenn eine zweite Fremdsprache nicht durchgängig vier Jahre in der SI belegt wurde.*/
	FS_3("FS_3", BKGymPruefungsArt.ZULASSUNG, BKGymPruefbedingungArt.FREMDSPRACHE, "MINIMUM", 4, "keine zweite Fremdsprache in SI", null, "APO-BK AnlageD § 15 Abs. 2 Nr. 4",
			"Es sind weniger als vier Kurse der neu einsetzenden Fremdsprache belegt worden, obwohl kein vier Jahre durchgängiger Unterricht in der SI belegt wurde."),

	/** Prüfe, dass mindestens 32 Kurse eingebracht wurden inklusive Leistungskurse. */
	AK_1("AK_1", BKGymPruefungsArt.ZULASSUNG, BKGymPruefbedingungArt.ANZAHLKURSE, "MININUM", 32, "ohne Facharbeit", null, "APO-BK AnlageD § 15 Abs. 2 Nr. 1",
			"Es sind weniger als 32 Kurse in den Block I eingebracht worden."),

	/** Prüfe, dass höchsten 39 Kurse eingebracht sind, wenn die Facharbeit eingebracht wurde */
	AK_2("AK_2", BKGymPruefungsArt.ZULASSUNG, BKGymPruefbedingungArt.ANZAHLKURSE, "MAXIMUM", 39, "mit Facharbeit", null, "APO-BK AnlageD § 15 Abs. 2 Nr. 1 in V. Abs. 4 Nr. 3",
			"Bei eingebrachter Facharbeit sind mehr als 39 Kurse eingebracht worden."),

	/** Prüfe, dass höchsten 40 Kurse eingebracht sind, wenn keine Facharbeit eingebracht wurde */
	AK_3("AK_3", BKGymPruefungsArt.ZULASSUNG, BKGymPruefbedingungArt.ANZAHLKURSE, "MAXIMUM", 40, "ohne Facharbeit", null, "APO-BK AnlageD § 15 Abs. 2 Nr. 1 in V. Abs. 4 Nr. 3",
			"Es sind mehr als 40 Kurse in den Block I eingebracht worden."),

	/** Prüfe, dass mindestens 200 Punkte erreicht wurden*/
	EP_1("EP_1", BKGymPruefungsArt.ZULASSUNG, BKGymPruefbedingungArt.PUNKTE, "MINIMUM", 200, "Block I", null, "APO-BK AnlageD § 15 Abs. 2 Nr. 2 in V. § 25 Abs. 3",
			"Es sind weniger als 200 Punkte im Block I erreicht worden."),

	/** Prüfe, dass nicht mehr als 3 Leistungskurs-Defizite aufweisen */
	AD_1("AD_1", BKGymPruefungsArt.ZULASSUNG, BKGymPruefbedingungArt.DEFIZITE_LK, "Leistungskurse", 3, "", null, "APO-BK AnlageD § 15 Abs. 2 Nr. 3",
			"Mehr als drei Leistungskurse weisen ein Defizit auf."),

	/** Prüfe, dass nicht mehr als 6 Defizite bei bis zu 32 eingebrachten Kursen vorliegen*/
	AD_2("AD_2", BKGymPruefungsArt.ZULASSUNG, BKGymPruefbedingungArt.DEFIZITE, "", 6, "", 32, "APO-BK AnlageD § 15 Abs. 2 Nr. 3 a)",
			"Es wurden 32 Kurse eingebracht und mehr als sechs davon weisen Defizite auf."),

	/** Prüfe, dass nicht mehr als 7 Defizite bei bis zu 37 eingebrachten Kursen vorliegen*/
	AD_3("AD_3", BKGymPruefungsArt.ZULASSUNG, BKGymPruefbedingungArt.DEFIZITE, "", 7, "", 37, "APO-BK AnlageD § 15 Abs. 2 Nr. 3 b)",
			"Es wurden 33 bis 37 Kurse eingebracht und mehr als sieben davon weisen Defizite auf."),

	/** Prüfe, dass nicht mehr als 8 Defizite bei bis zu 40 eingebrachten Kursen vorliegen*/
	AD_4("AD_4", BKGymPruefungsArt.ZULASSUNG, BKGymPruefbedingungArt.DEFIZITE, "", 8, "", 40, "APO-BK AnlageD § 15 Abs. 2 Nr. 3 c)",
			"Es wurden mehr als 37 Kurse eingebracht und mehr als acht davon weisen Defizite auf.");




	/** Der eindeutige Code der Bedingung */
	public final @NotNull String code;

	/** Die Art der Prüfung, zu der die Bedingung gehört*/
	public final @NotNull BKGymPruefungsArt pruefArt;

	/** Die Art der Bedingung */
	public final @NotNull BKGymPruefbedingungArt bbArt;

	/** Der erste Textwert-Parameter */
	public final @NotNull String text1;

	/** Der erste Zahlwert-Parameter */
	public final @NotNull Integer zahl1;

	/** Der zweite Textwert-Parameter */
	public final @NotNull String text2;

	/** Der zweite Zahlwert-Parameter*/
	public final Integer zahl2;

	/** Der Text des Fehlers, der ausgegeben wird */
	public final @NotNull String bezugAPOBK;

	/** Der Text des Fehlers, der ausgegeben wird */
	public final @NotNull String hinweis;


	/**
	 * Erstellt einen neue Prüfbedingung für die Aufzählung (s.o.).
	 *
	 * @param code         der eindeutige Code der Prüfbedingung
	 * @param pruefArt     die Prüfungsart
	 * @param bbArt        die PrüfbedingungsArt
	 * @param text1        der erste Textwert-Parameter
	 * @param zahl1        der erste Zahlenwert-Parameter
	 * @param text2        der zweite Textwert-Parameter
	 * @param zahl2        der zweite Zahlenwert-Parameter
	 * @param bezugAPOBK   die Stelle in der APOBK, an der die Bedingung steht.
	 * @param hinweis      der Hinweis, der bei Auftreten des Fehlers ausgegeben wird.
	 */
	BKGymPruefbedingung(final @NotNull String code, final @NotNull BKGymPruefungsArt pruefArt, final @NotNull BKGymPruefbedingungArt bbArt,
			final @NotNull String text1, final @NotNull Integer zahl1, final @NotNull String text2, final Integer zahl2,
			final @NotNull String bezugAPOBK, final @NotNull String hinweis) {
		this.code = code;
		this.pruefArt = pruefArt;
		this.bbArt = bbArt;
		this.text1 = text1;
		this.zahl1 = zahl1;
		this.text2 = text2;
		this.zahl2 = zahl2;
		this.bezugAPOBK = bezugAPOBK;
		this.hinweis = hinweis;
	}
}
