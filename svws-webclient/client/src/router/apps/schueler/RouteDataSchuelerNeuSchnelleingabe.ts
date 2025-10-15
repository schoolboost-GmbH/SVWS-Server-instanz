import { RouteData, type RouteStateInterface } from "~/router/RouteData";
import type { Fahrschuelerart, Haltestelle, Kindergarten, ReligionEintrag, SchuelerListeEintrag, SchuelerStammdaten, SchulEintrag, SchulformKatalogEintrag, TelefonArt, EinschulungsartKatalogEintrag, Erzieherart, OrtKatalogEintrag, OrtsteilKatalogEintrag, SchuelerLernabschnittListeEintrag, VermerkartEintrag, SchuelerSchulbesuchsdaten, Merkmal, KatalogEntlassgrund, List } from "@core";
import { ArrayList, DeveloperNotificationException, Schulform, SchuelerStammdatenNeu } from "@core";
import { api } from "~/router/Api";
import { routeSchueler } from "~/router/apps/schueler/RouteSchueler";
import { SchuelerSchulbesuchManager } from "~/components/schueler/schulbesuch/SchuelerSchulbesuchManager";
import { routeSchuelerSchulbesuch } from "~/router/apps/schueler/schulbesuch/RouteSchuelerSchulbesuch";
import { SchuelerLernabschnittManager } from "~/components/schueler/lernabschnitte/SchuelerLernabschnittManager";

interface RouteStateDataSchuelerNeuSchnelleingabe extends RouteStateInterface {
	mapKindergaerten: Map<number, Kindergarten>;
	mapFahrschuelerarten: Map<number, Fahrschuelerart>;
	mapHaltestellen: Map<number, Haltestelle>;
	mapReligionen: Map<number, ReligionEintrag>;
	mapSchulen: Map<string, SchulEintrag>;
	mapTelefonArten: Map<number, TelefonArt>;
	mapErzieherarten: Map<number, Erzieherart>;
	mapVermerkArten: Map<number, VermerkartEintrag>;
	mapEinschulungsarten: Map<number, EinschulungsartKatalogEintrag>;
	mapOrte: Map<number, OrtKatalogEintrag>;
	mapOrtsteile: Map<number, OrtsteilKatalogEintrag>;
	schuelerSchulbesuchManager: SchuelerSchulbesuchManager | undefined;
	schuelerLernabschnittsManager: SchuelerLernabschnittManager | undefined;
}

const defaultState = <RouteStateDataSchuelerNeuSchnelleingabe> {
	mapKindergaerten: new Map(),
	mapFahrschuelerarten: new Map(),
	mapHaltestellen: new Map(),
	mapReligionen: new Map(),
	mapSchulen: new Map<string, SchulEintrag>(),
	mapTelefonArten: new Map(),
	mapErzieherarten: new Map(),
	mapVermerkArten: new Map(),
	mapEinschulungsarten: new Map(),
	mapOrte: new Map(),
	mapOrtsteile: new Map(),
	schuelerSchulbesuchManager: undefined,
	schuelerLernabschnittsManager: undefined,
}

export class RouteDataSchuelerNeuSchnelleingabe extends RouteData<RouteStateDataSchuelerNeuSchnelleingabe> {

	public constructor() {
		super(defaultState);
	}

	public async ladeKataloge() : Promise<void> {
		const fahrschuelerarten = await api.server.getFahrschuelerarten(api.schema)
		const mapFahrschuelerarten = new Map();
		for (const fa of fahrschuelerarten)
			mapFahrschuelerarten.set(fa.id, fa);
		// Lade den Katalog der Haltestellen
		const haltestellen = await api.server.getHaltestellen(api.schema);
		const mapHaltestellen = new Map();
		for (const h of haltestellen)
			mapHaltestellen.set(h.id, h);
		// Lade den Katalog der Religionen
		const religionen = await api.server.getReligionen(api.schema)
		const mapReligionen = new Map();
		for (const r of religionen)
			mapReligionen.set(r.id, r);
		// Lade den Katalog der Kindergärten
		const kindergaerten = await api.server.getKindergaerten(api.schema)
		const mapKindergaerten = new Map();
		for (const k of kindergaerten)
			mapKindergaerten.set(k.id, k);
		// Lade den Katalog der TelefonArten
		const telefonArten = await api.server.getTelefonarten(api.schema);
		const mapTelefonArten = new Map();
		for (const ta of telefonArten)
			mapTelefonArten.set(ta.id, ta);
		// Lade den Katalog der Erzieherarten
		const erzieherarten = await api.server.getErzieherArten(api.schema);
		const mapErzieherarten = new Map();
		for (const ea of erzieherarten)
			mapErzieherarten.set(ea.id, ea);
		// Lade den Katalog der Vermerkarten
		const vermerkArten = await api.server.getVermerkarten(api.schema);
		const mapVermerkArten = new Map();
		for (const va of vermerkArten)
			mapVermerkArten.set(va.id, va);
		// Lade den Katalog der Einschulungsarten
		const einschulungsarten = await api.server.getEinschulungsarten(api.schema)
		const mapEinschulungsarten = new Map();
		for (const e of einschulungsarten)
			mapEinschulungsarten.set(e.id, e);
		// Lade den Katalog der Orte
		const orte = await api.server.getOrte(api.schema);
		const mapOrte = new Map();
		for (const o of orte)
			mapOrte.set(o.id, o);
		// Lade den Katalog der Ortsteile
		const ortsteile = await api.server.getOrtsteile(api.schema);
		const mapOrtsteile = new Map();
		for (const o of ortsteile)
			mapOrtsteile.set(o.id, o);
		// Ermittle den Katalog der Schulen, welche ein Kürzel haben und als Stammschulen für Schüler infrage kommen
		const schulen = await api.server.getSchulen(api.schema);
		const mapSchulen = new Map<string, SchulEintrag>();
		for (const schule of schulen) {
			if (schule.schulnummerStatistik === null)
				continue;
			const sfEintrag: SchulformKatalogEintrag | null = schule.idSchulform === null ? null : Schulform.data().getEintragByID(schule.idSchulform);
			const sf: Schulform | null = sfEintrag === null ? null : Schulform.data().getWertBySchluessel(sfEintrag.schluessel);
			if (sf === api.schulform)
				mapSchulen.set(schule.schulnummerStatistik, schule);
		}
		this.setPatchedState({ mapFahrschuelerarten, mapKindergaerten, mapHaltestellen, mapReligionen, mapTelefonArten, mapSchulen, mapErzieherarten, mapEinschulungsarten, mapOrte, mapOrtsteile, mapVermerkArten });

	}

	private async createSchuelerSchulbesuchManager(auswahl: SchuelerListeEintrag): Promise<SchuelerSchulbesuchManager> {
		const schuelerSchulbesuchsdaten: SchuelerSchulbesuchsdaten = await api.server.getSchuelerSchulbesuch(api.schema, auswahl.id);
		const kindergaerten = await api.server.getKindergaerten(api.schema)
		const schulen = new ArrayList<SchulEintrag>();
		const merkmale = new ArrayList<Merkmal>();
		const entlassgruende = new ArrayList<KatalogEntlassgrund>();
		return new SchuelerSchulbesuchManager(schuelerSchulbesuchsdaten, auswahl, api.schuleStammdaten.abschnitte, schulen, merkmale, entlassgruende, kindergaerten, routeSchuelerSchulbesuch.data.patch);
	}

	private selectBevorzugtenAbschnitt(listAbschnitte: List<SchuelerLernabschnittListeEintrag>): SchuelerLernabschnittListeEintrag | null {
		for (const a of listAbschnitte) {
			if ((a.schuljahresabschnitt === routeSchueler.data.idSchuljahresabschnitt) && (a.wechselNr === 0))
				return a;
		}
		if (!listAbschnitte.isEmpty())
			return listAbschnitte.get(listAbschnitte.size() - 1);
		return null;
	}

	public async ladeDaten(auswahl: SchuelerListeEintrag | null): Promise<SchuelerStammdaten | null> {
		if (auswahl === null)
			return null;
		const schuelerStammdaten = await api.server.getSchuelerStammdaten(api.schema, auswahl.id);
		const schuelerSchulbesuchManager = await this.createSchuelerSchulbesuchManager(auswahl);

		let schuelerLernabschnittsManager: SchuelerLernabschnittManager | undefined = undefined;
		const listAbschnitte = await api.server.getSchuelerLernabschnittsliste(api.schema, auswahl.id);
		// wähle bevorzugt einen Eintrag für den aktuellen Schuljahresabschnitt, WechselNr = 0, sonst letzten Eintrag
		const found = this.selectBevorzugtenAbschnitt(listAbschnitte);
		if (found !== null) {
			const daten = await api.server.getSchuelerLernabschnittsdatenByID(api.schema, found.id);
			const [ listFaecher, listJahrgaenge ] = await Promise.all([
				api.server.getFaecher(api.schema),
				api.server.getJahrgaenge(api.schema),
			]);
			const schuljahresabschnitt = api.mapAbschnitte.value.get(daten.schuljahresabschnitt);
			if (schuljahresabschnitt !== undefined) {
				schuelerLernabschnittsManager = new SchuelerLernabschnittManager(
					api.schulform, auswahl, daten, schuljahresabschnitt,
					listFaecher, new ArrayList(), listJahrgaenge,
					new ArrayList(), new ArrayList(), new ArrayList()
				);
			}
		}

		this.setPatchedState({ schuelerSchulbesuchManager, schuelerLernabschnittsManager });
		return schuelerStammdaten;
	}

	public async ladeInitialeDatenFuerWeiterenSchueler(auswahl: SchuelerListeEintrag | null): Promise<SchuelerStammdatenNeu | null> {
		if (auswahl === null)
			return null;
		const schuelerDaten: SchuelerStammdatenNeu = new SchuelerStammdatenNeu();
		const schuelerStammdaten = await api.server.getSchuelerStammdaten(api.schema, auswahl.id);

		schuelerDaten.anmeldedatum = schuelerStammdaten.anmeldedatum;
		schuelerDaten.aufnahmedatum = schuelerStammdaten.aufnahmedatum;
		schuelerDaten.beginnBildungsgang = schuelerStammdaten.beginnBildungsgang;
		schuelerDaten.dauerBildungsgang = schuelerStammdaten.dauerBildungsgang;

		const listAbschnitte = await api.server.getSchuelerLernabschnittsliste(api.schema, auswahl.id);
		// wähle bevorzugt einen Eintrag für den aktuellen Schuljahresabschnitt, WechselNr = 0, sonst letzten Eintrag
		const found = this.selectBevorzugtenAbschnitt(listAbschnitte);
		if (found !== null) {
			const daten = await api.server.getSchuelerLernabschnittsdatenByID(api.schema, found.id);

			schuelerDaten.schuljahresabschnitt = daten.schuljahresabschnitt;
			schuelerDaten.jahrgangID = daten.jahrgangID;
			schuelerDaten.klassenID = daten.klassenID;
		}
		return schuelerDaten;
	}

	get mapFahrschuelerarten(): Map<number, Fahrschuelerart> {
		return this._state.value.mapFahrschuelerarten;
	}

	get mapHaltestellen(): Map<number, Haltestelle> {
		return this._state.value.mapHaltestellen;
	}

	get mapReligionen(): Map<number, ReligionEintrag> {
		return this._state.value.mapReligionen;
	}

	get mapSchulen(): Map<string, SchulEintrag> {
		return this._state.value.mapSchulen;
	}

	get mapTelefonArten(): Map<number, TelefonArt> {
		return this._state.value.mapTelefonArten;
	}

	get mapErzieherarten(): Map<number, Erzieherart> {
		return this._state.value.mapErzieherarten;
	}

	get mapVermerkArten(): Map<number, VermerkartEintrag> {
		return this._state.value.mapVermerkArten;
	}

	get mapKindergaerten(): Map<number, Kindergarten> {
		return this._state.value.mapKindergaerten;
	}

	get mapEinschulungsarten(): Map<number, EinschulungsartKatalogEintrag> {
		return this._state.value.mapEinschulungsarten;
	}

	public get mapOrte(): Map<number, OrtKatalogEintrag> {
		return this._state.value.mapOrte;
	}

	public get mapOrtsteile(): Map<number, OrtsteilKatalogEintrag> {
		return this._state.value.mapOrtsteile;
	}

	get schuelerLernabschnittManager(): SchuelerLernabschnittManager {
		if (this._state.value.schuelerLernabschnittsManager === undefined)
			throw new DeveloperNotificationException("Unerwarteter Fehler: Schüler-Lernabschnittsdaten nicht initialisiert");
		return this._state.value.schuelerLernabschnittsManager;
	}

	get schuelerSchulbesuchManager(): SchuelerSchulbesuchManager {
		if (this._state.value.schuelerSchulbesuchManager === undefined)
			throw new DeveloperNotificationException("SchülerSchulbesuchManager nicht initialisiert.")
		return this._state.value.schuelerSchulbesuchManager;
	}
}
