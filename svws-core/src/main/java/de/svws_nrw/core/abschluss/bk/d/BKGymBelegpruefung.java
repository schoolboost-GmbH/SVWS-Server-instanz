package de.svws_nrw.core.abschluss.bk.d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.svws_nrw.asd.data.schule.BeruflichesGymnasiumStundentafel;
import de.svws_nrw.asd.data.schule.BeruflichesGymnasiumStundentafelFach;
import de.svws_nrw.core.exceptions.DeveloperNotificationException;
import de.svws_nrw.core.types.gost.GostAbiturFach;
import de.svws_nrw.core.types.gost.GostHalbjahr;
import jakarta.validation.constraints.NotNull;

/**
 * Die abstrakte Klasse für die Belegprüfungen bei Bildungsgängen.
 */
public abstract class BKGymBelegpruefung {

	/** Die Abiturdaten-Manager */
	protected final @NotNull BKGymAbiturdatenManager manager;

	/** Die Stundentafeln laut APO-BK der Anlage */
	final @NotNull List<BeruflichesGymnasiumStundentafel> stundentafeln;

	/** Die Belegungsfehler, die für jede Stundentafel bei der Prüfung festgehalten werden. */
	private final @NotNull HashMap<BeruflichesGymnasiumStundentafel, List<BKGymBelegungsfehler>> mapBelegungsfehler = new HashMap<>();

	/** Die Liste von Belegungsfehlern der am besten passenden Stundentafel */
	private @NotNull List<BKGymBelegungsfehler> besteFehlerliste = new ArrayList<>();

	/** Flag ob neue Fehler hinzugekommen sind */
	private boolean dirty = false;

	/** Flag ob Fehler ausgegeben werden sollen */
	private boolean fehlerAusgeben = true;


	/**
	 * Erzeugt eine neue Belegprüfung mit dem angegebenen Manager.
	 *
	 * @param manager   der Manager für die Abiturdaten
	 */
	protected BKGymBelegpruefung(final @NotNull BKGymAbiturdatenManager manager) {
		this.manager = manager;
		stundentafeln = manager.getStundentafeln();
	}


	/**
	 * Fügt einen Belegungsfehler zu der Belegprüfung hinzu. Diese Methode wird von den Sub-Klassen
	 * aufgerufen, wenn dort ein Belegungsfehler erkannt wird.
	 *
	 * @param tafel    die Stundentafel
	 * @param fehler   der hinzuzufügende Belegungsfehler
	 *
	 * @return true, falls ein Fehler vorliegt false, wenn nur ein Hinweis ausgegeben wurde.
	 */
	protected boolean addFehler(final @NotNull BeruflichesGymnasiumStundentafel tafel, final @NotNull BKGymBelegungsfehler fehler) {
		if (fehlerAusgeben) {
			final List<BKGymBelegungsfehler> fehlerliste = mapBelegungsfehler.get(tafel);
			if (fehlerliste != null && !fehlerliste.contains(fehler)) {
				fehlerliste.add(fehler);
				dirty = true;
			}
		}
		return fehler.istFehler();
	}


	/**
	 * Ermittelt die Stundentafel mit den wenigsten Fehlern und gibt die zugehörigen Belegungsfehler aus
	 */
	private void ermittleBesteTafel() {
		if (dirty) {
			int minFehlerZahl = Integer.MAX_VALUE;
			for (final @NotNull List<BKGymBelegungsfehler> fehlerliste : mapBelegungsfehler.values()) {
				int fehlerZahl = 0;
				for (final @NotNull BKGymBelegungsfehler fehler : fehlerliste)
					fehlerZahl += fehler.wert;
				if (fehlerZahl < minFehlerZahl) {
					minFehlerZahl = fehlerZahl;
					besteFehlerliste = fehlerliste;
				}
			}
		}
		dirty = false;
	}


	/**
	 * Ermittelt die Stundentafel mit den wenigsten Fehlern und gibt die zugehörigen Belegungsfehler aus
	 *
	 * @return die Belegungsfehler der Stundentafel
	 */
	public @NotNull List<BKGymBelegungsfehler> getBelegungsfehler() {
		ermittleBesteTafel();
		return besteFehlerliste;
	}


	/**
	 * Gibt zurück, ob mindestens eine Stundentafel existiert, die keine "echten" Belegungsfehler hat. Warnungen und Hinweise werden toleriert.
	 *
	 * @return true, wenn kein "echter" Belegungsfehler vorliegt, und ansonsten false.
	 */
	public boolean istErfolgreich() {
		for (final @NotNull BKGymBelegungsfehler fehler : getBelegungsfehler())
			if (!fehler.istInfo())
				return false;
		return true;
	}


	/**
	 * Die Methode wird zur Durchführung der Belegprüfung aufgerufen.
	 *
	 * Sie führt zuerst die allgemeinen Prüfungen aus, die für alle Anlagen des beruflichen Gymnasiums identisch sind.
	 * Im Anschluss werden dann ggf. die spezifischen Prüfungen für die jeweilige Anlage durchgeführt.
	 * Hierzu ist bei Bedarf die Methode spezifischePruefungen in der Kindklasse zu implementieren.
	 */
	public void pruefe() {
		for (final @NotNull BeruflichesGymnasiumStundentafel tafel : stundentafeln) {
			mapBelegungsfehler.put(tafel, new ArrayList<>());
			final @NotNull Map<Integer, List<BeruflichesGymnasiumStundentafelFach>> mapFaecherByIndex = manager.getMapFaecherFromTafelByIndex(tafel);
			final @NotNull Map<BeruflichesGymnasiumStundentafelFach, List<BKGymAbiturFachbelegung>> mapBelegungByFach =
					manager.getMapBelegungenForTafelByFach(tafel);
			fehlerAusgeben = false;
			bewegeBelegungZuWahlfach(tafel, mapFaecherByIndex, mapBelegungByFach);
			fehlerAusgeben = true;
			allgemeinePruefungen(tafel, mapFaecherByIndex, mapBelegungByFach);
			spezifischePruefungen(tafel, mapFaecherByIndex, mapBelegungByFach);
		}
	}


	/**
	 * Diese Methode identifiziert für Positionen einer Stundentafel, die Wahlmöglichkeiten vorsieht,
	 * ob mehrere Belegungen vorliegen, von denen nur eine der Position zugewiesen wird und die anderen
	 * dem Wahlfach zugeschlagen wird.
	 * Es sollte nur ein Fach pro Position vorhanden sein, dass in der Gruppe Berufsbezogen bzw. Berufsübergreifend ist.
	 * Die anderen Belegungen müssten Differenzierung zugewiesen sein.
	 * Dafür wird ermittelt welche Belegung den Vorgaben der Position entspricht. Die erste wird behalten, die
	 * anderen werden nach Wahlfach verschoben.
	 *
	 * @param tafel               die zu überprüfende Stundentafel
	 * @param mapFaecherByIndex   die Map mit den Fächern der Stundentafel
	 * @param mapBelegungByFach   die Map mit den Fächern der Belegungen
	 */
	private void bewegeBelegungZuWahlfach(@NotNull final BeruflichesGymnasiumStundentafel tafel,
			@NotNull final Map<Integer, List<BeruflichesGymnasiumStundentafelFach>> mapFaecherByIndex,
			@NotNull final Map<BeruflichesGymnasiumStundentafelFach, List<BKGymAbiturFachbelegung>> mapBelegungByFach) {
		final @NotNull List<BKGymAbiturFachbelegung> wahlBelegungenNeu = new ArrayList<>();
		@NotNull List<BKGymAbiturFachbelegung> wahlBelegungen = new ArrayList<>();

		// Prüfe Einträge der Stundentafel mit Wahlmöglichkeit auf mehrfache Belegung
		for (final int index : mapFaecherByIndex.keySet()) {
			final List<BeruflichesGymnasiumStundentafelFach> faecher = mapFaecherByIndex.get(index);
			if (faecher == null)
				continue;
			if (faecher.size() == 1) {
				final BeruflichesGymnasiumStundentafelFach tf = faecher.getFirst();
				if ((tf != null) && manager.istWahlfach(tf.fachbezeichnung)) {
					final List<BKGymAbiturFachbelegung> belegungen = mapBelegungByFach.get(tf);
					if (belegungen != null)
						wahlBelegungen = belegungen;
				}
				continue;
			}
			final Iterator<BeruflichesGymnasiumStundentafelFach> iter = faecher.iterator();
			// schaue nach nicht belegten Fächern (und Differenzierungsfächern)
			while (iter.hasNext()) {
				final BeruflichesGymnasiumStundentafelFach tafelFach = iter.next();
				final List<BKGymAbiturFachbelegung> belegungen = mapBelegungByFach.get(tafelFach);
				if ((belegungen == null) || (belegungen.size() == 0)) //|| manager.istDifferenzierung(belegung))
					iter.remove();
			}

			if (faecher.size() < 2)
				continue;

			boolean matched = false;
			for (int i = 0; i < faecher.size(); i++) {
				boolean fehlerfrei = true;
				final BeruflichesGymnasiumStundentafelFach tafelFach = faecher.get(i);
				if (tafelFach != null) {
					final List<BKGymAbiturFachbelegung> belegungen = mapBelegungByFach.get(tafelFach);
					if (manager.istZweiteFremdsprache(tafelFach.fachbezeichnung) || manager.istNeueFremdsprache(tafelFach.fachbezeichnung)) {
						pruefeBelegungZweiteFremdsprache(tafel, tafelFach, belegungen, wahlBelegungenNeu);
					} else if ((belegungen != null) && !belegungen.isEmpty()) {
						if (belegungen.size() == 1) {
							final BKGymAbiturFachbelegung belegung = belegungen.getFirst();
							if (belegung != null) {
								if (!matched)
									fehlerfrei = pruefeBelegung(tafel, tafelFach, belegung);
								if (matched || (!fehlerfrei && ((i + 1) < faecher.size())))
									//falls schon ein Match vorliegt oder eine fehlerhafte vorliegt und eine gute noch kommen kann
									//die Belegung zu den Wahlbelegungen zuordnen
									wahlBelegungenNeu.add(belegung);
								if (fehlerfrei)
									matched = true;
							} else
								throw new DeveloperNotificationException(
										"In der Belegungsprüfung ist ein interner Fehler aufgetreten: Die Belegung für ein Fach ist nicht vorhanden.");
						}
					}
				}
			}
		}
		// Wahlbelegungen dem Wahlfach zuordnen
		wahlBelegungen.addAll(wahlBelegungenNeu);
	}


	/**
	 * allgemeinePruefungen führt die Belegprüfungen durch, die für alle Anlagen des beruflichen Gymnasiums identisch ausgeführt werden können.
	 * Aufgetretene Fehler werden in die Map mapBelegungsfehler eingetragen.
	 *
	 * @param tafel                    die zu überprüfende Stundentafel
	 * @param mapFaecherByIndex        die Map mit den Fächern der Stundentafel
	 * @param mapBelegungByTafelfach   die Map mit den Fächern der Belegungen
	 */
	private void allgemeinePruefungen(final @NotNull BeruflichesGymnasiumStundentafel tafel,
			final @NotNull Map<Integer, List<BeruflichesGymnasiumStundentafelFach>> mapFaecherByIndex,
			final @NotNull Map<BeruflichesGymnasiumStundentafelFach, List<BKGymAbiturFachbelegung>> mapBelegungByTafelfach) {
		//final @NotNull List<BKGymAbiturFachbelegung> wahlBelegungen = new ArrayList<>();
		pruefeLKs(tafel);
		pruefeAbiGrundkurse(tafel);


		// Eigentliche Belegprüfung: Durchwandern der Zuordnung der Fächer in der Stundentafel
		for (final int index : mapFaecherByIndex.keySet()) {
			final List<BeruflichesGymnasiumStundentafelFach> faecher = mapFaecherByIndex.get(index);
			if (faecher == null || faecher.isEmpty())
				continue;
			boolean matched = false;
			for (int i = 0; i < faecher.size(); i++) {   // erst einen Durchgang starten, in dem bei mehrfacher Belegung einer Position, die schlechteren
				boolean fehlerfrei = true;               // zu Wahlfächern werden.
				final BeruflichesGymnasiumStundentafelFach tafelFach = faecher.get(i);
				if (tafelFach != null) {
					final List<BKGymAbiturFachbelegung> belegungen = mapBelegungByTafelfach.get(tafelFach);
					if (manager.istZweiteFremdsprache(tafelFach.fachbezeichnung) || manager.istNeueFremdsprache(tafelFach.fachbezeichnung)) {
						pruefeBelegungZweiteFremdsprache(tafel, tafelFach, belegungen, null);
					} else if (manager.istWahlfach(tafelFach.fachbezeichnung)) {
						pruefeBelegungWahlfach(tafel, tafelFach, belegungen);
					} else if ((belegungen == null) || belegungen.isEmpty()) {
						if (!matched && (i + 1 >= faecher.size()))
							addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.ST_4));
					} else {
						if (belegungen.size() > 1)
							addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.ST_1, tafelFach.fachbezeichnung));
						else {
							final BKGymAbiturFachbelegung belegung = belegungen.getFirst();
							if (belegung != null) {
								if (!matched)
									fehlerfrei = pruefeBelegung(tafel, tafelFach, belegung);
								if (fehlerfrei)
									matched = true;
							} else
								throw new DeveloperNotificationException(
										"In der Belegungsprüfung ist ein interner Fehler aufgetreten: Die Belegung für ein Fach ist nicht vorhanden.");
						}
					}
				}
			}
		}
	}


	/**
	 * Führt die Prüfungen für eine Belegung durch
	 *
	 * @param tafel          die Stundentafel der Anlage
	 * @param fachTafel      das zu prüfende Fach aus der Stundentafel
	 * @param fachBelegung   die zugeordnete Fachbelegung
	 *
	 * @return true, wenn die Prüfung keinen Fehler entdeckt, sonst false
	 */
	private boolean pruefeBelegung(final @NotNull BeruflichesGymnasiumStundentafel tafel, final @NotNull BeruflichesGymnasiumStundentafelFach fachTafel,
			final @NotNull BKGymAbiturFachbelegung fachBelegung) {
		final boolean successStundenumfang = pruefeStundenumfang(tafel, fachTafel, fachBelegung);
		final boolean successSchriftlichkeit = pruefeSchriftlich(tafel, fachTafel, fachBelegung);
		final boolean successAnzahl = pruefeAnzahlKurse(tafel, fachTafel, fachBelegung);
		return successStundenumfang && successSchriftlichkeit && successAnzahl;
	}


	/**
	 * Führt die Belegungsprüfung für die zweite Fremdsprache durch.
	 * Hier kann es vorkommen, dass eine dritte Fremdsprache belegt wird, die dann dem Wahlfach zugeschlagen wird.
	 *
	 * @param tafel            die Stundentafel der Anlage
	 * @param fachTafel        das zu prüfende Fach aus der Stundentafel
	 * @param belegungen       die zugeordnete Fachbelegung
	 * @param wahlBelegungen   die wahlBelegungen, die ggfs. durch die dritte vorliegende Fremdsprache ergänzt wird.
	 *
	 * @return true, wenn die Prüfung keinen Fehler entdeckt, sonst false
	 */
	private boolean pruefeBelegungZweiteFremdsprache(final @NotNull BeruflichesGymnasiumStundentafel tafel,
			final @NotNull BeruflichesGymnasiumStundentafelFach fachTafel, final List<BKGymAbiturFachbelegung> belegungen,
			final List<BKGymAbiturFachbelegung> wahlBelegungen) {
		if (belegungen == null || belegungen.isEmpty()) {
			addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.ST_4));
			return false;
		}
		boolean matched = false;
		for (int i = 0; i < belegungen.size(); i++) {
			boolean fehlerfrei = true;
			final BKGymAbiturFachbelegung fachBelegung = belegungen.get(i);
			if (fachBelegung != null) {
				fehlerfrei = pruefeBelegung(tafel, fachTafel, fachBelegung);
				if ((wahlBelegungen != null) && (matched || (!fehlerfrei && ((i + 1) < belegungen.size()))))
					//falls schon ein Match vorliegt oder eine fehlerhafte vorliegt und eine gute noch kommen kann
					//die Belegung zu den Wahlbelegungen zuordnen
					wahlBelegungen.add(fachBelegung);
				if (fehlerfrei)
					matched = true;
			}
		}
		return matched;
	}


	/**
	 * Führt die Belegungsprüfung für das Wahlfach aus.
	 * Hier muss nur die Summe der Wochenstunden pro Halbjahr erfüllt sein.
	 *
	 * @param tafel            die Stundentafel der Anlage
	 * @param fachTafel        das zu prüfende Fach aus der Stundentafel
	 * @param belegungen   die zugeordnete Fachbelegung
	 *
	 * @return true, wenn die Prüfung keinen Fehler entdeckt, sonst false
	 */
	private boolean pruefeBelegungWahlfach(final @NotNull BeruflichesGymnasiumStundentafel tafel,
			final @NotNull BeruflichesGymnasiumStundentafelFach fachTafel, final List<BKGymAbiturFachbelegung> belegungen) {
		boolean success = true;
		final @NotNull int[] summeWS = new int[GostHalbjahr.maxHalbjahre];
		if (belegungen == null)
			return addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.ST_4, fachTafel.fachbezeichnung));
		// Summe der Wochenstunden der Wahlbelegungen ermitteln.
		for (final BKGymAbiturFachbelegung fachBelegung : belegungen)
			for (final @NotNull GostHalbjahr hj : GostHalbjahr.values()) {
				final BKGymAbiturFachbelegungHalbjahr belegung = fachBelegung.belegungen[hj.id];
				if (belegung != null)
					summeWS[hj.id] += belegung.wochenstunden;
			}

		// Prüfen ob die Wochenstunden in den Halbjahren erreicht wurden
		for (final @NotNull GostHalbjahr hj : GostHalbjahr.values())
			if ((summeWS[hj.id] < fachTafel.stundenumfang[hj.id]) && addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.ST_3)))
				success = false;

		return success;
	}


	/**
	 * Prüft, ob Unterricht im erforderlichen Umfang erteilt wurde.
	 *
	 * @param tafel          die Stundentafel der Anlage
	 * @param fachTafel      das zu prüfende Fach aus der Stundentafel
	 * @param fachBelegung   die zugeordnete Fachbelegung
	 *
	 * @return true bei fehlerloser Prüfung sonst false
	 */
	private boolean pruefeStundenumfang(final @NotNull BeruflichesGymnasiumStundentafel tafel,
			final @NotNull BeruflichesGymnasiumStundentafelFach fachTafel, final @NotNull BKGymAbiturFachbelegung fachBelegung) {
		boolean success = true;
		int summeTafel = 0;
		int summeBelegung = 0;
		boolean unterbelegung = false;
		for (final @NotNull GostHalbjahr hj : GostHalbjahr.values()) {
			final BKGymAbiturFachbelegungHalbjahr belegung = fachBelegung.belegungen[hj.id];
			if (belegung != null) {
				summeTafel += fachTafel.stundenumfang[hj.id];
				if ((belegung.notenkuerzel != null) && (!belegung.notenkuerzel.isEmpty())) {
					summeBelegung += belegung.wochenstunden;
					if (fachTafel.stundenumfang[hj.id] > belegung.wochenstunden)
						unterbelegung = true;
				} else if (fachTafel.stundenumfang[hj.id] > 0) {
					if (addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.ST_2, fachTafel.fachbezeichnung, hj.kuerzel)))
						success = false;
					unterbelegung = true;
				}
			} else if ((fachTafel.stundenumfang[hj.id] > 0)
					&& addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.ST_4, fachTafel.fachbezeichnung)))
				success = false;
		}

		if (summeTafel > summeBelegung) {
			if (addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.ST_3, fachTafel.fachbezeichnung)))
				success = false;
		} else if (unterbelegung && addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.ST_5_INFO, fachTafel.fachbezeichnung)))
			success = false;
		return success;
	}



	/**
	 * Prüft, ob die Schriftlichkeit der Fächer korrekt erfüllt ist.
	 *
	 * @param tafel          die Stundentafel der Anlage
	 * @param fachTafel      das zu prüfende Fach aus der Stundentafel
	 * @param fachBelegung   die zugeordnete Fachbelegung
	 *
	 * @return true, wenn die Prüfung keinen Fehler entdeckt, sonst false
	 */
	private boolean pruefeSchriftlich(@NotNull final BeruflichesGymnasiumStundentafel tafel, final @NotNull BeruflichesGymnasiumStundentafelFach fachTafel,
			final @NotNull BKGymAbiturFachbelegung fachBelegung) {
		boolean success = true;
		final boolean istAbiFach = (fachBelegung.abiturFach != null);
		final boolean istLK = istAbiFach && ((fachBelegung.abiturFach == 1) || (fachBelegung.abiturFach == 2));
		final boolean istAbiSchriftlich = istAbiFach && (fachBelegung.abiturFach != 4);
		final boolean istMathe = ("Mathematik".equals(fachTafel.fachbezeichnung));
		final boolean istDeutsch = ("Deutsch".equals(fachTafel.fachbezeichnung));
		final boolean istFS = manager.istFremdsprachenbelegung(fachBelegung);
		final boolean brauchtSchriftlichEF = istLK || istMathe || istDeutsch || istFS;
		final boolean brauchtSchriftlichQ = brauchtSchriftlichEF || istAbiFach;

		// Schriftlich gemäß Vorgabe der Stundentafel
		for (final @NotNull GostHalbjahr hj : GostHalbjahr.values()) {
			final BKGymAbiturFachbelegungHalbjahr belegungHj = fachBelegung.belegungen[hj.id];
			if ((belegungHj != null) && (!belegungHj.schriftlich)) {
				if (brauchtSchriftlichEF && hj.istEinfuehrungsphase()
						&& addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.KL_1, fachTafel.fachbezeichnung, hj.kuerzel)))
					success = false;

				if (brauchtSchriftlichQ && hj.istQualifikationsphase() && (hj != GostHalbjahr.Q22)
						&& addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.KL_1, fachTafel.fachbezeichnung, hj.kuerzel)))
					success = false;

				if (istAbiSchriftlich && (hj == GostHalbjahr.Q22)
						&& addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.KL_1, fachTafel.fachbezeichnung, hj.kuerzel)))
					success = false;
			}
		}

		return success;
	}


	/**
	 * Prüft, ob die Mindestanzahl einzubringender Kurse vorhanden ist.
	 *
	 * @param tafel          die Stundentafel der Anlage
	 * @param fachTafel      das zu prüfende Fach aus der Stundentafel
	 * @param fachBelegung   die zugeordnete Fachbelegung
	 *
	 * @return true, wenn die Prüfung keinen Fehler entdeckt, sonst false
	 */
	private boolean pruefeAnzahlKurse(@NotNull final BeruflichesGymnasiumStundentafel tafel, final BeruflichesGymnasiumStundentafelFach fachTafel,
			final BKGymAbiturFachbelegung fachBelegung) {
		final boolean success = true;
		// TODO Prüfe die Anzahl
		return success;
	}


	/**
	 * Diese Methode prüft die LK-Belegung in der Stundentafel.
	 *
	 * @param tafel   die zu prüfende Stundentafel
	 */
	private void pruefeLKs(final @NotNull BeruflichesGymnasiumStundentafel tafel) {
		// Bestimme die Leistungskursbelegungen. Diese müssen für die weitere Prüfung vorhanden sein.
		final BKGymAbiturFachbelegung lk1 = manager.getAbiFachbelegung(GostAbiturFach.LK1);
		if (lk1 == null)
			addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.LK_1));
		final BKGymAbiturFachbelegung lk2 = manager.getAbiFachbelegung(GostAbiturFach.LK2);
		if (lk2 == null)
			addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.LK_2));

		if (lk1 != null && lk2 != null && !(manager.isValidKursartFachbelegung(tafel, lk1, GostAbiturFach.LK1)
				&& manager.isValidKursartFachbelegung(tafel, lk2, GostAbiturFach.LK2)))
			addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.LK_3, manager.getFachkuerzelFromFachbelegung(lk1),
					manager.getFachkuerzelFromFachbelegung(lk2), manager.getGliederung().name(), manager.getFachklassenschluessel()));
	}


	/**
	 * pruefe die Belegung des 3. und 4. Abiturfachs
	 *
	 * @param tafel
	 */
	private void pruefeAbiGrundkurse(final @NotNull BeruflichesGymnasiumStundentafel tafel) {
		// Prüfe, ob das dritte und vierte Abiturfach korrekt gewählt sind.
		// Bestimme das dritte Abiturfach
		final BKGymAbiturFachbelegung ab3 = manager.getAbiFachbelegung(GostAbiturFach.AB3);
		if (ab3 == null)
			addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.AB_3));

		// Bestimme das vierte Abiturfach
		final BKGymAbiturFachbelegung ab4 = manager.getAbiFachbelegung(GostAbiturFach.AB4);
		if (ab4 == null)
			addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.AB_4));

		// Prüfe die Wahlmöglichkeiten bei den Varianten in Bezug auf AB3 und AB4, verlasse ggf. die Prüfung, wenn keine Wahlmöglichkeit für AB3 und AB4 gefunden wurde
		if ((ab3 != null) && (ab4 != null) && !manager.pruefeAbiGrundkurswahl(tafel, ab3, ab4))
			addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.AB_5, manager.getFachkuerzelFromFachbelegung(ab3),
					manager.getFachkuerzelFromFachbelegung(ab4), manager.getGliederung().name(), manager.getFachklassenschluessel()));
	}


	/**
	 * Führt die für eine Anlage spezifische Belegprüfung durch.
	 * Diese Methode ist von jeder Kindklasse, welche spezifische Prüfungen hat, zu überschreiben.
	 *
	 * @param tafel                die zu überprüfende Stundentafel
	 * @param mapFaecherByIndex    die Map mit den Fächern der Stundentafel
	 * @param mapBelegungByIndex   die Map mit den Fächern der Belegungen
	 */
	protected void spezifischePruefungen(@NotNull final BeruflichesGymnasiumStundentafel tafel,
			@NotNull final Map<Integer, List<BeruflichesGymnasiumStundentafelFach>> mapFaecherByIndex,
			@NotNull final Map<BeruflichesGymnasiumStundentafelFach, List<BKGymAbiturFachbelegung>> mapBelegungByIndex) {
		// nichts zu tun
	}

}
