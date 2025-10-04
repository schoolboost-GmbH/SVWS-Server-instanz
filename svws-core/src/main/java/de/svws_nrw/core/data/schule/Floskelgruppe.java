package de.svws_nrw.core.data.schule;

import de.svws_nrw.asd.data.RGBFarbe;
import de.svws_nrw.transpiler.TranspilerDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Diese Klasse wird bei der Kommunikation über die Open-Api-Schnittstelle verwendet.
 * Sie beschreibt wie die Daten der Floskelgruppe übergeben werden.
 */
@XmlRootElement
@Schema(description = "Ein Eintrag im Katalog der schulspezifischen Floskelgruppen")
@TranspilerDTO
public class Floskelgruppe {

	/** Das Kürzel der Floskelgruppe */
	@Schema(description = "Das Kürzel der Floskelgruppe", example = "ALLG")
	public String kuerzel;

	/** Die Bezeichnung der Floskelgruppe */
	@Schema(description = "Die Bezeichnung der Floskelgruppe", example = "Allgemeine Floskeln")
	public String bezeichnung;

	/** Die ID der Floskelgruppenart */
	@Schema(description = "Die ID der Floskelgruppenart", example = "1")
	public Long idFloskelgruppenart;

	/** Die Farbe */
	@Schema(description = "Die Farbe", example = "8421376")
	public RGBFarbe farbe;

	/** Gibt an, ob die Floskelgruppe in anderen Datenbanktabellen referenziert ist. */
	@Schema(description = "Gibt an, ob die Floskelgruppe in anderen Datenbanktabellen referenziert ist.", example = "true", accessMode = Schema.AccessMode.READ_ONLY)
	public Boolean referenziertInAnderenTabellen;

}
