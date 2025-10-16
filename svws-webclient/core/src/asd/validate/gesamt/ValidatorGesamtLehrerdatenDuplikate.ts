import type { JavaSet } from '../../../java/util/JavaSet';
import { HashMap } from '../../../java/util/HashMap';
import { LehrerStammdaten } from '../../../asd/data/lehrer/LehrerStammdaten';
import type { List } from '../../../java/util/List';
import { Class } from '../../../java/lang/Class';
import { Geschlecht } from '../../../asd/types/Geschlecht';
import { ValidatorKontext } from '../../../asd/validate/ValidatorKontext';
import type { JavaMap } from '../../../java/util/JavaMap';
import { Validator } from '../../../asd/validate/Validator';
import { HashSet } from '../../../java/util/HashSet';

export class ValidatorGesamtLehrerdatenDuplikate extends Validator {

	private readonly listLehrerStammdaten: List<LehrerStammdaten>;


	/**
	 * Erstellt einen neuen Validator mit den übergebenen Daten und dem übergebenen Kontext
	 *
	 * @param listLehrerStammdaten      die Liste aller Lehrerstammdaten
	 * @param kontext             		der Kontext des Validators
	 */
	public constructor(listLehrerStammdaten: List<LehrerStammdaten>, kontext: ValidatorKontext) {
		super(kontext);
		this.listLehrerStammdaten = listLehrerStammdaten;
	}

	protected pruefe(): boolean {
		let success: boolean = true;
		if (this.listLehrerStammdaten.isEmpty())
			return success;
		const keys: JavaMap<string, LehrerStammdaten> = new HashMap<string, LehrerStammdaten>();
		const ids: JavaSet<number> = new HashSet<number>();
		for (const lehrer of this.listLehrerStammdaten) {
			const geschlecht: Geschlecht | null = Geschlecht.fromValue(lehrer.geschlecht);
			const key: string = lehrer.nachname + "__" + lehrer.vorname + "__" + ((lehrer.geburtsdatum === null) ? "" : lehrer.geburtsdatum) + "__" + ((geschlecht === null) ? lehrer.geschlecht : geschlecht.kuerzel);
			const istNeu: boolean = ids.add(lehrer.id);
			success = this.exec(0, { getAsBoolean: () => !istNeu }, "Lehrkäfte: Die ID " + lehrer.id + " kommt in der Liste mehrfach vor.");
			const other: LehrerStammdaten | null = keys.put(key, lehrer);
			if (other === null)
				continue;
			success = this.exec(1, { getAsBoolean: () => true }, "Lehrkäfte: Bei den IDs " + lehrer.id + " und " + other.id + " kommt die Kombination aus Nachname '" + lehrer.nachname + "', Vorname '" + lehrer.vorname + "', Geburtsdatum '" + lehrer.geburtsdatum + "' und Geschlecht '" + lehrer.geschlecht + "' mehrmals vor. Falls es sich hierbei um eine Person handelt, so fassen Sie die Datensätze bitte unter einer Lehrerabkürzung zusammen.");
		}
		return success;
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.asd.validate.gesamt.ValidatorGesamtLehrerdatenDuplikate';
	}

	isTranspiledInstanceOf(name: string): boolean {
		return ['de.svws_nrw.asd.validate.gesamt.ValidatorGesamtLehrerdatenDuplikate', 'de.svws_nrw.asd.validate.Validator'].includes(name);
	}

	public static class = new Class<ValidatorGesamtLehrerdatenDuplikate>('de.svws_nrw.asd.validate.gesamt.ValidatorGesamtLehrerdatenDuplikate');

}

export function cast_de_svws_nrw_asd_validate_gesamt_ValidatorGesamtLehrerdatenDuplikate(obj: unknown): ValidatorGesamtLehrerdatenDuplikate {
	return obj as ValidatorGesamtLehrerdatenDuplikate;
}
