import { JavaObject } from '../../../java/lang/JavaObject';
import { Class } from '../../../java/lang/Class';
import { RGBFarbe } from '../../../asd/data/RGBFarbe';

export class Floskelgruppe extends JavaObject {

	/**
	 * Das Kürzel der Floskelgruppe
	 */
	public kuerzel : string | null = null;

	/**
	 * Die Bezeichnung der Floskelgruppe
	 */
	public bezeichnung : string | null = null;

	/**
	 * Die ID der Floskelgruppenart
	 */
	public idFloskelgruppenart : number | null = null;

	/**
	 * Die Farbe
	 */
	public farbe : RGBFarbe | null = null;

	/**
	 * Gibt an, ob die Floskelgruppe in anderen Datenbanktabellen referenziert ist.
	 */
	public referenziertInAnderenTabellen : boolean | null = null;


	public constructor() {
		super();
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.data.schule.Floskelgruppe';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.data.schule.Floskelgruppe'].includes(name);
	}

	public static class = new Class<Floskelgruppe>('de.svws_nrw.core.data.schule.Floskelgruppe');

	public static transpilerFromJSON(json : string): Floskelgruppe {
		const obj = JSON.parse(json) as Partial<Floskelgruppe>;
		const result = new Floskelgruppe();
		result.kuerzel = (obj.kuerzel === undefined) ? null : obj.kuerzel === null ? null : obj.kuerzel;
		result.bezeichnung = (obj.bezeichnung === undefined) ? null : obj.bezeichnung === null ? null : obj.bezeichnung;
		result.idFloskelgruppenart = (obj.idFloskelgruppenart === undefined) ? null : obj.idFloskelgruppenart === null ? null : obj.idFloskelgruppenart;
		result.farbe = ((obj.farbe === undefined) || (obj.farbe === null)) ? null : RGBFarbe.transpilerFromJSON(JSON.stringify(obj.farbe));
		result.referenziertInAnderenTabellen = (obj.referenziertInAnderenTabellen === undefined) ? null : obj.referenziertInAnderenTabellen === null ? null : obj.referenziertInAnderenTabellen;
		return result;
	}

	public static transpilerToJSON(obj : Floskelgruppe) : string {
		let result = '{';
		result += '"kuerzel" : ' + ((obj.kuerzel === null) ? 'null' : JSON.stringify(obj.kuerzel)) + ',';
		result += '"bezeichnung" : ' + ((obj.bezeichnung === null) ? 'null' : JSON.stringify(obj.bezeichnung)) + ',';
		result += '"idFloskelgruppenart" : ' + ((obj.idFloskelgruppenart === null) ? 'null' : obj.idFloskelgruppenart.toString()) + ',';
		result += '"farbe" : ' + ((obj.farbe === null) ? 'null' : RGBFarbe.transpilerToJSON(obj.farbe)) + ',';
		result += '"referenziertInAnderenTabellen" : ' + ((obj.referenziertInAnderenTabellen === null) ? 'null' : obj.referenziertInAnderenTabellen.toString()) + ',';
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<Floskelgruppe>) : string {
		let result = '{';
		if (obj.kuerzel !== undefined) {
			result += '"kuerzel" : ' + ((obj.kuerzel === null) ? 'null' : JSON.stringify(obj.kuerzel)) + ',';
		}
		if (obj.bezeichnung !== undefined) {
			result += '"bezeichnung" : ' + ((obj.bezeichnung === null) ? 'null' : JSON.stringify(obj.bezeichnung)) + ',';
		}
		if (obj.idFloskelgruppenart !== undefined) {
			result += '"idFloskelgruppenart" : ' + ((obj.idFloskelgruppenart === null) ? 'null' : obj.idFloskelgruppenart.toString()) + ',';
		}
		if (obj.farbe !== undefined) {
			result += '"farbe" : ' + ((obj.farbe === null) ? 'null' : RGBFarbe.transpilerToJSON(obj.farbe)) + ',';
		}
		if (obj.referenziertInAnderenTabellen !== undefined) {
			result += '"referenziertInAnderenTabellen" : ' + ((obj.referenziertInAnderenTabellen === null) ? 'null' : obj.referenziertInAnderenTabellen.toString()) + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

}

export function cast_de_svws_nrw_core_data_schule_Floskelgruppe(obj : unknown) : Floskelgruppe {
	return obj as Floskelgruppe;
}
