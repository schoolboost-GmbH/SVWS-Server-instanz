package de.svws_nrw.asd.types.schueler;

import de.svws_nrw.asd.data.schueler.BetreuungsartKatalogEintrag;
import de.svws_nrw.asd.types.CoreType;
import de.svws_nrw.asd.utils.CoreTypeDataManager;
import jakarta.validation.constraints.NotNull;

/**
 * Ein Core-Type für den Katalog der Betreuungsarten.
 */
public enum Betreuungsart implements @NotNull CoreType<BetreuungsartKatalogEintrag, Betreuungsart> {

	/** keine Teilnahme an Ganztagsangeboten und/oder Übermittagbetreuung */
	KEINE,

	/** Übermittagbetreuung (Primarstufe) */
	UEBERMITTAG_PRIMARSTUFE,

	/** Übermittagbetreuung (Sekundarstufe) */
	UEBERMITTAG_SEKUNDARSTUFE,

	/** Übermittagbetreuung und zusätzliches Ganztagsangebot */
	UEBERMITTAG_UND_GANZTAG,

	/** ausschließlich Schule von acht bis eins */
	NUR_ACHT_BIS_EINS,

	/** Schule von acht bis eins und Dreizehn Plus */
	ACHT_BIS_EINS_UND_DREIZEHN_PLUS;


	/**
	 * Initialisiert den Core-Type mit dem angegebenen Manager.
	 *
	 * @param manager   der Manager für die Daten des Core-Types
	 */
	public static void init(final @NotNull CoreTypeDataManager<BetreuungsartKatalogEintrag, Betreuungsart> manager) {
		CoreTypeDataManager.putManager(Betreuungsart.class, manager);
	}


	/**
	 * Gibt den Daten-Manager für den Zugriff auf die Core-Type-Daten zurück, sofern dieser initialisiert wurde.
	 *
	 * @return der Daten-Manager
	 */
	public static @NotNull CoreTypeDataManager<BetreuungsartKatalogEintrag, Betreuungsart> data() {
		return CoreTypeDataManager.getManager(Betreuungsart.class);
	}

}
