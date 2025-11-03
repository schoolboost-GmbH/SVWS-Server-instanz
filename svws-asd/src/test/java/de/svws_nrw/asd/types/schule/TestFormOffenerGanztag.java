package de.svws_nrw.asd.types.schule;

import de.svws_nrw.asd.utils.ASDCoreTypeUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Diese Klasse stellt JUnit-Tests für den Core-Type {@link FormOffenerGanztag}
 * zur Verfügung.
 */
@DisplayName("Teste den Core-Type FormOffenerGanztag")
class TestFormOffenerGanztag {

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
	 * Test des CoreTypes TestFormOffenerGanztag
	 *
	 * CoreType: FormOffenerGanztag
	 * Testfall: Prüft die Anzahl der möglichen Werte
	 * Ergebnis: Erwartete Anzahl - 3
	 */
	@Test
	@DisplayName("Teste CoreType FormOffenerGanztag: Anzahl der vorhandenen Werte.")
	void testFormOffenerGanztag_AnzahlEintraege() {
		assertEquals(3, FormOffenerGanztag.data().getWerte().size());
	}

	/**
	 * Test des CoreTypes FormOffenerGanztag
	 *
	 * CoreType: FormOffenerGanztag
	 * Testfall: Prüft den Text beim ersten Wert von EIGENE_SCHULE
	 * Ergebnis: Erwarteter Wert - "an der eigenen Schule wahrgenommen (ggf. an anderer Einrichtung)"
	 */
	@Test
	@DisplayName("Teste CoreType FormOffenerGanztag: Korrekter Text beim Wert EIGENE_SCHULE.")
	void testFormOffenerGanztag_TextBeiEIGENE_SCHULE() {
		assertEquals("an der eigenen Schule wahrgenommen (ggf. an anderer Einrichtung)", FormOffenerGanztag.data().getHistorieByWert(FormOffenerGanztag.EIGENE_SCHULE).getFirst().text);
	}

}
