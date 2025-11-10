package de.svws_nrw.data.schule;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.svws_nrw.asd.utils.ASDCoreTypeUtils;
import de.svws_nrw.core.data.schule.FloskelJahrgangZuordnung;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.dto.current.katalog.DTOFloskelnJahrgaenge;
import de.svws_nrw.db.dto.current.schild.schule.DTOJahrgang;
import de.svws_nrw.db.utils.ApiOperationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Diese Klasse testet die Klasse DataFloskelJahrgangZuordnung")
class DataFloskelJahrgangZuordnungTest {

	@Mock
	private DBEntityManager conn;

	@InjectMocks
	private DataFloskelJahrgangZuordnung data;

	@BeforeAll
	static void setUp() {
		ASDCoreTypeUtils.initAll();
	}

	@Test
	@DisplayName("attributesRequiredOnCreation: idFloskel")
	void attributesRequiredOnCreation_idFloskel() {
		assertThatException()
				.isThrownBy(() -> this.data.add(Map.of("idJahrgang", 2)))
				.isInstanceOf(ApiOperationException.class)
				.withMessage("Es werden weitere Attribute (idFloskel) benötigt, damit die Entität erstellt werden kann.")
				.hasFieldOrPropertyWithValue("status", Response.Status.BAD_REQUEST);
	}

	@Test
	@DisplayName("attributesRequiredOnCreation: idJahrgang")
	void attributesRequiredOnCreation_idJahrgang() {
		assertThatException()
				.isThrownBy(() -> this.data.add(Map.of("idFloskel", 2)))
				.isInstanceOf(ApiOperationException.class)
				.withMessage("Es werden weitere Attribute (idJahrgang) benötigt, damit die Entität erstellt werden kann.")
				.hasFieldOrPropertyWithValue("status", Response.Status.BAD_REQUEST);
	}

	@Test
	@DisplayName("initDTO | id is assigned")
	void initDTO_idIsAssigned() {
		final var dto = new DTOFloskelnJahrgaenge(-1L, 2L, 3L);

		this.data.initDTO(dto, 2L, Collections.emptyMap());

		assertThat(dto.ID).isEqualTo(2L);
	}

	@Test
	@DisplayName("checkBeforeCreation | id combination not unique")
	void checkBeforeCreation_idCombinationNotUnique() {
		when(this.conn.queryList(DTOFloskelnJahrgaenge.QUERY_BY_FLOSKEL_ID, DTOFloskelnJahrgaenge.class, 2L))
				.thenReturn(List.of(new DTOFloskelnJahrgaenge(1L, 2L, 3L)));

		assertThatException()
				.isThrownBy(() -> this.data.checkBeforeCreation(2L, Map.of("idFloskel", 2L, "idJahrgang", 3L)))
				.isInstanceOf(ApiOperationException.class)
				.withMessage("Die Kombination aus idFloskel 2 und idJahrgang 3 ist bereits vorhanden.")
				.hasFieldOrPropertyWithValue("status", Response.Status.BAD_REQUEST);
	}

	@Test
	@DisplayName("checkBeforeCreation | id combination is unique")
	void checkBeforeCreation_idCombinationIsUnique() {
		when(this.conn.queryList(DTOFloskelnJahrgaenge.QUERY_BY_FLOSKEL_ID, DTOFloskelnJahrgaenge.class, 2L))
				.thenReturn(List.of(new DTOFloskelnJahrgaenge(1L, 2L, 4L)));

		assertThatNoException()
				.isThrownBy(() -> this.data.checkBeforeCreation(2L, Map.of("idFloskel", 2L, "idJahrgang", 3L)));
	}

	@Test
	@DisplayName("getById | Id is null")
	void getByIdTest_idIsNull() {
		assertThatException()
				.isThrownBy(() -> this.data.getById(null))
				.isInstanceOf(ApiOperationException.class)
				.withMessage("Die ID für die Floskel-Jahrgangs-Zuordnung darf nicht null sein.")
				.hasFieldOrPropertyWithValue("status", Response.Status.BAD_REQUEST);
	}

	@Test
	@DisplayName("getById | no entry found")
	void getByIdTest_noEntryFound() {
		when(this.conn.queryByKey(DTOFloskelnJahrgaenge.class, 1L)).thenReturn(null);

		assertThatException()
				.isThrownBy(() -> this.data.getById(1L))
				.isInstanceOf(ApiOperationException.class)
				.withMessage("Es wurde keine Floskel-Jahrgangs-Zuordnung mit der ID 1 gefunden.")
				.hasFieldOrPropertyWithValue("status", Response.Status.NOT_FOUND);
	}

	@Test
	@DisplayName("getById")
	void getByIdTest() throws ApiOperationException {
		final var dto = new DTOFloskelnJahrgaenge(1L, 2L, 3L);
		when(this.conn.queryByKey(DTOFloskelnJahrgaenge.class, 1L)).thenReturn(dto);

		assertThat(this.data.getById(1L))
				.isInstanceOf(FloskelJahrgangZuordnung.class)
				.hasFieldOrPropertyWithValue("id", 1L)
				.hasFieldOrPropertyWithValue("idFloskel", 2L)
				.hasFieldOrPropertyWithValue("idJahrgang", 3L);
	}

	@Test
	@DisplayName("getAll | no entries found")
	void getAllTest_noEntriesFound() {
		when(this.conn.queryAll(DTOFloskelnJahrgaenge.class)).thenReturn(Collections.emptyList());

		assertThat(this.data.getAll()).isEmpty();
	}

	@Test
	@DisplayName("getAll")
	void getAllTest() {
		final var dto = new DTOFloskelnJahrgaenge(1L, 11L, 111L);
		final var dto2 = new DTOFloskelnJahrgaenge(2L, 22L, 222L);
		when(this.conn.queryAll(DTOFloskelnJahrgaenge.class)).thenReturn(List.of(dto, dto2));

		assertThat(this.data.getAll())
				.hasSize(2)
				.satisfiesExactly(
						f1 -> assertThat(f1)
								.hasFieldOrPropertyWithValue("id", 1L)
								.hasFieldOrPropertyWithValue("idFloskel", 11L)
								.hasFieldOrPropertyWithValue("idJahrgang", 111L),
						f2 -> assertThat(f2)
								.hasFieldOrPropertyWithValue("id", 2L)
								.hasFieldOrPropertyWithValue("idFloskel", 22L)
								.hasFieldOrPropertyWithValue("idJahrgang", 222L)
				);
	}

	@Test
	@DisplayName("getListByIdFloskel | id is Null")
	void getListByIdFloskel_idIsNull() {
		assertThat(this.data.getListByIdFloskel(null)).isEmpty();
	}

	@Test
	@DisplayName("getListByIdFloskel | id not found")
	void getListByIdFloskel_idNotFound() {
		assertThat(this.data.getListByIdFloskel(-1L)).isEmpty();
	}

	@Test
	@DisplayName("getListByIdFloskel")
	void getListByIdFloskel() {
		final var dto = new DTOFloskelnJahrgaenge(1L, 1L, 111L);
		final var dto2 = new DTOFloskelnJahrgaenge(2L, 1L, 222L);
		when(this.conn.queryList(DTOFloskelnJahrgaenge.QUERY_BY_FLOSKEL_ID, DTOFloskelnJahrgaenge.class, 1L)).thenReturn(List.of(dto, dto2));

		assertThat(this.data.getListByIdFloskel(1L))
				.hasSize(2)
				.satisfiesExactly(
						f1 -> assertThat(f1)
								.hasFieldOrPropertyWithValue("id", 1L)
								.hasFieldOrPropertyWithValue("idFloskel", 1L)
								.hasFieldOrPropertyWithValue("idJahrgang", 111L),
						f2 -> assertThat(f2)
								.hasFieldOrPropertyWithValue("id", 2L)
								.hasFieldOrPropertyWithValue("idFloskel", 1L)
								.hasFieldOrPropertyWithValue("idJahrgang", 222L)
				);
	}

	@Test
	@DisplayName("mapAttribute | idFloskel | null")
	void mapAttribute_idFloskelIsNull() {
		final var map = new HashMap<String, Object>();
		map.put("idFloskel", null);
		map.put("idJahrgang", 1L);

		assertThatException()
				.isThrownBy(() -> this.data.add(map))
				.isInstanceOf(ApiOperationException.class)
				.withMessage("Attribut idFloskel: Der Wert null ist nicht erlaubt")
				.hasFieldOrPropertyWithValue("status", Response.Status.BAD_REQUEST);
	}

	@Test
	@DisplayName("mapAttribute | idFloskel | wrongType")
	void mapAttribute_idFloskelIswrongType() {
		assertThatException()
				.isThrownBy(() -> this.data.add(Map.of("idFloskel", "Abc", "idJahrgang", 1L)))
				.isInstanceOf(ApiOperationException.class)
				.withMessage("Attribut idFloskel: Der Wert kann nicht in einen Long umgewandelt werden")
				.hasFieldOrPropertyWithValue("status", Response.Status.BAD_REQUEST);
	}

	@Test
	@DisplayName("mapAttribute | idJahrgang | null")
	void mapAttribute_idJahrgangIsNull() {
		final var map = new HashMap<String, Object>();
		map.put("idFloskel", 1L);
		map.put("idJahrgang", null);

		assertThatException()
				.isThrownBy(() -> this.data.add(map))
				.isInstanceOf(ApiOperationException.class)
				.withMessage("Attribut idJahrgang: Der Wert null ist nicht erlaubt")
				.hasFieldOrPropertyWithValue("status", Response.Status.BAD_REQUEST);
	}

	@Test
	@DisplayName("mapAttribute | idJahrgang | wrongType")
	void mapAttribute_idJahrgangIswrongType() {
		assertThatException()
				.isThrownBy(() -> this.data.add(Map.of("idFloskel", 1L, "idJahrgang", "abc")))
				.isInstanceOf(ApiOperationException.class)
				.withMessage("Attribut idJahrgang: Der Wert kann nicht in einen Long umgewandelt werden")
				.hasFieldOrPropertyWithValue("status", Response.Status.BAD_REQUEST);
	}

	@Test
	@DisplayName("mapAttribute | idJahrgang | not found")
	void mapAttribute_idJahrgangIsNotFound() {
		when(this.conn.queryByKey(DTOJahrgang.class, 1L)).thenReturn(null);

		assertThatException()
				.isThrownBy(() -> this.data.add(Map.of("idFloskel", 1L, "idJahrgang", 1L)))
				.isInstanceOf(ApiOperationException.class)
				.withMessage("Es wurde kein Jahrgang mit der id 1 gefunden.")
				.hasFieldOrPropertyWithValue("status", Response.Status.NOT_FOUND);
	}

	@Test
	@DisplayName("mapAttribute | idFloskel & idJahrgang")
	void mapAttribute_idFloskelAndIdJahrgang() throws ApiOperationException {
		when(this.conn.queryByKey(DTOFloskelnJahrgaenge.class, 2L)).thenReturn(mock(DTOFloskelnJahrgaenge.class));
		when(this.conn.queryByKey(DTOJahrgang.class, 4L)).thenReturn(mock(DTOJahrgang.class));
		when(this.conn.transactionPersist(any())).thenReturn(true);
		when(this.conn.transactionGetNextID(DTOFloskelnJahrgaenge.class)).thenReturn(2L);

		this.data.add(Map.of("idFloskel", 3L, "idJahrgang", 4L));

		final ArgumentCaptor<DTOFloskelnJahrgaenge> captor = ArgumentCaptor.forClass(DTOFloskelnJahrgaenge.class);
		verify(this.conn, times(1)).transactionPersist(captor.capture());
		assertThat(captor.getValue())
				.hasFieldOrPropertyWithValue("ID", 2L)
				.hasFieldOrPropertyWithValue("Floskel_ID", 3L)
				.hasFieldOrPropertyWithValue("Jahrgang_ID", 4L);
	}

	@Test
	@DisplayName("mapAttribute | unknown argument")
	void mapAttribute_unknownArgument() {
		when(this.conn.queryByKey(DTOJahrgang.class, 1L)).thenReturn(mock(DTOJahrgang.class));
		final var map = new LinkedHashMap<String, Object>();
		map.put("idFloskel", 1L);
		map.put("idJahrgang", 1L);
		map.put("unknown", "unknown");

		assertThatException()
				.isThrownBy(() -> this.data.add(map))
				.isInstanceOf(ApiOperationException.class)
				.withMessage("Die Daten des Patches enthalten das unbekannte Attribut unknown.")
				.hasFieldOrPropertyWithValue("status", Response.Status.BAD_REQUEST);
	}

}
