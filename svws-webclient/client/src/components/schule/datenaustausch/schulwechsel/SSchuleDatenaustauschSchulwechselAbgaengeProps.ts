import type { Schulform, Schuljahresabschnitt, ServerMode } from "@core";

export interface SSchuleDatenaustauschSchulwechselAbgaengeProps {
	serverMode: ServerMode;
	schulform: Schulform;
	schuljahresabschnitt: () => Schuljahresabschnitt;
}
