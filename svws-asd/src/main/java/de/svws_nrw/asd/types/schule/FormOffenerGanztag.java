package de.svws_nrw.asd.types.schule;

import de.svws_nrw.asd.data.schule.FormOffenerGanztagKatalogEintrag;
import de.svws_nrw.asd.types.CoreType;
import de.svws_nrw.asd.utils.CoreTypeDataManager;
import jakarta.validation.constraints.NotNull;

/**
 * Der Core-Type für den Katalog der möglichen Formen des offenen Ganztags
 */
public enum FormOffenerGanztag implements @NotNull CoreType<FormOffenerGanztagKatalogEintrag, FormOffenerGanztag> {

	/** an der eigenen Schule wahrgenommen (ggf. an anderer Einrichtung) */
	EIGENE_SCHULE,

	/** vollständig an einer anderen Schule */
	ANDERE_SCHULE,

	/** nicht angeboten */
	NICHT_ANGEBOTEN;


	/**
	 * Initialisiert den Core-Type mit dem angegebenen Manager.
	 *
	 * @param manager   der Manager für die Daten des Core-Types
	 */
	public static void init(final @NotNull CoreTypeDataManager<FormOffenerGanztagKatalogEintrag, FormOffenerGanztag> manager) {
		CoreTypeDataManager.putManager(FormOffenerGanztag.class, manager);
	}


	/**
	 * Gibt den Daten-Manager für den Zugriff auf die Core-Type-Daten zurück, sofern dieser initialisiert wurde.
	 *
	 * @return der Daten-Manager
	 */
	public static @NotNull CoreTypeDataManager<FormOffenerGanztagKatalogEintrag, FormOffenerGanztag> data() {
		return CoreTypeDataManager.getManager(FormOffenerGanztag.class);
	}

}
