package de.svws_nrw.asd.data.schule;

import de.svws_nrw.asd.data.CoreTypeDataNurSchulformen;
import de.svws_nrw.transpiler.TranspilerDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Diese Klasse wird bei der Kommunikation über die Open-API-Schnittstelle verwendet.
 * Sie liefert die Daten einer Bildungsstufe.
 */
@XmlRootElement
@Schema(description = "Ein Eintrag im Katalog einer Bildungsstufe.")
@TranspilerDTO
public class BildungsstufeKatalogEintrag extends CoreTypeDataNurSchulformen {

	/**
	 * Leerer Standardkonstruktor.
	 */
	public BildungsstufeKatalogEintrag() {
		// leer
	}
}
