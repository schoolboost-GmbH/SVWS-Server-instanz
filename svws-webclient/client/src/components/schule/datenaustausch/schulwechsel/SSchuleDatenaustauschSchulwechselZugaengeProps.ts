import type { Schulform, Schuljahresabschnitt, ServerMode } from "@core";

export interface SSchuleDatenaustauschSchulwechselZugaengeProps {
	serverMode: ServerMode;
	schulform: Schulform;
	schuljahresabschnitt: () => Schuljahresabschnitt;
}
