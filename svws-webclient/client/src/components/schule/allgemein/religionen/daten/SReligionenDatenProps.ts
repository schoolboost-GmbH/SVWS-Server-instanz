import type { BenutzerKompetenz, ReligionEintrag, Schulform } from "@core";
import type { ReligionenListeManager } from "@ui";

export interface ReligionenDatenProps {
	manager: () => ReligionenListeManager;
	schulform: Schulform;
	patch: (data: Partial<ReligionEintrag>) => Promise<void>;
	benutzerKompetenzen: Set<BenutzerKompetenz>,
}
