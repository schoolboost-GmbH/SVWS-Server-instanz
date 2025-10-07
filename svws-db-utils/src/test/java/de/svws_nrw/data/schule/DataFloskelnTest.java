package de.svws_nrw.data.schule;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.svws_nrw.asd.data.schule.Schuljahresabschnitt;
import de.svws_nrw.asd.utils.ASDCoreTypeUtils;
import de.svws_nrw.core.data.schule.Floskel;
import de.svws_nrw.db.Benutzer;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.dto.current.schild.faecher.DTOFach;
import de.svws_nrw.db.dto.current.schild.katalog.DTOFloskelgruppen;
import de.svws_nrw.db.dto.current.schild.katalog.DTOFloskeln;
import de.svws_nrw.db.utils.ApiOperationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static jakarta.ws.rs.core.Response.Status;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Diese Klasse testet die Klasse DataFloskeln")
class DataFloskelnTest {

	@Mock
	private DBEntityManager conn;

	private DataFloskeln data;

	@BeforeAll
	static void setUpAll() {
		ASDCoreTypeUtils.initAll();
	}

	@BeforeEach
	void setUp() {
		final var dtoFach = new DTOFach(1L, true);
		dtoFach.Kuerzel = "D";
		lenient().when(conn.queryAll(DTOFach.class)).thenReturn(List.of(dtoFach));

		data = new DataFloskeln(conn);

		lenient().when(conn.getUser()).thenReturn(mock(Benutzer.class));
		lenient().when(conn.getUser().schuleGetSchuljahresabschnitt()).thenReturn(mock(Schuljahresabschnitt.class));
	}

	@Test
	@DisplayName("setAttributesRequiredOnCreation: kuerzel")
	void setAttributesRequiredOnCreation() {
		assertThatException()
				.isThrownBy(() -> this.data.add(Map.of("bezeichnung", "test")))
				.isInstanceOf(ApiOperationException.class)
				.withMessage("Attribut kuerzel: Der Wert null ist nicht erlaubt.")
				.hasFieldOrPropertyWithValue("status", Status.BAD_REQUEST);
	}

	@Test
	@DisplayName("setAttributesNotPatchable: kuerzel")
	void setAttributesNotPatchableTest() {
		when(this.conn.queryByKey(DTOFloskeln.class, "#A10")).thenReturn(mock(DTOFloskeln.class));

		assertThatException()
				.isThrownBy(() -> this.data.patch("#A10", Map.of("kuerzel", "test")))
				.isInstanceOf(ApiOperationException.class)
				.withMessage("Folgende Attribute werden für ein Patch nicht zugelassen: kuerzel.")
				.hasFieldOrPropertyWithValue("status", Status.BAD_REQUEST);
	}

	@Test
	@DisplayName("getID: must return kuerzel because its the primary key")
	void getIDMustReturnKuerzel() throws ApiOperationException {
		assertThat(this.data.getID(Map.of("kuerzel", "abc")))
				.isEqualTo("abc");
	}

	@Test
	@DisplayName("checkBeforeCreation | alreadyUsed")
	void checkBeforeCreation_alreadyUsed() {
		when(this.conn.queryAll(DTOFloskeln.class)).thenReturn(List.of(new DTOFloskeln("USED", "allgemein")));

		assertThatException()
				.isThrownBy(() -> this.data.checkBeforeCreation("USED", Map.of("kuerzel", "USED")))
				.isInstanceOf(ApiOperationException.class)
				.withMessage("Das Kürzel USED wird bereits verwendet.")
				.hasFieldOrPropertyWithValue("status", Status.BAD_REQUEST);
	}


	@Test
	@DisplayName("checkBeforeCreation | not in use")
	void checkBeforeCreation_notInUse() {
		when(this.conn.queryAll(DTOFloskeln.class)).thenReturn(List.of(new DTOFloskeln("USED", "allgemein")));

		assertThatNoException()
				.isThrownBy(() -> this.data.checkBeforeCreation("NOT_USED", Map.of("kuerzel", "NOT_USED")));
	}


	@Test
	@DisplayName("initDTO | kuerzel is assigned")
	void initDTO_kuerzelIsAssigned() {
		final var dto = new DTOFloskeln("placeholder", "allgemein");

		this.data.initDTO(dto, "real", Map.of("kuerzel", "real"));

		assertThat(dto.Kuerzel).isEqualTo("real");
	}

	@Test
	@DisplayName("map")
	void mapTest() {
		final var dto = new DTOFloskeln("#A06", "text");
		dto.FloskelGruppe = "ZB";
		dto.FloskelFach = "D";
		dto.FloskelNiveau = "AB";
		dto.FloskelJahrgang = "09";

		assertThat(this.data.map(dto))
				.isInstanceOf(Floskel.class)
				.hasFieldOrPropertyWithValue("kuerzel", "#A06")
				.hasFieldOrPropertyWithValue("text", "text")
				.hasFieldOrPropertyWithValue("kuerzelFloskelgruppe", "ZB")
				.hasFieldOrPropertyWithValue("idFach", 1L)
				.hasFieldOrPropertyWithValue("niveau", "AB")
				.hasFieldOrPropertyWithValue("idJahrgang", 9000000L);
	}

	@Test
	@DisplayName("map | kuerzelFach is null")
	void mapTest_kuerzelFachIsNull() {
		final var dto = new DTOFloskeln("#A10", "allgemein");
		dto.FloskelFach = null;

		assertThat(this.data.map(dto))
				.isInstanceOf(Floskel.class)
				.hasFieldOrPropertyWithValue("idFach", null);
	}

	@Test
	@DisplayName("map | jahrgang is null")
	void mapTest_jahrgangIsNull() {
		final var dto = new DTOFloskeln("#A10", "allgemein");
		dto.FloskelJahrgang = null;

		assertThat(this.data.map(dto))
				.isInstanceOf(Floskel.class)
				.hasFieldOrPropertyWithValue("idJahrgang", null);
	}

	@Test
	@DisplayName("getById | Id is null")
	void getByIdTest_idIsNull() {
		assertThatException()
				.isThrownBy(() -> this.data.getById(null))
				.isInstanceOf(ApiOperationException.class)
				.withMessage("Das Kürzel der Floskel darf nicht null sein.")
				.hasFieldOrPropertyWithValue("status", Status.BAD_REQUEST);
	}

	@Test
	@DisplayName("getById | no entry found")
	void getByIdTest_noEntryFound() {
		when(this.conn.queryByKey(DTOFloskeln.class, "#A10")).thenReturn(null);

		assertThatException()
				.isThrownBy(() -> this.data.getById("#A10"))
				.isInstanceOf(ApiOperationException.class)
				.withMessage("Es wurde keine Floskel mit dem Kürzel #A10 gefunden.")
				.hasFieldOrPropertyWithValue("status", Status.NOT_FOUND);
	}

	@Test
	@DisplayName("getById")
	void getByIdTest() throws ApiOperationException {
		final var dto = new DTOFloskeln("#A10", "allgemein");
		when(this.conn.queryByKey(DTOFloskeln.class, "#A10")).thenReturn(dto);

		assertThat(this.data.getById("#A10"))
				.isInstanceOf(Floskel.class)
				.hasFieldOrPropertyWithValue("kuerzel", "#A10")
				.hasFieldOrPropertyWithValue("text", "allgemein");
	}

	@Test
	@DisplayName("getAll | no entries found")
	void getAllTest_noEntriesFound() {
		when(this.conn.queryAll(DTOFloskeln.class)).thenReturn(Collections.emptyList());

		assertThat(this.data.getAll())
				.isNotNull()
				.isEmpty();
	}

	@Test
	@DisplayName("getAll")
	void getAllTest() {
		final var dto = new DTOFloskeln("#A10", "text1");
		final var dto2 = new DTOFloskeln("#A11", "text2");
		when(this.conn.queryAll(DTOFloskeln.class)).thenReturn(List.of(dto, dto2));

		assertThat(this.data.getAll())
				.hasSize(2)
				.satisfiesExactly(
						f1 -> assertThat(f1)
								.hasFieldOrPropertyWithValue("kuerzel", "#A10")
								.hasFieldOrPropertyWithValue("text", "text1"),
						f2 -> assertThat(f2)
								.hasFieldOrPropertyWithValue("kuerzel", "#A11")
								.hasFieldOrPropertyWithValue("text", "text2")
				);
	}

	@Test
	@DisplayName("mapAttribute | updateFloskelgruppe | keine Floskelgruppe gefunden")
	void mapAttribute_updateFloskelgruppe_notFound() {
		final var dto = new DTOFloskeln("kuerzel", "allgemein");

		assertThatException()
				.isThrownBy(() -> this.data.mapAttribute(dto, "kuerzelFloskelgruppe", "xyz", Collections.emptyMap()))
				.isInstanceOf(ApiOperationException.class)
				.withMessage("Keine Floskelgruppe mit dem Kürzel xyz vorhanden.")
				.hasFieldOrPropertyWithValue("status", Status.NOT_FOUND);
	}

	@Test
	@DisplayName("mapAttribute | updateFloskelgruppe")
	void mapAttribute_updateFloskelgruppe() throws ApiOperationException {
		final var dto = new DTOFloskeln("kuerzel", "allgemein");
		when(this.conn.queryByKey(DTOFloskelgruppen.class, "vorhanden")).thenReturn(mock(DTOFloskelgruppen.class));

		this.data.mapAttribute(dto, "kuerzelFloskelgruppe", "vorhanden", Collections.emptyMap());

		assertThat(dto.FloskelGruppe).isEqualTo("vorhanden");
	}

	@Test
	@DisplayName("mapAttribute | updateFach | idIsNull")
	void mapAttribute_updateFach_idIsNull() throws ApiOperationException {
		final var dto = new DTOFloskeln("kuerzel", "allgemein");
		dto.FloskelFach = "not null";

		this.data.mapAttribute(dto, "idFach", null, Collections.emptyMap());

		assertThat(dto.FloskelFach).isNull();
	}

	@Test
	@DisplayName("mapAttribute | updateFach | not_found")
	void mapAttribute_updateFach_notFound() {
		assertThatException()
				.isThrownBy(() -> this.data.mapAttribute(mock(DTOFloskeln.class), "idFach", 9999, Collections.emptyMap()))
				.isInstanceOf(ApiOperationException.class)
				.withMessage("Kein Fach mit der ID 9999 gefunden.")
				.hasFieldOrPropertyWithValue("status", Status.NOT_FOUND);
	}

	@Test
	@DisplayName("mapAttribute | updateFach")
	void mapAttribute_updateFach() throws ApiOperationException {
		final var fach = new DTOFach(1L, true);
		fach.Kuerzel = "nachher";
		when(this.conn.queryByKey(DTOFach.class, 1L)).thenReturn(fach);
		final var floskel = new DTOFloskeln("kuerzel", "allgemein");
		floskel.FloskelFach = "vorher";

		this.data.mapAttribute(floskel, "idFach", 1L, Collections.emptyMap());

		assertThat(floskel.FloskelFach).isEqualTo("nachher");
	}

	@Test
	@DisplayName("mapAttribute | updateJahrgang | id null")
	void mapAttribute_updateJahrgang_idNull() throws ApiOperationException {
		final var dto = new DTOFloskeln("kuerzel", "allgemein");
		dto.FloskelJahrgang = "vorher";

		this.data.mapAttribute(dto, "idJahrgang", null, Collections.emptyMap());

		assertThat(dto.FloskelJahrgang).isNull();
	}

	@Test
	@DisplayName("mapAttribute | updateJahrgang | kein Jahrgang zur id")
	void mapAttribute_updateJahrgang_keinJahrgang() {
		assertThatException()
				.isThrownBy(() -> this.data.mapAttribute(mock(DTOFloskeln.class), "idJahrgang", 9999, Collections.emptyMap()))
				.isInstanceOf(ApiOperationException.class)
				.withMessage("Kein Jahrgang mit der ID 9999 gefunden.")
				.hasFieldOrPropertyWithValue("status", Status.NOT_FOUND);
	}

	@Test
	@DisplayName("mapAttribute | updateJahrgang")
	void mapAttribute_updateJahrgang() throws ApiOperationException {
		final var dto = new DTOFloskeln("kuerzel", "allgemein");
		dto.FloskelJahrgang = "vorher";

		this.data.mapAttribute(dto, "idJahrgang", 90000, Collections.emptyMap());

		assertThat(dto.FloskelJahrgang).isEqualTo("90");
	}

	@Test
	@DisplayName("mapAttribute | update text")
	void mapAttribute_updateText() throws ApiOperationException {
		final var dto = new DTOFloskeln("kuerzel", "vorher");

		this.data.mapAttribute(dto, "text", "nachher", Collections.emptyMap());

		assertThat(dto.FloskelText).isEqualTo("nachher");
	}

	@Test
	@DisplayName("mapAttribute | update niveau")
	void mapAttribute_updateNiveau() throws ApiOperationException {
		final var dto = new DTOFloskeln("kuerzel", "text");
		dto.FloskelNiveau = "AA";

		this.data.mapAttribute(dto, "niveau", "BB", Collections.emptyMap());

		assertThat(dto.FloskelNiveau).isEqualTo("BB");
	}

	@Test
	@DisplayName("mapAttribute | unknown argument")
	void mapAttribute_unknownArgument() {
		assertThatException()
				.isThrownBy(() -> this.data.mapAttribute(mock(DTOFloskeln.class), "unknown", null, Collections.emptyMap()))
				.isInstanceOf(ApiOperationException.class)
				.withMessage("Die Daten des Patches enthalten das unbekannte Attribut unknown.")
				.hasFieldOrPropertyWithValue("status", Status.BAD_REQUEST);
	}
}
