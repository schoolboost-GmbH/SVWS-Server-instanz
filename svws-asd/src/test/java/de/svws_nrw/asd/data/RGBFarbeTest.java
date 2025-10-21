package de.svws_nrw.asd.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Diese Klasse testet die Klasse {@link RGBFarbe}*/
@DisplayName("Diese Klasse testet die Klasse RGBFarbe")
class RGBFarbeTest {

	@Test
	@DisplayName("decimalFromRgb")
	void decimalFromRgbTest() {
		assertThat(new RGBFarbe(42, 128, 0).asDecimal())
				.isEqualTo(2785280);
	}

	@Test
	@DisplayName("rgbToDecimal")
	void rgbToDecimalTest() {
		assertThat(new RGBFarbe(2785280))
				.hasFieldOrPropertyWithValue("red", 42)
				.hasFieldOrPropertyWithValue("green", 128)
				.hasFieldOrPropertyWithValue("blue", 0);
	}
}
