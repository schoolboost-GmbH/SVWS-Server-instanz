import type { RouteParams } from "vue-router";
import { BenutzerKompetenz, Schulform, ServerMode } from "@core";
import type { KonfessionenListeManager } from "@ui";
import type { RouteNode } from "~/router/RouteNode";
import type { RouteApp } from "~/router/apps/RouteApp";
import { routeKonfessionenDaten } from "~/router/apps/schule/allgemein/konfessionen/RouteKonfessionenDaten";
import { RouteDataKonfessionen } from "./RouteDataKonfessionen";
import { RouteSchuleMenuGroup } from "../../RouteSchuleMenuGroup";
import { RouteAuswahlNode } from "~/router/RouteAuswahlNode";
import { routeKonfessionenGruppenprozesse } from "~/router/apps/schule/allgemein/konfessionen/RouteKonfessionenGruppenprozesse";
import { routeKonfessionenNeu } from "~/router/apps/schule/allgemein/konfessionen/RouteKonfessionenNeu";
import { routeApp } from "~/router/apps/RouteApp";
import type { KonfessionenAuswahlProps } from "~/components/schule/allgemein/konfessionen/SKonfessionenAuswahlPops";

const SKonfessionenAuswahl = () => import("~/components/schule/allgemein/konfessionen/SKonfessionenAuswahl.vue");
const SKonfessionenApp = () => import("~/components/schule/allgemein/konfessionen/SKonfessionenApp.vue");

export class RouteKonfessionen extends RouteAuswahlNode<KonfessionenListeManager, RouteDataKonfessionen, RouteApp> {

	public constructor() {
		super(Schulform.values(), [BenutzerKompetenz.KATALOG_EINTRAEGE_ANSEHEN, BenutzerKompetenz.KATALOG_EINTRAEGE_AENDERN], "schule.konfessionen",
			"schule/konfessionen/:id(\\d+)?", SKonfessionenApp, SKonfessionenAuswahl, new RouteDataKonfessionen());
		super.mode = ServerMode.STABLE;
		super.text = "Konfessionen";
		super.menugroup = RouteSchuleMenuGroup.ALLGEMEIN;
		super.children = [
			routeKonfessionenDaten,
			routeKonfessionenGruppenprozesse,
			routeKonfessionenNeu,
		];
		super.defaultChild = routeKonfessionenDaten;
		super.updateIfTarget = this.doUpdateIfTarget;
		super.getAuswahlListProps = (props) => (<KonfessionenAuswahlProps> {
			...props,
			schuljahresabschnittsauswahl: () => routeApp.data.getSchuljahresabschnittsauswahl(false),
		});
	}

	protected doUpdateIfTarget = async (to: RouteNode<any, any>, to_params: RouteParams, from: RouteNode<any, any> | undefined) => {
		if (!this.data.manager.hasDaten())
			return;
		return this.getRouteSelectedChild();
	};
}

export const routeKonfessionen = new RouteKonfessionen();
