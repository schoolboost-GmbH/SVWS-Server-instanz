import { CoreTypeSimple } from '../../../asd/types/CoreTypeSimple';
import { HerkunftsartenKatalogEintrag } from '../../../asd/data/schueler/HerkunftsartenKatalogEintrag';
import { CoreTypeDataManager } from '../../../asd/utils/CoreTypeDataManager';
import { Class } from '../../../java/lang/Class';

export class Herkunftsarten extends CoreTypeSimple<HerkunftsartenKatalogEintrag, Herkunftsarten> {


	/**
	 * Erstellt eine Herkunftsarten mit Standardwerten
	 */
	public constructor() {
		super();
	}

	/**
	 * Initialisiert den Core-Type mit dem angegebenen Manager.
	 *
	 * @param manager   der Manager für die Daten des Core-Types
	 */
	public static init(manager: CoreTypeDataManager<HerkunftsartenKatalogEintrag, Herkunftsarten>): void {
		CoreTypeDataManager.putManager(Herkunftsarten.class, manager);
	}

	/**
	 * Gibt den Daten-Manager für den Zugriff auf die Core-Type-Daten zurück, sofern dieser initialisiert wurde.
	 *
	 * @return der Daten-Manager
	 */
	public static data(): CoreTypeDataManager<HerkunftsartenKatalogEintrag, Herkunftsarten> {
		return CoreTypeDataManager.getManager(Herkunftsarten.class);
	}

	/**
	 * Gibt alle Werte des Core-Types zurück.
	 *
	 * @return die Werte des Core-Types als Array
	 */
	public static values(): Array<Herkunftsarten> {
		return CoreTypeSimple.valuesByClass(Herkunftsarten.class);
	}

	/**
	 * Erzeugt eine Instance dieser Klasse.
	 */
	public getInstance(): Herkunftsarten | null {
		return new Herkunftsarten();
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.asd.types.schueler.Herkunftsarten';
	}

	isTranspiledInstanceOf(name: string): boolean {
		return ['de.svws_nrw.asd.types.CoreType', 'java.lang.Comparable', 'de.svws_nrw.asd.types.schueler.Herkunftsarten', 'de.svws_nrw.asd.types.CoreTypeSimple'].includes(name);
	}

	public static class = new Class<Herkunftsarten>('de.svws_nrw.asd.types.schueler.Herkunftsarten');

}

export function cast_de_svws_nrw_asd_types_schueler_Herkunftsarten(obj: unknown): Herkunftsarten {
	return obj as Herkunftsarten;
}
