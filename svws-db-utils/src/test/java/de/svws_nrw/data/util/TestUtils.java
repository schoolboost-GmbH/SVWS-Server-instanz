package de.svws_nrw.data.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.svws_nrw.db.utils.ApiOperationException;
import jakarta.ws.rs.core.Response;


/**
 * Diese Klasse stellt Utility Methoden zum Testen zur Verfügung
 */
public class TestUtils {

	/**
	 * Wandelt ein beliebiges Objekt (z. B. eine Map oder Liste) in einen {@link InputStream} um.
	 * Nützlich, um Methoden zu testen, die einen InputStream als Eingabe erwarten.
	 *
	 * @param input das zu konvertierende Objekt
	 * @param <T>   der Typ des Eingabeobjekts
	 *
	 * @return      ein InputStream mit der JSON-Repräsentation des Objekts oder {@code null}, falls die Serialisierung fehlschlägt
	 */
	public static <T> InputStream fromObject(final T input) throws ApiOperationException {
		try {
			return new ByteArrayInputStream(new ObjectMapper().writeValueAsString(input).getBytes(StandardCharsets.UTF_8));
		} catch (final JsonProcessingException e) {
			throw new ApiOperationException(Response.Status.BAD_REQUEST, "Objekt ist nicht serialisierbar: %s".formatted(e.getMessage()));
		}
	}

}
