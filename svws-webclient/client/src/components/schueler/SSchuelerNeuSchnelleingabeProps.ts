import type { SchuelerListeManager } from "@ui";
import type { BenutzerKompetenz, EinschulungsartKatalogEintrag, Erzieherart, ErzieherStammdaten, Fahrschuelerart, Haltestelle, Kindergarten, KlassenDaten, List, OrtKatalogEintrag, OrtsteilKatalogEintrag, ReligionEintrag, SchuelerLernabschnittsdaten, SchuelerSchulbesuchsdaten, SchuelerStammdaten, SchuelerTelefon, SchulEintrag, Schulform, Schuljahresabschnitt, ServerMode, TelefonArt } from "@core";
import type { SchuelerSchulbesuchManager } from "~/components/schueler/schulbesuch/SchuelerSchulbesuchManager";
import type { SchuelerLernabschnittManager } from "~/components/schueler/lernabschnitte/SchuelerLernabschnittManager";

export interface SchuelerNeuSchnelleingabeProps {
	schuelerListeManager: () => SchuelerListeManager;
	schuelerSchulbesuchsManager: () => SchuelerSchulbesuchManager;
	schuelerLernabschnittManager: () => SchuelerLernabschnittManager;
	patch: (patchObject: Partial<SchuelerStammdaten>, id: number) => Promise<void>;
	mapSchulen: Map<string, SchulEintrag>;
	mapOrte: Map<number, OrtKatalogEintrag>;
	mapOrtsteile: Map<number, OrtsteilKatalogEintrag>;
	mapReligionen: Map<number, ReligionEintrag>;
	mapFahrschuelerarten: Map<number, Fahrschuelerart>;
	mapHaltestellen: Map<number, Haltestelle>;
	mapKindergaerten: Map<number, Kindergarten>;
	mapEinschulungsarten: Map<number, EinschulungsartKatalogEintrag>;
	mapTelefonArten: Map<number, TelefonArt>;
	mapErzieherarten: Map<number, Erzieherart>;
	getListSchuelerErziehereintraege: () => List<ErzieherStammdaten>;
	addSchuelerErziehereintrag: (data: Partial<ErzieherStammdaten>, idEintrag: number, pos: number) => Promise<ErzieherStammdaten>;
	patchSchuelerErziehereintrag: (data: Partial<ErzieherStammdaten>, idEintrag: number) => Promise<void>;
	patchSchuelerErzieherAnPosition: (data : Partial<ErzieherStammdaten>, idEintrag: number, idSchueler: number, pos: number) => Promise<void>;
	deleteSchuelerErziehereintrage: (idsEintraege: List<number>) => Promise<void>;
	getListSchuelerTelefoneintraege: () => List<SchuelerTelefon>;
	addSchuelerTelefoneintrag: (data: Partial<SchuelerTelefon>, idSchueler: number) => Promise<void>;
	patchSchuelerTelefoneintrag: (data: Partial<SchuelerTelefon>, idEintrag: number) => Promise<void>;
	deleteSchuelerTelefoneintrage: (idsEintraege: List<number>) => Promise<void>;
	patchSchuelerSchulbesuchsdaten: (data: Partial<SchuelerSchulbesuchsdaten>, idSchueler: number) => Promise<void>;
	patchSchuelerLernabschnittsdaten: (data : Partial<SchuelerLernabschnittsdaten>, idEintrag: number) => Promise<void>;
	getSchuelerKlassenFuerAbschnitt: (idAbschnitt: number) => Promise<List<KlassenDaten>>;
	gotoDefaultView: (idEintrag?: number | null) => Promise<void>;
	aktAbschnitt: Schuljahresabschnitt;
	serverMode: ServerMode;
	schulform: Schulform,
	benutzerKompetenzen: Set<BenutzerKompetenz>,
}
