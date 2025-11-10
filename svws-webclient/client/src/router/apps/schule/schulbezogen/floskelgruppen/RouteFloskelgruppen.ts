import type { RouteParams } from "vue-router";
import type { RouteApp } from "~/router/apps/RouteApp";
import type { RouteNode } from "~/router/RouteNode";
import type { FloskelgruppenListeManager } from "../../../../../../../ui/src/ui/managers/schule/schulbezogen/FloskelgruppenListeManager";
import { BenutzerKompetenz, Schulform, ServerMode } from "@core";
import { RouteAuswahlNode } from "~/router/RouteAuswahlNode";
import { RouteSchuleMenuGroup } from "~/router/apps/schule/RouteSchuleMenuGroup";
import { RouteDataFloskelgruppen } from "~/router/apps/schule/schulbezogen/floskelgruppen/RouteDataFloskelgruppen";
import { routeFloskelgruppenDaten } from "~/router/apps/schule/schulbezogen/floskelgruppen/RouteFloskelgruppenDaten";
import { routeFloskelgruppenNeu } from "~/router/apps/schule/schulbezogen/floskelgruppen/RouteFloskelgruppenNeu";
import { routeFloskelgruppenGruppenprozesse } from "~/router/apps/schule/schulbezogen/floskelgruppen/RouteFloskelgruppenGruppenprozesse";

const FloskelgruppenApp = () => import("~/components/schule/schulbezogen/floskelgruppen/FloskelgruppenApp.vue");
const FloskelgruppenAuswahl = () => import("~/components/schule/schulbezogen/floskelgruppen/FloskelgruppenAuswahl.vue");

export class RouteFloskelgruppen extends RouteAuswahlNode<FloskelgruppenListeManager, RouteDataFloskelgruppen, RouteApp> {

	public constructor() {
		super(Schulform.values(), [BenutzerKompetenz.KATALOG_EINTRAEGE_ANSEHEN, BenutzerKompetenz.KATALOG_EINTRAEGE_AENDERN], "schule.floskelgruppen",
			`schule/floskelgruppen/:id(\\d+)?`, FloskelgruppenApp, FloskelgruppenAuswahl, new RouteDataFloskelgruppen());
		super.mode = ServerMode.DEV;
		super.text = "Floskelgruppen";
		super.menugroup = RouteSchuleMenuGroup.SCHULBEZOGEN;
		super.children = [
			routeFloskelgruppenDaten,
			routeFloskelgruppenNeu,
			routeFloskelgruppenGruppenprozesse,
		];
		super.defaultChild = routeFloskelgruppenDaten;
		super.updateIfTarget = this.doUpdateIfTarget;
	}

	protected doUpdateIfTarget = async (to: RouteNode<any, any>, to_params: RouteParams, from: RouteNode<any, any> | undefined) => {
		if (!this.data.manager.hasDaten())
			return;
		return this.getRouteSelectedChild();
	};
}

export const routeFloskelgruppen = new RouteFloskelgruppen();
