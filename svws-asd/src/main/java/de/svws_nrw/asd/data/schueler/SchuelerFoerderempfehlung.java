package de.svws_nrw.asd.data.schueler;

import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;
import de.svws_nrw.transpiler.TranspilerDTO;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Diese Klassen enthält die Angaben zu der Förderempfehlung eines Schülers.
 */
@XmlRootElement
@Schema(description = "Die Daten einer Förderempfehlung eines Schülers.")
@TranspilerDTO
public class SchuelerFoerderempfehlung {

	/** Die GUID der Förderempfehlung */
	@Schema(description = "die GUID der Förderempfehlung", example = "")
	public String guid = "";

	/** Die ID des Schülerlernabschnittes, welchem die Förderempfehlung zugeordnet ist */
	@Schema(description = "die ID des Schülerlernabschnittes, welchem die Förderempfehlung zugeordnet ist", example = "1")
	public Long idLernabschnitt;

	/** Die ID der Klasse auf welche sich die Förderempfehlung bezieht */
	@Schema(description = "die ID der Klasse auf welche sich die Förderempfehlung bezieht", example = "1")
	public Long idKlasse;

	/** Die ID des Lehrers, welcher die Förderempfehlung erstellt hat. */
	@Schema(description = "die ID des Lehrers, welcher die Förderempfehlung erstellt hat", example = "1")
	public Long idLehrer;

	/** Das Datum, wann die Förderempfehlung angelegt wurde */
	@Schema(description = "das Datum, wann die Förderempfehlung angelegt wurde", example = "")
	public String datumAngelegt = "";

	/** Das Datum, wann die Förderempfehlung zuletzt angepasst wurde */
	@Schema(description = "das Datum, wann die Förderempfehlung zuletzt angepasst wurde", example = "")
	public String datumLetzteAenderung = "";

	/** Die diagnostizierten Förderbedarfe im Bereich der Prozessbezogenen Kompetenzen */
	@Schema(description = "die diagnostizierten Förderbedarfe im Bereich der Prozessbezogenen Kompetenzen", example = "")
	public String diagnoseKompetenzenInhaltlichProzessbezogen = "";

	/** Die diagnostizierten Förderbedarfe im Bereich der methodische Kompetenzen */
	@Schema(description = "die diagnostizierten Förderbedarfe im Bereich der methodische Kompetenzen", example = "")
	public String diagnoseKompetenzenMethodisch = "";

	/** Die diagnostizierten Förderbedarfe im Bereich in Bezug auf das Lern- und Arbeitsverhalten */
	@Schema(description = "die diagnostizierten Förderbedarfe im Bereich in Bezug auf das Lern- und Arbeitsverhalten", example = "")
	public String diagnoseLernUndArbeitsverhalten = "";

	/** Die geplanten Maßnahmen in Bezug auf die Förderbedarfe im Bereich der Prozessbezogenen Kompetenzen */
	@Schema(description = "die geplanten Maßnahmen in Bezug auf die Förderbedarfe im Bereich der Prozessbezogenen Kompetenzen", example = "")
	public String massnahmeKompetenzenInhaltlichProzessbezogen = "";

	/** Die geplanten Maßnahmen in Bezug auf die Förderbedarfe im Bereich der methodische Kompetenzen */
	@Schema(description = "die geplanten Maßnahmen in Bezug auf die Förderbedarfe im Bereich der methodische Kompetenzen", example = "")
	public String massnahmeKompetenzenMethodische = "";

	/** Die geplanten Maßnahmen in Bezug auf die Förderbedarfe in Bezug auf das Lern- und Arbeitsverhalten */
	@Schema(description = "die geplanten Maßnahmen in Bezug auf die Förderbedarfe in Bezug auf das Lern- und Arbeitsverhalten", example = "")
	public String massnahmeLernArbeitsverhalten = "";

	/** Die Verantwortlichkeiten der Eltern in Bezug auf die Förderempfehlung */
	@Schema(description = "die Verantwortlichkeiten der Eltern in Bezug auf die Förderempfehlung", example = "")
	public String verantwortlichkeitEltern = "";

	/** Die Verantwortlichkeiten des Schülers in Bezug auf die Förderempfehlung */
	@Schema(description = "die Verantwortlichkeiten des Schülers in Bezug auf die Förderempfehlung", example = "")
	public String verantwortlichkeitSchueler = "";

	/** Das Startdatum des Zeitrahmens für die Umsetzung der Förderempfehlung. */
	@Schema(description = "das Startdatum des Zeitrahmens für die Umsetzung der Förderempfehlung", example = "")
	public String datumUmsetzungVon = "";

	/** Das Enddatum des Zeitrahmens für die Umsetzung der Förderempfehlung */
	@Schema(description = "das Enddatum des Zeitrahmens für die Umsetzung der Förderempfehlung", example = "")
	public String datumUmsetzungBis = "";

	/** Das Datum, wann die Erfolge bei der Umsetzung der Förderempfehlung überprüft werden sollen */
	@Schema(description = "das Datum, wann die Erfolge bei der Umsetzung der Förderempfehlung überprüft werden sollen", example = "")
	public String datumUeberpruefung = "";

	/** Das Datum für das nächste geplante Beratungsgespräch in Bezug auf die Förderung */
	@Schema(description = "das Datum für das nächste geplante Beratungsgespräch in Bezug auf die Förderung", example = "")
	public String datumNaechstesBeratungsgespraech = "";

	/** Gibt an, ob die Eingabe der Förderempfehlung abgeschlossen wurde oder nicht */
	@Schema(description = "gibt an, ob die Eingabe der Förderempfehlung abgeschlossen wurde oder nicht", example = "true")
	public @NotNull boolean eingabeFertig;

	/** Die Kürzel der Fächer, auf welche sich die Förderempfehlung bezieht, als komma-separierter String */
	@Schema(description = "die Kürzel der Fächer, auf welche sich die Förderempfehlung bezieht, als komma-separierter String", example = "")
	public String faecher = "";

	/** Gibt an, ob die Umsetzung der Förderempfehlung beendet wurde oder nicht */
	@Schema(description = "gibt an, ob die Umsetzung der Förderempfehlung beendet wurde oder nicht", example = "true")
	public @NotNull boolean abgeschlossen;


	/**
	 * Leerer Standardkonstruktor.
	 */
	public SchuelerFoerderempfehlung() {
		// leer
	}

}
