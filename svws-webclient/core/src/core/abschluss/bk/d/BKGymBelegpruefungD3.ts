import { BeruflichesGymnasiumStundentafelAbiturfaecherWahlmoeglichkeit } from '../../../../asd/data/schule/BeruflichesGymnasiumStundentafelAbiturfaecherWahlmoeglichkeit';
import { BKGymBelegungsfehler } from '../../../../core/abschluss/bk/d/BKGymBelegungsfehler';
import { BKGymAbiturdatenManager } from '../../../../core/abschluss/bk/d/BKGymAbiturdatenManager';
import { BKGymBelegpruefung, cast_de_svws_nrw_core_abschluss_bk_d_BKGymBelegpruefung } from '../../../../core/abschluss/bk/d/BKGymBelegpruefung';
import { BeruflichesGymnasiumPruefungsordnungAnlage } from '../../../../asd/types/schule/BeruflichesGymnasiumPruefungsordnungAnlage';
import type { List } from '../../../../java/util/List';
import { BeruflichesGymnasiumStundentafel } from '../../../../asd/data/schule/BeruflichesGymnasiumStundentafel';
import { Class } from '../../../../java/lang/Class';
import type { JavaMap } from '../../../../java/util/JavaMap';

export class BKGymBelegpruefungD3 extends BKGymBelegpruefung {


	/**
	 * Erzeugt einen neue Belegprüfung
	 *
	 * @param manager   der Manager für die Abiturdaten
	 */
	public constructor(manager: BKGymAbiturdatenManager) {
		super(manager);
	}

	public pruefe(): void {
		const mglStundentafeln: List<BeruflichesGymnasiumStundentafel> = this.getStundentafelnByAbiturfaechern(BeruflichesGymnasiumPruefungsordnungAnlage.D3);
		if (mglStundentafeln.isEmpty())
			return;
		const mapWahlmoeglichkeiten: JavaMap<BeruflichesGymnasiumStundentafel, BeruflichesGymnasiumStundentafelAbiturfaecherWahlmoeglichkeit> = this.manager.getWahlmoeglichekeiten(mglStundentafeln);
		if (mapWahlmoeglichkeiten.isEmpty()) {
			this.addFehler(BKGymBelegungsfehler.AB_5);
			return;
		}
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.abschluss.bk.d.BKGymBelegpruefungD3';
	}

	isTranspiledInstanceOf(name: string): boolean {
		return ['de.svws_nrw.core.abschluss.bk.d.BKGymBelegpruefungD3', 'de.svws_nrw.core.abschluss.bk.d.BKGymBelegpruefung'].includes(name);
	}

	public static class = new Class<BKGymBelegpruefungD3>('de.svws_nrw.core.abschluss.bk.d.BKGymBelegpruefungD3');

}

export function cast_de_svws_nrw_core_abschluss_bk_d_BKGymBelegpruefungD3(obj: unknown): BKGymBelegpruefungD3 {
	return obj as BKGymBelegpruefungD3;
}
