import { LehrerStammdaten } from '../../../asd/data/lehrer/LehrerStammdaten';
import { Class } from '../../../java/lang/Class';
import { JavaString } from '../../../java/lang/JavaString';
import { ValidatorKontext } from '../../../asd/validate/ValidatorKontext';
import { Validator } from '../../../asd/validate/Validator';

export class ValidatorLskLehrerStammdatenKuerzel extends Validator {

	/**
	 * Die Lehrer-Stammdaten
	 */
	private readonly daten: LehrerStammdaten;


	/**
	 * Erstellt einen neuen Validator mit den übergebenen Daten und dem übergebenen Kontext
	 *
	 * @param daten     die Daten des Validators
	 * @param kontext   der Kontext des Validators
	 */
	public constructor(daten: LehrerStammdaten, kontext: ValidatorKontext) {
		super(kontext);
		this.daten = daten;
	}

	protected pruefe(): boolean {
		if ((this.daten.kuerzel === null) || JavaString.isBlank(this.daten.kuerzel))
			return true;
		let success: boolean = true;
		if (this.exec(0, { getAsBoolean: () => JavaString.matches(this.daten.kuerzel, "^[A-ZÄÖÜ][A-ZÄÖÜ0-9\\-\\ ]{0,3}$") }, "Der Eintrag " + this.daten.kuerzel + " ist als Lehrerkürzel unzulässig. Zulässig sind: 1. Stelle: A-Z, Ä, Ö, Ü; 2.-4. Stelle: A-Z, Ä, Ö, Ü, -, 'kein Eintrag'. Buchstaben müssen großgeschrieben werden."))
			success = false;
		return success;
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.asd.validate.lehrer.ValidatorLskLehrerStammdatenKuerzel';
	}

	isTranspiledInstanceOf(name: string): boolean {
		return ['de.svws_nrw.asd.validate.BasicValidator', 'de.svws_nrw.asd.validate.lehrer.ValidatorLskLehrerStammdatenKuerzel', 'de.svws_nrw.asd.validate.Validator'].includes(name);
	}

	public static class = new Class<ValidatorLskLehrerStammdatenKuerzel>('de.svws_nrw.asd.validate.lehrer.ValidatorLskLehrerStammdatenKuerzel');

}

export function cast_de_svws_nrw_asd_validate_lehrer_ValidatorLskLehrerStammdatenKuerzel(obj: unknown): ValidatorLskLehrerStammdatenKuerzel {
	return obj as ValidatorLskLehrerStammdatenKuerzel;
}
