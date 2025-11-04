import { ValidatorFehlerart } from '../../asd/validate/ValidatorFehlerart';
import { ValidatorManager } from '../../asd/validate/ValidatorManager';
import { ValidatorException } from '../../asd/validate/ValidatorException';
import { BasicValidator } from '../../asd/validate/BasicValidator';
import { ArrayList } from '../../java/util/ArrayList';
import { ValidatorFehler } from '../../asd/validate/ValidatorFehler';
import type { List } from '../../java/util/List';
import { Class } from '../../java/lang/Class';
import { ValidatorKontext } from '../../asd/validate/ValidatorKontext';
import type { BooleanSupplier } from '../../java/util/function/BooleanSupplier';
import { Exception } from '../../java/lang/Exception';

export abstract class Validator extends BasicValidator {

	/**
	 * Der vom Validator genutzte Kontext
	 */
	private readonly _kontext: ValidatorKontext;

	/**
	 * Eine Liste von Validatoren, die bei diesem Validator mitgeprüft werden.
	 */
	protected readonly _validatoren: List<Validator> = new ArrayList<Validator>();


	/**
	 * Erstellt einen neuen Validator in dem übegebenen Kontext
	 *
	 * @param kontext   der Kontext, in dem der Validator ausgeführt wird
	 */
	protected constructor(kontext: ValidatorKontext) {
		super(ValidatorFehlerart.UNGENUTZT);
		this._kontext = kontext;
		this._defaultValidatorFehlerart = this.getValidatorFehlerart(-1);
	}

	/**
	 * Gibt den Kontext des Validators zurück.
	 *
	 * @return der Kontext des Validators
	 */
	public kontext(): ValidatorKontext {
		return this._kontext;
	}

	/**
	 * Gibt den zugehörigen ValidatorManager zurück.
	 *
	 * @return der ValidatorManager
	 */
	public getValidatorManager(): ValidatorManager {
		return this._kontext.getValidatorManager();
	}

	/**
	 * Führt die Prüfungen des Validators aus. Dabei wird zunächst die Fehlerliste
	 * geleert und durch die ausführenden Prüfroutinen befüllt.
	 *
	 * @return true, falls alle Prüfroutinen erfolgreich waren, und ansonsten false
	 */
	public run(): boolean {
		let success: boolean = true;
		this._fehler.clear();
		if (this._kontext.getValidatorManager().isValidatorActiveInSchuljahr(this._kontext.getSchuljahr(), this.getClass().getCanonicalName())) {
			for (const validator of this._validatoren) {
				if (!validator.run())
					success = false;
				this._fehler.addAll(validator._fehler);
				this.updateFehlerart(validator.getFehlerart());
			}
			try {
				if (!this.pruefe())
					success = false;
			} catch(e : any) {
				this.addFehler(-1, "Unerwarteter Fehler bei der Validierung: " + e.getMessage());
			}
		}
		return success;
	}

	/**
	 * Diese Methode führt einen Prüfschritt aus, genau dann, wenn der Validator selbst und dieser explixite Schritt aktiv sind.
	 * Das Lambda stellt die Fehlerbedingung da, die TRUE liefert, wenn ein Fehler vorliegt.
	 *
	 * @param schrittNummer     die Nummer des Prüfschrittes. Startet in der Regel mit 0
	 * @param fehlerbedingung   die Prüfschrittbedingung als Lambda
	 * @param error             die Fehlermeldung, falls der Prüfschritt fehlschlägt
	 *
	 * @return true, wenn der Prüfschritt erfolgreich ausgeführt wurde oder nicht aktiv ist und false, wenn ein Fehler beim Prüfschritt auftritt
	 */
	protected exec(schrittNummer: number, fehlerbedingung: BooleanSupplier, error: string): boolean {
		if (schrittNummer < 0)
			throw new ValidatorException("Ein negativer Wert als Nummer für einen Validator-Prüfschritt ist nicht zulässig. Die -1 wird in Fehlercodes nur für interne Fehler verwendet.")
		const isActive: boolean = this._kontext.getValidatorManager().isPruefschrittActiveInSchuljahr(this._kontext.getSchuljahr(), this.getClass().getCanonicalName(), schrittNummer);
		if (!isActive)
			return true;
		const result: boolean = fehlerbedingung.getAsBoolean();
		if (result) {
			this.addFehler(schrittNummer, error);
			return false;
		}
		return true;
	}

	/**
	 * Gibt die Fehler des Validators als unmodifiable List zurück.
	 *
	 * @return die Liste der Fehler als unmodifiable List
	 */
	public getFehler(): List<ValidatorFehler> {
		return new ArrayList<ValidatorFehler>(this._fehler);
	}

	/**
	 * Die Fehlerart, welche diesem speziellen Validator zugeordnet ist.
	 *
	 * @param pruefschritt   der Prüfschritt, bei welchem der Fehler aufgetreten ist
	 *
	 * @return die Fehlerart
	 */
	public getValidatorFehlerart(pruefschritt: number): ValidatorFehlerart {
		return this._kontext.getValidatorManager().getFehlerartBySchuljahrAndValidatorClassAndPruefschritt(this._kontext.getSchuljahr(), this.getClass(), pruefschritt);
	}

	/**
	 * Gibt das Fehlercode-Präfix zurück, welcher diesem speziellen Validator zugeordnet ist.
	 *
	 * @return das Fehlercode-Präfix
	 */
	public getFehlercodePraefix(): string {
		return this._kontext.getValidatorManager().getFehlercodePraefixBySchuljahrAndValidatorClass(this._kontext.getSchuljahr(), this.getClass());
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.asd.validate.Validator';
	}

	isTranspiledInstanceOf(name: string): boolean {
		return ['de.svws_nrw.asd.validate.BasicValidator', 'de.svws_nrw.asd.validate.Validator'].includes(name);
	}

	public static class = new Class<Validator>('de.svws_nrw.asd.validate.Validator');

}

export function cast_de_svws_nrw_asd_validate_Validator(obj: unknown): Validator {
	return obj as Validator;
}
