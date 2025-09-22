import { DateManager } from '../../../asd/validate/DateManager';
import { LehrerStammdaten } from '../../../asd/data/lehrer/LehrerStammdaten';
import { Class } from '../../../java/lang/Class';
import { ValidatorKontext } from '../../../asd/validate/ValidatorKontext';
import { Validator } from '../../../asd/validate/Validator';

export class ValidatorLehrerStammdatenGeburtsdatum extends Validator {

	/**
	 * Die Lehrer-Stammdaten
	 */
	private readonly daten : LehrerStammdaten;


	/**
	 * Erstellt einen neuen Validator mit den übergebenen Daten und dem übergebenen Kontext
	 *
	 * @param daten     die Daten des Validators
	 * @param kontext   der Kontext des Validators
	 */
	public constructor(daten : LehrerStammdaten, kontext : ValidatorKontext) {
		super(kontext);
		this.daten = daten;
	}

	protected pruefe() : boolean {
		let success : boolean = true;
		let geburtsdatum : DateManager | null = null;
		let errorMsg : string = "";
		try {
			geburtsdatum = DateManager.from(this.daten.geburtsdatum);
		} catch(e : any) {
			errorMsg = e.getMessage();
		}
		const finalGeburtsdatum : DateManager | null = geburtsdatum;
		success = this.exec(0, { getAsBoolean : () => finalGeburtsdatum === null }, "Das Geburtsdatum ist ungültig: " + errorMsg);
		if (!success)
			return false;
		const schuljahr : number = this.kontext().getSchuljahr();
		success = this.exec(1, { getAsBoolean : () => finalGeburtsdatum === null || !finalGeburtsdatum.istInJahren(schuljahr - 80, schuljahr - 18) }, "Unzulässige Eintragung im Feld Jahr (Geburtsdatum). Zulässig sind die Werte " + (schuljahr - 80) + " bis " + (schuljahr - 18) + ".");
		return success;
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.asd.validate.lehrer.ValidatorLehrerStammdatenGeburtsdatum';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.asd.validate.lehrer.ValidatorLehrerStammdatenGeburtsdatum', 'de.svws_nrw.asd.validate.Validator'].includes(name);
	}

	public static class = new Class<ValidatorLehrerStammdatenGeburtsdatum>('de.svws_nrw.asd.validate.lehrer.ValidatorLehrerStammdatenGeburtsdatum');

}

export function cast_de_svws_nrw_asd_validate_lehrer_ValidatorLehrerStammdatenGeburtsdatum(obj : unknown) : ValidatorLehrerStammdatenGeburtsdatum {
	return obj as ValidatorLehrerStammdatenGeburtsdatum;
}
