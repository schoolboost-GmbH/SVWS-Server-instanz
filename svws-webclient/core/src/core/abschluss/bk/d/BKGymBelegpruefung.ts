import { JavaObject } from '../../../../java/lang/JavaObject';
import { BKGymAbiturdatenManager } from '../../../../core/abschluss/bk/d/BKGymAbiturdatenManager';
import { GostAbiturFach } from '../../../../core/types/gost/GostAbiturFach';
import { HashMap } from '../../../../java/util/HashMap';
import { ArrayList } from '../../../../java/util/ArrayList';
import { BKGymAbiturFachbelegung } from '../../../../core/abschluss/bk/d/BKGymAbiturFachbelegung';
import { BKGymAbiturFachbelegungHalbjahr } from '../../../../core/abschluss/bk/d/BKGymAbiturFachbelegungHalbjahr';
import { BeruflichesGymnasiumStundentafel } from '../../../../asd/data/schule/BeruflichesGymnasiumStundentafel';
import { DeveloperNotificationException } from '../../../../core/exceptions/DeveloperNotificationException';
import { JavaString } from '../../../../java/lang/JavaString';
import { BKGymBelegungsfehlerTyp } from '../../../../core/abschluss/bk/d/BKGymBelegungsfehlerTyp';
import { BKGymBelegungsfehler } from '../../../../core/abschluss/bk/d/BKGymBelegungsfehler';
import { JavaInteger } from '../../../../java/lang/JavaInteger';
import type { JavaIterator } from '../../../../java/util/JavaIterator';
import { GostHalbjahr } from '../../../../core/types/gost/GostHalbjahr';
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
	 * Die Stundentafeln laut APO-BK der Anlage
	 */
	readonly stundentafeln: List<BeruflichesGymnasiumStundentafel>;

	/**
	 * Die Belegungsfehler, die für jede Stundentafel bei der Prüfung festgehalten werden.
	 */
	private readonly mapBelegungsfehler: HashMap<BeruflichesGymnasiumStundentafel, List<BKGymBelegungsfehler>> = new HashMap<BeruflichesGymnasiumStundentafel, List<BKGymBelegungsfehler>>();

	/**
	 * Die Liste von Belegungsfehlern der am besten passenden Stundentafel
	 */
	private besteFehlerliste: List<BKGymBelegungsfehler> = new ArrayList<BKGymBelegungsfehler>();

	/**
	 * Flag ob neue Fehler hinzugekommen sind
	 */
	private dirty: boolean = false;

	/**
	 * Flag ob Fehler ausgegeben werden sollen
	 */
	private fehlerAusgeben: boolean = true;


	/**
	 * Erzeugt eine neue Belegprüfung mit dem angegebenen Manager.
	 *
	 * @param manager   der Manager für die Abiturdaten
	 */
	protected constructor(manager: BKGymAbiturdatenManager) {
		super();
		this.manager = manager;
		this.stundentafeln = manager.getStundentafeln();
	}

	/**
	 * Fügt einen Belegungsfehler zu der Belegprüfung hinzu. Diese Methode wird von den Sub-Klassen
	 * aufgerufen, wenn dort ein Belegungsfehler erkannt wird.
	 *
	 * @param tafel    die Stundentafel
	 * @param fehler   der hinzuzufügende Belegungsfehler
	 *
	 * @return true, falls ein Fehler vorliegt false, wenn nur ein Hinweis ausgegeben wurde.
	 */
	protected addFehler(tafel: BeruflichesGymnasiumStundentafel, fehler: BKGymBelegungsfehler): boolean {
		if (this.fehlerAusgeben) {
			const fehlerliste: List<BKGymBelegungsfehler> | null = this.mapBelegungsfehler.get(tafel);
			if (fehlerliste !== null && !fehlerliste.contains(fehler)) {
				fehlerliste.add(fehler);
				this.dirty = true;
			}
		}
		return fehler.istFehler();
	}

	/**
	 * Ermittelt die Stundentafel mit den wenigsten Fehlern und gibt die zugehörigen Belegungsfehler aus
	 */
	private ermittleBesteTafel(): void {
		if (this.dirty) {
			let minFehlerZahl: number = JavaInteger.MAX_VALUE;
			for (const fehlerliste of this.mapBelegungsfehler.values()) {
				let fehlerZahl: number = 0;
				for (const fehler of fehlerliste)
					fehlerZahl += fehler.wert;
				if (fehlerZahl < minFehlerZahl) {
					minFehlerZahl = fehlerZahl;
					this.besteFehlerliste = fehlerliste;
				}
			}
		}
		this.dirty = false;
	}

	/**
	 * Ermittelt die Stundentafel mit den wenigsten Fehlern und gibt die zugehörigen Belegungsfehler aus
	 *
	 * @return die Belegungsfehler der Stundentafel
	 */
	public getBelegungsfehler(): List<BKGymBelegungsfehler> {
		this.ermittleBesteTafel();
		return this.besteFehlerliste;
	}

	/**
	 * Gibt zurück, ob mindestens eine Stundentafel existiert, die keine "echten" Belegungsfehler hat. Warnungen und Hinweise werden toleriert.
	 *
	 * @return true, wenn kein "echter" Belegungsfehler vorliegt, und ansonsten false.
	 */
	public istErfolgreich(): boolean {
		for (const fehler of this.getBelegungsfehler())
			if (!fehler.istInfo())
				return false;
		return true;
	}

	/**
	 * Die Methode wird zur Durchführung der Belegprüfung aufgerufen.
	 *
	 * Sie führt zuerst die allgemeinen Prüfungen aus, die für alle Anlagen des beruflichen Gymnasiums identisch sind.
	 * Im Anschluss werden dann ggf. die spezifischen Prüfungen für die jeweilige Anlage durchgeführt.
	 * Hierzu ist bei Bedarf die Methode spezifischePruefungen in der Kindklasse zu implementieren.
	 */
	public pruefe(): void {
		for (const tafel of this.stundentafeln) {
			this.mapBelegungsfehler.put(tafel, new ArrayList<BKGymBelegungsfehler>());
			const mapFaecherByIndex: JavaMap<number, List<BeruflichesGymnasiumStundentafelFach>> = this.manager.getMapFaecherFromTafelByIndex(tafel);
			const mapBelegungByFach: JavaMap<BeruflichesGymnasiumStundentafelFach, List<BKGymAbiturFachbelegung>> = this.manager.getMapBelegungenForTafelByFach(tafel);
			this.fehlerAusgeben = false;
			this.bewegeBelegungZuWahlfach(tafel, mapFaecherByIndex, mapBelegungByFach);
			this.fehlerAusgeben = true;
			this.allgemeinePruefungen(tafel, mapFaecherByIndex, mapBelegungByFach);
			this.spezifischePruefungen(tafel, mapFaecherByIndex, mapBelegungByFach);
		}
	}

	/**
	 * Diese Methode identifiziert für Positionen einer Stundentafel, die Wahlmöglichkeiten vorsieht,
	 * ob mehrere Belegungen vorliegen, von denen nur eine der Position zugewiesen wird und die anderen
	 * dem Wahlfach zugeschlagen wird.
	 * Es sollte nur ein Fach pro Position vorhanden sein, dass in der Gruppe Berufsbezogen bzw. Berufsübergreifend ist.
	 * Die anderen Belegungen müssten Differenzierung zugewiesen sein.
	 * Dafür wird ermittelt welche Belegung den Vorgaben der Position entspricht. Die erste wird behalten, die
	 * anderen werden nach Wahlfach verschoben.
	 *
	 * @param tafel               die zu überprüfende Stundentafel
	 * @param mapFaecherByIndex   die Map mit den Fächern der Stundentafel
	 * @param mapBelegungByFach   die Map mit den Fächern der Belegungen
	 */
	private bewegeBelegungZuWahlfach(tafel: BeruflichesGymnasiumStundentafel, mapFaecherByIndex: JavaMap<number, List<BeruflichesGymnasiumStundentafelFach>>, mapBelegungByFach: JavaMap<BeruflichesGymnasiumStundentafelFach, List<BKGymAbiturFachbelegung>>): void {
		const wahlBelegungenNeu: List<BKGymAbiturFachbelegung> = new ArrayList<BKGymAbiturFachbelegung>();
		let wahlBelegungen: List<BKGymAbiturFachbelegung> = new ArrayList<BKGymAbiturFachbelegung>();
		for (const index of mapFaecherByIndex.keySet()) {
			const faecher: List<BeruflichesGymnasiumStundentafelFach> | null = mapFaecherByIndex.get(index);
			if (faecher === null)
				continue;
			if (faecher.size() === 1) {
				const tf: BeruflichesGymnasiumStundentafelFach | null = faecher.getFirst();
				if ((tf !== null) && this.manager.istWahlfach(tf.fachbezeichnung)) {
					const belegungen: List<BKGymAbiturFachbelegung> | null = mapBelegungByFach.get(tf);
					if (belegungen !== null)
						wahlBelegungen = belegungen;
				}
				continue;
			}
			const iter: JavaIterator<BeruflichesGymnasiumStundentafelFach> | null = faecher.iterator();
			while (iter.hasNext()) {
				const tafelFach: BeruflichesGymnasiumStundentafelFach | null = iter.next();
				const belegungen: List<BKGymAbiturFachbelegung> | null = mapBelegungByFach.get(tafelFach);
				if ((belegungen === null) || (belegungen.size() === 0))
					iter.remove();
			}
			if (faecher.size() < 2)
				continue;
			let matched: boolean = false;
			for (let i: number = 0; i < faecher.size(); i++) {
				let fehlerfrei: boolean = true;
				const tafelFach: BeruflichesGymnasiumStundentafelFach | null = faecher.get(i);
				if (tafelFach !== null) {
					const belegungen: List<BKGymAbiturFachbelegung> | null = mapBelegungByFach.get(tafelFach);
					if (this.manager.istZweiteFremdsprache(tafelFach.fachbezeichnung) || this.manager.istNeueFremdsprache(tafelFach.fachbezeichnung)) {
						this.pruefeBelegungZweiteFremdsprache(tafel, tafelFach, belegungen, wahlBelegungenNeu);
					} else
						if ((belegungen !== null) && !belegungen.isEmpty()) {
							if (belegungen.size() === 1) {
								const belegung: BKGymAbiturFachbelegung | null = belegungen.getFirst();
								if (belegung !== null) {
									if (!matched)
										fehlerfrei = this.pruefeBelegung(tafel, tafelFach, belegung);
									if (matched || (!fehlerfrei && ((i + 1) < faecher.size())))
										wahlBelegungenNeu.add(belegung);
									if (fehlerfrei)
										matched = true;
								} else
									throw new DeveloperNotificationException("In der Belegungsprüfung ist ein interner Fehler aufgetreten: Die Belegung für ein Fach ist nicht vorhanden.")
							}
						}
				}
			}
		}
		wahlBelegungen.addAll(wahlBelegungenNeu);
	}

	/**
	 * allgemeinePruefungen führt die Belegprüfungen durch, die für alle Anlagen des beruflichen Gymnasiums identisch ausgeführt werden können.
	 * Aufgetretene Fehler werden in die Map mapBelegungsfehler eingetragen.
	 *
	 * @param tafel                    die zu überprüfende Stundentafel
	 * @param mapFaecherByIndex        die Map mit den Fächern der Stundentafel
	 * @param mapBelegungByTafelfach   die Map mit den Fächern der Belegungen
	 */
	private allgemeinePruefungen(tafel: BeruflichesGymnasiumStundentafel, mapFaecherByIndex: JavaMap<number, List<BeruflichesGymnasiumStundentafelFach>>, mapBelegungByTafelfach: JavaMap<BeruflichesGymnasiumStundentafelFach, List<BKGymAbiturFachbelegung>>): void {
		this.pruefeLKs(tafel);
		this.pruefeAbiGrundkurse(tafel);
		for (const index of mapFaecherByIndex.keySet()) {
			const faecher: List<BeruflichesGymnasiumStundentafelFach> | null = mapFaecherByIndex.get(index);
			if (faecher === null || faecher.isEmpty())
				continue;
			let matched: boolean = false;
			for (let i: number = 0; i < faecher.size(); i++) {
				let fehlerfrei: boolean = true;
				const tafelFach: BeruflichesGymnasiumStundentafelFach | null = faecher.get(i);
				if (tafelFach !== null) {
					const belegungen: List<BKGymAbiturFachbelegung> | null = mapBelegungByTafelfach.get(tafelFach);
					if (this.manager.istZweiteFremdsprache(tafelFach.fachbezeichnung) || this.manager.istNeueFremdsprache(tafelFach.fachbezeichnung)) {
						this.pruefeBelegungZweiteFremdsprache(tafel, tafelFach, belegungen, null);
					} else
						if (this.manager.istWahlfach(tafelFach.fachbezeichnung)) {
							this.pruefeBelegungWahlfach(tafel, tafelFach, belegungen);
						} else
							if ((belegungen === null) || belegungen.isEmpty()) {
								if (!matched && (i + 1 >= faecher.size()))
									this.addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.ST_4));
							} else {
								if (belegungen.size() > 1)
									this.addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.ST_1, tafelFach.fachbezeichnung));
								else {
									const belegung: BKGymAbiturFachbelegung | null = belegungen.getFirst();
									if (belegung !== null) {
										if (!matched)
											fehlerfrei = this.pruefeBelegung(tafel, tafelFach, belegung);
										if (fehlerfrei)
											matched = true;
									} else
										throw new DeveloperNotificationException("In der Belegungsprüfung ist ein interner Fehler aufgetreten: Die Belegung für ein Fach ist nicht vorhanden.")
								}
							}
				}
			}
		}
	}

	/**
	 * Führt die Prüfungen für eine Belegung durch
	 *
	 * @param tafel          die Stundentafel der Anlage
	 * @param fachTafel      das zu prüfende Fach aus der Stundentafel
	 * @param fachBelegung   die zugeordnete Fachbelegung
	 *
	 * @return true, wenn die Prüfung keinen Fehler entdeckt, sonst false
	 */
	private pruefeBelegung(tafel: BeruflichesGymnasiumStundentafel, fachTafel: BeruflichesGymnasiumStundentafelFach, fachBelegung: BKGymAbiturFachbelegung): boolean {
		const successStundenumfang: boolean = this.pruefeStundenumfang(tafel, fachTafel, fachBelegung);
		const successSchriftlichkeit: boolean = this.pruefeSchriftlich(tafel, fachTafel, fachBelegung);
		const successAnzahl: boolean = this.pruefeAnzahlKurse(tafel, fachTafel, fachBelegung);
		return successStundenumfang && successSchriftlichkeit && successAnzahl;
	}

	/**
	 * Führt die Belegungsprüfung für die zweite Fremdsprache durch.
	 * Hier kann es vorkommen, dass eine dritte Fremdsprache belegt wird, die dann dem Wahlfach zugeschlagen wird.
	 *
	 * @param tafel            die Stundentafel der Anlage
	 * @param fachTafel        das zu prüfende Fach aus der Stundentafel
	 * @param belegungen       die zugeordnete Fachbelegung
	 * @param wahlBelegungen   die wahlBelegungen, die ggfs. durch die dritte vorliegende Fremdsprache ergänzt wird.
	 *
	 * @return true, wenn die Prüfung keinen Fehler entdeckt, sonst false
	 */
	private pruefeBelegungZweiteFremdsprache(tafel: BeruflichesGymnasiumStundentafel, fachTafel: BeruflichesGymnasiumStundentafelFach, belegungen: List<BKGymAbiturFachbelegung> | null, wahlBelegungen: List<BKGymAbiturFachbelegung> | null): boolean {
		if (belegungen === null || belegungen.isEmpty()) {
			this.addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.ST_4));
			return false;
		}
		let matched: boolean = false;
		for (let i: number = 0; i < belegungen.size(); i++) {
			let fehlerfrei: boolean = true;
			const fachBelegung: BKGymAbiturFachbelegung | null = belegungen.get(i);
			if (fachBelegung !== null) {
				fehlerfrei = this.pruefeBelegung(tafel, fachTafel, fachBelegung);
				if ((wahlBelegungen !== null) && (matched || (!fehlerfrei && ((i + 1) < belegungen.size()))))
					wahlBelegungen.add(fachBelegung);
				if (fehlerfrei)
					matched = true;
			}
		}
		return matched;
	}

	/**
	 * Führt die Belegungsprüfung für das Wahlfach aus.
	 * Hier muss nur die Summe der Wochenstunden pro Halbjahr erfüllt sein.
	 *
	 * @param tafel            die Stundentafel der Anlage
	 * @param fachTafel        das zu prüfende Fach aus der Stundentafel
	 * @param belegungen   die zugeordnete Fachbelegung
	 *
	 * @return true, wenn die Prüfung keinen Fehler entdeckt, sonst false
	 */
	private pruefeBelegungWahlfach(tafel: BeruflichesGymnasiumStundentafel, fachTafel: BeruflichesGymnasiumStundentafelFach, belegungen: List<BKGymAbiturFachbelegung> | null): boolean {
		let success: boolean = true;
		const summeWS: Array<number> = Array(GostHalbjahr.maxHalbjahre).fill(0);
		if (belegungen === null)
			return this.addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.ST_4, fachTafel.fachbezeichnung));
		for (const fachBelegung of belegungen)
			for (const hj of GostHalbjahr.values()) {
				const belegung: BKGymAbiturFachbelegungHalbjahr | null = fachBelegung.belegungen[hj.id];
				if (belegung !== null)
					summeWS[hj.id] += belegung.wochenstunden;
			}
		for (const hj of GostHalbjahr.values())
			if ((summeWS[hj.id] < fachTafel.stundenumfang[hj.id]) && this.addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.ST_3)))
				success = false;
		return success;
	}

	/**
	 * Prüft, ob Unterricht im erforderlichen Umfang erteilt wurde.
	 *
	 * @param tafel          die Stundentafel der Anlage
	 * @param fachTafel      das zu prüfende Fach aus der Stundentafel
	 * @param fachBelegung   die zugeordnete Fachbelegung
	 *
	 * @return true bei fehlerloser Prüfung sonst false
	 */
	private pruefeStundenumfang(tafel: BeruflichesGymnasiumStundentafel, fachTafel: BeruflichesGymnasiumStundentafelFach, fachBelegung: BKGymAbiturFachbelegung): boolean {
		let success: boolean = true;
		let summeTafel: number = 0;
		let summeBelegung: number = 0;
		let unterbelegung: boolean = false;
		for (const hj of GostHalbjahr.values()) {
			const belegung: BKGymAbiturFachbelegungHalbjahr | null = fachBelegung.belegungen[hj.id];
			if (belegung !== null) {
				summeTafel += fachTafel.stundenumfang[hj.id];
				if ((belegung.notenkuerzel !== null) && (!JavaString.isEmpty(belegung.notenkuerzel))) {
					summeBelegung += belegung.wochenstunden;
					if (fachTafel.stundenumfang[hj.id] > belegung.wochenstunden)
						unterbelegung = true;
				} else
					if (fachTafel.stundenumfang[hj.id] > 0) {
						if (this.addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.ST_2, fachTafel.fachbezeichnung, hj.kuerzel)))
							success = false;
						unterbelegung = true;
					}
			} else
				if ((fachTafel.stundenumfang[hj.id] > 0) && this.addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.ST_4, fachTafel.fachbezeichnung)))
					success = false;
		}
		if (summeTafel > summeBelegung) {
			if (this.addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.ST_3, fachTafel.fachbezeichnung)))
				success = false;
		} else
			if (unterbelegung && this.addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.ST_5_INFO, fachTafel.fachbezeichnung)))
				success = false;
		return success;
	}

	/**
	 * Prüft, ob die Schriftlichkeit der Fächer korrekt erfüllt ist.
	 *
	 * @param tafel          die Stundentafel der Anlage
	 * @param fachTafel      das zu prüfende Fach aus der Stundentafel
	 * @param fachBelegung   die zugeordnete Fachbelegung
	 *
	 * @return true, wenn die Prüfung keinen Fehler entdeckt, sonst false
	 */
	private pruefeSchriftlich(tafel: BeruflichesGymnasiumStundentafel, fachTafel: BeruflichesGymnasiumStundentafelFach, fachBelegung: BKGymAbiturFachbelegung): boolean {
		let success: boolean = true;
		const istAbiFach: boolean = (fachBelegung.abiturFach !== null);
		const istLK: boolean = istAbiFach && ((fachBelegung.abiturFach === 1) || (fachBelegung.abiturFach === 2));
		const istAbiSchriftlich: boolean = istAbiFach && (fachBelegung.abiturFach !== 4);
		const istMathe: boolean = (JavaObject.equalsTranspiler("Mathematik", (fachTafel.fachbezeichnung)));
		const istDeutsch: boolean = (JavaObject.equalsTranspiler("Deutsch", (fachTafel.fachbezeichnung)));
		const istFS: boolean = this.manager.istFremdsprachenbelegung(fachBelegung);
		const brauchtSchriftlichEF: boolean = istLK || istMathe || istDeutsch || istFS;
		const brauchtSchriftlichQ: boolean = brauchtSchriftlichEF || istAbiFach;
		for (const hj of GostHalbjahr.values()) {
			const belegungHj: BKGymAbiturFachbelegungHalbjahr | null = fachBelegung.belegungen[hj.id];
			if ((belegungHj !== null) && (!belegungHj.schriftlich)) {
				if (brauchtSchriftlichEF && hj.istEinfuehrungsphase() && this.addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.KL_1, fachTafel.fachbezeichnung, hj.kuerzel)))
					success = false;
				if (brauchtSchriftlichQ && hj.istQualifikationsphase() && (hj as unknown !== GostHalbjahr.Q22 as unknown) && this.addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.KL_1, fachTafel.fachbezeichnung, hj.kuerzel)))
					success = false;
				if (istAbiSchriftlich && (hj as unknown === GostHalbjahr.Q22 as unknown) && this.addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.KL_1, fachTafel.fachbezeichnung, hj.kuerzel)))
					success = false;
			}
		}
		return success;
	}

	/**
	 * Prüft, ob die Mindestanzahl einzubringender Kurse vorhanden ist.
	 *
	 * @param tafel          die Stundentafel der Anlage
	 * @param fachTafel      das zu prüfende Fach aus der Stundentafel
	 * @param fachBelegung   die zugeordnete Fachbelegung
	 *
	 * @return true, wenn die Prüfung keinen Fehler entdeckt, sonst false
	 */
	private pruefeAnzahlKurse(tafel: BeruflichesGymnasiumStundentafel, fachTafel: BeruflichesGymnasiumStundentafelFach | null, fachBelegung: BKGymAbiturFachbelegung | null): boolean {
		const success: boolean = true;
		return success;
	}

	/**
	 * Diese Methode prüft die LK-Belegung in der Stundentafel.
	 *
	 * @param tafel   die zu prüfende Stundentafel
	 */
	private pruefeLKs(tafel: BeruflichesGymnasiumStundentafel): void {
		const lk1: BKGymAbiturFachbelegung | null = this.manager.getAbiFachbelegung(GostAbiturFach.LK1);
		if (lk1 === null)
			this.addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.LK_1));
		const lk2: BKGymAbiturFachbelegung | null = this.manager.getAbiFachbelegung(GostAbiturFach.LK2);
		if (lk2 === null)
			this.addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.LK_2));
		if (lk1 !== null && lk2 !== null && !(this.manager.isValidKursartFachbelegung(tafel, lk1, GostAbiturFach.LK1) && this.manager.isValidKursartFachbelegung(tafel, lk2, GostAbiturFach.LK2)))
			this.addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.LK_3, this.manager.getFachkuerzelFromFachbelegung(lk1), this.manager.getFachkuerzelFromFachbelegung(lk2), this.manager.getGliederung().name(), this.manager.getFachklassenschluessel()));
	}

	/**
	 * pruefe die Belegung des 3. und 4. Abiturfachs
	 *
	 * @param tafel
	 */
	private pruefeAbiGrundkurse(tafel: BeruflichesGymnasiumStundentafel): void {
		const ab3: BKGymAbiturFachbelegung | null = this.manager.getAbiFachbelegung(GostAbiturFach.AB3);
		if (ab3 === null)
			this.addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.AB_3));
		const ab4: BKGymAbiturFachbelegung | null = this.manager.getAbiFachbelegung(GostAbiturFach.AB4);
		if (ab4 === null)
			this.addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.AB_4));
		if ((ab3 !== null) && (ab4 !== null) && !this.manager.pruefeAbiGrundkurswahl(tafel, ab3, ab4))
			this.addFehler(tafel, new BKGymBelegungsfehler(BKGymBelegungsfehlerTyp.AB_5, this.manager.getFachkuerzelFromFachbelegung(ab3), this.manager.getFachkuerzelFromFachbelegung(ab4), this.manager.getGliederung().name(), this.manager.getFachklassenschluessel()));
	}

	/**
	 * Führt die für eine Anlage spezifische Belegprüfung durch.
	 * Diese Methode ist von jeder Kindklasse, welche spezifische Prüfungen hat, zu überschreiben.
	 *
	 * @param tafel                die zu überprüfende Stundentafel
	 * @param mapFaecherByIndex    die Map mit den Fächern der Stundentafel
	 * @param mapBelegungByIndex   die Map mit den Fächern der Belegungen
	 */
	protected spezifischePruefungen(tafel: BeruflichesGymnasiumStundentafel, mapFaecherByIndex: JavaMap<number, List<BeruflichesGymnasiumStundentafelFach>>, mapBelegungByIndex: JavaMap<BeruflichesGymnasiumStundentafelFach, List<BKGymAbiturFachbelegung>>): void {
		// empty block
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
