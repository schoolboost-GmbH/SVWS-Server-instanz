import { JavaObject } from '../../../java/lang/JavaObject';
import type { JavaSet } from '../../../java/util/JavaSet';
import { java_util_Set_of } from '../../../java/util/JavaSet';
import { LehrerPersonalabschnittsdaten } from '../../../asd/data/lehrer/LehrerPersonalabschnittsdaten';
import { Class } from '../../../java/lang/Class';
import { LehrerEinsatzstatus } from '../../../asd/types/lehrer/LehrerEinsatzstatus';
import { ValidatorKontext } from '../../../asd/validate/ValidatorKontext';
import { Validator } from '../../../asd/validate/Validator';

export class ValidatorLehrerPersonalabschnittsdatenPflichtstundensoll extends Validator {

	/**
	 * Die Lehrer-Personalabschnittsdaten
	 */
	private readonly daten: LehrerPersonalabschnittsdaten;


	/**
	 * Erstellt einen neuen Validator mit den übergebenen Daten und dem übergebenen Kontext
	 *
	 * @param daten     die Daten des Validators
	 * @param kontext   der Kontext des Validators
	 */
	public constructor(daten: LehrerPersonalabschnittsdaten, kontext: ValidatorKontext) {
		super(kontext);
		this.daten = daten;
	}

	protected pruefe(): boolean {
		let success: boolean = true;
		const pflichtstundensoll: number | null = this.daten.pflichtstundensoll;
		const einsatzstatus: LehrerEinsatzstatus | null = LehrerEinsatzstatus.getBySchluessel(this.daten.einsatzstatus);
		success = this.exec(0, { getAsBoolean: () => pflichtstundensoll === null }, "Kein Wert im Feld 'pflichtstundensoll'.");
		if (!success)
			return false;
		success = this.exec(1, { getAsBoolean: () => (pflichtstundensoll === null || pflichtstundensoll < 0.0) || (pflichtstundensoll > 41.0) }, "Unzulässiger Wert im Feld 'pflichtstundensoll'. Zulässig sind im Stundenmodell Werte im Bereich von 0,00 bis 41,00 Wochenstunden. Im Minutenmodell zwischen 0,00 und 1845,00 Minuten.");
		if (!this.exec(2, { getAsBoolean: () => (einsatzstatus as unknown === LehrerEinsatzstatus.B as unknown) && (pflichtstundensoll === 0.0) }, "Bei Lehrkräften, die von einer anderen Schule abgeordnet wurden (Einsatzstatus = 'B'), darf das Pflichtstundensoll nicht 0,00 betragen."))
			success = false;
		const beschaeftigungsart: string | null = this.daten.beschaeftigungsart;
		const setBeschaeftigungsart: JavaSet<string> = java_util_Set_of("WV", "WT");
		const fehlertext3: string | null = "Ist bei einer Lehrkraft im Feld 'Pflichtstundensoll' der Wert = 0.00 eingetragen, so muss das Feld 'Einsatzstatus' den Schlüssel 'Stammschule, ganz oder teilweise auch an anderen Schulen tätig' oder die 'Beschäftigungsart' den Schlüssel 'Beamte auf Widerruf (LAA) in Vollzeit' bzw. 'Beamte auf Widerruf (LAA) in Teilzeit' aufweisen.";
		if (!this.exec(3, { getAsBoolean: () => (pflichtstundensoll === 0) && (!JavaObject.equalsTranspiler(LehrerEinsatzstatus.A, (einsatzstatus)) && !setBeschaeftigungsart.contains(beschaeftigungsart)) }, fehlertext3))
			success = false;
		return success;
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.asd.validate.lehrer.ValidatorLehrerPersonalabschnittsdatenPflichtstundensoll';
	}

	isTranspiledInstanceOf(name: string): boolean {
		return ['de.svws_nrw.asd.validate.BasicValidator', 'de.svws_nrw.asd.validate.lehrer.ValidatorLehrerPersonalabschnittsdatenPflichtstundensoll', 'de.svws_nrw.asd.validate.Validator'].includes(name);
	}

	public static class = new Class<ValidatorLehrerPersonalabschnittsdatenPflichtstundensoll>('de.svws_nrw.asd.validate.lehrer.ValidatorLehrerPersonalabschnittsdatenPflichtstundensoll');

}

export function cast_de_svws_nrw_asd_validate_lehrer_ValidatorLehrerPersonalabschnittsdatenPflichtstundensoll(obj: unknown): ValidatorLehrerPersonalabschnittsdatenPflichtstundensoll {
	return obj as ValidatorLehrerPersonalabschnittsdatenPflichtstundensoll;
}
