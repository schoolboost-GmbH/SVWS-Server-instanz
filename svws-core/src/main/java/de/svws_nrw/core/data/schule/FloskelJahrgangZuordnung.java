package de.svws_nrw.core.data.schule;

import de.svws_nrw.transpiler.TranspilerDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Dieses Core-DTO beinhaltet die Zuordnung einer Floskel zu einem Jahrgang.
 */
@XmlRootElement
@Schema(description = "Die Zuordnung einer Floskel zu einem Jahrgang.")
@TranspilerDTO
public class FloskelJahrgangZuordnung {

	/** Die ID der Floskel-Jahrgangs-Zuordnung */
	@Schema(description = "Die ID der Floskel-Jahrgangs-Zuordnung", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	public long id;

	/** Die ID der Floskel */
	@Schema(description = "Die ID der Floskel", example = "1")
	public long idFloskel;

	/** Die ID des Jahrgangs */
	@Schema(description = "Die ID des Jahrgangs", example = "1")
	public long idJahrgang;

}
