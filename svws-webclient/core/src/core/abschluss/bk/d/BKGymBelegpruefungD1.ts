import { JavaObject } from '../../../../java/lang/JavaObject';
import { BKGymAbiturdatenManager } from '../../../../core/abschluss/bk/d/BKGymAbiturdatenManager';
import { HashMap } from '../../../../java/util/HashMap';
import { BKGymAbiturFachbelegung } from '../../../../core/abschluss/bk/d/BKGymAbiturFachbelegung';
import { BKGymAbiturFachbelegungHalbjahr } from '../../../../core/abschluss/bk/d/BKGymAbiturFachbelegungHalbjahr';
import { BeruflichesGymnasiumStundentafel } from '../../../../asd/data/schule/BeruflichesGymnasiumStundentafel';
import { BeruflichesGymnasiumStundentafelAbiturfaecherWahlmoeglichkeit } from '../../../../asd/data/schule/BeruflichesGymnasiumStundentafelAbiturfaecherWahlmoeglichkeit';
import { BKGymBelegungsfehler } from '../../../../core/abschluss/bk/d/BKGymBelegungsfehler';
import { JavaInteger } from '../../../../java/lang/JavaInteger';
import { BKGymBelegpruefung, cast_de_svws_nrw_core_abschluss_bk_d_BKGymBelegpruefung } from '../../../../core/abschluss/bk/d/BKGymBelegpruefung';
import { BeruflichesGymnasiumPruefungsordnungAnlage } from '../../../../asd/types/schule/BeruflichesGymnasiumPruefungsordnungAnlage';
import { GostHalbjahr } from '../../../../core/types/gost/GostHalbjahr';
import type { List } from '../../../../java/util/List';
import { Class } from '../../../../java/lang/Class';
import type { JavaMap } from '../../../../java/util/JavaMap';
import { BeruflichesGymnasiumStundentafelFach } from '../../../../asd/data/schule/BeruflichesGymnasiumStundentafelFach';

export class BKGymBelegpruefungD1 extends BKGymBelegpruefung {


	/**
	 * Erzeugt einen neue Belegprüfung
	 *
	 * @param manager   der Manager für die Abiturdaten
	 */
	public constructor(manager: BKGymAbiturdatenManager) {
		super(manager);
	}

	public pruefe(): void {
		const mglStundentafeln: List<BeruflichesGymnasiumStundentafel> = this.getStundentafelnByAbiturfaechern(BeruflichesGymnasiumPruefungsordnungAnlage.D1);
		if (mglStundentafeln.isEmpty())
			return;
		const mapWahlmoeglichkeiten: JavaMap<BeruflichesGymnasiumStundentafel, BeruflichesGymnasiumStundentafelAbiturfaecherWahlmoeglichkeit> = this.manager.getWahlmoeglichekeiten(mglStundentafeln);
		if (mapWahlmoeglichkeiten.isEmpty()) {
			this.addFehler(BKGymBelegungsfehler.AB_5);
			return;
		}
		const tmpMapStundentafelFachbelegungen: JavaMap<BeruflichesGymnasiumStundentafel, JavaMap<number, List<BKGymAbiturFachbelegung>>> = this.getZuordnungStundentafelFachbelegung(mglStundentafeln);
		if (tmpMapStundentafelFachbelegungen.isEmpty()) {
			this.addFehler(BKGymBelegungsfehler.ST_1);
			return;
		}
		const mapStundentafelFachbelegungen: JavaMap<BeruflichesGymnasiumStundentafel, JavaMap<number, List<BKGymAbiturFachbelegung>>> = new HashMap<BeruflichesGymnasiumStundentafel, JavaMap<number, List<BKGymAbiturFachbelegung>>>();
		for (const entry of tmpMapStundentafelFachbelegungen.entrySet()) {
			const wahlmoeglichkeit: BeruflichesGymnasiumStundentafelAbiturfaecherWahlmoeglichkeit | null = mapWahlmoeglichkeiten.get(entry.getKey());
			if (wahlmoeglichkeit === null)
				continue;
			mapStundentafelFachbelegungen.put(entry.getKey(), entry.getValue());
		}
		if (mapStundentafelFachbelegungen.size() > 1) {
			this.addFehler(BKGymBelegungsfehler.ST_2);
			return;
		}
		const tafel: BeruflichesGymnasiumStundentafel | null = mapStundentafelFachbelegungen.keySet().iterator().next();
		const zuordnung: JavaMap<number, List<BKGymAbiturFachbelegung>> | null = mapStundentafelFachbelegungen.get(tafel);
		if ((tafel === null) || (zuordnung === null))
			return;
		const wahlmoeglichkeit: BeruflichesGymnasiumStundentafelAbiturfaecherWahlmoeglichkeit | null = mapWahlmoeglichkeiten.get(tafel);
		if (wahlmoeglichkeit === null)
			return;
		const mapFaecherByIndex: JavaMap<number, List<BeruflichesGymnasiumStundentafelFach>> = this.manager.getMapFaecherFromTafelByIndex(tafel);
		for (const index of zuordnung.keySet()) {
			const fachbelegungen: List<BKGymAbiturFachbelegung> | null = zuordnung.get(index);
			if ((fachbelegungen === null) || (fachbelegungen.isEmpty()))
				continue;
			let fachbelegung: BKGymAbiturFachbelegung | null = null;
			if (fachbelegungen.size() > 1) {
				let minFehlerZahl: number = JavaInteger.MAX_VALUE;
				let aktFehlerZahl: number = 0;
				for (const fb of fachbelegungen) {
					const fach: BeruflichesGymnasiumStundentafelFach | null = this.manager.getFachByBelegung(tafel, fb);
					if (fach === null)
						continue;
					const istAbiFach: boolean = (fb.abiturFach !== null);
					const istLK: boolean = istAbiFach && ((fb.abiturFach === 1) || (fb.abiturFach === 2));
					const istAbiSchriftlich: boolean = istAbiFach && (fb.abiturFach !== 4);
					const istMathe: boolean = (JavaObject.equalsTranspiler("Mathematik", (fach.fachbezeichnung)));
					const istDeutsch: boolean = (JavaObject.equalsTranspiler("Deutsch", (fach.fachbezeichnung)));
					const istFS: boolean = this.manager.istFremdsprachenbelegung(fb);
					const brauchtSchriftlichEF: boolean = istLK || istMathe || istDeutsch || istFS;
					const brauchtSchriftlichQ: boolean = brauchtSchriftlichEF || istAbiFach;
					for (const hj of GostHalbjahr.values()) {
						const belegung: BKGymAbiturFachbelegungHalbjahr | null = fb.belegungen[hj.id];
						if (belegung === null) {
							if (fach.stundenumfang[hj.id] > 0)
								aktFehlerZahl += 10;
							continue;
						}
						if (belegung.wochenstunden < fach.stundenumfang[hj.id])
							aktFehlerZahl++;
						if (!belegung.schriftlich) {
							if (brauchtSchriftlichEF && hj.istEinfuehrungsphase())
								aktFehlerZahl++;
							if (brauchtSchriftlichQ && hj.istQualifikationsphase() && (hj as unknown !== GostHalbjahr.Q22 as unknown))
								aktFehlerZahl++;
							if (istAbiSchriftlich && (hj as unknown === GostHalbjahr.Q22 as unknown))
								aktFehlerZahl++;
						}
					}
					if (aktFehlerZahl < minFehlerZahl) {
						minFehlerZahl = aktFehlerZahl;
						fachbelegung = fb;
					}
				}
				if (minFehlerZahl > 0)
					this.addFehler(BKGymBelegungsfehler.ST_3_INFO);
			} else
				fachbelegung = fachbelegungen.get(0);
			if (fachbelegung === null)
				return;
			const fach: BeruflichesGymnasiumStundentafelFach | null = this.manager.getFachByBelegung(tafel, fachbelegung);
			for (const hj of GostHalbjahr.values()) {
				const belegung: BKGymAbiturFachbelegungHalbjahr | null = fachbelegung.belegungen[hj.id];
			}
		}
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.abschluss.bk.d.BKGymBelegpruefungD1';
	}

	isTranspiledInstanceOf(name: string): boolean {
		return ['de.svws_nrw.core.abschluss.bk.d.BKGymBelegpruefungD1', 'de.svws_nrw.core.abschluss.bk.d.BKGymBelegpruefung'].includes(name);
	}

	public static class = new Class<BKGymBelegpruefungD1>('de.svws_nrw.core.abschluss.bk.d.BKGymBelegpruefungD1');

}

export function cast_de_svws_nrw_core_abschluss_bk_d_BKGymBelegpruefungD1(obj: unknown): BKGymBelegpruefungD1 {
	return obj as BKGymBelegpruefungD1;
}
