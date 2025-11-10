package de.svws_nrw.data.schule;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.svws_nrw.asd.data.RGBFarbe;
import de.svws_nrw.asd.types.schule.Floskelgruppenart;
import de.svws_nrw.core.data.schule.Floskelgruppe;
import de.svws_nrw.data.DataManagerRevised;
import de.svws_nrw.data.JSONMapper;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.dto.current.katalog.DTOFloskelgruppen;
import de.svws_nrw.db.schema.Schema;
import de.svws_nrw.db.utils.ApiOperationException;

import static jakarta.ws.rs.core.Response.Status;

/**
 * Diese Klasse erweitert den abstrakten {@link DataManagerRevised} für das Core-DTO {@link Floskelgruppe}.
 */
public final class DataFloskelgruppen extends DataManagerRevised<Long, DTOFloskelgruppen, Floskelgruppe> {

	/**
	 * Erstellt einen neuen Datenmanager mit der angegebenen Verbindung
	 *
	 * @param conn die Datenbank-Verbindung, welche vom Daten-Manager benutzt werden soll
	 */
	public DataFloskelgruppen(final DBEntityManager conn) {
		super(conn);
		setAttributesNotPatchable("id");
		setAttributesRequiredOnCreation("kuerzel");
	}

	@Override
	protected void initDTO(final DTOFloskelgruppen dto, final Long newID, final Map<String, Object> initAttributes) {
		dto.ID = newID;
	}

	@Override
	protected long getLongId(final DTOFloskelgruppen dto) throws ApiOperationException {
		return dto.ID;
	}

	@Override
	public Floskelgruppe getById(final Long id) throws ApiOperationException {
		if (id == null)
			throw new ApiOperationException(Status.BAD_REQUEST, "Die ID für die Floskelgruppe darf nicht null sein.");

		final DTOFloskelgruppen dto = conn.queryByKey(DTOFloskelgruppen.class, id);
		if (dto == null)
			throw new ApiOperationException(Status.NOT_FOUND, "Es wurde keine Floskelgruppe mit der ID %d gefunden.".formatted(id));

		return map(dto);
	}

	@Override
	public List<Floskelgruppe> getAll() {
		return conn.queryAll(DTOFloskelgruppen.class).stream()
				.map(this::map)
				.toList();
	}

	@Override
	protected Floskelgruppe map(final DTOFloskelgruppen dto) {
		final Floskelgruppe floskelgruppe = new Floskelgruppe();
		floskelgruppe.id = dto.ID;
		floskelgruppe.kuerzel = dto.Kuerzel;
		floskelgruppe.bezeichnung = dto.Bezeichnung;
		floskelgruppe.idFloskelgruppenart = dto.Hauptgruppe_ID;
		floskelgruppe.farbe = (dto.Farbe == null) ? null : new RGBFarbe(dto.Farbe);
		return floskelgruppe;
	}

	@Override
	protected void mapAttribute(final DTOFloskelgruppen dto, final String name, final Object value, final Map<String, Object> map)
			throws ApiOperationException {
		switch (name) {
			case "kuerzel" -> updateKuerzel(dto, value, name);
			case "bezeichnung" -> updateBezeichnung(dto, name, value);
			case "idFloskelgruppenart" -> updateFloskelgruppenart(dto, name, value);
			case "farbe" -> updateFarbe(dto, value);
			default -> throw new ApiOperationException(Status.BAD_REQUEST, "Die Daten des Patches enthalten das unbekannte Attribut %s.".formatted(name));
		}
	}

	private static void updateBezeichnung(final DTOFloskelgruppen dto, final String name, final Object value) throws ApiOperationException {
		dto.Bezeichnung =
				JSONMapper.convertToString(value, false, false, Schema.tab_Katalog_Floskeln_Gruppen.col_Bezeichnung.datenlaenge(), name);
	}

	private void updateKuerzel(final DTOFloskelgruppen dto, final Object value, final String name) throws ApiOperationException {
		final String kuerzel = JSONMapper.convertToString(value, false, false, Schema.tab_Katalog_Floskeln_Gruppen.col_Kuerzel.datenlaenge(), name);
		if (Objects.equals(dto.Kuerzel, kuerzel) || kuerzel.isBlank())
			return;

		final boolean alreadyUsed = this.conn
				.queryAll(DTOFloskelgruppen.class).stream()
				.anyMatch(f -> (f.ID != dto.ID) && kuerzel.equalsIgnoreCase(f.Kuerzel));

		if (alreadyUsed)
			throw new ApiOperationException(Status.BAD_REQUEST, "Das Kürzel %s wird bereits verwendet.".formatted(kuerzel));

		dto.Kuerzel = kuerzel;
	}

	private static void updateFloskelgruppenart(final DTOFloskelgruppen dto, final String name, final Object value) throws ApiOperationException {
		final Long idFloskelgruppenart = JSONMapper.convertToLong(value, true, name);
		if (idFloskelgruppenart == null) {
			dto.Hauptgruppe_ID = null;
			return;
		}
		if (Objects.equals(idFloskelgruppenart, dto.Hauptgruppe_ID))
			return;

		if (matchingFloskelgruppenartNotFound(idFloskelgruppenart))
			throw new ApiOperationException(Status.NOT_FOUND, "Es wurde keine Floskelgruppenart zur ID %d gefunden.".formatted(idFloskelgruppenart));

		dto.Hauptgruppe_ID = idFloskelgruppenart;
	}

	private static boolean matchingFloskelgruppenartNotFound(final long id) {
		return Floskelgruppenart.data().getEintragByID(id) == null;
	}

	private static void updateFarbe(final DTOFloskelgruppen dto, final Object value) throws ApiOperationException {
		try {
			if (value == null) {
				dto.Farbe = null;
				return;
			}
			final RGBFarbe farbe = JSONMapper.mapper.convertValue(value, RGBFarbe.class);
			dto.Farbe = farbe.asDecimal();
		} catch (final IllegalArgumentException e) {
			throw new ApiOperationException(Status.BAD_REQUEST, "Die Farbe entspricht nicht dem richtigen Datentyp: %s.".formatted(e.getMessage()));
		}
	}

}
