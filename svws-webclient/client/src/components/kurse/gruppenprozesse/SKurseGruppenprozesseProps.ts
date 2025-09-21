import type {List, ServerMode, BenutzerKompetenz, ReportingParameter, ApiFile, SimpleOperationResponse, StundenplanListeEintrag, Schulform, Schulgliederung} from "@core";
import type { KursListeManager } from "@ui";
import type {ApiStatus} from "~/components/ApiStatus";

export interface KurseGruppenprozesseProps {
	apiStatus: ApiStatus;
	serverMode: ServerMode;
	getPDF: (parameter: ReportingParameter) => Promise<ApiFile>;
	sendEMail: (parameter: ReportingParameter) => Promise<SimpleOperationResponse>;
	schulform: Schulform;
	benutzerKompetenzen: Set<BenutzerKompetenz>;
	schulgliederungen: List<Schulgliederung>;
	manager: () => KursListeManager;
	deleteKurse: () => Promise<[boolean, List<string | null>]>;
}
