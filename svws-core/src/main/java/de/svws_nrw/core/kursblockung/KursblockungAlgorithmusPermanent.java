package de.svws_nrw.core.kursblockung;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.svws_nrw.core.logger.Logger;
import de.svws_nrw.core.utils.gost.GostBlockungsdatenManager;
import de.svws_nrw.core.utils.gost.GostBlockungsergebnisManager;
import jakarta.validation.constraints.NotNull;

/**
 * Diese Klasse dient zur Berechnung von Blockungsergebnissen.
 * <br>Die Methode {@link #next(long)} dient dazu, den Rechenprozess beliebig fortzuführen.
 * <br>Die Methode {@link #getBlockungsergebnisse()} liefert eine Liste der bisher besten Blockungsergebnisse.
 *
 * @author Benjamin A. Bartsch
 */
public final class KursblockungAlgorithmusPermanent {

	private static final long MILLIS_START = 4000; // Lineare Inkrementierung ist nicht gut.
	private static final int TOP_ERGEBNISSE = 3;

	private final @NotNull Random _random = new Random();
	private final @NotNull Logger _logger = new Logger();

	/** Die TOP-Ergebnisse werden als {@link KursblockungDynDaten}-Objekt gespeichert, da diese sortierbar sind. */
	private final @NotNull ArrayList<KursblockungDynDaten> _top;

	/** Jeder Algorithmus hat sein eigenes {@link KursblockungDynDaten}-Objekt. Das ist wichtig. */
	private final @NotNull KursblockungAlgorithmusPermanentK @NotNull [] algorithmenK;

	/** Die Eingabe-Daten von der GUI. */
	private final @NotNull GostBlockungsdatenManager _input;

	/** Die Zeitspanne nachdem alle Algorithmen neu erzeugt werden. */
	private long _zeitMax;

	/** Die Zeitspanne reduziert sich schrittweise, da die GUI nur kurze Rechenintervalle dem Algorithmus gibt.*/
	private long _zeitRest;

	/** Der Index des aktuellen Algorithmus der als nächstes ausgeführt wird.*/
	private int _currentIndex;

	/**
	 * Initialisiert den Blockungsalgorithmus für eine vom Clienten initiierte dauerhafte Berechnung.
	 *
	 * @param pInput  Das Eingabe-Objekt (der Daten-Manager).
	 */
	public KursblockungAlgorithmusPermanent(final @NotNull GostBlockungsdatenManager pInput) {
		// Random-Objekt erzeugen (Größter Integer Wert in TypeScript --> 9007199254740991L).
		final long seed = _random.nextLong();
		_logger.logLn("KursblockungAlgorithmusPermanent: Seed = " + seed);

		_input = pInput;
		_zeitMax = MILLIS_START;
		_zeitRest = MILLIS_START;
		_currentIndex = 0;
		_top = new ArrayList<>();

		algorithmenK = new KursblockungAlgorithmusPermanentK @NotNull [] {
				// Alle Algorithmen zur Verteilung von Kursen auf ihre Schienen ...
				// new KursblockungAlgorithmusPermanentKSchnellW(_random, _logger, _input),
				// new KursblockungAlgorithmusPermanentKFachgruppe(_random, _logger, _input),
				// new KursblockungAlgorithmusPermanentKFachwahlmatrix(_random, _logger, _input),
				new KursblockungAlgorithmusPermanentKMatching(_random, _logger, _input),
				new KursblockungAlgorithmusPermanentKSchuelervorschlag(_random, _logger, _input),
				new KursblockungAlgorithmusPermanentKSchuelervorschlagSingle(_random, _logger, _input),
				new KursblockungAlgorithmusPermanentKOptimiereBest(_random, _logger, _input, null),
				new KursblockungAlgorithmusPermanentKOptimiereBest(_random, _logger, _input, null),
				// ... Ende der K-Algorithmen.
		};
	}

	/**
	 * Liefert TRUE, falls die GUI die TOP-Liste aktualisieren soll.
	 *
	 * @param zeitProAufruf  Die zur Verfügung stehende Zeit (in Millisekunden), um die ehemaligen Ergebnisse zu optimieren.
	 * @return TRUE, falls die GUI die TOP-Liste aktualisieren soll.
	 */
	public boolean next(final long zeitProAufruf) {
		// Rechnen innerhalb des Zeitlimits (ca.100 ms).
		final long zeitStart = System.currentTimeMillis();
		final long zeitEnde = zeitStart + zeitProAufruf;
		algorithmenK[_currentIndex].next(zeitEnde);
		_currentIndex = (_currentIndex + 1) % algorithmenK.length;
		_zeitRest -= (System.currentTimeMillis() - zeitStart);

		// Neustart und GUI soll die Liste aktualisieren.
		if (_zeitRest < 100) {
			// DEBUG-AUSGABE
			// System.out.println("    NEUSTART: " + _zeitRest + " / " + _zeitMax + " / " + System.currentTimeMillis());
			_neustart();
			return true;
		}

		// GUI soll nicht aktualisieren.
		return false;
	}

	/**
	 * Liefert TRUE, falls mindestens ein Algorithmus ein besseres Ergebnis gefunden hat.
	 *
	 * @return TRUE, falls mindestens ein Algorithmus ein besseres Ergebnis gefunden hat.
	 */
	private int _neustart() {
		int verbesserungen = 0;
		for (int iK = 0; iK < algorithmenK.length; iK++) {
			// Lädt beim Algorithmus den besten Zustand. Einige Algorithmen verteilen hier erst die SuS.
			algorithmenK[iK].ladeBestMitSchuelerverteilung();

			// DEBUG-AUSGABE
			// System.out.println("        Algorithmus " + algorithmenK[iK].toString() + " hat " + algorithmenK[iK].gibDynDaten().gibStatistik().debugRowKurz());

			// Sortiert einfügen.
			boolean eingefuegt = false;
			for (int i = 0; (i < _top.size()) && (!eingefuegt); i++)
				if (algorithmenK[iK].dynDaten.gibIstBesser_NW_KD_FW_Als(_top.get(i))) {
					_top.add(i, algorithmenK[iK].dynDaten);
					eingefuegt = true;
				}

			// Sonderfälle
			if (eingefuegt) {
				verbesserungen++;
				// Prüfe, ob die Liste zu groß ist.
				if (_top.size() > TOP_ERGEBNISSE)
					_top.removeLast();
			} else {
				// Prüfe, ob die Liste zu klein ist.
				if (_top.size() < TOP_ERGEBNISSE) {
					_top.addLast(algorithmenK[iK].dynDaten);
					verbesserungen++;
				}
			}

		}

		// DEBUG-AUSGABE
		// for (int iTop = 0; iTop < _top.size(); iTop++)
		//     System.out.println("        TOP " + iTop + ": " + _top.get(iTop).gibStatistik().debugRowKurz());

		// Alle K-Algorithmen neu erzeugen, da jeweils ein neues "KursblockungDynDaten"-Objekt erzeugt werden muss.
		// algorithmenK[0] = new KursblockungAlgorithmusPermanentKSchnellW(_random, _logger, _input);
		// algorithmenK[4] = new KursblockungAlgorithmusPermanentKFachgruppe(_random, _logger, _input);
		// algorithmenK[0] = new KursblockungAlgorithmusPermanentKFachwahlmatrix(_random, _logger, _input);
		algorithmenK[0] = new KursblockungAlgorithmusPermanentKMatching(_random, _logger, _input);
		algorithmenK[1] = new KursblockungAlgorithmusPermanentKSchuelervorschlag(_random, _logger, _input);
		algorithmenK[2] = new KursblockungAlgorithmusPermanentKSchuelervorschlagSingle(_random, _logger, _input);
		algorithmenK[3] = new KursblockungAlgorithmusPermanentKOptimiereBest(_random, _logger, _input, _gibTopElementOrNull());
		algorithmenK[4] = new KursblockungAlgorithmusPermanentKOptimiereBest(_random, _logger, _input, _gibTopElementOrNull());

		// Die Berechnungszeit steigt exponentiell. Mehrere Tests ergaben, dass dies besser ist als linear.
		_zeitMax = (int) (_zeitMax * 1.5);
		_zeitRest = _zeitMax;

		return verbesserungen;
	}

	/**
	 * Liefert ein zufälliges Element aus der TOP-Liste (oder NULL);
	 *
	 * @return ein zufälliges Element aus der TOP-Liste (oder NULL);
	 */
	private KursblockungDynDaten _gibTopElementOrNull() {
		if (_top.isEmpty())
			return null;
		int index = _random.nextInt(_top.size());
		return _top.get(index);
	}

	/**
	 * Liefert die Liste der aktuellen Top-Blockungsergebnisse.
	 * <br> Die ID der Blockungsergebnisse entspricht dem Index in der TOP-Liste.
	 *
	 * @return die Liste der aktuellen Top-Blockungsergebnisse.
	 */
	public @NotNull List<GostBlockungsergebnisManager> getBlockungsergebnisse() {
		// Konvertiere "KursblockungDynDaten" zu "GostBlockungsergebnisManager".
		final @NotNull List<GostBlockungsergebnisManager> list = new ArrayList<>();

		for (int i = 0; i < _top.size(); i++)
			list.add(_top.get(i).gibErzeugtesKursblockungOutput(_input, i));

		return list;
	}

}
