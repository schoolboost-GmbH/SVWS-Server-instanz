package de.svws_nrw.asd.types.schueler;

import de.svws_nrw.asd.utils.ASDCoreTypeUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Diese Klasse stellt JUnit-Tests für den Core-Type {@link Betreuungsart}
 * zur Verfügung.
 */
@DisplayName("Teste den Core-Type Betreuungsart")
class TestBetreuungsart {

	/**
	 * Initialisiert die Core-Types, damit die Tests ausgeführt werden können.
	 * Beim Laden der Core-Type-Daten werden die JSON-Dateien auf Plausibilität
	 * geprüft.
	 */
	@BeforeAll
	static void setup() {
		ASDCoreTypeUtils.initAll();
	}


	/**
	 * Test des CoreTypes Betreuungsart
	 *
	 * CoreType: Betreuungsart
	 * Testfall: Prüft die Anzahl der möglichen Werte
	 * Ergebnis: Erwartete Anzahl - 6
	 */
	@Test
	@DisplayName("Teste CoreType Note: Anzahl der vorhandenen Werte.")
	void testBetreuungsart_AnzahlEintraege() {
		assertEquals(6, Betreuungsart.data().getWerte().size());
	}

	/**
	 * Test des CoreTypes Betreuungsart
	 *
	 * CoreType: Betreuungsart
	 * Testfall: Prüft den Text beim ersten Wert von NUR_ACHT_BIS_EINS
	 * Ergebnis: Erwarteter Wert - "ausschließlich Schule von acht bis eins"
	 */
	@Test
	@DisplayName("Teste CoreType Betreuungsart: Korrekter Text beim Wert NUR_ACHT_BIS_EINS.")
	void testBetreuungsart_TextBeiNUR_ACHT_BIS_EINS() {
		assertEquals("ausschließlich Schule von acht bis eins",
				Betreuungsart.data().getHistorieByWert(Betreuungsart.NUR_ACHT_BIS_EINS).getFirst().text);
	}

}

