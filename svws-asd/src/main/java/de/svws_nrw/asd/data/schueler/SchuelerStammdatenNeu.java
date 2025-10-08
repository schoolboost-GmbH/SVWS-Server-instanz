package de.svws_nrw.asd.data.schueler;

import de.svws_nrw.transpiler.TranspilerDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Diese Klasse wird bei der Kommunikation über die Open-API-Schnittstelle verwendet.
 * Sie beschreibt die Stammdaten eines Schülers mit der angegebenen ID.
 */
@XmlRootElement
@Schema(description = "Die Stammdaten eines Schüler-Eintrags für das Anlegen mit den Minimaldaten.")
@TranspilerDTO
public class SchuelerStammdatenNeu {

	// **** Basisdaten

	/** Die ID des Schülerdatensatzes. */
	@Schema(description = "die ID", example = "4711", accessMode = Schema.AccessMode.READ_ONLY)
	public long id;

	/** Der Nachname des Schülerdatensatzes. */
	@Schema(description = "der Nachname", example = "Mustermann")
	public @NotNull String nachname = "";

	/** Der Vorname des Schülerdatensatzes. */
	@Schema(description = "der Vorname", example = "Max")
	public @NotNull String vorname = "";

	/** Alle Vornamen, sofern es mehrere gibt, des Schülerdatensatzes. */
	@Schema(description = "alle Vornamen, sofern es mehrere gibt, sonst erfolgt der Zugriff nur auf Vorname", example = "Max Moritz")
	public @NotNull String alleVornamen = "";

	/** Die ID des Geschlechtes */
	@Schema(description = "die ID des Geschlechtes", example = "3")
	public int geschlecht;

	/** Das Geburtsdatum des Schülerdatensatzes. */
	@Schema(description = "das Geburtsdatum", example = "11.11.1911")
	public String geburtsdatum;

	// **** Statusdaten

	/** Die ID des Status des Schülerdatensatzes. */
	@Schema(description = "die ID des aktuellen Schülerstatus", example = "2")
	public int status;

	/** Das Anmeldedatum des Schülerdatensatzes. */
	@Schema(description = "das Anmeldedatum", example = "11.11.1911")
	public String anmeldedatum;

	/** Das Aufnahmedatum des Schülerdatensatzes. */
	@Schema(description = "das Aufnahmedatum", example = "11.11.1911")
	public String aufnahmedatum;

	/** Der Beginn des Bildungsgangs eines Schülers. */
	@Schema(description = "der Beginn des Bildungsgangs eines Schülers", example = "null")
	public String beginnBildungsgang;

	/** Dauer des Bildungsgangs am BK eines Schülers. */
	@Schema(description = "die Dauer des Bildungsgangs am BK eines Schülers", example = "null")
	public Integer dauerBildungsgang;

	/** Die ID des Schuljahresabschnitts, zu welchem diese Lernabschnittdaten gehören. */
	@Schema(description = "die ID des Schuljahresabschnitts, zu welchem diese Lernabschnittsdaten gehören", example = "42")
	public long schuljahresabschnitt;

	/** Die ID des Jahrgangs des Schülers oder null, falls kein Jahrgang zugeordnet ist */
	@Schema(description = "die ID des Jahrgangs des Schülers oder null, falls kein Jahrgang zugeordnet ist", example = "78")
	public Long jahrgangID = null;

	/** Die ID der Klasse des Schülers oder null, falls keine Klasse zugeordnet ist. */
	@Schema(description = "die ID der Klasse des Schülers oder null, falls keine Klasse zugeordnet ist", example = "46")
	public Long klassenID = null;


	/**
	 * Leerer Standardkonstruktor.
	 */
	public SchuelerStammdatenNeu() {
		// leer
	}

}
