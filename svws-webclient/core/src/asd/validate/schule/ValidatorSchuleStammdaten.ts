import { Schulform } from '../../../asd/types/schule/Schulform';
import { Class } from '../../../java/lang/Class';
import { JavaString } from '../../../java/lang/JavaString';
import { ValidatorKontext } from '../../../asd/validate/ValidatorKontext';
import { Validator } from '../../../asd/validate/Validator';

export class ValidatorSchuleStammdaten extends Validator {


	/**
	 * Erstellt einen neuen Validator mit den übergebenen Daten und dem übergebenen Kontext
	 *
	 * @param kontext   der Kontext des Validators
	 */
	public constructor(kontext: ValidatorKontext) {
		super(kontext);
	}

	protected pruefe(): boolean {
		let success: boolean = true;
		const schulformKrz: string | null = super.kontext().getSchuleStammdaten().schulform;
		success = this.exec(0, { getAsBoolean: () => (schulformKrz === null) || (JavaString.isBlank(schulformKrz)) }, "Die Schulform muss gesetzt sein.");
		if (!success)
			return false;
		success = this.exec(1, { getAsBoolean: () => {
			try {
				return Schulform.data().getWertByKuerzel(schulformKrz) === null;
			} catch(e : any) {
				return false;
			}
		} }, "Das Kürzel für die Schulform ist ungültig.");
		return success;
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.asd.validate.schule.ValidatorSchuleStammdaten';
	}

	isTranspiledInstanceOf(name: string): boolean {
		return ['de.svws_nrw.asd.validate.schule.ValidatorSchuleStammdaten', 'de.svws_nrw.asd.validate.BasicValidator', 'de.svws_nrw.asd.validate.Validator'].includes(name);
	}

	public static class = new Class<ValidatorSchuleStammdaten>('de.svws_nrw.asd.validate.schule.ValidatorSchuleStammdaten');

}

export function cast_de_svws_nrw_asd_validate_schule_ValidatorSchuleStammdaten(obj: unknown): ValidatorSchuleStammdaten {
	return obj as ValidatorSchuleStammdaten;
}
