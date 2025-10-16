import { JavaObject } from '../../../java/lang/JavaObject';
import { Class } from '../../../java/lang/Class';

export class Floskel extends JavaObject {

	/**
	 * Das Kürzel der Floskel
	 */
	public kuerzel: string | null = null;

	/**
	 * Der Text
	 */
	public text: string | null = null;

	/**
	 * Das Kürzel der Floskelgruppe
	 */
	public kuerzelFloskelgruppe: string | null = null;

	/**
	 * Die ID des Fachs
	 */
	public idFach: number | null = null;

	/**
	 * Das Niveau
	 */
	public niveau: string | null = null;

	/**
	 * Die ID des Jahrgangs
	 */
	public idJahrgang: number | null = null;

	/**
	 * Gibt an, ob die Floskel in anderen Datenbanktabellen referenziert ist.
	 */
	public referenziertInAnderenTabellen: boolean | null = null;


	public constructor() {
		super();
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.data.schule.Floskel';
	}

	isTranspiledInstanceOf(name: string): boolean {
		return ['de.svws_nrw.core.data.schule.Floskel'].includes(name);
	}

	public static class = new Class<Floskel>('de.svws_nrw.core.data.schule.Floskel');

	public static transpilerFromJSON(json: string): Floskel {
		const obj = JSON.parse(json) as Partial<Floskel>;
		const result = new Floskel();
		result.kuerzel = (obj.kuerzel === undefined) ? null : obj.kuerzel === null ? null : obj.kuerzel;
		result.text = (obj.text === undefined) ? null : obj.text === null ? null : obj.text;
		result.kuerzelFloskelgruppe = (obj.kuerzelFloskelgruppe === undefined) ? null : obj.kuerzelFloskelgruppe === null ? null : obj.kuerzelFloskelgruppe;
		result.idFach = (obj.idFach === undefined) ? null : obj.idFach === null ? null : obj.idFach;
		result.niveau = (obj.niveau === undefined) ? null : obj.niveau === null ? null : obj.niveau;
		result.idJahrgang = (obj.idJahrgang === undefined) ? null : obj.idJahrgang === null ? null : obj.idJahrgang;
		result.referenziertInAnderenTabellen = (obj.referenziertInAnderenTabellen === undefined) ? null : obj.referenziertInAnderenTabellen === null ? null : obj.referenziertInAnderenTabellen;
		return result;
	}

	public static transpilerToJSON(obj: Floskel): string {
		let result = '{';
		result += '"kuerzel" : ' + ((obj.kuerzel === null) ? 'null' : JSON.stringify(obj.kuerzel)) + ',';
		result += '"text" : ' + ((obj.text === null) ? 'null' : JSON.stringify(obj.text)) + ',';
		result += '"kuerzelFloskelgruppe" : ' + ((obj.kuerzelFloskelgruppe === null) ? 'null' : JSON.stringify(obj.kuerzelFloskelgruppe)) + ',';
		result += '"idFach" : ' + ((obj.idFach === null) ? 'null' : obj.idFach.toString()) + ',';
		result += '"niveau" : ' + ((obj.niveau === null) ? 'null' : JSON.stringify(obj.niveau)) + ',';
		result += '"idJahrgang" : ' + ((obj.idJahrgang === null) ? 'null' : obj.idJahrgang.toString()) + ',';
		result += '"referenziertInAnderenTabellen" : ' + ((obj.referenziertInAnderenTabellen === null) ? 'null' : obj.referenziertInAnderenTabellen.toString()) + ',';
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj: Partial<Floskel>): string {
		let result = '{';
		if (obj.kuerzel !== undefined) {
			result += '"kuerzel" : ' + ((obj.kuerzel === null) ? 'null' : JSON.stringify(obj.kuerzel)) + ',';
		}
		if (obj.text !== undefined) {
			result += '"text" : ' + ((obj.text === null) ? 'null' : JSON.stringify(obj.text)) + ',';
		}
		if (obj.kuerzelFloskelgruppe !== undefined) {
			result += '"kuerzelFloskelgruppe" : ' + ((obj.kuerzelFloskelgruppe === null) ? 'null' : JSON.stringify(obj.kuerzelFloskelgruppe)) + ',';
		}
		if (obj.idFach !== undefined) {
			result += '"idFach" : ' + ((obj.idFach === null) ? 'null' : obj.idFach.toString()) + ',';
		}
		if (obj.niveau !== undefined) {
			result += '"niveau" : ' + ((obj.niveau === null) ? 'null' : JSON.stringify(obj.niveau)) + ',';
		}
		if (obj.idJahrgang !== undefined) {
			result += '"idJahrgang" : ' + ((obj.idJahrgang === null) ? 'null' : obj.idJahrgang.toString()) + ',';
		}
		if (obj.referenziertInAnderenTabellen !== undefined) {
			result += '"referenziertInAnderenTabellen" : ' + ((obj.referenziertInAnderenTabellen === null) ? 'null' : obj.referenziertInAnderenTabellen.toString()) + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

}

export function cast_de_svws_nrw_core_data_schule_Floskel(obj: unknown): Floskel {
	return obj as Floskel;
}
