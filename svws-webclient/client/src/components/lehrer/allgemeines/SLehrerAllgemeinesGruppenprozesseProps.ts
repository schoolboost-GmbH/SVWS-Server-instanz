import {List, BenutzerKompetenz, ApiFile, ReportingParameter, StundenplanListeEintrag, SimpleOperationResponse} from "@core";
import type { LehrerListeManager } from "@ui";
import type { ApiStatus } from "~/components/ApiStatus";

export interface SLehrerAllgemeinesGruppenprozesseProps {
	apiStatus: ApiStatus;
	getPDF: (parameter: ReportingParameter, idStundenplan: number) => Promise<ApiFile>;
	sendEMail: (parameter: ReportingParameter) => Promise<SimpleOperationResponse>;
	mapStundenplaene: Map<number, StundenplanListeEintrag>;
	benutzerKompetenzen: Set<BenutzerKompetenz>;
	lehrerListeManager: () => LehrerListeManager;
	deleteLehrer: () => Promise<[boolean, List<string | null>]>;
	deleteLehrerCheck: () => { success: boolean, logs: List<string> };
}
