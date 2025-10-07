package de.svws_nrw.data.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.svws_nrw.db.utils.ApiOperationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

/* Diese Klasse testet die Klasse TestUtils */
class TestUtilsTest {

	@Test
	@DisplayName("fromObject | list")
	void fromObjectTest_list() throws Exception {
		final var inputStream = TestUtils.fromObject(List.of("a", "b", "c"));
		assertThat(inputStream).isNotNull();

		final var json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
		assertThat(json).isEqualTo("[\"a\",\"b\",\"c\"]");
	}

	@Test
	@DisplayName("fromObject | map")
	void fromObjectTest_map() throws Exception {
		final var inputStream = TestUtils.fromObject(Map.of("x", 1, "y", 2));
		assertThat(inputStream).isNotNull();

		final var json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
		final Map<String, Integer> result = new ObjectMapper()
				.readValue(json, new TypeReference<>() { });

		assertThat(result)
				.hasSize(2)
				.containsEntry("x", 1)
				.containsEntry("y", 2);
	}

	@Test
	@DisplayName("fromObject | null")
	void fromObjectTest_null() throws IOException, ApiOperationException {
		final var inputStream = TestUtils.fromObject(null);
		assertThat(inputStream).isNotNull();
		assertThat(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8))
				.isEqualTo("null");
	}

	@Test
	@DisplayName("fromObject | non serializable | no exception")
	void testFromObject_withNonSerializableObject() throws ApiOperationException {
		final Object nonSerializable = new Object();

		assertThatException()
				.isThrownBy(() -> TestUtils.fromObject(nonSerializable))
				.isInstanceOf(ApiOperationException.class)
				.withMessageStartingWith("Objekt ist nicht serialisierbar: No serializer found for class java.lang.Object")
				.hasFieldOrPropertyWithValue("status", Response.Status.BAD_REQUEST);
	}

}
