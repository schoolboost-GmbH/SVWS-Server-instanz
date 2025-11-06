package de.svws_nrw.api.server;

import java.io.InputStream;
import java.util.List;

import de.svws_nrw.core.data.uv.LongPair;
import de.svws_nrw.core.data.uv.UvKlasse;
import de.svws_nrw.core.data.uv.UvKurs;
import de.svws_nrw.core.data.uv.UvFach;
import de.svws_nrw.core.data.uv.UvZeitraster;
import de.svws_nrw.core.data.uv.UvZeitrasterEintrag;
import de.svws_nrw.core.data.uv.UvUnterricht;
import de.svws_nrw.core.data.uv.UvLehrer;
import de.svws_nrw.core.data.uv.UvLehrerAnrechnungsstunden;
import de.svws_nrw.core.data.uv.UvLehrerPflichtstundensoll;
import de.svws_nrw.core.data.uv.UvLerngruppenLehrer;
import de.svws_nrw.core.data.uv.UvPlanungsabschnitt;
import de.svws_nrw.core.data.uv.UvSchueler;
import de.svws_nrw.core.data.uv.UvRaum;
import de.svws_nrw.core.data.uv.UvSchuelergruppe;
import de.svws_nrw.core.data.uv.UvStundentafel;
import de.svws_nrw.core.data.uv.UvStundentafelFach;
import de.svws_nrw.core.data.uv.UvSchiene;
import de.svws_nrw.core.data.uv.UvLerngruppe;
import de.svws_nrw.core.types.ServerMode;
import de.svws_nrw.core.types.benutzer.BenutzerKompetenz;
import de.svws_nrw.data.benutzer.DBBenutzerUtils;
import de.svws_nrw.data.uv.DataUvFaecher;
import de.svws_nrw.data.uv.DataUvKlassen;
import de.svws_nrw.data.uv.DataUvKurse;
import de.svws_nrw.data.uv.DataUvLehrer;
import de.svws_nrw.data.uv.DataUvLehrerAnrechnungsstunden;
import de.svws_nrw.data.uv.DataUvLehrerPflichtstundensoll;
import de.svws_nrw.data.uv.DataUvLerngruppen;
import de.svws_nrw.data.uv.DataUvLerngruppenLehrer;
import de.svws_nrw.data.uv.DataUvPlanungsabschnitte;
import de.svws_nrw.data.uv.DataUvRaeume;
import de.svws_nrw.data.uv.DataUvSchienen;
import de.svws_nrw.data.uv.DataUvSchueler;
import de.svws_nrw.data.uv.DataUvSchuelergruppen;
import de.svws_nrw.data.uv.DataUvStundentafeln;
import de.svws_nrw.data.uv.DataUvStundentafelnFaecher;
import de.svws_nrw.data.uv.DataUvUnterrichte;
import de.svws_nrw.data.uv.DataUvZeitraster;
import de.svws_nrw.data.uv.DataUvZeitrasterEintraege;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Die Klasse spezifiziert die OpenAPI-Schnittstelle für den Zugriff auf die
 * Unterrichtsverteilung aus der SVWS-Datenbank. Ein Zugriff erfolgt
 * über den Pfad https://{Hostname}/db/{schema}/unterrichtsverteilung/...
 */
@Path("/db/{schema}/unterrichtsverteilung")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Server")
public class APIUnterrichtsverteilung {

	/**
	 * Leerer Standardkonstruktor.
	 */
	public APIUnterrichtsverteilung() {
		// leer
	}

	// ============================================================
	//   PLANUNGSABSCHNITT (get -> create -> patch -> patchMultiple -> delete -> deleteMultiple)
	// ============================================================

	/**
	 * Die OpenAPI-Methode für die Abfrage der Liste aller UV-Planungsabschnitte eines Schuljahres.
	 *
	 * @param schema      das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
	 * @param schuljahr   das Schuljahr, für welches die Planungsabschnitte abgefragt werden sollen
	 * @param request     die Informationen zur HTTP-Anfrage
	 *
	 * @return die Liste mit den Planungsabschnitten
	 */
	@GET
	@Path("/planungsabschnitte/schuljahr/{schuljahr : \\d+}")
	@Operation(summary = "Gibt eine sortierte Übersicht der Stundenpläne des angegebenen Schuljahresabschnitts zurück.",
			description = "Erstellt eine Liste der Stundenpläne des angegebenen Schuljahresabschnitts. Die Stundenpläne sind anhand der Gültigkeit sortiert."
					+ "Es wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Ansehen von Stundenplanlisten besitzt.")
	@ApiResponse(responseCode = "200", description = "Eine Liste der Stundenpläne",
			content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UvPlanungsabschnitt.class))))
	@ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um Stundenplanlisten anzusehen.")
	@ApiResponse(responseCode = "404", description = "Keine Stundenpläne gefunden")
	public Response getUvPlanungsabschnitte(@PathParam("schema") final String schema, @PathParam("schuljahr") final int schuljahr,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(DataUvPlanungsabschnitte.getUvPlanungsabschnitteBySchuljahr(conn, schuljahr)).build(),
				request, ServerMode.STABLE,
				BenutzerKompetenz.STUNDENPLAN_ALLGEMEIN_ANSEHEN);
	}

	/**
	 * Erstellt einen neuen {@link UvPlanungsabschnitt} und gibt ihn zurück.
	 *
	 * @param schema     das Datenbankschema, in welchem der {@link UvPlanungsabschnitt} erstellt wird
	 * @param request    die Informationen zur HTTP-Anfrage
	 * @param is         JSON-Objekt mit den Daten
	 *
	 * @return 			 die HTTP-Antwort mit dem neuen {@link UvPlanungsabschnitt}
	 */
	@POST
	@Path("/planungsabschnitte/create")
	@Operation(summary = "Erstellt einen neuen UvPlanungsabschnitt und gibt ihn zurück.",
			description = "Erstellt einen neuen UvPlanungsabschnitt und gibt ihn zurück."
					+ "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Erstellen eines UvPlanungsabschnitts besitzt.")
	@ApiResponse(responseCode = "201", description = "UvPlanungsabschnitt wurde erfolgreich angelegt.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UvPlanungsabschnitt.class)))
	@ApiResponse(responseCode = "400", description = "Die Daten sind fehlerhaft aufgebaut.")
	@ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um einen GUvPlanungsabschnitt anzulegen.")
	@ApiResponse(responseCode = "409", description = "Der UvPlanungsabschnitt ist schon in der Datenbank enthalten.")
	@ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z.B. beim Datenbankzugriff)")
	public Response createUvPlanungsabschnitt(@PathParam("schema") final String schema,
			@RequestBody(description = "Der Post für die UvPlanungsabschnitt-Daten", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(implementation = UvPlanungsabschnitt.class))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvPlanungsabschnitte(conn).addAsResponse(is),
				request,
				ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Patcht einen bestehenden {@link UvPlanungsabschnitt} anhand der ID.
	 *
	 * @param schema     das Datenbankschema
	 * @param id         die ID des Planungsabschnitts
	 * @param is         JSON-Objekt mit den Patch-Daten
	 * @param request    die HTTP-Anfrage
	 * @return           die HTTP-Antwort
	 */
	@PATCH
	@Path("/planungsabschnitte/{id : \\d+}")
	@Operation(summary = "Patcht einen bestehenden UvPlanungsabschnitt.", description = "Patcht einen bestehenden UvPlanungsabschnitt.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvPlanungsabschnitt(@PathParam("schema") final String schema, @PathParam("id") final long id,
			@RequestBody(description = "Patch-Daten für den UvPlanungsabschnitt", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(implementation = UvPlanungsabschnitt.class))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvPlanungsabschnitte(conn).patchAsResponse(id, is), request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Die OpenAPI-Methode für das Patchen mehrerer {@link UvPlanungsabschnitt}-Einträge. Die IDs in dem Patch
	 * müssen vorhanden sein, damit die zu patchenden Daten in der DB gefunden werden können.
	 *
	 * @param schema     das Datenbankschema
	 * @param is         JSON-Objekt mit den Patch-Daten
	 * @param request    die HTTP-Anfrage
	 *
	 * @return das Ergebnis der Patch-Operation
	 */
	@PATCH
	@Path("/planungsabschnitte/patch/multiple")
	@Operation(summary = "Patcht einen bestehenden UvPlanungsabschnitt.", description = "Patcht einen bestehenden UvPlanungsabschnitt.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvPlanungsabschnitteMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Der Patch für den Zeitrastereintrag", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = UvPlanungsabschnitt.class)))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvPlanungsabschnitte(conn).patchMultipleAsResponse(is), request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Löscht einen bestehenden {@link UvPlanungsabschnitt} anhand der ID.
	 *
	 * @param schema     das Datenbankschema
	 * @param id         die ID des Planungsabschnitts
	 * @param request    die HTTP-Anfrage
	 * @return           die HTTP-Antwort
	 */
	@DELETE
	@Path("/planungsabschnitte/{id : \\d+}")
	@Operation(summary = "Löscht einen bestehenden UvPlanungsabschnitt.", description = "Löscht einen bestehenden UvPlanungsabschnitt.")
	@ApiResponse(responseCode = "204", description = "Löschung erfolgreich.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response deleteUvPlanungsabschnitt(@PathParam("schema") final String schema, @PathParam("id") final long id,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvPlanungsabschnitte(conn).deleteAsResponse(id), request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Löscht mehrere bestehende {@link UvPlanungsabschnitt}.
	 *
	 * @param schema     das Datenbankschema
	 * @param ids        die IDs der zu löschenden UvPlanungsabschnitte
	 * @param request    die HTTP-Anfrage
	 * @return           die HTTP-Antwort
	 */
	@DELETE
	@Path("/planungsabschnitte/multiple")
	@Operation(summary = "Löscht mehrere bestehende UvPlanungsabschnitte.", description = "Löscht mehrere bestehende UvPlanungsabschnitte anhand ihrer IDs.")
	@ApiResponse(responseCode = "200", description = "Die UvPlanungsabschnitte für die angegebenen IDs wurden erfolgreich gelöscht.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema(implementation = Long.class))))
	@ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um UvPlanungsabschnitte zu löschen.")
	@ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z.B. beim Datenbankzugriff)")
	public Response deleteUvPlanungsabschnitteMultiple(@PathParam("schema") final String schema,
			@RequestBody(description = "die IDs der UvPlanungsabschnitte", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
					array = @ArraySchema(schema = @Schema(implementation = Long.class)))) final List<Long> ids,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvPlanungsabschnitte(conn).deleteMultipleAsResponse(ids),
				request,
				ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	// ============================================================
	//   SCHÜLER (create -> patch -> patchMultiple -> delete -> deleteMultiple)
	// ============================================================

	/**
	 * Erstellt einen neuen {@link UvSchueler} und gibt ihn zurück.
	 *
	 * @param schema     das Datenbankschema, in welchem der {@link UvSchueler} erstellt wird
	 * @param request    die Informationen zur HTTP-Anfrage
	 * @param is         JSON-Objekt mit den Daten
	 * @return           die HTTP-Antwort mit dem neuen {@link UvSchueler}
	 */
	@POST
	@Path("/schueler/create")
	@Operation(summary = "Erstellt einen neuen UvSchueler und gibt ihn zurück.",
			description = "Erstellt einen neuen UvSchueler und gibt ihn zurück. Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Erstellen besitzt.")
	@ApiResponse(responseCode = "201", description = "UvSchueler wurde erfolgreich angelegt.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UvSchueler.class)))
	@ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um einen UvSchueler anzulegen.")
	@ApiResponse(responseCode = "409", description = "Der UvSchueler ist schon in der Datenbank enthalten.")
	public Response createUvSchueler(@PathParam("schema") final String schema,
			@Context final HttpServletRequest request,
			@RequestBody(description = "Der Post für die UvSchueler-Daten", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(implementation = UvSchueler.class))) final InputStream is) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvSchueler(conn).addAsResponse(is), request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Patcht einen bestehenden {@link UvSchueler} anhand der ID.
	 *
	 * @param schema     das Datenbankschema
	 * @param idPlanungsabschnitt  die ID des Planungsabschnitts
	 * @param idSchueler die ID des Schülers
	 * @param is         JSON-Objekt mit den Patch-Daten
	 * @param request    die HTTP-Anfrage
	 * @return           die HTTP-Antwort
	 */
	@PATCH
	@Path("/schueler/{idPlanungsabschnitt : \\d+}/{idSchueler : \\d+}")
	@Operation(summary = "Patcht einen bestehenden UvSchueler.", description = "Patcht einen bestehenden UvSchueler.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvSchueler(
			@PathParam("schema") final String schema,
			@PathParam("idPlanungsabschnitt") final long idPlanungsabschnitt,
			@PathParam("idSchueler") final long idSchueler,
			@RequestBody(description = "Patch-Daten für den UvSchueler", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema =
			@Schema(implementation = UvSchueler.class))) final InputStream is,
			@Context final HttpServletRequest request) {
		final LongPair uvSchuelerID = new LongPair(idPlanungsabschnitt, idSchueler);
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvSchueler(conn).patchAsResponse(uvSchuelerID, is), request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Die OpenAPI-Methode für das Patchen mehrerer {@link UvSchueler}-Einträge. Die IDs in dem Patch
	 * müssen vorhanden sein, damit die zu patchenden Daten in der DB gefunden werden können.
	 *
	 * @param schema     das Datenbankschema
	 * @param is         JSON-Objekt mit den Patch-Daten
	 * @param request    die HTTP-Anfrage
	 *
	 * @return das Ergebnis der Patch-Operation
	 */
	@PATCH
	@Path("/schueler/patch/multiple")
	@Operation(summary = "Patcht einen bestehenden UvSchueler.", description = "Patcht einen bestehenden UvSchueler.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvSchuelerMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Der Patch für die UvSchueler", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = UvSchueler.class)))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvSchueler(conn).patchMultipleAsResponse(is), request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Löscht einen bestehenden {@link UvSchueler} anhand der ID.
	 *
	 * @param schema     das Datenbankschema
	 * @param idPlanungsabschnitt  die ID des Planungsabschnitts
	 * @param idSchueler die ID des Schülers
	 * @param request    die HTTP-Anfrage
	 * @return           die HTTP-Antwort
	 */
	@DELETE
	@Path("/schueler/{idPlanungsabschnitt : \\d+}/{idSchueler : \\d+}")
	@Operation(summary = "Löscht einen bestehenden UvSchueler.", description = "Löscht einen bestehenden UvSchueler.")
	@ApiResponse(responseCode = "204", description = "Löschung erfolgreich.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response deleteUvSchueler(
			@PathParam("schema") final String schema,
			@PathParam("idPlanungsabschnitt") final long idPlanungsabschnitt,
			@PathParam("idSchueler") final long idSchueler,
			@Context final HttpServletRequest request) {
		final LongPair uvSchuelerID = new LongPair(idPlanungsabschnitt, idSchueler);
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvSchueler(conn).deleteAsResponse(uvSchuelerID), request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Löscht mehrere bestehende {@link UvSchueler}.
	 *
	 * @param schema     das Datenbankschema
	 * @param ids        die IDs der zu löschenden UvSchueler
	 * @param request    die HTTP-Anfrage
	 * @return           die HTTP-Antwort
	 */
	@DELETE
	@Path("/schueler/multiple")
	@Operation(summary = "Löscht mehrere bestehende UvSchueler.", description = "Löscht mehrere bestehende UvSchueler anhand ihrer IDs.")
	@ApiResponse(responseCode = "200", description = "Die UvSchueler für die angegebenen IDs wurden erfolgreich gelöscht.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema(implementation = Long.class))))
	@ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um UvSchueler zu löschen.")
	@ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z.B. beim Datenbankzugriff)")
	public Response deleteUvSchuelerMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "die IDs der UvSchueler", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
					array = @ArraySchema(schema = @Schema(implementation = LongPair.class)))) final List<LongPair> ids,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvSchueler(conn).deleteMultipleAsResponse(ids),
				request,
				ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	// ============================================================
	//   SCHÜLERGRUPPE (create -> patch -> patchMultiple -> delete -> deleteMultiple)
	// ============================================================

	/**
	 * Erstellt eine neue {@link UvSchuelergruppe} und gibt sie zurück.
	 *
	 * @param schema     das Datenbankschema, in welchem die {@link UvSchuelergruppe} erstellt wird
	 * @param request    die Informationen zur HTTP-Anfrage
	 * @param is         JSON-Objekt mit den Daten
	 * @return           die HTTP-Antwort mit der neuen {@link UvSchuelergruppe}
	 */
	@POST
	@Path("/schueler/gruppen/create")
	@Operation(summary = "Erstellt eine neue UvSchuelergruppe und gibt sie zurück.",
			description = "Erstellt eine neue UvSchuelergruppe und gibt sie zurück. Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Erstellen besitzt.")
	@ApiResponse(responseCode = "201", description = "UvSchuelergruppe wurde erfolgreich angelegt.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UvSchuelergruppe.class)))
	@ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um eine UvSchuelergruppe anzulegen.")
	@ApiResponse(responseCode = "409", description = "Die UvSchuelergruppe ist schon in der Datenbank enthalten.")
	public Response createUvSchuelergruppe(@PathParam("schema") final String schema,
			@Context final HttpServletRequest request,
			@RequestBody(description = "Der Post für die UvSchuelergruppe-Daten", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(implementation = UvSchuelergruppe.class))) final InputStream is) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvSchuelergruppen(conn).addAsResponse(is), request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Patcht eine bestehende {@link UvSchuelergruppe} anhand der ID.
	 *
	 * @param schema     das Datenbankschema
	 * @param id         die ID der Schülergruppe
	 * @param is         JSON-Objekt mit den Patch-Daten
	 * @param request    die HTTP-Anfrage
	 * @return           die HTTP-Antwort
	 */
	@PATCH
	@Path("/schueler/gruppen/{id : \\d+}")
	@Operation(summary = "Patcht eine bestehende UvSchuelergruppe.", description = "Patcht eine bestehende UvSchuelergruppe.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvSchuelergruppe(@PathParam("schema") final String schema, @PathParam("id") final long id,
			@RequestBody(description = "Patch-Daten für die UvSchuelergruppe", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(implementation = UvSchuelergruppe.class))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvSchuelergruppen(conn).patchAsResponse(id, is), request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Die OpenAPI-Methode für das Patchen mehrerer {@link UvSchuelergruppe}-Einträge. Die IDs in dem Patch
	 * müssen vorhanden sein, damit die zu patchenden Daten in der DB gefunden werden können.
	 *
	 * @param schema     das Datenbankschema
	 * @param is         JSON-Objekt mit den Patch-Daten
	 * @param request    die HTTP-Anfrage
	 *
	 * @return das Ergebnis der Patch-Operation
	 */
	@PATCH
	@Path("/schueler/gruppen/patch/multiple")
	@Operation(summary = "Patcht einen bestehenden UvSchuelergruppe.", description = "Patcht einen bestehenden UvSchuelergruppe.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvSchuelergruppenMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Der Patch für die UvSchuelergruppen", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = UvSchuelergruppe.class)))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvSchuelergruppen(conn).patchMultipleAsResponse(is), request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Löscht eine bestehende {@link UvSchuelergruppe} anhand der ID.
	 *
	 * @param schema     das Datenbankschema
	 * @param id         die ID der Schülergruppe
	 * @param request    die HTTP-Anfrage
	 * @return           die HTTP-Antwort
	 */
	@DELETE
	@Path("/schueler/gruppen/{id : \\d+}")
	@Operation(summary = "Löscht eine bestehende UvSchuelergruppe.", description = "Löscht eine bestehende UvSchuelergruppe.")
	@ApiResponse(responseCode = "204", description = "Löschung erfolgreich.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response deleteUvSchuelergruppe(@PathParam("schema") final String schema, @PathParam("id") final long id,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvSchuelergruppen(conn).deleteAsResponse(id), request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Löscht mehrere bestehende {@link UvSchuelergruppe}.
	 *
	 * @param schema     das Datenbankschema
	 * @param ids        die IDs der zu löschenden UvSchuelergruppen
	 * @param request    die HTTP-Anfrage
	 * @return           die HTTP-Antwort
	 */
	@DELETE
	@Path("/schueler/gruppen/multiple")
	@Operation(summary = "Löscht mehrere bestehende UvSchuelergruppen.", description = "Löscht mehrere bestehende UvSchuelergruppen anhand ihrer IDs.")
	@ApiResponse(responseCode = "200", description = "Die UvSchuelergruppen für die angegebenen IDs wurden erfolgreich gelöscht.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema(implementation = Long.class))))
	@ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um UvSchuelergruppen zu löschen.")
	@ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z.B. beim Datenbankzugriff)")
	public Response deleteUvSchuelergruppenMultiple(@PathParam("schema") final String schema,
			@RequestBody(description = "die IDs der UvSchuelergruppen", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
					array = @ArraySchema(schema = @Schema(implementation = Long.class)))) final List<Long> ids,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvSchuelergruppen(conn).deleteMultipleAsResponse(ids),
				request,
				ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	// ============================================================
	//   KLASSEN (create -> patch -> patchMultiple -> delete -> deleteMultiple)
	// ============================================================

	/**
	 * Erstellt eine neue {@link UvKlasse} und gibt sie zurück.
	 *
	 * @param schema     das Datenbankschema, in welchem die {@link UvKlasse} erstellt wird
	 * @param request    die Informationen zur HTTP-Anfrage
	 * @param is         JSON-Objekt mit den Daten
	 * @return           die HTTP-Antwort mit der neuen {@link UvKlasse}
	 */
	@POST
	@Path("/klassen/create")
	@Operation(summary = "Erstellt eine neue UvKlasse und gibt sie zurück.",
			description = "Erstellt eine neue UvKlasse und gibt sie zurück. Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung besitzt.")
	@ApiResponse(responseCode = "201", description = "UvKlasse wurde erfolgreich angelegt.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UvKlasse.class)))
	@ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um eine UvKlasse anzulegen.")
	@ApiResponse(responseCode = "409", description = "Die UvKlasse ist schon in der Datenbank enthalten.")
	public Response createUvKlasse(@PathParam("schema") final String schema,
			@Context final HttpServletRequest request,
			@RequestBody(description = "Der Post für die UvKlasse-Daten", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(implementation = UvKlasse.class))) final InputStream is) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvKlassen(conn).addAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Patcht eine bestehende {@link UvKlasse} anhand der ID.
	 *
	 * @param schema     das Datenbankschema
	 * @param id         die ID der Klasse
	 * @param is         JSON-Objekt mit den Patch-Daten
	 * @param request    die HTTP-Anfrage
	 * @return           die HTTP-Antwort
	 */
	@PATCH
	@Path("/klassen/{id : \\d+}")
	@Operation(summary = "Patcht eine bestehende UvKlasse.", description = "Patcht eine bestehende UvKlasse anhand der ID.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvKlasse(@PathParam("schema") final String schema, @PathParam("id") final long id,
			@RequestBody(description = "Patch-Daten für die UvKlasse", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(implementation = UvKlasse.class))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvKlassen(conn).patchAsResponse(id, is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Die OpenAPI-Methode für das Patchen mehrerer {@link UvKlasse}-Einträge. Die IDs in dem Patch
	 * müssen vorhanden sein, damit die zu patchenden Daten in der DB gefunden werden können.
	 *
	 * @param schema     das Datenbankschema
	 * @param is         JSON-Objekt mit den Patch-Daten
	 * @param request    die HTTP-Anfrage
	 *
	 * @return das Ergebnis der Patch-Operation
	 */
	@PATCH
	@Path("/klassen/patch/multiple")
	@Operation(summary = "Patcht einen bestehenden UvKlasse.", description = "Patcht einen bestehenden UvKlasse.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvKlassenMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Der Patch für die UvKlassen", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = UvKlasse.class)))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvKlassen(conn).patchMultipleAsResponse(is), request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Löscht eine bestehende {@link UvKlasse} anhand der ID.
	 *
	 * @param schema     das Datenbankschema
	 * @param id         die ID der Klasse
	 * @param request    die HTTP-Anfrage
	 * @return           die HTTP-Antwort
	 */
	@DELETE
	@Path("/klassen/{id : \\d+}")
	@Operation(summary = "Löscht eine bestehende UvKlasse.", description = "Löscht eine bestehende UvKlasse anhand der ID.")
	@ApiResponse(responseCode = "204", description = "Löschung erfolgreich.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response deleteUvKlasse(@PathParam("schema") final String schema, @PathParam("id") final long id,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvKlassen(conn).deleteAsResponse(id),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Löscht mehrere bestehende {@link UvKlasse}.
	 *
	 * @param schema     das Datenbankschema
	 * @param ids        die IDs der zu löschenden UvKlassen
	 * @param request    die HTTP-Anfrage
	 * @return           die HTTP-Antwort
	 */
	@DELETE
	@Path("/klassen/multiple")
	@Operation(summary = "Löscht mehrere bestehende UvKlassen.", description = "Löscht mehrere bestehende UvKlassen anhand ihrer IDs.")
	@ApiResponse(responseCode = "200", description = "Die UvKlassen für die angegebenen IDs wurden erfolgreich gelöscht.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema(implementation = Long.class))))
	@ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um UvKlassen zu löschen.")
	@ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z.B. beim Datenbankzugriff)")
	public Response deleteUvKlassenMultiple(@PathParam("schema") final String schema,
			@RequestBody(description = "die IDs der UvKlassen", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
					array = @ArraySchema(schema = @Schema(implementation = Long.class)))) final List<Long> ids,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvKlassen(conn).deleteMultipleAsResponse(ids),
				request,
				ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	// ============================================================
	//   KURSE (create -> patch -> patchMultiple -> delete -> deleteMultiple)
	// ============================================================

	/**
	 * Erstellt einen neuen {@link UvKurs} und gibt ihn zurück.
	 *
	 * @param schema     das Datenbankschema, in welchem der {@link UvKurs} erstellt wird
	 * @param request    die Informationen zur HTTP-Anfrage
	 * @param is         JSON-Objekt mit den Daten
	 * @return           die HTTP-Antwort mit dem neuen {@link UvKurs}
	 */
	@POST
	@Path("/kurse/create")
	@Operation(summary = "Erstellt einen neuen UvKurs und gibt ihn zurück.",
			description = "Erstellt einen neuen UvKurs und gibt ihn zurück. Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung besitzt.")
	@ApiResponse(responseCode = "201", description = "UvKurs wurde erfolgreich angelegt.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UvKurs.class)))
	@ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um einen UvKurs anzulegen.")
	@ApiResponse(responseCode = "409", description = "Der UvKurs ist schon in der Datenbank enthalten.")
	public Response createUvKurs(@PathParam("schema") final String schema,
			@Context final HttpServletRequest request,
			@RequestBody(description = "Der Post für die UvKurs-Daten", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(implementation = UvKurs.class))) final InputStream is) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvKurse(conn).addAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Patcht einen bestehenden {@link UvKurs} anhand der ID.
	 *
	 * @param schema     das Datenbankschema
	 * @param id         die ID des Kurses
	 * @param is         JSON-Objekt mit den Patch-Daten
	 * @param request    die HTTP-Anfrage
	 * @return           die HTTP-Antwort
	 */
	@PATCH
	@Path("/kurse/{id : \\d+}")
	@Operation(summary = "Patcht einen bestehenden UvKurs.", description = "Patcht einen bestehenden UvKurs anhand der ID.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvKurs(@PathParam("schema") final String schema, @PathParam("id") final long id,
			@RequestBody(description = "Patch-Daten für den UvKurs", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(implementation = UvKurs.class))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvKurse(conn).patchAsResponse(id, is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Die OpenAPI-Methode für das Patchen mehrerer {@link UvKurs}-Einträge. Die IDs in dem Patch
	 * müssen vorhanden sein, damit die zu patchenden Daten in der DB gefunden werden können.
	 *
	 * @param schema     das Datenbankschema
	 * @param is         JSON-Objekt mit den Patch-Daten
	 * @param request    die HTTP-Anfrage
	 *
	 * @return das Ergebnis der Patch-Operation
	 */
	@PATCH
	@Path("/kurse/patch/multiple")
	@Operation(summary = "Patcht einen bestehenden UvKurs.", description = "Patcht einen bestehenden UvKurs.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvKurseMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Der Patch für die UvKurse", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = UvKurs.class)))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvKurse(conn).patchMultipleAsResponse(is), request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Löscht einen bestehenden {@link UvKurs} anhand der ID.
	 *
	 * @param schema     das Datenbankschema
	 * @param id         die ID des Kurses
	 * @param request    die HTTP-Anfrage
	 * @return           die HTTP-Antwort
	 */
	@DELETE
	@Path("/kurse/{id : \\d+}")
	@Operation(summary = "Löscht einen bestehenden UvKurs.", description = "Löscht einen bestehenden UvKurs anhand der ID.")
	@ApiResponse(responseCode = "204", description = "Löschung erfolgreich.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response deleteUvKurs(@PathParam("schema") final String schema, @PathParam("id") final long id,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvKurse(conn).deleteAsResponse(id),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Löscht mehrere bestehende {@link UvKurs} anhand der ID.
	 *
	 * @param schema     das Datenbankschema
	 * @param kursIds    die IDs der zu löschenden UV-Kurse
	 * @param request    die HTTP-Anfrage
	 * @return           die HTTP-Antwort
	 */
	@DELETE
	@Path("/kurse/multiple")
	@Operation(summary = "Löscht mehrere bestehende UV-Kurse.", description = "Löscht mehrere bestehende UV-Kurse anhand der ID.")
	@ApiResponse(responseCode = "200", description = "Die UV-Kurse für die angegebenen IDs wurden erfolgreich gelöscht.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema(implementation = Long.class))))
	@ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um einen UV-Kurs zu löschen.")
	@ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z.B. beim Datenbankzugriff)")
	public Response deleteUvKurseMultiple(@PathParam("schema") final String schema,
			@RequestBody(description = "die IDs der UV-Kurse", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
					array = @ArraySchema(schema = @Schema(implementation = Long.class)))) final List<Long> kursIds,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvKurse(conn).deleteMultipleAsResponse(kursIds),
				request,
				ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	// ============================================================
	//   STUNDENTAFEL (create -> patch -> patchMultiple -> delete -> deleteMultiple)
	// ============================================================

	/**
	 * Erstellt eine neue {@link UvStundentafel} und gibt sie zurück.
	 *
	 * @param schema     das Datenbankschema, in welchem die {@link UvStundentafel} erstellt wird
	 * @param request    die Informationen zur HTTP-Anfrage
	 * @param is         JSON-Objekt mit den Daten
	 * @return           die HTTP-Antwort mit der neuen {@link UvStundentafel}
	 */
	@POST
	@Path("/stundentafeln/create")
	@Operation(summary = "Erstellt eine neue UvStundentafel und gibt sie zurück.",
			description = "Erstellt eine neue UvStundentafel und gibt sie zurück. Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Erstellen besitzt.")
	@ApiResponse(responseCode = "201", description = "UvStundentafel wurde erfolgreich angelegt.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UvStundentafel.class)))
	@ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um eine UvStundentafel anzulegen.")
	@ApiResponse(responseCode = "409", description = "Die UvStundentafel ist schon in der Datenbank enthalten.")
	public Response createUvStundentafel(@PathParam("schema") final String schema,
			@Context final HttpServletRequest request,
			@RequestBody(description = "Der Post für die UvStundentafel-Daten", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(implementation = UvStundentafel.class))) final InputStream is) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvStundentafeln(conn).addAsResponse(is), request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Patcht eine bestehende {@link UvStundentafel} anhand der ID.
	 *
	 * @param schema     das Datenbankschema
	 * @param id         die ID der Stundentafel
	 * @param is         JSON-Objekt mit den Patch-Daten
	 * @param request    die HTTP-Anfrage
	 * @return           die HTTP-Antwort
	 */
	@PATCH
	@Path("/stundentafeln/{id : \\d+}")
	@Operation(summary = "Patcht eine bestehende UvStundentafel.", description = "Patcht eine bestehende UvStundentafel.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvStundentafel(@PathParam("schema") final String schema, @PathParam("id") final long id,
			@RequestBody(description = "Patch-Daten für die UvStundentafel", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(implementation = UvStundentafel.class))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvStundentafeln(conn).patchAsResponse(id, is), request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Die OpenAPI-Methode für das Patchen mehrerer {@link UvStundentafel}-Einträge. Die IDs in dem Patch
	 * müssen vorhanden sein, damit die zu patchenden Daten in der DB gefunden werden können.
	 *
	 * @param schema     das Datenbankschema
	 * @param is         JSON-Objekt mit den Patch-Daten
	 * @param request    die HTTP-Anfrage
	 *
	 * @return das Ergebnis der Patch-Operation
	 */
	@PATCH
	@Path("/stundentafeln/patch/multiple")
	@Operation(summary = "Patcht einen bestehenden UvStundentafel.", description = "Patcht einen bestehenden UvStundentafel.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvStundentafelnMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Der Patch für die UvStundentafeln", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = UvStundentafel.class)))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvStundentafeln(conn).patchMultipleAsResponse(is), request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Löscht eine bestehende {@link UvStundentafel} anhand der ID.
	 *
	 * @param schema     das Datenbankschema
	 * @param id         die ID der Stundentafel
	 * @param request    die HTTP-Anfrage
	 * @return           die HTTP-Antwort
	 */
	@DELETE
	@Path("/stundentafeln/{id : \\d+}")
	@Operation(summary = "Löscht eine bestehende UvStundentafel.", description = "Löscht eine bestehende UvStundentafel.")
	@ApiResponse(responseCode = "204", description = "Löschung erfolgreich.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response deleteUvStundentafel(@PathParam("schema") final String schema, @PathParam("id") final long id,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvStundentafeln(conn).deleteAsResponse(id), request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Löscht mehrere bestehende {@link UvStundentafel}.
	 *
	 * @param schema     das Datenbankschema
	 * @param ids        die IDs der zu löschenden UvStundentafeln
	 * @param request    die HTTP-Anfrage
	 * @return           die HTTP-Antwort
	 */
	@DELETE
	@Path("/stundentafeln/multiple")
	@Operation(summary = "Löscht mehrere bestehende UvStundentafeln.", description = "Löscht mehrere bestehende UvStundentafeln anhand ihrer IDs.")
	@ApiResponse(responseCode = "200", description = "Die UvStundentafeln für die angegebenen IDs wurden erfolgreich gelöscht.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema(implementation = Long.class))))
	@ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um UvStundentafeln zu löschen.")
	@ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z.B. beim Datenbankzugriff)")
	public Response deleteUvStundentafelnMultiple(@PathParam("schema") final String schema,
			@RequestBody(description = "die IDs der UvStundentafeln", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
					array = @ArraySchema(schema = @Schema(implementation = Long.class)))) final List<Long> ids,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvStundentafeln(conn).deleteMultipleAsResponse(ids),
				request,
				ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	// ============================================================
	//   FACH (create -> patch -> patchMultiple -> delete -> deleteMultiple)
	// ============================================================

	/**
	 * Erstellt ein neues {@link UvFach} und gibt es zurück.
	 *
	 * @param schema     das Datenbankschema, in welchem das {@link UvFach} erstellt wird
	 * @param request    die Informationen zur HTTP-Anfrage
	 * @param is         JSON-Objekt mit den Daten
	 *
	 * @return           die HTTP-Antwort mit dem neuen {@link UvFach}
	 */
	@POST
	@Path("/faecher/create")
	@Operation(summary = "Erstellt ein neues UvFach und gibt es zurück.",
			description = "Erstellt ein neues UvFach und gibt es zurück. Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung besitzt.")
	@ApiResponse(responseCode = "201", description = "UvFach wurde erfolgreich angelegt.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UvFach.class)))
	@ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um ein UvFach anzulegen.")
	@ApiResponse(responseCode = "409", description = "Das UvFach ist schon in der Datenbank enthalten.")
	public Response createUvFach(@PathParam("schema") final String schema,
			@Context final HttpServletRequest request,
			@RequestBody(description = "Der Post für die UvFach-Daten", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UvFach.class))) final InputStream is) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvFaecher(conn).addAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Patcht ein bestehendes {@link UvFach} anhand der ID.
	 *
	 * @param schema     das Datenbankschema
	 * @param id         die ID des Faches
	 * @param is         JSON-Objekt mit den Patch-Daten
	 * @param request    die HTTP-Anfrage
	 *
	 * @return           die HTTP-Antwort
	 */
	@PATCH
	@Path("/faecher/{id : \\d+}")
	@Operation(summary = "Patcht ein bestehendes UvFach.", description = "Patcht ein bestehendes UvFach anhand der ID.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvFach(@PathParam("schema") final String schema, @PathParam("id") final long id,
			@RequestBody(description = "Patch-Daten für das UvFach", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UvFach.class))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvFaecher(conn).patchAsResponse(id, is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Die OpenAPI-Methode für das Patchen mehrerer {@link UvFach}-Einträge.
	 * Die IDs in dem Patch müssen vorhanden sein, damit die zu patchenden Daten in der DB gefunden werden können.
	 *
	 * @param schema     das Datenbankschema
	 * @param is         JSON-Objekt mit den Patch-Daten
	 * @param request    die HTTP-Anfrage
	 *
	 * @return           das Ergebnis der Patch-Operation
	 */
	@PATCH
	@Path("/faecher/patch/multiple")
	@Operation(summary = "Patcht mehrere bestehende UvFach-Einträge.",
			description = "Patcht mehrere bestehende UvFach-Einträge. Die IDs müssen vorhanden sein, damit die entsprechenden Datensätze gefunden werden können.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvFaecherMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Patch-Daten für mehrere UvFach-Einträge", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = UvFach.class)))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvFaecher(conn).patchMultipleAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Löscht ein bestehendes {@link UvFach} anhand der ID.
	 *
	 * @param schema     das Datenbankschema
	 * @param id         die ID des Faches
	 * @param request    die HTTP-Anfrage
	 *
	 * @return           die HTTP-Antwort
	 */
	@DELETE
	@Path("/faecher/{id : \\d+}")
	@Operation(summary = "Löscht ein bestehendes UvFach.", description = "Löscht ein bestehendes UvFach anhand der ID.")
	@ApiResponse(responseCode = "204", description = "Löschung erfolgreich.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response deleteUvFach(@PathParam("schema") final String schema, @PathParam("id") final long id,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvFaecher(conn).deleteAsResponse(id),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	/**
	 * Löscht mehrere bestehende {@link UvFach}-Einträge anhand ihrer IDs.
	 *
	 * @param schema     das Datenbankschema
	 * @param fachIds    die IDs der zu löschenden UV-Fächer
	 * @param request    die HTTP-Anfrage
	 *
	 * @return           die HTTP-Antwort
	 */
	@DELETE
	@Path("/faecher/delete/multiple")
	@Operation(summary = "Löscht mehrere bestehende UV-Fächer.",
			description = "Löscht mehrere bestehende UV-Fächer anhand ihrer IDs.")
	@ApiResponse(responseCode = "200", description = "Die UV-Fächer für die angegebenen IDs wurden erfolgreich gelöscht.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					array = @ArraySchema(schema = @Schema(implementation = Long.class))))
	@ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um UV-Fächer zu löschen.")
	@ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z.B. beim Datenbankzugriff).")
	public Response deleteUvFaecherMultiple(@PathParam("schema") final String schema,
			@RequestBody(description = "Die IDs der zu löschenden UV-Fächer", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = Long.class)))) final List<Long> fachIds,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn -> new DataUvFaecher(conn).deleteMultipleAsResponse(fachIds),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	// ============================================================
	//   STUNDENTAFEL-FACH (create -> patch -> patchMultiple -> delete -> deleteMultiple)
	// ============================================================

	/**
	 * Erstellt ein neues {@link UvStundentafelFach} und gibt es zurück.
	 *
	 * @param schema   das Datenbankschema, in welchem das {@link UvStundentafelFach} erstellt wird
	 * @param request  die Informationen zur HTTP-Anfrage
	 * @param is       JSON-Objekt mit den Daten
	 *
	 * @return die HTTP-Antwort mit dem neuen {@link UvStundentafelFach}
	 */
	@POST
	@Path("/stundentafeln/faecher/create")
	@Operation(summary = "Erstellt ein neues UvStundentafelFach und gibt es zurück.",
			description = "Erstellt ein neues UvStundentafelFach und gibt es zurück. "
					+ "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Erstellen besitzt.")
	@ApiResponse(responseCode = "201", description = "UvStundentafelFach wurde erfolgreich angelegt.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UvStundentafelFach.class)))
	@ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um ein UvStundentafelFach anzulegen.")
	@ApiResponse(responseCode = "409", description = "Das UvStundentafelFach ist schon in der Datenbank enthalten.")
	public Response createUvStundentafelFach(
			@PathParam("schema") final String schema,
			@Context final HttpServletRequest request,
			@RequestBody(description = "Der Post für die UvStundentafelFach-Daten", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							schema = @Schema(implementation = UvStundentafelFach.class))) final InputStream is) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvStundentafelnFaecher(conn).addAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Patcht ein bestehendes {@link UvStundentafelFach} anhand der ID.
	 *
	 * @param schema   das Datenbankschema
	 * @param id       die ID des Stundentafel-Fachs
	 * @param is       JSON-Objekt mit den Patch-Daten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@PATCH
	@Path("/stundentafeln/faecher/{id : \\d+}")
	@Operation(summary = "Patcht ein bestehendes UvStundentafelFach.",
			description = "Patcht ein bestehendes UvStundentafelFach anhand der ID.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvStundentafelFach(
			@PathParam("schema") final String schema,
			@PathParam("id") final long id,
			@RequestBody(description = "Patch-Daten für das UvStundentafelFach", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							schema = @Schema(implementation = UvStundentafelFach.class))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvStundentafelnFaecher(conn).patchAsResponse(id, is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Die OpenAPI-Methode für das Patchen mehrerer {@link UvStundentafelFach}-Einträge.
	 * Die IDs in dem Patch müssen vorhanden sein, damit die zu patchenden Daten gefunden werden können.
	 *
	 * @param schema   das Datenbankschema
	 * @param is       JSON-Objekt mit den Patch-Daten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return das Ergebnis der Patch-Operation
	 */
	@PATCH
	@Path("/stundentafeln/faecher/patch/multiple")
	@Operation(summary = "Patcht mehrere bestehende UvStundentafelFach-Einträge.",
			description = "Patcht mehrere bestehende UvStundentafelFach-Einträge anhand der IDs.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvStundentafelFaecherMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Der Patch für die UvStundentafelFach-Einträge", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = UvStundentafelFach.class)))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvStundentafelnFaecher(conn).patchMultipleAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Löscht ein bestehendes {@link UvStundentafelFach} anhand der ID.
	 *
	 * @param schema   das Datenbankschema
	 * @param id       die ID des Stundentafel-Fachs
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@DELETE
	@Path("/stundentafeln/faecher/{id : \\d+}")
	@Operation(summary = "Löscht ein bestehendes UvStundentafelFach.",
			description = "Löscht ein bestehendes UvStundentafelFach anhand der ID.")
	@ApiResponse(responseCode = "204", description = "Löschung erfolgreich.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response deleteUvStundentafelFach(
			@PathParam("schema") final String schema,
			@PathParam("id") final long id,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvStundentafelnFaecher(conn).deleteAsResponse(id),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Löscht mehrere bestehende {@link UvStundentafelFach}-Einträge anhand der IDs.
	 *
	 * @param schema   das Datenbankschema
	 * @param ids      die IDs der zu löschenden Stundentafel-Fächer
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@DELETE
	@Path("/stundentafeln/faecher/delete/multiple")
	@Operation(summary = "Löscht mehrere bestehende UvStundentafelFach-Einträge.",
			description = "Löscht mehrere bestehende UvStundentafelFach-Einträge anhand der IDs.")
	@ApiResponse(responseCode = "200", description = "Die Einträge wurden erfolgreich gelöscht.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					array = @ArraySchema(schema = @Schema(implementation = Long.class))))
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z. B. beim Datenbankzugriff)")
	public Response deleteUvStundentafelFaecherMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Die IDs der zu löschenden UV-Stundentafel-Fächer", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = Long.class)))) final List<Long> ids,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvStundentafelnFaecher(conn).deleteMultipleAsResponse(ids),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	// ============================================================
	//   SCHIENE (create -> patch -> patchMultiple -> delete -> deleteMultiple)
	// ============================================================

	/**
	 * Erstellt eine neue {@link UvSchiene} und gibt sie zurück.
	 *
	 * @param schema   das Datenbankschema, in welchem die {@link UvSchiene} erstellt wird
	 * @param request  die Informationen zur HTTP-Anfrage
	 * @param is       JSON-Objekt mit den Daten
	 *
	 * @return die HTTP-Antwort mit der neuen {@link UvSchiene}
	 */
	@POST
	@Path("/schienen/create")
	@Operation(summary = "Erstellt eine neue UvSchiene und gibt sie zurück.",
			description = "Erstellt eine neue UvSchiene und gibt sie zurück. "
					+ "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung besitzt.")
	@ApiResponse(responseCode = "201", description = "UvSchiene wurde erfolgreich angelegt.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(implementation = UvSchiene.class)))
	@ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um eine UvSchiene anzulegen.")
	@ApiResponse(responseCode = "409", description = "Die UvSchiene ist schon in der Datenbank enthalten.")
	public Response createUvSchiene(
			@PathParam("schema") final String schema,
			@Context final HttpServletRequest request,
			@RequestBody(description = "Der Post für die UvSchiene-Daten", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							schema = @Schema(implementation = UvSchiene.class))) final InputStream is) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvSchienen(conn).addAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Patcht eine bestehende {@link UvSchiene} anhand der ID.
	 *
	 * @param schema   das Datenbankschema
	 * @param id       die ID der Schiene
	 * @param is       JSON-Objekt mit den Patch-Daten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@PATCH
	@Path("/schienen/{id : \\d+}")
	@Operation(summary = "Patcht eine bestehende UvSchiene.",
			description = "Patcht eine bestehende UvSchiene anhand der ID.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvSchiene(
			@PathParam("schema") final String schema,
			@PathParam("id") final long id,
			@RequestBody(description = "Patch-Daten für die UvSchiene", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							schema = @Schema(implementation = UvSchiene.class))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvSchienen(conn).patchAsResponse(id, is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Die OpenAPI-Methode für das Patchen mehrerer {@link UvSchiene}-Einträge.
	 * Die IDs in dem Patch müssen vorhanden sein, damit die zu patchenden Daten gefunden werden können.
	 *
	 * @param schema   das Datenbankschema
	 * @param is       JSON-Objekt mit den Patch-Daten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return das Ergebnis der Patch-Operation
	 */
	@PATCH
	@Path("/schienen/patch/multiple")
	@Operation(summary = "Patcht mehrere bestehende UvSchienen.",
			description = "Patcht mehrere bestehende UvSchienen anhand der IDs.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvSchienenMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Der Patch für die UvSchienen", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = UvSchiene.class)))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvSchienen(conn).patchMultipleAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Löscht eine bestehende {@link UvSchiene} anhand der ID.
	 *
	 * @param schema   das Datenbankschema
	 * @param id       die ID der Schiene
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@DELETE
	@Path("/schienen/{id : \\d+}")
	@Operation(summary = "Löscht eine bestehende UvSchiene.",
			description = "Löscht eine bestehende UvSchiene anhand der ID.")
	@ApiResponse(responseCode = "204", description = "Löschung erfolgreich.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response deleteUvSchiene(
			@PathParam("schema") final String schema,
			@PathParam("id") final long id,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvSchienen(conn).deleteAsResponse(id),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Löscht mehrere bestehende {@link UvSchiene}-Einträge anhand der IDs.
	 *
	 * @param schema   das Datenbankschema
	 * @param ids      die IDs der zu löschenden Schienen
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@DELETE
	@Path("/schienen/delete/multiple")
	@Operation(summary = "Löscht mehrere bestehende UvSchienen.",
			description = "Löscht mehrere bestehende UvSchienen anhand der IDs.")
	@ApiResponse(responseCode = "200", description = "Die UvSchienen wurden erfolgreich gelöscht.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					array = @ArraySchema(schema = @Schema(implementation = Long.class))))
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z. B. beim Datenbankzugriff)")
	public Response deleteUvSchienenMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Die IDs der zu löschenden Schienen", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = Long.class)))) final List<Long> ids,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvSchienen(conn).deleteMultipleAsResponse(ids),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	// ============================================================
	//   LERNGRUPPE (create -> patch -> patchMultiple -> delete -> deleteMultiple)
	// ============================================================

	/**
	 * Erstellt eine neue {@link UvLerngruppe} und gibt sie zurück.
	 *
	 * @param schema   das Datenbankschema, in welchem die {@link UvLerngruppe} erstellt wird
	 * @param request  die Informationen zur HTTP-Anfrage
	 * @param is       JSON-Objekt mit den Daten
	 *
	 * @return die HTTP-Antwort mit der neuen {@link UvLerngruppe}
	 */
	@POST
	@Path("/lerngruppen/create")
	@Operation(summary = "Erstellt eine neue UvLerngruppe und gibt sie zurück.",
			description = "Erstellt eine neue UvLerngruppe und gibt sie zurück. "
					+ "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung besitzt.")
	@ApiResponse(responseCode = "201", description = "UvLerngruppe wurde erfolgreich angelegt.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(implementation = UvLerngruppe.class)))
	@ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um eine UvLerngruppe anzulegen.")
	@ApiResponse(responseCode = "409", description = "Die UvLerngruppe ist schon in der Datenbank enthalten.")
	public Response createUvLerngruppe(
			@PathParam("schema") final String schema,
			@Context final HttpServletRequest request,
			@RequestBody(description = "Der Post für die UvLerngruppe-Daten", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							schema = @Schema(implementation = UvLerngruppe.class))) final InputStream is) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLerngruppen(conn).addAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Patcht eine bestehende {@link UvLerngruppe} anhand der ID.
	 *
	 * @param schema   das Datenbankschema
	 * @param id       die ID der Lerngruppe
	 * @param is       JSON-Objekt mit den Patch-Daten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@PATCH
	@Path("/lerngruppen/{id : \\d+}")
	@Operation(summary = "Patcht eine bestehende UvLerngruppe.",
			description = "Patcht eine bestehende UvLerngruppe anhand der ID.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvLerngruppe(
			@PathParam("schema") final String schema,
			@PathParam("id") final long id,
			@RequestBody(description = "Patch-Daten für die UvLerngruppe", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							schema = @Schema(implementation = UvLerngruppe.class))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLerngruppen(conn).patchAsResponse(id, is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Die OpenAPI-Methode für das Patchen mehrerer {@link UvLerngruppe}-Einträge.
	 * Die IDs in dem Patch müssen vorhanden sein, damit die zu patchenden Daten gefunden werden können.
	 *
	 * @param schema   das Datenbankschema
	 * @param is       JSON-Objekt mit den Patch-Daten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return das Ergebnis der Patch-Operation
	 */
	@PATCH
	@Path("/lerngruppen/patch/multiple")
	@Operation(summary = "Patcht mehrere bestehende UvLerngruppen.",
			description = "Patcht mehrere bestehende UvLerngruppen anhand der IDs.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvLerngruppenMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Der Patch für die UvLerngruppen", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = UvLerngruppe.class)))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLerngruppen(conn).patchMultipleAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Löscht eine bestehende {@link UvLerngruppe} anhand der ID.
	 *
	 * @param schema   das Datenbankschema
	 * @param id       die ID der Lerngruppe
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@DELETE
	@Path("/lerngruppen/{id : \\d+}")
	@Operation(summary = "Löscht eine bestehende UvLerngruppe.",
			description = "Löscht eine bestehende UvLerngruppe anhand der ID.")
	@ApiResponse(responseCode = "204", description = "Löschung erfolgreich.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response deleteUvLerngruppe(
			@PathParam("schema") final String schema,
			@PathParam("id") final long id,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLerngruppen(conn).deleteAsResponse(id),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Löscht mehrere bestehende {@link UvLerngruppe}-Einträge anhand der IDs.
	 *
	 * @param schema   das Datenbankschema
	 * @param ids      die IDs der zu löschenden Lerngruppen
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@DELETE
	@Path("/lerngruppen/delete/multiple")
	@Operation(summary = "Löscht mehrere bestehende UvLerngruppen.",
			description = "Löscht mehrere bestehende UvLerngruppen anhand der IDs.")
	@ApiResponse(responseCode = "200", description = "Die UvLerngruppen wurden erfolgreich gelöscht.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					array = @ArraySchema(schema = @Schema(implementation = Long.class))))
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z. B. beim Datenbankzugriff)")
	public Response deleteUvLerngruppenMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Die IDs der zu löschenden Lerngruppen", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = Long.class)))) final java.util.List<Long> ids,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLerngruppen(conn).deleteMultipleAsResponse(ids),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	// ============================================================
	//   RAUM (create -> patch -> patchMultiple -> delete -> deleteMultiple)
	// ============================================================

	/**
	 * Erstellt einen neuen {@link UvRaum} und gibt ihn zurück.
	 *
	 * @param schema   das Datenbankschema, in welchem der {@link UvRaum} erstellt wird
	 * @param request  die Informationen zur HTTP-Anfrage
	 * @param is       JSON-Objekt mit den Daten
	 *
	 * @return die HTTP-Antwort mit dem neuen {@link UvRaum}
	 */
	@POST
	@Path("/raeume/create")
	@Operation(summary = "Erstellt einen neuen UvRaum und gibt ihn zurück.",
			description = "Erstellt einen neuen UvRaum und gibt ihn zurück. "
					+ "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung besitzt.")
	@ApiResponse(responseCode = "201", description = "UvRaum wurde erfolgreich angelegt.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(implementation = UvRaum.class)))
	@ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um einen UvRaum anzulegen.")
	@ApiResponse(responseCode = "409", description = "Der UvRaum ist schon in der Datenbank enthalten.")
	public Response createUvRaum(
			@PathParam("schema") final String schema,
			@Context final HttpServletRequest request,
			@RequestBody(description = "Der Post für die UvRaum-Daten", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							schema = @Schema(implementation = UvRaum.class))) final InputStream is) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvRaeume(conn).addAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Patcht einen bestehenden {@link UvRaum} anhand der ID.
	 *
	 * @param schema   das Datenbankschema
	 * @param id       die ID des Raums
	 * @param is       JSON-Objekt mit den Patch-Daten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@PATCH
	@Path("/raeume/{id : \\d+}")
	@Operation(summary = "Patcht einen bestehenden UvRaum.",
			description = "Patcht einen bestehenden UvRaum anhand der ID.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvRaum(
			@PathParam("schema") final String schema,
			@PathParam("id") final long id,
			@RequestBody(description = "Patch-Daten für den UvRaum", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							schema = @Schema(implementation = UvRaum.class))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvRaeume(conn).patchAsResponse(id, is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Die OpenAPI-Methode für das Patchen mehrerer {@link UvRaum}-Einträge.
	 * Die IDs in dem Patch müssen vorhanden sein, damit die zu patchenden Daten gefunden werden können.
	 *
	 * @param schema   das Datenbankschema
	 * @param is       JSON-Objekt mit den Patch-Daten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return das Ergebnis der Patch-Operation
	 */
	@PATCH
	@Path("/raeume/patch/multiple")
	@Operation(summary = "Patcht mehrere bestehende UvRaeume.",
			description = "Patcht mehrere bestehende UvRaeume anhand der IDs.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvRaeumeMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Der Patch für die UvRaeume", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = UvRaum.class)))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvRaeume(conn).patchMultipleAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Löscht einen bestehenden {@link UvRaum} anhand der ID.
	 *
	 * @param schema   das Datenbankschema
	 * @param id       die ID des Raums
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@DELETE
	@Path("/raeume/{id : \\d+}")
	@Operation(summary = "Löscht einen bestehenden UvRaum.",
			description = "Löscht einen bestehenden UvRaum anhand der ID.")
	@ApiResponse(responseCode = "204", description = "Löschung erfolgreich.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response deleteUvRaum(
			@PathParam("schema") final String schema,
			@PathParam("id") final long id,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvRaeume(conn).deleteAsResponse(id),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Löscht mehrere bestehende {@link UvRaum}-Einträge anhand der IDs.
	 *
	 * @param schema   das Datenbankschema
	 * @param ids      die IDs der zu löschenden Räume
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@DELETE
	@Path("/raeume/delete/multiple")
	@Operation(summary = "Löscht mehrere bestehende UvRaeume.",
			description = "Löscht mehrere bestehende UvRaeume anhand der IDs.")
	@ApiResponse(responseCode = "200", description = "Die UvRaeume wurden erfolgreich gelöscht.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					array = @ArraySchema(schema = @Schema(implementation = Long.class))))
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z. B. beim Datenbankzugriff)")
	public Response deleteUvRaeumeMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Die IDs der zu löschenden UvRaeume", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = Long.class)))) final java.util.List<Long> ids,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvRaeume(conn).deleteMultipleAsResponse(ids),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	// ============================================================
	//   LEHRER (create -> patch -> patchMultiple -> delete -> deleteMultiple)
	// ============================================================

	/**
	 * Erstellt einen neuen {@link UvLehrer} und gibt ihn zurück.
	 *
	 * @param schema   das Datenbankschema, in welchem der {@link UvLehrer} erstellt wird
	 * @param request  die Informationen zur HTTP-Anfrage
	 * @param is       JSON-Objekt mit den Daten
	 *
	 * @return die HTTP-Antwort mit dem neuen {@link UvLehrer}
	 */
	@POST
	@Path("/lehrer/create")
	@Operation(summary = "Erstellt einen neuen UvLehrer und gibt ihn zurück.",
			description = "Erstellt einen neuen UvLehrer und gibt ihn zurück. "
					+ "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung besitzt.")
	@ApiResponse(responseCode = "201", description = "UvLehrer wurde erfolgreich angelegt.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(implementation = UvLehrer.class)))
	@ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um einen UvLehrer anzulegen.")
	@ApiResponse(responseCode = "409", description = "Der UvLehrer ist schon in der Datenbank enthalten.")
	public Response createUvLehrer(
			@PathParam("schema") final String schema,
			@Context final HttpServletRequest request,
			@RequestBody(description = "Der Post für die UvLehrer-Daten", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							schema = @Schema(implementation = UvLehrer.class))) final InputStream is) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLehrer(conn).addAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Patcht einen bestehenden {@link UvLehrer} anhand der ID.
	 *
	 * @param schema   das Datenbankschema
	 * @param id       die ID des Lehrers
	 * @param is       JSON-Objekt mit den Patch-Daten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@PATCH
	@Path("/lehrer/{id : \\d+}")
	@Operation(summary = "Patcht einen bestehenden UvLehrer.",
			description = "Patcht einen bestehenden UvLehrer anhand der ID.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvLehrer(
			@PathParam("schema") final String schema,
			@PathParam("id") final long id,
			@RequestBody(description = "Patch-Daten für den UvLehrer", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							schema = @Schema(implementation = UvLehrer.class))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLehrer(conn).patchAsResponse(id, is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Die OpenAPI-Methode für das Patchen mehrerer {@link UvLehrer}-Einträge.
	 * Die IDs in dem Patch müssen vorhanden sein, damit die zu patchenden Daten gefunden werden können.
	 *
	 * @param schema   das Datenbankschema
	 * @param is       JSON-Objekt mit den Patch-Daten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return das Ergebnis der Patch-Operation
	 */
	@PATCH
	@Path("/lehrer/patch/multiple")
	@Operation(summary = "Patcht mehrere bestehende UvLehrer.",
			description = "Patcht mehrere bestehende UvLehrer anhand der IDs.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvLehrerMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Der Patch für die UvLehrer", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = UvLehrer.class)))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLehrer(conn).patchMultipleAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Löscht einen bestehenden {@link UvLehrer} anhand der ID.
	 *
	 * @param schema   das Datenbankschema
	 * @param id       die ID des Lehrers
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@DELETE
	@Path("/lehrer/{id : \\d+}")
	@Operation(summary = "Löscht einen bestehenden UvLehrer.",
			description = "Löscht einen bestehenden UvLehrer anhand der ID.")
	@ApiResponse(responseCode = "204", description = "Löschung erfolgreich.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response deleteUvLehrer(
			@PathParam("schema") final String schema,
			@PathParam("id") final long id,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLehrer(conn).deleteAsResponse(id),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Löscht mehrere bestehende {@link UvLehrer}-Einträge anhand der IDs.
	 *
	 * @param schema   das Datenbankschema
	 * @param ids      die IDs der zu löschenden Lehrer
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@DELETE
	@Path("/lehrer/delete/multiple")
	@Operation(summary = "Löscht mehrere bestehende UvLehrer.",
			description = "Löscht mehrere bestehende UvLehrer anhand der IDs.")
	@ApiResponse(responseCode = "200", description = "Die UvLehrer wurden erfolgreich gelöscht.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					array = @ArraySchema(schema = @Schema(implementation = Long.class))))
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z. B. beim Datenbankzugriff)")
	public Response deleteUvLehrerMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Die IDs der zu löschenden UvLehrer", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = Long.class)))) final java.util.List<Long> ids,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLehrer(conn).deleteMultipleAsResponse(ids),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	// ============================================================
	//   LEHRER-ANRECHNUNGSSTUNDEN (create -> patch -> patchMultiple -> delete -> deleteMultiple)
	// ============================================================

	/**
	 * Erstellt eine neue {@link UvLehrerAnrechnungsstunden}-Zuordnung und gibt sie zurück.
	 *
	 * @param schema   das Datenbankschema, in welchem die {@link UvLehrerAnrechnungsstunden}-Zuordnung erstellt wird
	 * @param request  die Informationen zur HTTP-Anfrage
	 * @param is       JSON-Objekt mit den Daten
	 *
	 * @return die HTTP-Antwort mit der neuen {@link UvLehrerAnrechnungsstunden}-Zuordnung
	 */
	@POST
	@Path("/lehrer/anrechnungsstunden/create")
	@Operation(summary = "Erstellt eine neue UvLehrerAnrechnungsstunden-Zuordnung und gibt sie zurück.",
			description = "Erstellt eine neue UvLehrerAnrechnungsstunden-Zuordnung und gibt sie zurück. "
					+ "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung besitzt.")
	@ApiResponse(responseCode = "201", description = "UvLehrerAnrechnungsstunden-Zuordnung wurde erfolgreich angelegt.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(implementation = UvLehrerAnrechnungsstunden.class)))
	@ApiResponse(responseCode = "403", description = "Keine Rechte, um eine Zuordnung anzulegen.")
	@ApiResponse(responseCode = "409", description = "Die Zuordnung ist bereits vorhanden.")
	public Response createUvLehrerAnrechnungsstunde(
			@PathParam("schema") final String schema,
			@Context final HttpServletRequest request,
			@RequestBody(description = "Der Post für die UvLehrerAnrechnungsstunden-Daten", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							schema = @Schema(implementation = UvLehrerAnrechnungsstunden.class))) final InputStream is) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLehrerAnrechnungsstunden(conn).addAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Patcht eine bestehende {@link UvLehrerAnrechnungsstunden}-Zuordnung anhand der ID.
	 *
	 * @param schema   das Datenbankschema
	 * @param id       die ID der Zuordnung
	 * @param is       JSON-Objekt mit den Patch-Daten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@PATCH
	@Path("/lehrer/anrechnungsstunden/{id : \\d+}")
	@Operation(summary = "Patcht eine bestehende UvLehrerAnrechnungsstunden-Zuordnung.",
			description = "Patcht eine bestehende UvLehrerAnrechnungsstunden-Zuordnung anhand der ID.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvLehrerAnrechnungsstunde(
			@PathParam("schema") final String schema,
			@PathParam("id") final long id,
			@RequestBody(description = "Patch-Daten für die UvLehrerAnrechnungsstunden-Zuordnung", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							schema = @Schema(implementation = UvLehrerAnrechnungsstunden.class))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLehrerAnrechnungsstunden(conn).patchAsResponse(id, is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Die OpenAPI-Methode für das Patchen mehrerer {@link UvLehrerAnrechnungsstunden}-Einträge.
	 * Die IDs in dem Patch müssen vorhanden sein, damit die zu patchenden Daten gefunden werden können.
	 *
	 * @param schema   das Datenbankschema
	 * @param is       JSON-Objekt mit den Patch-Daten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return das Ergebnis der Patch-Operation
	 */
	@PATCH
	@Path("/lehrer/anrechnungsstunden/patch/multiple")
	@Operation(summary = "Patcht mehrere bestehende UvLehrerAnrechnungsstunden-Zuordnungen.",
			description = "Patcht mehrere bestehende UvLehrerAnrechnungsstunden-Zuordnungen anhand der IDs.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvLehrerAnrechnungsstundenMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Der Patch für mehrere UvLehrerAnrechnungsstunden-Zuordnungen", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = UvLehrerAnrechnungsstunden.class)))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLehrerAnrechnungsstunden(conn).patchMultipleAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Löscht eine bestehende {@link UvLehrerAnrechnungsstunden}-Zuordnung anhand der ID.
	 *
	 * @param schema   das Datenbankschema
	 * @param id       die ID der Zuordnung
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@DELETE
	@Path("/lehrer/anrechnungsstunden/{id : \\d+}")
	@Operation(summary = "Löscht eine bestehende UvLehrerAnrechnungsstunden-Zuordnung.",
			description = "Löscht eine bestehende UvLehrerAnrechnungsstunden-Zuordnung anhand der ID.")
	@ApiResponse(responseCode = "204", description = "Löschung erfolgreich.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response deleteUvLehrerAnrechnungsstunde(
			@PathParam("schema") final String schema,
			@PathParam("id") final long id,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLehrerAnrechnungsstunden(conn).deleteAsResponse(id),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Löscht mehrere bestehende {@link UvLehrerAnrechnungsstunden}-Zuordnungen anhand ihrer IDs.
	 *
	 * @param schema   das Datenbankschema
	 * @param ids      die IDs der zu löschenden Zuordnungen
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@DELETE
	@Path("/lehrer/anrechnungsstunden/delete/multiple")
	@Operation(summary = "Löscht mehrere bestehende UvLehrerAnrechnungsstunden-Zuordnungen.",
			description = "Löscht mehrere bestehende UvLehrerAnrechnungsstunden-Zuordnungen anhand der IDs.")
	@ApiResponse(responseCode = "200", description = "Die Zuordnungen wurden erfolgreich gelöscht.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					array = @ArraySchema(schema = @Schema(implementation = Long.class))))
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z. B. beim Datenbankzugriff).")
	public Response deleteUvLehrerAnrechnungsstundenMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Die IDs der zu löschenden UvLehrerAnrechnungsstunden-Zuordnungen", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = Long.class)))) final java.util.List<Long> ids,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLehrerAnrechnungsstunden(conn).deleteMultipleAsResponse(ids),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

// ============================================================
//   LEHRER-PFLICHTSTUNDENSOLL (create -> patch -> patchMultiple -> delete -> deleteMultiple)
// ============================================================

	/**
	 * Erstellt einen neuen {@link UvLehrerPflichtstundensoll}-Eintrag und gibt ihn zurück.
	 *
	 * @param schema   das Datenbankschema, in welchem der Eintrag erstellt wird
	 * @param request  die Informationen zur HTTP-Anfrage
	 * @param is       JSON-Objekt mit den Daten
	 *
	 * @return die HTTP-Antwort mit dem neuen {@link UvLehrerPflichtstundensoll}-Eintrag
	 */
	@POST
	@Path("/lehrer/pflichtstundensoll/create")
	@Operation(summary = "Erstellt einen neuen UvLehrerPflichtstundensoll-Eintrag und gibt ihn zurück.",
			description = "Erstellt einen neuen UvLehrerPflichtstundensoll-Eintrag und gibt ihn zurück. "
					+ "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung besitzt.")
	@ApiResponse(responseCode = "201", description = "UvLehrerPflichtstundensoll wurde erfolgreich angelegt.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(implementation = UvLehrerPflichtstundensoll.class)))
	@ApiResponse(responseCode = "403", description = "Keine Rechte, um den Eintrag anzulegen.")
	@ApiResponse(responseCode = "409", description = "Der Eintrag ist bereits vorhanden.")
	public Response createUvLehrerPflichtstundensoll(
			@PathParam("schema") final String schema,
			@Context final HttpServletRequest request,
			@RequestBody(description = "Die Daten für den neuen UvLehrerPflichtstundensoll-Eintrag", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							schema = @Schema(implementation = UvLehrerPflichtstundensoll.class))) final InputStream is) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLehrerPflichtstundensoll(conn).addAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Patcht einen bestehenden {@link UvLehrerPflichtstundensoll}-Eintrag anhand der ID.
	 *
	 * @param schema   das Datenbankschema
	 * @param id       die ID des Eintrags
	 * @param is       JSON-Objekt mit den Patch-Daten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@PATCH
	@Path("/lehrer/pflichtstundensoll/{id : \\d+}")
	@Operation(summary = "Patcht einen bestehenden UvLehrerPflichtstundensoll-Eintrag.",
			description = "Patcht einen bestehenden UvLehrerPflichtstundensoll-Eintrag anhand der ID.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvLehrerPflichtstundensoll(
			@PathParam("schema") final String schema,
			@PathParam("id") final long id,
			@RequestBody(description = "Patch-Daten für den UvLehrerPflichtstundensoll-Eintrag", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							schema = @Schema(implementation = UvLehrerPflichtstundensoll.class))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLehrerPflichtstundensoll(conn).patchAsResponse(id, is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Die OpenAPI-Methode für das Patchen mehrerer {@link UvLehrerPflichtstundensoll}-Einträge.
	 * Die IDs in dem Patch müssen vorhanden sein, damit die zu patchenden Daten gefunden werden können.
	 *
	 * @param schema   das Datenbankschema
	 * @param is       JSON-Objekt mit den Patch-Daten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return das Ergebnis der Patch-Operation
	 */
	@PATCH
	@Path("/lehrer/pflichtstundensoll/patch/multiple")
	@Operation(summary = "Patcht mehrere bestehende UvLehrerPflichtstundensoll-Einträge.",
			description = "Patcht mehrere bestehende UvLehrerPflichtstundensoll-Einträge anhand der IDs.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvLehrerPflichtstundensollMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Der Patch für mehrere UvLehrerPflichtstundensoll-Einträge", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = UvLehrerPflichtstundensoll.class)))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLehrerPflichtstundensoll(conn).patchMultipleAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Löscht einen bestehenden {@link UvLehrerPflichtstundensoll}-Eintrag anhand der ID.
	 *
	 * @param schema   das Datenbankschema
	 * @param id       die ID des Eintrags
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@DELETE
	@Path("/lehrer/pflichtstundensoll/{id : \\d+}")
	@Operation(summary = "Löscht einen bestehenden UvLehrerPflichtstundensoll-Eintrag.",
			description = "Löscht einen bestehenden UvLehrerPflichtstundensoll-Eintrag anhand der ID.")
	@ApiResponse(responseCode = "204", description = "Löschung erfolgreich.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response deleteUvLehrerPflichtstundensoll(
			@PathParam("schema") final String schema,
			@PathParam("id") final long id,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLehrerPflichtstundensoll(conn).deleteAsResponse(id),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Löscht mehrere bestehende {@link UvLehrerPflichtstundensoll}-Einträge anhand ihrer IDs.
	 *
	 * @param schema   das Datenbankschema
	 * @param ids      die IDs der zu löschenden Einträge
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@DELETE
	@Path("/lehrer/pflichtstundensoll/delete/multiple")
	@Operation(summary = "Löscht mehrere bestehende UvLehrerPflichtstundensoll-Einträge.",
			description = "Löscht mehrere bestehende UvLehrerPflichtstundensoll-Einträge anhand der IDs.")
	@ApiResponse(responseCode = "200", description = "Die Einträge wurden erfolgreich gelöscht.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					array = @ArraySchema(schema = @Schema(implementation = Long.class))))
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z. B. beim Datenbankzugriff).")
	public Response deleteUvLehrerPflichtstundensollMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Die IDs der zu löschenden UvLehrerPflichtstundensoll-Einträge", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = Long.class)))) final java.util.List<Long> ids,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLehrerPflichtstundensoll(conn).deleteMultipleAsResponse(ids),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	// ============================================================
	//   LERNGRUPPEN-LEHRER (create -> patch -> patchMultiple -> delete -> deleteMultiple)
	// ============================================================

	/**
	 * Erstellt eine neue {@link UvLerngruppenLehrer}-Zuordnung und gibt sie zurück.
	 *
	 * @param schema   das Datenbankschema, in welchem die {@link UvLerngruppenLehrer}-Zuordnung erstellt wird
	 * @param request  die Informationen zur HTTP-Anfrage
	 * @param is       JSON-Objekt mit den Daten
	 *
	 * @return die HTTP-Antwort mit der neuen {@link UvLerngruppenLehrer}-Zuordnung
	 */
	@POST
	@Path("/lerngruppen/lehrer/create")
	@Operation(summary = "Erstellt eine neue UvLerngruppenLehrer-Zuordnung und gibt sie zurück.",
			description = "Erstellt eine neue UvLerngruppenLehrer-Zuordnung und gibt sie zurück. "
					+ "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung besitzt.")
	@ApiResponse(responseCode = "201", description = "UvLerngruppenLehrer-Zuordnung wurde erfolgreich angelegt.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(implementation = UvLerngruppenLehrer.class)))
	@ApiResponse(responseCode = "403", description = "Keine Rechte, um eine Zuordnung anzulegen.")
	@ApiResponse(responseCode = "409", description = "Die Zuordnung ist bereits vorhanden.")
	public Response createUvLerngruppenLehrer(
			@PathParam("schema") final String schema,
			@Context final HttpServletRequest request,
			@RequestBody(description = "Der Post für die UvLerngruppenLehrer-Daten", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							schema = @Schema(implementation = UvLerngruppenLehrer.class))) final InputStream is) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLerngruppenLehrer(conn).addAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Patcht eine bestehende {@link UvLerngruppenLehrer}-Zuordnung anhand der ID.
	 *
	 * @param schema   das Datenbankschema
	 * @param id       die ID der Zuordnung
	 * @param is       JSON-Objekt mit den Patch-Daten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@PATCH
	@Path("/lerngruppen/lehrer/{id : \\d+}")
	@Operation(summary = "Patcht eine bestehende UvLerngruppenLehrer-Zuordnung.",
			description = "Patcht eine bestehende UvLerngruppenLehrer-Zuordnung anhand der ID.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvLerngruppenLehrer(
			@PathParam("schema") final String schema,
			@PathParam("id") final long id,
			@RequestBody(description = "Patch-Daten für die UvLerngruppenLehrer-Zuordnung", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							schema = @Schema(implementation = UvLerngruppenLehrer.class))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLerngruppenLehrer(conn).patchAsResponse(id, is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Die OpenAPI-Methode für das Patchen mehrerer {@link UvLerngruppenLehrer}-Einträge.
	 * Die IDs in dem Patch müssen vorhanden sein, damit die zu patchenden Daten gefunden werden können.
	 *
	 * @param schema   das Datenbankschema
	 * @param is       JSON-Objekt mit den Patch-Daten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return das Ergebnis der Patch-Operation
	 */
	@PATCH
	@Path("/lerngruppen/lehrer/patch/multiple")
	@Operation(summary = "Patcht mehrere bestehende UvLerngruppenLehrer-Zuordnungen.",
			description = "Patcht mehrere bestehende UvLerngruppenLehrer-Zuordnungen anhand der IDs.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response patchUvLerngruppenLehrerMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Der Patch für mehrere UvLerngruppenLehrer-Zuordnungen", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = UvLerngruppenLehrer.class)))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLerngruppenLehrer(conn).patchMultipleAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Löscht eine bestehende {@link UvLerngruppenLehrer}-Zuordnung anhand der ID.
	 *
	 * @param schema   das Datenbankschema
	 * @param id       die ID der Zuordnung
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@DELETE
	@Path("/lerngruppen/lehrer/{id : \\d+}")
	@Operation(summary = "Löscht eine bestehende UvLerngruppenLehrer-Zuordnung.",
			description = "Löscht eine bestehende UvLerngruppenLehrer-Zuordnung anhand der ID.")
	@ApiResponse(responseCode = "204", description = "Löschung erfolgreich.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Nicht gefunden.")
	public Response deleteUvLerngruppenLehrer(
			@PathParam("schema") final String schema,
			@PathParam("id") final long id,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLerngruppenLehrer(conn).deleteAsResponse(id),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Löscht mehrere bestehende {@link UvLerngruppenLehrer}-Zuordnungen anhand ihrer IDs.
	 *
	 * @param schema   das Datenbankschema
	 * @param ids      die IDs der zu löschenden Zuordnungen
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort
	 */
	@DELETE
	@Path("/lerngruppen/lehrer/delete/multiple")
	@Operation(summary = "Löscht mehrere bestehende UvLerngruppenLehrer-Zuordnungen.",
			description = "Löscht mehrere bestehende UvLerngruppenLehrer-Zuordnungen anhand der IDs.")
	@ApiResponse(responseCode = "200", description = "Die Zuordnungen wurden erfolgreich gelöscht.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					array = @ArraySchema(schema = @Schema(implementation = Long.class))))
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z. B. beim Datenbankzugriff).")
	public Response deleteUvLerngruppenLehrerMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Die IDs der zu löschenden UvLerngruppenLehrer-Zuordnungen", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = Long.class)))) final java.util.List<Long> ids,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvLerngruppenLehrer(conn).deleteMultipleAsResponse(ids),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	// ============================================================
	//   ZEITRASTER (create -> patch -> patchMultiple -> delete -> deleteMultiple)
	// ============================================================

	/**
	 * Erstellt ein neues {@link UvZeitraster}-Objekt und gibt dieses zurück.
	 *
	 * @param schema   das Datenbankschema, in welchem das {@link UvZeitraster}-Objekt erstellt wird
	 * @param request  die HTTP-Anfrage
	 * @param is       JSON-Objekt mit den zu erstellenden Daten
	 *
	 * @return die HTTP-Antwort mit dem neu erstellten {@link UvZeitraster}-Objekt
	 */
	@POST
	@Path("/zeitraster/create")
	@Operation(summary = "Erstellt ein neues Zeitraster und gibt es zurück.",
			description = "Erstellt ein neues Zeitraster und gibt es zurück. "
					+ "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung besitzt.")
	@ApiResponse(responseCode = "201", description = "Zeit­raster wurde erfolgreich angelegt.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(implementation = UvZeitraster.class)))
	@ApiResponse(responseCode = "403", description = "Keine Rechte, um ein Zeitraster anzulegen.")
	@ApiResponse(responseCode = "409", description = "Das Zeitraster ist bereits vorhanden.")
	public Response createUvZeitraster(
			@PathParam("schema") final String schema,
			@Context final HttpServletRequest request,
			@RequestBody(description = "Das Zeitraster-Objekt im JSON-Format", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							schema = @Schema(implementation = UvZeitraster.class))) final InputStream is) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvZeitraster(conn).addAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Patcht ein bestehendes {@link UvZeitraster}-Objekt anhand der ID.
	 *
	 * @param schema   das Datenbankschema
	 * @param id       die ID des Zeitrasters
	 * @param is       JSON-Objekt mit den Patch-Daten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort mit dem Ergebnis der Patch-Operation
	 */
	@PATCH
	@Path("/zeitraster/{id : \\d+}")
	@Operation(summary = "Patcht ein bestehendes Zeitraster anhand der ID.",
			description = "Patcht ein bestehendes Zeitraster anhand der ID.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Zeit­raster nicht gefunden.")
	public Response patchUvZeitraster(
			@PathParam("schema") final String schema,
			@PathParam("id") final long id,
			@RequestBody(description = "Patch-Daten für das Zeitraster", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							schema = @Schema(implementation = UvZeitraster.class))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvZeitraster(conn).patchAsResponse(id, is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Patcht mehrere bestehende {@link UvZeitraster}-Objekte.
	 * Die IDs müssen vorhanden sein, damit die zu patchenden Datensätze gefunden werden können.
	 *
	 * @param schema   das Datenbankschema
	 * @param is       JSON-Array mit den Patch-Daten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort mit dem Ergebnis der Patch-Operation
	 */
	@PATCH
	@Path("/zeitraster/patch/multiple")
	@Operation(summary = "Patcht mehrere bestehende Zeitraster.",
			description = "Patcht mehrere bestehende Zeitraster anhand der angegebenen IDs.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Ein oder mehrere Zeitraster nicht gefunden.")
	public Response patchUvZeitrasterMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Patch-Daten für mehrere Zeitraster", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = UvZeitraster.class)))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvZeitraster(conn).patchMultipleAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Löscht ein bestehendes {@link UvZeitraster}-Objekt anhand der ID.
	 *
	 * @param schema   das Datenbankschema
	 * @param id       die ID des zu löschenden Zeitrasters
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort mit dem Ergebnis der Lösch-Operation
	 */
	@DELETE
	@Path("/zeitraster/{id : \\d+}")
	@Operation(summary = "Löscht ein bestehendes Zeitraster anhand der ID.",
			description = "Löscht ein bestehendes Zeitraster anhand der ID.")
	@ApiResponse(responseCode = "204", description = "Löschung erfolgreich.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Zeit­raster nicht gefunden.")
	public Response deleteUvZeitraster(
			@PathParam("schema") final String schema,
			@PathParam("id") final long id,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvZeitraster(conn).deleteAsResponse(id),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Löscht mehrere bestehende {@link UvZeitraster}-Objekte anhand ihrer IDs.
	 *
	 * @param schema   das Datenbankschema
	 * @param ids      Liste der IDs der zu löschenden Zeitraster
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort mit dem Ergebnis der Lösch-Operation
	 */
	@DELETE
	@Path("/zeitraster/delete/multiple")
	@Operation(summary = "Löscht mehrere bestehende Zeitraster anhand der IDs.",
			description = "Löscht mehrere bestehende Zeitraster anhand der IDs.")
	@ApiResponse(responseCode = "200", description = "Löschung erfolgreich.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					array = @ArraySchema(schema = @Schema(implementation = Long.class))))
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "500", description = "Fehler beim Löschen der Datensätze.")
	public Response deleteUvZeitrasterMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Liste der IDs der zu löschenden Zeitraster", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = Long.class)))) final java.util.List<Long> ids,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvZeitraster(conn).deleteMultipleAsResponse(ids),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

	// ============================================================
	//   ZEITRASTEREINTRAG (create -> patch -> patchMultiple -> delete -> deleteMultiple)
	// ============================================================

	/**
	 * Erstellt einen neuen {@link UvZeitrasterEintrag}-Eintrag und gibt diesen zurück.
	 *
	 * @param schema   das Datenbankschema
	 * @param request  die HTTP-Anfrage
	 * @param is       JSON-Objekt mit den zu erstellenden Daten
	 *
	 * @return die HTTP-Antwort mit dem neu erstellten Eintrag
	 */
	@POST
	@Path("/zeitrastereintraege/create")
	@Operation(summary = "Erstellt einen neuen Zeitrastereintrag.",
			description = "Erstellt einen neuen Zeitrastereintrag und gibt diesen zurück.")
	@ApiResponse(responseCode = "201", description = "Zeit­rastereintrag erfolgreich erstellt.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(implementation = UvZeitrasterEintrag.class)))
	@ApiResponse(responseCode = "403", description = "Keine Rechte zum Erstellen.")
	public Response createUvZeitrasterEintrag(
			@PathParam("schema") final String schema,
			@Context final HttpServletRequest request,
			@RequestBody(description = "Der zu erstellende Zeitrastereintrag", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							schema = @Schema(implementation = UvZeitrasterEintrag.class))) final InputStream is) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvZeitrasterEintraege(conn).addAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Patcht einen bestehenden {@link UvZeitrasterEintrag}-Eintrag.
	 *
	 * @param schema   das Datenbankschema
	 * @param id       die ID des Zeitrastereintrags
	 * @param is       JSON-Objekt mit den Patch-Daten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return das Ergebnis der Patch-Operation
	 */
	@PATCH
	@Path("/zeitrastereintraege/{id : \\d+}")
	@Operation(summary = "Patcht einen bestehenden Zeitrastereintrag.",
			description = "Patcht einen bestehenden Zeitrastereintrag anhand der ID.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Zeit­rastereintrag nicht gefunden.")
	public Response patchUvZeitrasterEintrag(
			@PathParam("schema") final String schema,
			@PathParam("id") final long id,
			@RequestBody(description = "Patch-Daten für den Zeitrastereintrag", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							schema = @Schema(implementation = UvZeitrasterEintrag.class))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvZeitrasterEintraege(conn).patchAsResponse(id, is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Patcht mehrere bestehende {@link UvZeitrasterEintrag}-Einträge.
	 *
	 * @param schema   das Datenbankschema
	 * @param is       JSON-Array mit den Patch-Daten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return das Ergebnis der Patch-Operation
	 */
	@PATCH
	@Path("/zeitrastereintraege/patch/multiple")
	@Operation(summary = "Patcht mehrere Zeitrastereinträge.",
			description = "Patcht mehrere Zeitrastereinträge gleichzeitig anhand ihrer IDs.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	@ApiResponse(responseCode = "400", description = "Fehlerhafte Daten.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Ein oder mehrere Einträge nicht gefunden.")
	public Response patchUvZeitrasterEintraegeMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Patch-Daten für mehrere Zeitrastereinträge", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = UvZeitrasterEintrag.class)))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvZeitrasterEintraege(conn).patchMultipleAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Löscht einen bestehenden {@link UvZeitrasterEintrag}.
	 *
	 * @param schema   das Datenbankschema
	 * @param id       die ID des zu löschenden Eintrags
	 * @param request  die HTTP-Anfrage
	 *
	 * @return das Ergebnis der Lösch-Operation
	 */
	@DELETE
	@Path("/zeitrastereintraege/{id : \\d+}")
	@Operation(summary = "Löscht einen bestehenden Zeitrastereintrag.",
			description = "Löscht einen bestehenden Zeitrastereintrag anhand der ID.")
	@ApiResponse(responseCode = "204", description = "Löschung erfolgreich.")
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "404", description = "Zeit­rastereintrag nicht gefunden.")
	public Response deleteUvZeitrasterEintrag(
			@PathParam("schema") final String schema,
			@PathParam("id") final long id,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvZeitrasterEintraege(conn).deleteAsResponse(id),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Löscht mehrere bestehende {@link UvZeitrasterEintrag}-Einträge anhand ihrer IDs.
	 *
	 * @param schema   das Datenbankschema
	 * @param ids      Liste der IDs der zu löschenden Einträge
	 * @param request  die HTTP-Anfrage
	 *
	 * @return das Ergebnis der Lösch-Operation
	 */
	@DELETE
	@Path("/zeitrastereintraege/delete/multiple")
	@Operation(summary = "Löscht mehrere bestehende Zeitrastereinträge.",
			description = "Löscht mehrere bestehende Zeitrastereinträge anhand der IDs.")
	@ApiResponse(responseCode = "200", description = "Löschung erfolgreich.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					array = @ArraySchema(schema = @Schema(implementation = Long.class))))
	@ApiResponse(responseCode = "403", description = "Keine Rechte.")
	@ApiResponse(responseCode = "500", description = "Fehler beim Löschen der Datensätze.")
	public Response deleteUvZeitrasterEintraegeMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Liste der IDs der zu löschenden Zeitrastereinträge", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = Long.class)))) final java.util.List<Long> ids,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvZeitrasterEintraege(conn).deleteMultipleAsResponse(ids),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}

// ============================================================
//   UNTERRICHTE (create -> patch -> patchMultiple -> delete -> deleteMultiple)
// ============================================================

	/**
	 * Erstellt eine neue {@link UvUnterricht}-Einheit und gibt sie zurück.
	 *
	 * @param schema   das Datenbankschema, in welchem die {@link UvUnterricht}-Einheit erstellt wird
	 * @param request  die Informationen zur HTTP-Anfrage
	 * @param is       JSON-Objekt mit den Daten der zu erstellenden {@link UvUnterricht}-Einheit
	 *
	 * @return die HTTP-Antwort mit der neu erstellten {@link UvUnterricht}-Einheit
	 */
	@POST
	@Path("/unterrichte/create")
	@Operation(summary = "Erstellt eine neue Unterrichtseinheit.",
			description = "Erstellt eine neue Unterrichtseinheit und gibt diese zurück.")
	@ApiResponse(responseCode = "201", description = "Unterricht erfolgreich erstellt.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(implementation = UvUnterricht.class)))
	public Response createUvUnterricht(
			@PathParam("schema") final String schema,
			@Context final HttpServletRequest request,
			@RequestBody(description = "Die zu erstellende Unterrichtseinheit", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							schema = @Schema(implementation = UvUnterricht.class))) final InputStream is) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvUnterrichte(conn).addAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Patcht eine bestehende {@link UvUnterricht}-Einheit anhand ihrer ID.
	 *
	 * @param schema   das Datenbankschema
	 * @param id       die ID der zu patchenden {@link UvUnterricht}-Einheit
	 * @param is       JSON-Objekt mit den Patch-Daten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort mit dem Ergebnis der Patch-Operation
	 */
	@PATCH
	@Path("/unterrichte/{id : \\d+}")
	@Operation(summary = "Patcht eine bestehende Unterrichtseinheit.",
			description = "Patcht eine bestehende Unterrichtseinheit anhand der ID.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	public Response patchUvUnterricht(
			@PathParam("schema") final String schema,
			@PathParam("id") final long id,
			@RequestBody(description = "Patch-Daten für den Unterricht", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							schema = @Schema(implementation = UvUnterricht.class))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvUnterrichte(conn).patchAsResponse(id, is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Patcht mehrere bestehende {@link UvUnterricht}-Einheiten anhand ihrer IDs.
	 * Die im Patch enthaltenen IDs müssen vorhanden sein.
	 *
	 * @param schema   das Datenbankschema
	 * @param is       JSON-Array mit den Patch-Daten für mehrere {@link UvUnterricht}-Einheiten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort mit dem Ergebnis der Patch-Operation
	 */
	@PATCH
	@Path("/unterrichte/patch/multiple")
	@Operation(summary = "Patcht mehrere Unterrichtseinheiten.",
			description = "Patcht mehrere Unterrichtseinheiten gleichzeitig anhand ihrer IDs.")
	@ApiResponse(responseCode = "200", description = "Patch erfolgreich.")
	public Response patchUvUnterrichteMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Patch-Daten für mehrere Unterrichtseinheiten", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = UvUnterricht.class)))) final InputStream is,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvUnterrichte(conn).patchMultipleAsResponse(is),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Löscht eine bestehende {@link UvUnterricht}-Einheit anhand ihrer ID.
	 *
	 * @param schema   das Datenbankschema
	 * @param id       die ID der zu löschenden {@link UvUnterricht}-Einheit
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort (204 bei Erfolg)
	 */
	@DELETE
	@Path("/unterrichte/{id : \\d+}")
	@Operation(summary = "Löscht eine bestehende Unterrichtseinheit.",
			description = "Löscht eine bestehende Unterrichtseinheit anhand der ID.")
	@ApiResponse(responseCode = "204", description = "Löschung erfolgreich.")
	public Response deleteUvUnterricht(
			@PathParam("schema") final String schema,
			@PathParam("id") final long id,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvUnterrichte(conn).deleteAsResponse(id),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}


	/**
	 * Löscht mehrere bestehende {@link UvUnterricht}-Einheiten anhand ihrer IDs.
	 *
	 * @param schema   das Datenbankschema
	 * @param ids      die Liste der IDs der zu löschenden {@link UvUnterricht}-Einheiten
	 * @param request  die HTTP-Anfrage
	 *
	 * @return die HTTP-Antwort mit den IDs der gelöschten Einträge oder Fehlerdetails
	 */
	@DELETE
	@Path("/unterrichte/delete/multiple")
	@Operation(summary = "Löscht mehrere bestehende Unterrichtseinheiten.",
			description = "Löscht mehrere bestehende Unterrichtseinheiten anhand ihrer IDs.")
	@ApiResponse(responseCode = "200", description = "Löschung erfolgreich.")
	public Response deleteUvUnterrichteMultiple(
			@PathParam("schema") final String schema,
			@RequestBody(description = "Liste der IDs der zu löschenden Unterrichtseinheiten", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON,
							array = @ArraySchema(schema = @Schema(implementation = Long.class)))) final java.util.List<Long> ids,
			@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
						new DataUvUnterrichte(conn).deleteMultipleAsResponse(ids),
				request, ServerMode.DEV,
				BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ALLGEMEIN_AENDERN);
	}



}
