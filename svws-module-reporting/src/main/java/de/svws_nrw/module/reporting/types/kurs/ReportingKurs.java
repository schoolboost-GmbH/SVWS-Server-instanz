package de.svws_nrw.module.reporting.types.kurs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import de.svws_nrw.module.reporting.types.ReportingBaseType;
import de.svws_nrw.module.reporting.types.fach.ReportingFach;
import de.svws_nrw.module.reporting.types.jahrgang.ReportingJahrgang;
import de.svws_nrw.module.reporting.types.klasse.ReportingKlasse;
import de.svws_nrw.module.reporting.types.lehrer.ReportingLehrer;
import de.svws_nrw.module.reporting.types.schueler.ReportingSchueler;
import de.svws_nrw.module.reporting.types.schueler.lernabschnitte.ReportingSchuelerLeistungsdaten;
import de.svws_nrw.module.reporting.types.schueler.lernabschnitte.ReportingSchuelerLernabschnitt;
import de.svws_nrw.module.reporting.types.schule.ReportingSchuljahresabschnitt;


/**
 * Basis-Klasse im Rahmen des Reportings für Daten vom Typ Kurs.
 */
public class ReportingKurs extends ReportingBaseType {

	/** Eine Liste der Klassen, deren Schüler den Kurs belegen. */
	private final List<ReportingKlasse> klassen = new ArrayList<>();

	/** Eine Map mit den individuellen Kursarten der Schüler zur ID des Schüler. */
	private final Map<Long, String> mapSchuelerIndividuelleKursarten = new HashMap<>();

	/** Eine Map mit den individuellen Kursarten der Schüler und deren Anzahl im Kurs. */
	private final Map<String, Integer> mapStatistikIndividuelleKursarten = new HashMap<>();


	/** Ggf. die Zeugnisbezeichnung des Kurses. */
	protected String bezeichnungZeugnis;

	/** Das Fach, das dem Kurs zugeordnet ist. */
	protected ReportingFach fach;

	/** Die ID des Kurses. */
	protected long id;

	/** Gibt an, ob der Kurs zu einem epochalen Unterricht gehört. */
	protected boolean istEpochalunterricht;

	/** Gibt an, ob der Eintrag in der Anwendung sichtbar sein soll oder nicht. */
	protected boolean istSichtbar;

	/** Die Jahrgänge, denen der Kurs zugeordnet ist. */
	protected List<ReportingJahrgang> jahrgaenge;

	/** Das Kürzel des Kurses. */
	protected String kuerzel;

	/** Die allgemeine Kursart, welche zur Filterung der speziellen Kursarten verwendet wird. */
	protected String kursartAllg;

	/** Die Lehrkraft, die den Kurs unterrichtet und verantwortlich leitet. */
	protected ReportingLehrer kursleitung;

	/** Die Nummern der Kurs-Schienen, in welchen sich der Kurs befindet - sofern eine Schiene zugeordnet wurde */
	protected List<Integer> schienen;

	/** Die IDs der Schüler des Kurses. */
	protected List<Long> idsSchueler;

	/** Die Schüler des Kurses. */
	protected List<ReportingSchueler> schueler;

	/** Der Schuljahresabschnitt des Kurses. */
	protected ReportingSchuljahresabschnitt schuljahresabschnitt;

	/** Die Schulnummer des Kurses, falls der Kurs an einer anderen Schule stattfindet. */
	protected Integer schulnummer;

	/** Die Sortierreihenfolge des Listen-Eintrags. */
	protected int sortierung;

	/** Die Wochenstunden des Kurses für die Schüler. */
	protected int wochenstunden;

	/** Eine Map mit den Wochenstunden der Lehrkräfte zu deren ID. */
	protected Map<Long, Double> wochenstundenLehrkraefte;

	/** Die Lehrkräfte, die den Kurs neben der Kursleitung unterrichten. */
	protected List<ReportingLehrer> zusatzLehrkraefte;


	/**
	 * Erstellt ein neues Reporting-Objekt auf Basis dieser Klasse.
	 *
	 * @param bezeichnungZeugnis 		Ggf. die Zeugnisbezeichnung des Kurses.
	 * @param fach 						Das Fach, das dem Kurs zugeordnet ist.
	 * @param id 						Die ID des Kurses.
	 * @param istEpochalunterricht 		Gibt an, ob der Kurs zu einem epochalen Unterricht gehört.
	 * @param istSichtbar 				Gibt an, ob der Eintrag in der Anwendung sichtbar sein soll oder nicht.
	 * @param jahrgaenge 				Die Jahrgänge, denen der Kurs zugeordnet ist.
	 * @param kuerzel 					Das Kürzel des Kurses.
	 * @param kursartAllg 				Die allgemeine Kursart, welche zur Filterung der speziellen Kursarten verwendet wird.
	 * @param kursleitung 				Der Lehrer, der den Kurs unterrichtet und verantwortlich leitet.
	 * @param schienen 					Die Nummern der Kurs-Schienen, in welchen sich der Kurs befindet - sofern eine Schiene zugeordnet wurde.
	 * @param schueler 					Die Schüler des Kurses.
	 * @param idsSchueler 				Die Schüler des Kurses als Liste ihrer IDs.
	 * @param schuljahresabschnitt 		Der Schuljahresabschnitt des Kurses.
	 * @param schulnummer 				Die Schulnummer des Kurses, falls der Kurs an einer anderen Schule stattfindet.
	 * @param sortierung 				Die Sortierreihenfolge des Listen-Eintrags.
	 * @param wochenstunden 			Die Wochenstunden des Kurses für die Schüler.
	 * @param wochenstundenLehrkraefte 	Eine Map mit den Wochenstunden der Lehrkräfte zu deren ID.
	 * @param zusatzLehrkraefte 		Die Lehrkräfte, die den Kurs neben der Kursleitung unterrichten.
	 */
	public ReportingKurs(final String bezeichnungZeugnis, final ReportingFach fach, final long id, final boolean istEpochalunterricht,
			final boolean istSichtbar, final List<ReportingJahrgang> jahrgaenge, final String kuerzel, final String kursartAllg,
			final List<Integer> schienen, final List<Long> idsSchueler, final List<ReportingSchueler> schueler, final ReportingLehrer kursleitung,
			final ReportingSchuljahresabschnitt schuljahresabschnitt, final Integer schulnummer, final int sortierung,
			final int wochenstunden, final Map<Long, Double> wochenstundenLehrkraefte, final List<ReportingLehrer> zusatzLehrkraefte) {
		this.bezeichnungZeugnis = bezeichnungZeugnis;
		this.fach = fach;
		this.id = id;
		this.istEpochalunterricht = istEpochalunterricht;
		this.istSichtbar = istSichtbar;
		this.jahrgaenge = jahrgaenge;
		this.kuerzel = kuerzel;
		this.kursartAllg = kursartAllg;
		this.kursleitung = kursleitung;
		this.schienen = schienen;
		this.idsSchueler = idsSchueler;
		this.schueler = schueler;
		this.schuljahresabschnitt = schuljahresabschnitt;
		this.schulnummer = schulnummer;
		this.sortierung = sortierung;
		this.wochenstunden = wochenstunden;
		this.wochenstundenLehrkraefte = wochenstundenLehrkraefte;
		this.zusatzLehrkraefte = zusatzLehrkraefte;
	}


	/**
	 * Hashcode der Klasse
	 * @return Hashcode der Klasse
	 */
	public int hashCode() {
		return 31 + Long.hashCode(id);
	}

	/**
	 * Equals der Klasse
	 * @param obj Das Vergleichsobjekt
	 * @return	True, falls es das gleiche Objekt ist, andernfalls false.
	 */
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof final ReportingKurs other))
			return false;
		return (id == other.id);
	}


	// ##### Berechnete Methoden #####

	/**
	 * Auflistung der Jahrgänge des Kurses als kommaseparierte Liste der Kürzel.
	 *
	 * @return		Kommaseparierte Liste der Jahrgänge des Kurses
	 */
	public String auflistungJahrgaenge() {
		final List<ReportingJahrgang> listeJahrgaenge = this.jahrgaenge();
		if (listeJahrgaenge.isEmpty())
			return "";
		return listeJahrgaenge.stream().filter(Objects::nonNull).map(ReportingJahrgang::kuerzel).collect(Collectors.joining(","));
	}

	/**
	 * Auflistung der Klassen des Kurses als kommaseparierte Liste der Kürzel.
	 *
	 * @return		Kommaseparierte Liste der Klassen der Schüler des Kurses
	 */
	public String auflistungKlassen() {
		final List<ReportingKlasse> listeKlassen = this.klassen();
		if (listeKlassen.isEmpty())
			return "";
		return listeKlassen.stream().filter(Objects::nonNull).map(ReportingKlasse::kuerzel).collect(Collectors.joining(","));
	}

	/**
	 * Auflistung der Lehrkräfte des Kurses als kommaseparierte Liste der Kürzel.
	 *
	 * @return		Kommaseparierte Liste der Lehrkräfte, beginnend mit der Kursleitung.
	 */
	public String auflistungLehrkraefte() {
		final List<ReportingLehrer> listeLehrkraefte = lehrkraefte();
		if (listeLehrkraefte.isEmpty())
			return "";
		return listeLehrkraefte.stream().filter(Objects::nonNull).map(ReportingLehrer::kuerzel).collect(Collectors.joining(","));
	}

	/**
	 * Ermittelte alle Klassen der Schüler, welche im Kurs vertreten sind.
	 *
	 * @return Liste der Klassen der Schüler im Kurs.
	 */
	public List<ReportingKlasse> klassen() {
		final List<ReportingSchueler> reportingSchueler = this.schueler();
		if (klassen.isEmpty() && (reportingSchueler != null) && !reportingSchueler.isEmpty()) {
			final List<ReportingKlasse> result = new ArrayList<>();
			for (final ReportingSchueler s : reportingSchueler) {
				final ReportingSchuelerLernabschnitt lernabschnitt = s.aktiverLernabschnittInSchuljahresabschnitt(this.schuljahresabschnitt);
				if ((lernabschnitt != null) && (lernabschnitt.klasse() != null))
					result.add(lernabschnitt.klasse());
			}
			if (!result.isEmpty()) {
				this.klassen.addAll(result.stream().distinct().sorted(Comparator.comparing(ReportingKlasse::kuerzel)).toList());
			}
		}
		return this.klassen;
	}


	/**
	 * Gibt die individuelle Kursart für den angegebenen Schüler anhand seiner ID zurück.
	 * Falls keine spezifische Kursart für den Schüler vorhanden ist, wird ein leerer String zurückgegeben.
	 * Die Methode initialisiert oder aktualisiert zuerst die interne Map, bevor die Kursart abgerufen wird.
	 *
	 * @param idSchueler Die ID des Schülers, für den die individuelle Kursart ermittelt werden soll.
	 *
	 * @return Die individuelle Kursart des Schülers als String. Gibt einen leeren String zurück,
	 *         falls keine spezifische Kursart hinterlegt ist oder der idSchueler null ist
	 */
	public String schuelerIndividuelleKursart(final Long idSchueler) {
		if (idSchueler == null)
			return "";
		this.erstelleMapIndividuelleKursartenProSchueler();
		return this.mapSchuelerIndividuelleKursarten.getOrDefault(idSchueler, "");
	}

	/**
	 * Befüllt die Hashmap der individuellen Kursarten pro Schüler-ID.
	 * Ist für einen Schüler keine individuelle Kursart vorhanden, wird ein leerer String gespeichert.
	 * Die Map wird nur einmal befüllt und anschließend zwischengespeichert.
	 */
	private void erstelleMapIndividuelleKursartenProSchueler() {
		// Wenn die HashMap bereits gefüllt wurde, muss sie nicht noch einmal erstellt werden.
		if (!this.mapSchuelerIndividuelleKursarten.isEmpty())
			return;

		// Wenn keine Schüler im Kurs sind, breche die Berechnung ebenfalls ab.
		final List<ReportingSchueler> kursSchueler = this.schueler();
		if ((kursSchueler == null) || kursSchueler.isEmpty())
			return;

		for (final ReportingSchueler reportingSchueler : kursSchueler) {
			if (reportingSchueler == null)
				continue;

			final Long idSchueler = (reportingSchueler.id() <= 0) ? null : reportingSchueler.id();
			if (idSchueler == null)
				continue;

			// Hole den aktiven Lernabschnitt des Schülers aus dem Schuljahresabschnitt dieses Kurses.
			final ReportingSchuelerLernabschnitt lernabschnitt =
					reportingSchueler.aktiverLernabschnittInSchuljahresabschnitt(this.schuljahresabschnitt);
			if (lernabschnitt == null) {
				this.mapSchuelerIndividuelleKursarten.put(idSchueler, "");
				continue;
			}

			final ReportingSchuelerLeistungsdaten leistungsdaten = lernabschnitt.leistungsdatenZurIdKurs(this.id);
			if (leistungsdaten == null) {
				this.mapSchuelerIndividuelleKursarten.put(idSchueler, "");
				continue;
			}

			final String individuelleKursart = (leistungsdaten.kursart() == null) ? "" : leistungsdaten.kursart().trim();
			this.mapSchuelerIndividuelleKursarten.put(idSchueler, (individuelleKursart.isEmpty() ? "" : individuelleKursart));
		}
	}

	/**
	 * Erstellt aus den Geschlechtern der Schüler eine Statistik in der Form (m/w/d) für diese Klasse.
	 *
	 * @return Statistik in der Form (m/w/d).
	 */
	public String statistikGeschlechter() {
		final List<ReportingSchueler> reportingSchueler = this.schueler();
		if ((reportingSchueler == null) || reportingSchueler.isEmpty())
			return "(0/0/0)";
		final long anzahlM = reportingSchueler.stream().filter(s -> "m".equalsIgnoreCase(s.geschlecht().kuerzel)).count();
		final long anzahlW = reportingSchueler.stream().filter(s -> "w".equalsIgnoreCase(s.geschlecht().kuerzel)).count();
		final long anzahlD = reportingSchueler.stream().filter(s -> "d".equalsIgnoreCase(s.geschlecht().kuerzel)).count();
		return String.format("(%d/%d/%d)", anzahlM, anzahlW, anzahlD);
	}

	/**
	 * Erstellt eine Statistik der Kursarten und gibt diese als formatierte Zeichenkette zurück. Die Statistik zeigt die Anzahl der Kurse pro Kursart,
	 * getrennt durch Kommas.
	 *
	 * @return Eine formatierte Zeichenkette, die die Statistik der Kursarten darstellt, z. B. "Kursart1: Anzahl, Kursart2: Anzahl".
	 */
	public String statistikIndividuelleKursarten() {
		this.erstelleMapStatistikKursarten();
		final StringBuilder stringBuilder = new StringBuilder();
		// Sortiere die Einträge alphabetisch nach Kursart
		this.mapStatistikIndividuelleKursarten.entrySet().stream()
				.sorted(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER))
				.forEach(kursart -> {
					if (!stringBuilder.isEmpty())
						stringBuilder.append(", ");
					stringBuilder.append(kursart.getKey()).append(": ").append(kursart.getValue());
				});
		return stringBuilder.toString();
	}

	/**
	 * Befüllt die HashMap mit der Statistik zu den Kursarten auf Basis der individuellen Kursarten der Schüler des Kurses aus den Leistungsdaten des
	 * Lernabschnitts dieses Kurses. Die HashMap wird nur einmal berechnet und anschließend zwischengespeichert.
	 */
	private void erstelleMapStatistikKursarten() {
		// Wenn die HashMap bereits gefüllt wurde, muss nicht noch einmal die Statistik erstellt werden.
		if (!this.mapStatistikIndividuelleKursarten.isEmpty())
			return;

		// Stelle sicher, dass die individuellen Kursarten pro Schüler ermittelt wurden
		this.erstelleMapIndividuelleKursartenProSchueler();

		// Wenn keine Schüler im Kurs sind, breche die Berechnung ebenfalls ab.
		final List<ReportingSchueler> kursSchueler = this.schueler();
		if ((kursSchueler == null) || kursSchueler.isEmpty())
			return;

		// Zähle die nicht-leeren Kursarten aus der vorbereiteten Map
		for (final ReportingSchueler reportingSchueler : kursSchueler) {
			if (reportingSchueler == null)
				continue;
			final String individuelleKursart = this.mapSchuelerIndividuelleKursarten.getOrDefault(reportingSchueler.id(), "");
			if ((individuelleKursart != null) && !individuelleKursart.isEmpty())
				this.mapStatistikIndividuelleKursarten.merge(individuelleKursart, 1, Integer::sum);
		}
	}

	/**
	 * Gibt eine Liste aller Lehrkräfte des Kurses aus, wobei die erste die Kursleitung ist.
	 *
	 * @return		Die Liste der Lehrkräfte im Kurs, beginnend mit der Kursleitung.
	 */
	public List<ReportingLehrer> lehrkraefte() {
		final List<ReportingLehrer> listeLehrkraefte = new ArrayList<>();
		if (kursleitung != null)
			listeLehrkraefte.add(kursleitung);
		if ((zusatzLehrkraefte != null) && !zusatzLehrkraefte.isEmpty())
			listeLehrkraefte.addAll(zusatzLehrkraefte);
		return listeLehrkraefte;
	}

	/**
	 * Gibt die Wochenstunden zur ID einer Lehrkraft zurück.
	 *
	 * @param id	Die ID der Lehrkraft.
	 *
	 * @return		Die Wochenstunden der Lehrkraft in diesem Kurs.
	 */
	public double wochenstundenLehrerZurID(final Long id) {
		if ((id == null) || !wochenstundenLehrkraefte.containsKey(id))
			return 0;
		return wochenstundenLehrkraefte.get(id);
	}


	// ##### Getter #####

	/**
	 * Ggf. die Zeugnisbezeichnung des Kurses.
	 *
	 * @return Inhalt des Feldes bezeichnungZeugnis
	 */
	public String bezeichnungZeugnis() {
		return bezeichnungZeugnis;
	}

	/**
	 * Das Fach, das dem Kurs zugeordnet ist.
	 *
	 * @return Inhalt des Feldes fach
	 */
	public ReportingFach fach() {
		return fach;
	}

	/**
	 * Die ID des Kurses.
	 *
	 * @return Inhalt des Feldes id
	 */
	public long id() {
		return id;
	}

	/**
	 * Gibt an, ob der Kurs zu einem epochalen Unterricht gehört.
	 *
	 * @return Inhalt des Feldes istEpochalunterricht
	 */
	public boolean istEpochalunterricht() {
		return istEpochalunterricht;
	}

	/**
	 * Gibt an, ob der Eintrag in der Anwendung sichtbar sein soll oder nicht.
	 *
	 * @return Inhalt des Feldes istSichtbar
	 */
	public boolean istSichtbar() {
		return istSichtbar;
	}

	/**
	 * Die Jahrgänge, denen der Kurs zugeordnet ist.
	 *
	 * @return Inhalt des Feldes jahrgaenge
	 */
	public List<ReportingJahrgang> jahrgaenge() {
		return jahrgaenge;
	}

	/**
	 * Das Kürzel des Kurses.
	 *
	 * @return Inhalt des Feldes kuerzel
	 */
	public String kuerzel() {
		return kuerzel;
	}

	/**
	 * Die allgemeine Kursart, welche zur Filterung der speziellen Kursarten verwendet wird.
	 *
	 * @return Inhalt des Feldes kursartAllg
	 */
	public String kursartAllg() {
		return kursartAllg;
	}

	/**
	 * Die Lehrkraft, die den Kurs unterrichtet und verantwortlich leitet.
	 *
	 * @return Inhalt des Feldes lehrer
	 */
	public ReportingLehrer kursleitung() {
		return kursleitung;
	}

	/**
	 * Die Nummern der Kurs-Schienen, in welchen sich der Kurs befindet - sofern eine Schiene zugeordnet wurde.
	 *
	 * @return Inhalt des Feldes schienen
	 */
	public List<Integer> schienen() {
		return schienen;
	}

	/**
	 * Die Schüler des Kurses.
	 *
	 * @return Inhalt des Feldes schueler
	 */
	public List<ReportingSchueler> schueler() {
		return schueler;
	}

	/**
	 * Die Schüler des Kurses als Liste ihrer IDs.
	 *
	 * @return Inhalt des Feldes idsSchueler
	 */
	public List<Long> idsSchueler() {
		return idsSchueler;
	}


	/**
	 * Der Schuljahresabschnitt des Kurses.
	 *
	 * @return Inhalt des Feldes schuljahresabschnitt
	 */
	public ReportingSchuljahresabschnitt schuljahresabschnitt() {
		return schuljahresabschnitt;
	}

	/**
	 * Die Schulnummer des Kurses, falls der Kurs an einer anderen Schule stattfindet.
	 *
	 * @return Inhalt des Feldes schulnummer
	 */
	public Integer schulnummer() {
		return schulnummer;
	}

	/**
	 * Die Sortierreihenfolge des Listen-Eintrags.
	 *
	 * @return Inhalt des Feldes sortierung
	 */
	public int sortierung() {
		return sortierung;
	}

	/**
	 * Die Wochenstunden des Kurses für die Schüler.
	 *
	 * @return Inhalt des Feldes wochenstunden
	 */
	public int wochenstunden() {
		return wochenstunden;
	}

	/**
	 * Eine Map mit den Wochenstunden der Lehrkräfte zu deren ID.
	 *
	 * @return Inhalt des Feldes wochenstundenLehrkraefte
	 */
	public Map<Long, Double> wochenstundenLehrkraefte() {
		return wochenstundenLehrkraefte;
	}

	/**
	 * Die Lehrkräfte, die den Kurs neben der Kursleitung unterrichten.
	 *
	 * @return Inhalt des Feldes zusatzLehrkraefte
	 */
	public List<ReportingLehrer> zusatzLehrkraefte() {
		return zusatzLehrkraefte;
	}

}
