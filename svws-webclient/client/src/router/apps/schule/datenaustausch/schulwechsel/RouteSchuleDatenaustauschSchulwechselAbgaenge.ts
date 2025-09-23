import { BenutzerKompetenz, Schulform, ServerMode } from "@core";
import { RouteNode } from "~/router/RouteNode";
import { routeApp } from "~/router/apps/RouteApp";
import { api } from "~/router/Api";
import type { RouteLocationNormalized } from "vue-router";
import type { RouteSchuleDatenaustauschSchulwechsel } from "~/router/apps/schule/datenaustausch/schulwechsel/RouteSchuleDatenaustauschSchulwechsel";
import type { SSchuleDatenaustauschSchulwechselAbgaengeProps } from "~/components/schule/datenaustausch/schulwechsel/SSchuleDatenaustauschSchulwechselAbgaengeProps";

const SSchuleDatenaustauschSchulwechselAbgaenge = () => import("~/components/schule/datenaustausch/schulwechsel/SSchuleDatenaustauschSchulwechselAbgaenge.vue");

export class RouteSchuleDatenaustauschSchulwechselAbgaenge extends RouteNode<any, RouteSchuleDatenaustauschSchulwechsel> {

	public constructor() {
		super(Schulform.values(), [BenutzerKompetenz.IMPORT_EXPORT_SCHULBEWERBUNG_DE], "schule.datenaustausch.schulwechsel.abgaenge", "abgaenge", SSchuleDatenaustauschSchulwechselAbgaenge);
		super.mode = ServerMode.DEV;
		super.propHandler = (route) => this.getProps(route);
		super.text = "Abgänge";
	}

	public getProps(to: RouteLocationNormalized): SSchuleDatenaustauschSchulwechselAbgaengeProps {
		return {
			serverMode: api.mode,
			schulform: api.schulform,
			schuljahresabschnitt: () => routeApp.data.aktAbschnitt.value,
		};
	}
}

export const routeSchuleDatenaustauschSchulwechselAbgaenge = new RouteSchuleDatenaustauschSchulwechselAbgaenge();
