import { RouteData, type RouteStateInterface } from "~/router/RouteData";
import { routeSchuleDatenaustauschSchulwechselAbgaenge } from "~/router/apps/schule/datenaustausch/schulwechsel/RouteSchuleDatenaustauschSchulwechselAbgaenge";

type RouteStateDatenaustauschSchulwechsel = RouteStateInterface;

const defaultState = <RouteStateDatenaustauschSchulwechsel> {
	view: routeSchuleDatenaustauschSchulwechselAbgaenge,
};

export class RouteDataSchuleDatenaustauschSchulwechsel extends RouteData<RouteStateDatenaustauschSchulwechsel> {

	public constructor() {
		super(defaultState);
	}
}
