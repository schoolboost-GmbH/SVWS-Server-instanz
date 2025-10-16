import { JavaObject } from '../../../../java/lang/JavaObject';
import { BKGymAbiturdatenManager } from '../../../../core/abschluss/bk/d/BKGymAbiturdatenManager';
import { GostAbiturFach } from '../../../../core/types/gost/GostAbiturFach';
import { HashMap } from '../../../../java/util/HashMap';
import { ArrayList } from '../../../../java/util/ArrayList';
import { BKGymAbiturFachbelegung } from '../../../../core/abschluss/bk/d/BKGymAbiturFachbelegung';
import { BeruflichesGymnasiumStundentafel } from '../../../../asd/data/schule/BeruflichesGymnasiumStundentafel';
import { BKGymBelegungsfehler } from '../../../../core/abschluss/bk/d/BKGymBelegungsfehler';
import { BeruflichesGymnasiumPruefungsordnungAnlage } from '../../../../asd/types/schule/BeruflichesGymnasiumPruefungsordnungAnlage';
import type { List } from '../../../../java/util/List';
import { Class } from '../../../../java/lang/Class';
import type { JavaMap } from '../../../../java/util/JavaMap';
import { BeruflichesGymnasiumStundentafelFach } from '../../../../asd/data/schule/BeruflichesGymnasiumStundentafelFach';

export abstract class BKGymBelegpruefung extends JavaObject {

	/**
	 * Die Abiturdaten-Manager
	 */
	protected readonly manager: BKGymAbiturdatenManager;

	/**
	 * Die Belegungsfehler, die bei der Prüfung entstanden sind.
	 */
	private readonly belegungsfehler: List<BKGymBelegungsfehler> = new ArrayList<BKGymBelegungsfehler>();


	/**
	 * Erzeugt eine neue Belegprüfung mit dem angegebenen Manager.
	 *
	 * @param manager   der Manager für die Abiturdaten
	 */
	public constructor(manager: BKGymAbiturdatenManager) {
		super();
		this.manager = manager;
	}

	/**
	 * Fügt einen Belegungsfehler zu der Belegprüfung hinzu. Diese Methode wird von den Sub-Klassen
	 * aufgerufen, wenn dort ein Belegungsfehler erkannt wird.
	 *
	 * @param fehler   der hinzuzufügende Belegungsfehler
	 */
	protected addFehler(fehler: BKGymBelegungsfehler): void {
		if (!this.belegungsfehler.contains(fehler))
			this.belegungsfehler.add(fehler);
	}

	/**
	 * Gibt die Belegungsfehler zurück, welche bei der Prüfung aufgetreten sind.
	 *
	 * @return die Belegungsfehler
	 */
	public getBelegungsfehler(): List<BKGymBelegungsfehler> {
		return this.belegungsfehler;
	}

	/**
	 * Gibt zurück, ob ein "echter" Belegungsfehler vorliegt und nicht nur eine Warnung oder ein Hinweis.
	 *
	 * @return true, wenn kein "echter" Belegungsfehler vorliegt, und ansonsten false.
	 */
	public istErfolgreich(): boolean {
		for (const fehler of this.belegungsfehler)
			if (!fehler.istInfo())
				return false;
		return true;
	}

	/**
	 * Führt die Belegprüfung durch.
	 */
	public abstract pruefe(): void;

	/**
	 * Diese Methode bestimmt die möglichen Stundentafeln anhand der Abiturfächer und der übergebenen Anlage.
	 * Sollten die Abiturfächer nicht korrekt bestimmt werden können, so wird eine entsprechende Fehlermeldung erzeugt, dass das Abiturfach fehlt.
	 *
	 * @param anlage   die Anlage aus der Prüfungsordnung
	 *
	 * @return die Liste der möglichen Stundentafeln
	 */
	protected getStundentafelnByAbiturfaechern(anlage: BeruflichesGymnasiumPruefungsordnungAnlage): List<BeruflichesGymnasiumStundentafel> {
		const lk1: BKGymAbiturFachbelegung | null = this.manager.getAbiFachbelegung(GostAbiturFach.LK1);
		if (lk1 === null)
			this.addFehler(BKGymBelegungsfehler.LK_1);
		const lk2: BKGymAbiturFachbelegung | null = this.manager.getAbiFachbelegung(GostAbiturFach.LK2);
		if (lk2 === null)
			this.addFehler(BKGymBelegungsfehler.LK_2);
		const mglStundentafeln: List<BeruflichesGymnasiumStundentafel> = this.manager.getStundentafelByLeistungskurse(anlage);
		if (mglStundentafeln.isEmpty())
			this.addFehler(BKGymBelegungsfehler.LK_3);
		const ab3: BKGymAbiturFachbelegung | null = this.manager.getAbiFachbelegung(GostAbiturFach.AB3);
		if (ab3 === null)
			this.addFehler(BKGymBelegungsfehler.AB_3);
		const ab4: BKGymAbiturFachbelegung | null = this.manager.getAbiFachbelegung(GostAbiturFach.AB4);
		if (ab4 === null)
			this.addFehler(BKGymBelegungsfehler.AB_4);
		return mglStundentafeln;
	}

	/**
	 * Bestimmt die Fachbelegungszuordnung zu den einzelnen Stundentafel-Einträgen für die übergebene Stundentafel-Variante.
	 *
	 * @param tafel   die Stundentafel
	 *
	 * @return die Zuordnung der Fachbelegungen zu den Stundentafeleinträgen
	 */
	public getMapStundentafelFaecherByVariante(tafel: BeruflichesGymnasiumStundentafel): JavaMap<number, List<BKGymAbiturFachbelegung>> {
		const mapZuordnung: JavaMap<number, List<BKGymAbiturFachbelegung>> = new HashMap<number, List<BKGymAbiturFachbelegung>>();
		for (const fach of tafel.faecher)
			mapZuordnung.computeIfAbsent(fach.sortierung, { apply: (index: number | null) => new ArrayList() });
		for (const fachbelegung of this.manager.getAbidaten().fachbelegungen) {
			const fach: BeruflichesGymnasiumStundentafelFach | null = this.manager.getFachByBelegung(tafel, fachbelegung);
			if (fach === null)
				continue;
			const tafelBelegungen: List<BKGymAbiturFachbelegung> | null = mapZuordnung.get(fach.sortierung);
			if (tafelBelegungen === null)
				continue;
			tafelBelegungen.add(fachbelegung);
		}
		return mapZuordnung;
	}

	/**
	 * Diese Methode liefert für die Zuordnung der Fachbelegungen des Schülers zu den einzelnen Einträgen der jeweiligen
	 * Stundentafel für alle möglichen Stundentafeln
	 *
	 * @param mglStundentafeln    die möglichen Stundentafeln
	 *
	 * @return die Zuordnung
	 */
	public getZuordnungStundentafelFachbelegung(mglStundentafeln: List<BeruflichesGymnasiumStundentafel>): JavaMap<BeruflichesGymnasiumStundentafel, JavaMap<number, List<BKGymAbiturFachbelegung>>> {
		const mapStundentafelFachbelegungen: JavaMap<BeruflichesGymnasiumStundentafel, JavaMap<number, List<BKGymAbiturFachbelegung>>> = new HashMap<BeruflichesGymnasiumStundentafel, JavaMap<number, List<BKGymAbiturFachbelegung>>>();
		for (const tafel of mglStundentafeln) {
			const mapStundentafelFaecher: JavaMap<number, List<BKGymAbiturFachbelegung>> = this.getMapStundentafelFaecherByVariante(tafel);
			let valid: boolean = true;
			for (const fach of tafel.faecher) {
				const tafelFachbelegungen: List<BKGymAbiturFachbelegung> | null = mapStundentafelFaecher.get(fach.sortierung);
				if ((fach.stundenumfang[0] === 0) && (fach.stundenumfang[1] === 0) && (fach.stundenumfang[2] === 0) && (fach.stundenumfang[3] === 0) && (fach.stundenumfang[4] === 0) && (fach.stundenumfang[5] === 0))
					continue;
				if (JavaObject.equalsTranspiler("Wahlfach", (fach.fachbezeichnung)))
					continue;
				if ((tafelFachbelegungen === null) || tafelFachbelegungen.isEmpty()) {
					valid = false;
					break;
				}
			}
			if (!valid)
				continue;
			mapStundentafelFachbelegungen.put(tafel, mapStundentafelFaecher);
		}
		return mapStundentafelFachbelegungen;
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.abschluss.bk.d.BKGymBelegpruefung';
	}

	isTranspiledInstanceOf(name: string): boolean {
		return ['de.svws_nrw.core.abschluss.bk.d.BKGymBelegpruefung'].includes(name);
	}

	public static class = new Class<BKGymBelegpruefung>('de.svws_nrw.core.abschluss.bk.d.BKGymBelegpruefung');

}

export function cast_de_svws_nrw_core_abschluss_bk_d_BKGymBelegpruefung(obj: unknown): BKGymBelegpruefung {
	return obj as BKGymBelegpruefung;
}
