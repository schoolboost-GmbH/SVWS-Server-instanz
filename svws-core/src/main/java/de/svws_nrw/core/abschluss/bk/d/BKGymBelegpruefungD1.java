package de.svws_nrw.core.abschluss.bk.d;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.svws_nrw.asd.data.schule.BeruflichesGymnasiumStundentafel;
import de.svws_nrw.asd.data.schule.BeruflichesGymnasiumStundentafelAbiturfaecherWahlmoeglichkeit;
import de.svws_nrw.asd.data.schule.BeruflichesGymnasiumStundentafelFach;
import de.svws_nrw.asd.types.schule.BeruflichesGymnasiumPruefungsordnungAnlage;
import de.svws_nrw.core.types.gost.GostHalbjahr;
import jakarta.validation.constraints.NotNull;

/**
 * Der Belegprüfungsalgorithmus für den Bildungsgang der Schulgliederung D01
 * und der Fachklasse 106 00.
 */
public final class BKGymBelegpruefungD1 extends BKGymBelegpruefung {

	/**
	 * Erzeugt einen neue Belegprüfung
	 *
	 * @param manager   der Manager für die Abiturdaten
	 */
	public BKGymBelegpruefungD1(@NotNull final BKGymAbiturdatenManager manager) {
		super(manager);
	}

	@Override
	public void pruefe() {
		// Prüfe die Abiturfächer darauf, ob diese bei mehreren Fachbelegungen eingetragen sind

		// Bestimme zunächst die möglichen Stundentafeln laut der Anlage und der beiden belegten Leistungskurse.
		final @NotNull List<BeruflichesGymnasiumStundentafel> mglStundentafeln = getStundentafelnByAbiturfaechern(BeruflichesGymnasiumPruefungsordnungAnlage.D1);

		// Verlasse die Prüfung, wenn bis hier keine Variante gefunden wurde
		if (mglStundentafeln.isEmpty())
			return;

		// Prüfe die Wahlmöglichkeiten bei den Varianten in Bezug auf AB3 und AB4, verlasse ggf. die Prüfung, wenn keine Wahlmöglichkeit für AB3 und AB4 gefunden wurde
		final @NotNull Map<BeruflichesGymnasiumStundentafel, BeruflichesGymnasiumStundentafelAbiturfaecherWahlmoeglichkeit> mapWahlmoeglichkeiten =
				manager.getWahlmoeglichekeiten(mglStundentafeln);
		if (mapWahlmoeglichkeiten.isEmpty()) {
			addFehler(BKGymBelegungsfehler.AB_5);
			return;
		}

		// Zuordnung von der Sortiernummer in der Stundentafel auf die Fachbelegung(-en)
		final @NotNull Map<BeruflichesGymnasiumStundentafel, Map<Integer, List<BKGymAbiturFachbelegung>>> tmpMapStundentafelFachbelegungen =
				getZuordnungStundentafelFachbelegung(mglStundentafeln);
		if (tmpMapStundentafelFachbelegungen.isEmpty()) {
			addFehler(BKGymBelegungsfehler.ST_1);
			return;
		}

		// Filtere die möglichen Zuordnungen noch so, dass nur welche mit gültiger Belegung für das 3. und 4. Fach übernommen werden
		final @NotNull Map<BeruflichesGymnasiumStundentafel, Map<Integer, List<BKGymAbiturFachbelegung>>> mapStundentafelFachbelegungen =
				new HashMap<>();
		for (final Map.Entry<BeruflichesGymnasiumStundentafel, Map<Integer, List<BKGymAbiturFachbelegung>>> entry : tmpMapStundentafelFachbelegungen.entrySet()) {
			final BeruflichesGymnasiumStundentafelAbiturfaecherWahlmoeglichkeit wahlmoeglichkeit = mapWahlmoeglichkeiten.get(entry.getKey());
			if (wahlmoeglichkeit == null)
				continue;
			mapStundentafelFachbelegungen.put(entry.getKey(), entry.getValue());
		}

		// Prüfe, ob die Stundentafel eindeutig bestimmt werden konnte
		if (mapStundentafelFachbelegungen.size() > 1) {
			addFehler(BKGymBelegungsfehler.ST_2);
			return;
		}
		final BeruflichesGymnasiumStundentafel tafel = mapStundentafelFachbelegungen.keySet().iterator().next();
		final Map<Integer, List<BKGymAbiturFachbelegung>> zuordnung = mapStundentafelFachbelegungen.get(tafel);
		if ((tafel == null) || (zuordnung == null))
			return;

		// Prüfe, ob eine Wahlmöglichekeit für das dritte und vierte Fach zu der Stundentafel zuvor gefunden wurde
		final BeruflichesGymnasiumStundentafelAbiturfaecherWahlmoeglichkeit wahlmoeglichkeit = mapWahlmoeglichkeiten.get(tafel);
		if (wahlmoeglichkeit == null)
			return;

		// Eigentliche Belegprüfung: Durchwandern der Zuordnung der Fächer in der Stundentafel
		final @NotNull Map<Integer, List<BeruflichesGymnasiumStundentafelFach>> mapFaecherByIndex = manager.getMapFaecherFromTafelByIndex(tafel);
		for (final int index : zuordnung.keySet()) {
			// Bestimme die zu prüfende Belegung
			final List<BKGymAbiturFachbelegung> fachbelegungen = zuordnung.get(index);
			if ((fachbelegungen == null) || (fachbelegungen.isEmpty()))
				continue;

			BKGymAbiturFachbelegung fachbelegung = null;
			if (fachbelegungen.size() > 1) {
				// Berücksichtige den Spezialfall, dass in Belegungen mehr als ein Eintrag ist...
				int minFehlerZahl = Integer.MAX_VALUE;
				int aktFehlerZahl = 0;
				for (final BKGymAbiturFachbelegung fb : fachbelegungen) {
					final BeruflichesGymnasiumStundentafelFach fach = manager.getFachByBelegung(tafel, fb);
					if (fach == null)
						continue;
					final boolean istAbiFach = (fb.abiturFach != null);
					final boolean istLK = istAbiFach && ((fb.abiturFach == 1) || (fb.abiturFach == 2));
					final boolean istAbiSchriftlich = istAbiFach && (fb.abiturFach != 4);
					final boolean istMathe = ("Mathematik".equals(fach.fachbezeichnung));
					final boolean istDeutsch = ("Deutsch".equals(fach.fachbezeichnung));
					final boolean istFS = manager.istFremdsprachenbelegung(fb);
					final boolean brauchtSchriftlichEF = istLK || istMathe || istDeutsch || istFS;
					final boolean brauchtSchriftlichQ = brauchtSchriftlichEF || istAbiFach;

					// Bestimme geeignetste Belegung (Fehleranzahl bestimmen als Auswahlkriterium)
					for (final @NotNull GostHalbjahr hj : GostHalbjahr.values()) {
						final BKGymAbiturFachbelegungHalbjahr belegung = fb.belegungen[hj.id];
						if (belegung == null) {
							if (fach.stundenumfang[hj.id] > 0)
								aktFehlerZahl += 10;
							continue;
						}

						// Wochenstunden erreicht
						if (belegung.wochenstunden < fach.stundenumfang[hj.id])
							aktFehlerZahl++;

						// Schriftlich gemäß Vorgabe der Stundentafel
						if (!belegung.schriftlich) {
							if (brauchtSchriftlichEF && hj.istEinfuehrungsphase())
								aktFehlerZahl++;
							if (brauchtSchriftlichQ && hj.istQualifikationsphase() && (hj != GostHalbjahr.Q22))
								aktFehlerZahl++;
							if (istAbiSchriftlich && (hj == GostHalbjahr.Q22))
								aktFehlerZahl++;
						}
					}

					if (aktFehlerZahl < minFehlerZahl) {
						minFehlerZahl = aktFehlerZahl;
						fachbelegung = fb;
					}
				}
				if (minFehlerZahl > 0)
					addFehler(BKGymBelegungsfehler.ST_3_INFO);
			} else
				fachbelegung = fachbelegungen.get(0);
			if (fachbelegung == null)
				return;

			final BeruflichesGymnasiumStundentafelFach fach = manager.getFachByBelegung(tafel, fachbelegung);

			// Durchwandere alle Halbjahresbelegungen und prüfe Wochenstunden, Kursart, etc.
			for (final @NotNull GostHalbjahr hj : GostHalbjahr.values()) {
				final BKGymAbiturFachbelegungHalbjahr belegung = fachbelegung.belegungen[hj.id];

				// TODO
			}
		}

	}

}
