package de.svws_nrw.core.data.schule;


import de.svws_nrw.transpiler.TranspilerDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Diese Klasse wird bei der Kommunikation über die Open-Api-Schnittstelle verwendet.
 * Sie beschreibt wie die Daten der Floskel übergeben werden.
 */
@XmlRootElement
@Schema(description = "Ein Eintrag im Katalog der schulspezifischen Floskeln")
@TranspilerDTO
public class Floskel {

	/** Das Kürzel der Floskel */
	@Schema(description = "Das Kürzel der Floskel", example = "#103")
	public String kuerzel;

	/** Der Text */
	@Schema(description = "Der Text", example = "#$Vorname$ bringt sich bei Teamarbeit sehr produktiv in die Gruppe ein.")
	public String text;

	/** Das Kürzel der Floskelgruppe */
	@Schema(description = "Das Kürzel der Floskelgruppe", example = "ZB")
	public String kuerzelFloskelgruppe;

	/** Die ID des Fachs */
	@Schema(description = "Die ID des Fachs", example = "8")
	public Long idFach;

	/** Das Niveau */
	@Schema(description = "Das Niveau", example = "AB")
	public String niveau;

	/** Die ID des Jahrgangs */
	@Schema(description = "Die ID des Jahrgangs", example = "9000000")
	public Long idJahrgang;

	/** Gibt an, ob die Floskel in anderen Datenbanktabellen referenziert ist. */
	@Schema(description = "Gibt an, ob die Floskel in anderen Datenbanktabellen referenziert ist.", example = "true", accessMode = Schema.AccessMode.READ_ONLY)
	public Boolean referenziertInAnderenTabellen;

}
