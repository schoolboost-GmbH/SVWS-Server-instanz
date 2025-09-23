import type { Schulform, Schuljahresabschnitt, ServerMode } from "@core";

export interface SSchuleDatenaustauschSchulwechselKonfigurationProps {
	serverMode: ServerMode;
	schulform: Schulform;
	schuljahresabschnitt: () => Schuljahresabschnitt;
}
