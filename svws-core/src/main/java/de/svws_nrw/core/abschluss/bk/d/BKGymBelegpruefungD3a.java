package de.svws_nrw.core.abschluss.bk.d;

import jakarta.validation.constraints.NotNull;

/**
 * Der Belegprüfungsalgorithmus für den Bildungsgang der Schulgliederung D01
 * und der Fachklasse 106 00.
 */
public final class BKGymBelegpruefungD3a extends BKGymBelegpruefung {

	/**
	 * Erzeugt einen neue Belegprüfung
	 *
	 * @param manager   der Manager für die Abiturdaten
	 */
	public BKGymBelegpruefungD3a(@NotNull final BKGymAbiturdatenManager manager) {
		super(manager);
	}

}
