package de.svws_nrw.core.abschluss.bk.d;

import java.util.List;
import java.util.Map;

import de.svws_nrw.asd.data.schule.BeruflichesGymnasiumStundentafel;
import de.svws_nrw.asd.data.schule.BeruflichesGymnasiumStundentafelAbiturfaecherWahlmoeglichkeit;
import de.svws_nrw.asd.types.schule.BeruflichesGymnasiumPruefungsordnungAnlage;
import jakarta.validation.constraints.NotNull;

/**
 * Der Belegprüfungsalgorithmus für den Bildungsgang der Schulgliederung D01
 * und der Fachklasse 106 00.
 */
public final class BKGymBelegpruefungD3 extends BKGymBelegpruefung {

	/**
	 * Erzeugt einen neue Belegprüfung
	 *
	 * @param manager   der Manager für die Abiturdaten
	 */
	public BKGymBelegpruefungD3(@NotNull final BKGymAbiturdatenManager manager) {
		super(manager);
	}

	@Override
	public void pruefe() {
		// Prüfe die Abiturfächer darauf, ob diese bei mehreren Fachbelegungen eingetragen sind

		// Bestimme zunächst die möglichen Stundentafeln laut der Anlage und der beiden belegten Leistungskurse.
		final @NotNull List<BeruflichesGymnasiumStundentafel> mglStundentafeln = getStundentafelnByAbiturfaechern(BeruflichesGymnasiumPruefungsordnungAnlage.D3);

		// Verlasse die Prüfung, wenn bis hier keine Variante gefunden wurde
		if (mglStundentafeln.isEmpty())
			return;

		// Prüfe die Wahlmöglichkeiten bei den Varianten in Bezug auf AB3 und AB4, verlasse ggf. die Prüfung, wenn keine Wahlmöglichkeit für AB3 und AB4 gefunden wurde
		final @NotNull Map<BeruflichesGymnasiumStundentafel, BeruflichesGymnasiumStundentafelAbiturfaecherWahlmoeglichkeit> mapWahlmoeglichkeiten = manager.getWahlmoeglichekeiten(mglStundentafeln);
		if (mapWahlmoeglichkeiten.isEmpty()) {
			addFehler(BKGymBelegungsfehler.AB_5);
			return;
		}


		// 1. Abiturfachkombination prüfen und Zuordnung der Abiturfachs zum Fach (O punkte als nicht belegt)

		// 2. Schritt identifikation der Variante:

		// 3. restliche Fachbelegung prüfen
	}

}
