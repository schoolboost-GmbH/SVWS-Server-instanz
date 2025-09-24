import type { BenutzerKompetenz, FachDaten } from "@core";
import type { FachListeManager } from "@ui";

export interface FaecherDatenProps {
	patch: (data : Partial<FachDaten>) => Promise<void>;
	manager: () => FachListeManager;
	benutzerKompetenzen: Set<BenutzerKompetenz>;
}
