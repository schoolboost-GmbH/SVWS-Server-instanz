package de.svws_nrw.asd.types.schule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Teste den Core-Type FLoskelgruppenart")
public class FloskelgruppenartTest {

	@Test
	@DisplayName("Anzahl der Coretypes")
	void anzahlCoretypesTest() {
		assertThat(Floskelgruppenart.data().getWerte().size()).isEqualTo(9);
	}

}
