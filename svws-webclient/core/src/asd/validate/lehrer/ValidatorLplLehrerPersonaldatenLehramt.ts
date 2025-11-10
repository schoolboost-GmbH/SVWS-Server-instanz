import { JavaObject } from '../../../java/lang/JavaObject';
import { LehrerPersonaldaten } from '../../../asd/data/lehrer/LehrerPersonaldaten';
import { LehrerLehramtEintrag } from '../../../asd/data/lehrer/LehrerLehramtEintrag';
import type { JavaSet } from '../../../java/util/JavaSet';
import { java_util_Set_of } from '../../../java/util/JavaSet';
import { HashMap } from '../../../java/util/HashMap';
import { Schulform } from '../../../asd/types/schule/Schulform';
import { DateManager } from '../../../asd/validate/DateManager';
import { LehrerLehramt } from '../../../asd/types/lehrer/LehrerLehramt';
import { Class } from '../../../java/lang/Class';
import { ValidatorKontext } from '../../../asd/validate/ValidatorKontext';
import type { JavaMap } from '../../../java/util/JavaMap';
import { Validator } from '../../../asd/validate/Validator';

export class ValidatorLplLehrerPersonaldatenLehramt extends Validator {

	/**
	 * Die Lehrer-Personalabschnittsdaten
	 */
	private readonly lehrerPersonaldaten: LehrerPersonaldaten;

	/**
	 * Das Geburtsdatum des Lehrers
	 */
	private readonly geburtsdatum: DateManager;

	private regulaereLehraemter: JavaSet<LehrerLehramt> = java_util_Set_of(LehrerLehramt.ID_00, LehrerLehramt.ID_01, LehrerLehramt.ID_02, LehrerLehramt.ID_04, LehrerLehramt.ID_08, LehrerLehramt.ID_09, LehrerLehramt.ID_10, LehrerLehramt.ID_11, LehrerLehramt.ID_12, LehrerLehramt.ID_14, LehrerLehramt.ID_15, LehrerLehramt.ID_16, LehrerLehramt.ID_17, LehrerLehramt.ID_19, LehrerLehramt.ID_20, LehrerLehramt.ID_21, LehrerLehramt.ID_24, LehrerLehramt.ID_25, LehrerLehramt.ID_27, LehrerLehramt.ID_29, LehrerLehramt.ID_30, LehrerLehramt.ID_31, LehrerLehramt.ID_35, LehrerLehramt.ID_40, LehrerLehramt.ID_50, LehrerLehramt.ID_51, LehrerLehramt.ID_52, LehrerLehramt.ID_53, LehrerLehramt.ID_54, LehrerLehramt.ID_55, LehrerLehramt.ID_96);


	/**
	 * Erstellt einen neuen Validator mit den übergebenen Daten und dem übergebenen Kontext
	 *
	 * @param lehrerPersonaldaten   die Lehrer-Personaldaten, die geprüft werden sollen
	 * @param geburtsdatum          das Geburtsdatum des Lehrers
	 * @param kontext               der Kontext des Validators
	 */
	public constructor(lehrerPersonaldaten: LehrerPersonaldaten, geburtsdatum: DateManager, kontext: ValidatorKontext) {
		super(kontext);
		this.lehrerPersonaldaten = lehrerPersonaldaten;
		this.geburtsdatum = geburtsdatum;
	}

	protected pruefe(): boolean {
		let success: boolean = true;
		const schulform: Schulform = this.kontext().getSchulform();
		const istFW: boolean = JavaObject.equalsTranspiler(Schulform.FW, (schulform));
		const anzahlLehraemter: number = this.lehrerPersonaldaten.lehraemter.size();
		if (!istFW && !this.exec(0, { getAsBoolean: () => anzahlLehraemter === 0 }, "Zu jeder Lehrkraft muss mindest ein Lehramt vorliegen. Lehrer ID: " + this.lehrerPersonaldaten.id))
			success = false;
		if (istFW && !this.exec(1, { getAsBoolean: () => anzahlLehraemter > 0 }, "Bei Freien Waldorfschulen darf kein Lehramt erfasst sein. Lehrer ID: " + this.lehrerPersonaldaten.id))
			success = false;
		const lehramtMap: JavaMap<number, LehrerLehramtEintrag> = new HashMap<number, LehrerLehramtEintrag>();
		for (const lehrerLehramtEintrag of this.lehrerPersonaldaten.lehraemter) {
			if (lehramtMap.put(lehrerLehramtEintrag.idKatalogLehramt, lehrerLehramtEintrag) !== null) {
				try {
					success = this.exec(2, { getAsBoolean: () => true }, "Das Lehramt '" + LehrerLehramt.data().getEintragByIDOrException(lehrerLehramtEintrag.idKatalogLehramt).text + "' ist mehrfach eingetragen. Bitte löschen Sie die überflüssigen Einträge.");
				} catch(e : any) {
					success = this.exec(2, { getAsBoolean: () => true }, "Das Lehramt '" + lehrerLehramtEintrag.idKatalogLehramt + "' ist mehrfach eingetragen. Bitte löschen Sie die überflüssigen Einträge.");
				}
			}
		}
		if (this.geburtsdatum.getJahr() >= 2003 && this.geburtsdatum.getJahr() <= 2006) {
			for (const lehrerLehramtEintrag2 of this.lehrerPersonaldaten.lehraemter) {
				const lehrerLehramt2: LehrerLehramt | null = LehrerLehramt.data().getWertByIDOrNull(lehrerLehramtEintrag2.idKatalogLehramt);
				if (lehrerLehramt2 === null)
					continue;
				if (this.regulaereLehraemter.contains(lehrerLehramt2)) {
					try {
						success = this.exec(3, { getAsBoolean: () => true }, "Für das Lehramt '" + LehrerLehramt.data().getEintragByIDOrException(lehrerLehramtEintrag2.idKatalogLehramt).text + "' ist die Lehrkraft sehr jung. Wenn das Alter der Lehrkraft korrekt ist, sollte das eingetragene Lehramt überprüft werden. Bitte verwenden Sie die 'regulären' Lehrämter nur dann, wenn eine entsprechende abgeschlossene Ausbildung vorliegt. Wenn es sich um einen Studierenden handelt, der neben seinem Studium als Lehrkraft tätig ist, verwenden sie bitte das Lehramt 'Studierende'. Ansonsten tragen Sie bitte das Lehramt 'Sonstiges' ein. ");
					} catch(e : any) {
						success = this.exec(3, { getAsBoolean: () => true }, "Für das Lehramt mit der ID '" + lehrerLehramtEintrag2.idKatalogLehramt + "' ist die Lehrkraft sehr jung. Wenn das Alter der Lehrkraft korrekt ist, sollte das eingetragene Lehramt überprüft werden. Bitte verwenden Sie die 'regulären' Lehrämter nur dann, wenn eine entsprechende abgeschlossene Ausbildung vorliegt. Wenn es sich um einen Studierenden handelt, der neben seinem Studium als Lehrkraft tätig ist, verwenden sie bitte das Lehramt 'Studierende'. Ansonsten tragen Sie bitte das Lehramt 'Sonstiges' ein. ");
					}
				}
			}
		}
		return success;
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.asd.validate.lehrer.ValidatorLplLehrerPersonaldatenLehramt';
	}

	isTranspiledInstanceOf(name: string): boolean {
		return ['de.svws_nrw.asd.validate.BasicValidator', 'de.svws_nrw.asd.validate.lehrer.ValidatorLplLehrerPersonaldatenLehramt', 'de.svws_nrw.asd.validate.Validator'].includes(name);
	}

	public static class = new Class<ValidatorLplLehrerPersonaldatenLehramt>('de.svws_nrw.asd.validate.lehrer.ValidatorLplLehrerPersonaldatenLehramt');

}

export function cast_de_svws_nrw_asd_validate_lehrer_ValidatorLplLehrerPersonaldatenLehramt(obj: unknown): ValidatorLplLehrerPersonaldatenLehramt {
	return obj as ValidatorLplLehrerPersonaldatenLehramt;
}
