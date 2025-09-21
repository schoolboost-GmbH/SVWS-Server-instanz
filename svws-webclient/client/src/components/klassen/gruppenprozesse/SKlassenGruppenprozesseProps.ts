import type { Schulform, List, Schulgliederung, BenutzerKompetenz, ApiFile, ReportingParameter, StundenplanListeEintrag, SimpleOperationResponse, ServerMode } from "@core";
import type { KlassenListeManager } from "@ui";
import type { ApiStatus } from "~/components/ApiStatus";

export interface KlassenGruppenprozesseProps {
	apiStatus: ApiStatus;
	serverMode: ServerMode;
	getPDF: (parameter: ReportingParameter) => Promise<ApiFile>;
	sendEMail: (parameter: ReportingParameter) => Promise<SimpleOperationResponse>;
	mapStundenplaene: Map<number, StundenplanListeEintrag>;
	schulform: Schulform;
	benutzerKompetenzen: Set<BenutzerKompetenz>;
	schulgliederungen: List<Schulgliederung>;
	manager: () => KlassenListeManager;
	deleteKlassen: () => Promise<[boolean, List<string | null>]>;
}
