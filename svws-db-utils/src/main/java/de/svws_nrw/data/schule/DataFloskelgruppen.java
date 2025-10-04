package de.svws_nrw.data.schule;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.svws_nrw.asd.data.RGBFarbe;
import de.svws_nrw.asd.data.schule.FloskelgruppenartKatalogEintrag;
import de.svws_nrw.asd.types.schule.Floskelgruppenart;
import de.svws_nrw.core.data.schule.Floskelgruppe;
import de.svws_nrw.data.DataManagerRevised;
import de.svws_nrw.data.JSONMapper;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.dto.current.schild.katalog.DTOFloskelgruppen;
import de.svws_nrw.db.schema.Schema;
import de.svws_nrw.db.utils.ApiOperationException;

import static jakarta.ws.rs.core.Response.Status;

/**
 * Diese Klasse erweitert den abstrakten {@link DataManagerRevised} für das Core-DTO {@link Floskelgruppe}.
 */
public final class DataFloskelgruppen extends DataManagerRevised<String, DTOFloskelgruppen, Floskelgruppe> {

	/**
	 * Erstellt einen neuen Datenmanager mit der angegebenen Verbindung
	 *
	 * @param conn die Datenbank-Verbindung, welche vom Daten-Manager benutzt werden soll
	 */
	public DataFloskelgruppen(final DBEntityManager conn) {
		super(conn);
		setAttributesNotPatchable("kuerzel");
		setAttributesRequiredOnCreation("kuerzel");
	}

	@Override
	protected String getID(final Map<String, Object> initAttributes) throws ApiOperationException {
		return JSONMapper.convertToString(initAttributes.get("kuerzel"), false, false, Schema.tab_Floskelgruppen.col_Bezeichnung.datenlaenge(), "kuerzel");
	}

	@Override
	public void checkBeforeCreation(final String newID, final Map<String, Object> initAttributes) throws ApiOperationException {
		final String kuerzel =
				JSONMapper.convertToString(initAttributes.get("kuerzel"), false, false, Schema.tab_Floskelgruppen.col_Kuerzel.datenlaenge(), "kuerzel");
		final boolean alreadyUsed = this.conn
				.queryAll(DTOFloskelgruppen.class).stream()
				.anyMatch(f -> kuerzel.equalsIgnoreCase(f.Kuerzel));

		if (alreadyUsed)
			throw new ApiOperationException(Status.BAD_REQUEST, "Das Kürzel %s wird bereits verwendet.".formatted(kuerzel));
	}

	@Override
	protected void initDTO(final DTOFloskelgruppen dto, final String newID, final Map<String, Object> initAttributes) {
		dto.Kuerzel = newID;
	}

	@Override
	protected Floskelgruppe map(final DTOFloskelgruppen dto) {
		final Floskelgruppe floskelgruppe = new Floskelgruppe();
		floskelgruppe.kuerzel = dto.Kuerzel;
		floskelgruppe.bezeichnung = dto.Bezeichnung;
		floskelgruppe.idFloskelgruppenart = mapIdFloskelgruppe(dto);
		floskelgruppe.farbe = (dto.Farbe == null) ? null : new RGBFarbe(dto.Farbe);
		return floskelgruppe;
	}

	private Long mapIdFloskelgruppe(final DTOFloskelgruppen dto) {
		if (dto.Hauptgruppe == null)
			return null;

		final int schuljahr = this.conn.getUser().schuleGetSchuljahresabschnitt().schuljahr;
		final FloskelgruppenartKatalogEintrag eintrag = Floskelgruppenart.data().getEintragBySchuljahrUndSchluessel(schuljahr, dto.Hauptgruppe);
		return (eintrag == null) ? null : eintrag.id;
	}

	@Override
	public Floskelgruppe getById(final String kuerzel) throws ApiOperationException {
		if (kuerzel == null)
			throw new ApiOperationException(Status.BAD_REQUEST, "Das Kürzel für die Floskelgruppe darf nicht null sein.");

		final DTOFloskelgruppen dto = conn.queryByKey(DTOFloskelgruppen.class, kuerzel);
		if (dto == null)
			throw new ApiOperationException(Status.NOT_FOUND, "Es wurde keine Floskelgruppe mit dem Kürzel %s gefunden.".formatted(kuerzel));

		return map(dto);
	}

	@Override
	public List<Floskelgruppe> getAll() {
		final List<DTOFloskelgruppen> floskelgruppen = Optional
				.ofNullable(conn.queryAll(DTOFloskelgruppen.class))
				.orElse(Collections.emptyList());

		return floskelgruppen.stream()
				.map(this::map)
				.toList();
	}

	@Override
	protected void mapAttribute(final DTOFloskelgruppen dto, final String name, final Object value, final Map<String, Object> map)
			throws ApiOperationException {
		switch (name) {
			case "kuerzel" -> {
				// nicht patchbar - wird beim neu erstellen in der Methode initDto gesetzt - nicht patchbar
			}
			case "bezeichnung" -> dto.Bezeichnung =
					JSONMapper.convertToString(value, false, false, Schema.tab_Floskelgruppen.col_Bezeichnung.datenlaenge(), name);
			case "idFloskelgruppenart" -> updateFloskelgruppenart(dto, name, value);
			case "farbe" -> updateFarbe(dto, value);
			default -> throw new ApiOperationException(Status.BAD_REQUEST, "Die Daten des Patches enthalten das unbekannte Attribut %s.".formatted(name));
		}
	}

	private static void updateFloskelgruppenart(final DTOFloskelgruppen dto, final String name, final Object value) throws ApiOperationException {
		final long idFloskelgruppenart = JSONMapper.convertToLong(value, false, name);
		final FloskelgruppenartKatalogEintrag eintrag = Floskelgruppenart.data().getEintragByID(idFloskelgruppenart);
		if (eintrag == null)
			throw new ApiOperationException(Status.NOT_FOUND, "Es wurde keine Floskelgruppenart zur ID %d gefunden.".formatted(idFloskelgruppenart));

		dto.Hauptgruppe = eintrag.schluessel;
	}

	private static void updateFarbe(final DTOFloskelgruppen dto, final Object value) throws ApiOperationException {
		try {
			final RGBFarbe farbe = JSONMapper.mapper.convertValue(value, RGBFarbe.class);
			dto.Farbe = farbe.asDecimal();
		} catch (final IllegalArgumentException e) {
			throw new ApiOperationException(Status.BAD_REQUEST, "Die Farbe entspricht nicht dem richtigen Datentyp: %s.".formatted(e.getMessage()));
		}
	}

}
