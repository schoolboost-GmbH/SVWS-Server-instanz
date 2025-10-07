package de.svws_nrw.core.kursblockung;

import java.util.Random;

import de.svws_nrw.core.Service;
import de.svws_nrw.core.data.kursblockung.SchuelerblockungInput;
import de.svws_nrw.core.data.kursblockung.SchuelerblockungOutput;
import de.svws_nrw.core.logger.LogLevel;
import jakarta.validation.constraints.NotNull;

/**
 * Dieser Service teilt EINEN Schüler anhand seiner Fachwahlen auf Kurse zu. Dabei geht der Algorithmus davon aus,
 * dass die Kurse bereits auf Schienen verteilt wurden. Die Eingabe {@link SchuelerblockungInput} wird in
 * {@link SchuelerblockungOutput} umgewandelt.
 *
 * @author Benjamin A. Bartsch
 */
public final class SchuelerblockungAlgorithmus extends Service {

	private static final @NotNull Random _random = new Random();

	/**
	 * Leerer Standardkonstruktor.
	 */
	public SchuelerblockungAlgorithmus() {
		// leer
	}

	/**
	 * Berechnen die Kurs-Schülerfachwahl-Zuordnung für einen Schüler anhand seiner Fachwahlen.
	 *
	 * @param pInput   die Daten mit Informationen zu den Kursen und den Fachwahlen
	 *
	 * @return die Kurs-Schülerfachwahl-Zuordnung
	 */
	public @NotNull SchuelerblockungOutput handle(final @NotNull SchuelerblockungInput pInput) {
		// Logger-Einrückung (relativ +4).
		logger.modifyIndent(+4);

		// Random-Objekt mit bestimmten Seed erzeugen.
		final long seed = _random.nextLong();
		final @NotNull Random random = new Random(seed);
		logger.log(LogLevel.APP, "SchuelerblockungAlgorithmus.handle(): Seed (" + seed + ") verwendet.");

		// SchuelerblockungInput --> SchuelerblockungDynDaten
		final @NotNull SchuelerblockungDynDaten dynDaten = new SchuelerblockungDynDaten(random, pInput);

		// Logger-Einrückung (relativ -4).
		logger.modifyIndent(-4);

		// Rückgabe des besten Ergebnisses
		return dynDaten.gibBestesMatching();
	}

}
