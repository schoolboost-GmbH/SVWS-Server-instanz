import { BenutzerKompetenz, Floskel, Schulform } from "@core";
import { FloskelnListeManager } from "@ui";

export interface FloskelnDatenProps {
	manager: () => FloskelnListeManager;
	schuljahr: number,
	schulform: Schulform,
	benutzerKompetenzen: Set<BenutzerKompetenz>;
	patch: (data: Partial<Floskel>) => Promise<void>;
}
