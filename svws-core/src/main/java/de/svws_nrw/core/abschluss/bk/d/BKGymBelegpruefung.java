package de.svws_nrw.core.abschluss.bk.d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.svws_nrw.asd.data.schule.BeruflichesGymnasiumStundentafel;
import de.svws_nrw.asd.data.schule.BeruflichesGymnasiumStundentafelFach;
import de.svws_nrw.asd.types.schule.BeruflichesGymnasiumPruefungsordnungAnlage;
import de.svws_nrw.core.types.gost.GostAbiturFach;
import jakarta.validation.constraints.NotNull;

/**
 * Die abstrakte Klasse für die Belegprüfungen bei Bildungsgängen.
 */
public abstract class BKGymBelegpruefung {

	/** Die Abiturdaten-Manager */
	protected final @NotNull BKGymAbiturdatenManager manager;

	/** Die Belegungsfehler, die bei der Prüfung entstanden sind. */
	private final @NotNull List<BKGymBelegungsfehler> belegungsfehler = new ArrayList<>();


	/**
	 * Erzeugt eine neue Belegprüfung mit dem angegebenen Manager.
	 *
	 * @param manager   der Manager für die Abiturdaten
	 */
	public BKGymBelegpruefung(final @NotNull BKGymAbiturdatenManager manager) {
		this.manager = manager;
	}


	/**
	 * Fügt einen Belegungsfehler zu der Belegprüfung hinzu. Diese Methode wird von den Sub-Klassen
	 * aufgerufen, wenn dort ein Belegungsfehler erkannt wird.
	 *
	 * @param fehler   der hinzuzufügende Belegungsfehler
	 */
	protected void addFehler(final @NotNull BKGymBelegungsfehler fehler) {
		if (!belegungsfehler.contains(fehler))
			belegungsfehler.add(fehler);
	}


	/**
	 * Gibt die Belegungsfehler zurück, welche bei der Prüfung aufgetreten sind.
	 *
	 * @return die Belegungsfehler
	 */
	public @NotNull List<BKGymBelegungsfehler> getBelegungsfehler() {
		return belegungsfehler;
	}


	/**
	 * Gibt zurück, ob ein "echter" Belegungsfehler vorliegt und nicht nur eine Warnung oder ein Hinweis.
	 *
	 * @return true, wenn kein "echter" Belegungsfehler vorliegt, und ansonsten false.
	 */
	public boolean istErfolgreich() {
		for (final @NotNull BKGymBelegungsfehler fehler : belegungsfehler)
			if (!fehler.istInfo())
				return false;
		return true;
	}


	/**
	 * Führt die Belegprüfung durch.
	 */
	public abstract void pruefe();

	/**
	 * Diese Methode bestimmt die möglichen Stundentafeln anhand der Abiturfächer und der übergebenen Anlage.
	 * Sollten die Abiturfächer nicht korrekt bestimmt werden können, so wird eine entsprechende Fehlermeldung erzeugt, dass das Abiturfach fehlt.
	 *
	 * @param anlage   die Anlage aus der Prüfungsordnung
	 *
	 * @return die Liste der möglichen Stundentafeln
	 */
	protected @NotNull List<BeruflichesGymnasiumStundentafel> getStundentafelnByAbiturfaechern(final @NotNull BeruflichesGymnasiumPruefungsordnungAnlage anlage) {
		// Bestimme die Leistungskursbelegungen. Diese müssen für die weitere Prüfung vorhanden sein.
		final BKGymAbiturFachbelegung lk1 = manager.getAbiFachbelegung(GostAbiturFach.LK1);
		if (lk1 == null)
			addFehler(BKGymBelegungsfehler.LK_1);
		final BKGymAbiturFachbelegung lk2 = manager.getAbiFachbelegung(GostAbiturFach.LK2);
		if (lk2 == null)
			addFehler(BKGymBelegungsfehler.LK_2);

		// Bestimme zunächst die möglichen Stundentafeln laut der Anlage und der beiden belegten Leistungskurse.
		final @NotNull List<BeruflichesGymnasiumStundentafel> mglStundentafeln =
				manager.getStundentafelByLeistungskurse(anlage);
		if (mglStundentafeln.isEmpty())
			addFehler(BKGymBelegungsfehler.LK_3);

		// Bestimme das dritte Abiturfach
		final BKGymAbiturFachbelegung ab3 = manager.getAbiFachbelegung(GostAbiturFach.AB3);
		if (ab3 == null)
			addFehler(BKGymBelegungsfehler.AB_3);

		// Bestimme das vierte Abiturfach
		final BKGymAbiturFachbelegung ab4 = manager.getAbiFachbelegung(GostAbiturFach.AB4);
		if (ab4 == null)
			addFehler(BKGymBelegungsfehler.AB_4);

		// Verlasse die Prüfung, wenn bis hier keine Variante gefunden wurde
		return mglStundentafeln;
	}


	/**
	 * Bestimmt die Fachbelegungszuordnung zu den einzelnen Stundentafel-Einträgen für die übergebene Stundentafel-Variante.
	 *
	 * @param tafel   die Stundentafel

	 * @return die Zuordnung der Fachbelegungen zu den Stundentafeleinträgen
	 */
	public @NotNull Map<Integer, List<BKGymAbiturFachbelegung>> getMapStundentafelFaecherByVariante(
			final @NotNull BeruflichesGymnasiumStundentafel tafel) {
		final @NotNull Map<Integer, List<BKGymAbiturFachbelegung>> mapZuordnung = new HashMap<>();
		for (final @NotNull BeruflichesGymnasiumStundentafelFach fach : tafel.faecher)
			mapZuordnung.computeIfAbsent(fach.sortierung, index -> new ArrayList<>());

		for (final @NotNull BKGymAbiturFachbelegung fachbelegung : manager.getAbidaten().fachbelegungen) {
			final BeruflichesGymnasiumStundentafelFach fach = manager.getFachByBelegung(tafel, fachbelegung);
			if (fach == null)
				continue;
			final List<BKGymAbiturFachbelegung> tafelBelegungen = mapZuordnung.get(fach.sortierung);
			if (tafelBelegungen == null)
				continue;
			tafelBelegungen.add(fachbelegung);
		}
		return mapZuordnung;
	}


	/**
	 * Diese Methode liefert für die Zuordnung der Fachbelegungen des Schülers zu den einzelnen Einträgen der jeweiligen
	 * Stundentafel für alle möglichen Stundentafeln
	 *
	 * @param mglStundentafeln    die möglichen Stundentafeln
	 *
	 * @return die Zuordnung
	 */
	public @NotNull Map<BeruflichesGymnasiumStundentafel, Map<Integer, List<BKGymAbiturFachbelegung>>> getZuordnungStundentafelFachbelegung(
			final @NotNull List<BeruflichesGymnasiumStundentafel> mglStundentafeln) {
		final @NotNull Map<BeruflichesGymnasiumStundentafel, Map<Integer, List<BKGymAbiturFachbelegung>>> mapStundentafelFachbelegungen = new HashMap<>();
		for (final @NotNull BeruflichesGymnasiumStundentafel tafel : mglStundentafeln) {
			// Bestimme die Zuordnung der Fachbelegungen zu den einzelnen Einträgen der Stundentafel
			final @NotNull Map<Integer, List<BKGymAbiturFachbelegung>> mapStundentafelFaecher = getMapStundentafelFaecherByVariante(tafel);

			// Prüfe, ob diese Stundentafel aufgrund der Zuordnungen überhaupt noch möglich ist
			boolean valid = true;
			for (final @NotNull BeruflichesGymnasiumStundentafelFach fach : tafel.faecher) {
				final List<BKGymAbiturFachbelegung> tafelFachbelegungen = mapStundentafelFaecher.get(fach.sortierung);

				// Sind keine Wochenstunden bei der Belegung gefordert, so muss auch keine Fachbelegung zugeordnet sein, da diese in den Leistungsdaten nicht auftritt
				if ((fach.stundenumfang[0] == 0) && (fach.stundenumfang[1] == 0) && (fach.stundenumfang[2] == 0)
						&& (fach.stundenumfang[3] == 0) && (fach.stundenumfang[4] == 0) && (fach.stundenumfang[5] == 0))
					continue;
				// Das Wahlfach muss zu diesem Zeitpunkt nicht zwingend als solches der Stundentafel zugeordnet sein
				if ("Wahlfach".equals(fach.fachbezeichnung))
					continue;
				// Sind keine Fachbelegungen vorhanden, dann ist die Belegung nicht in Ordnung
				if ((tafelFachbelegungen == null) || tafelFachbelegungen.isEmpty()) {
					valid = false;
					break;
				}
			}
			if (!valid)
				continue;

			// Füge die Zuordnungs-Map zu der Stundentafel-Map hinzu
			mapStundentafelFachbelegungen.put(tafel, mapStundentafelFaecher);
		}
		return mapStundentafelFachbelegungen;
	}

}
