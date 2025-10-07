package de.svws_nrw.data.schule;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.svws_nrw.asd.data.jahrgang.JahrgaengeKatalogEintrag;
import de.svws_nrw.asd.types.jahrgang.Jahrgaenge;
import de.svws_nrw.core.data.schule.Floskel;
import de.svws_nrw.data.DataManagerRevised;
import de.svws_nrw.data.JSONMapper;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.dto.current.schild.faecher.DTOFach;
import de.svws_nrw.db.dto.current.schild.katalog.DTOFloskelgruppen;
import de.svws_nrw.db.dto.current.schild.katalog.DTOFloskeln;
import de.svws_nrw.db.schema.Schema;
import de.svws_nrw.db.utils.ApiOperationException;
import jakarta.ws.rs.core.Response;


/**
 * Diese Klasse erweitert den abstrakten {@link DataManagerRevised} für das Core-DTO {@link Floskel}.
 */
public final class DataFloskeln extends DataManagerRevised<String, DTOFloskeln, Floskel> {

	private final Map<String, DTOFach> faecherByKuerzel;

	/**
	 * Erstellt einen neuen Datenmanager mit der angegebenen Verbindung
	 *
	 * @param conn die Datenbank-Verbindung, welche vom Daten-Manager benutzt werden soll
	 */
	public DataFloskeln(final DBEntityManager conn) {
		super(conn);
		setAttributesRequiredOnCreation("kuerzel");
		setAttributesNotPatchable("kuerzel");
		this.faecherByKuerzel = getFaecherByKuerzel();
	}

	@Override
	protected String getID(final Map<String, Object> initAttributes) throws ApiOperationException {
		return JSONMapper.convertToString(initAttributes.get("kuerzel"), false, false, Schema.tab_Floskeln.col_Kuerzel.datenlaenge(), "kuerzel");
	}

	@Override
	public void checkBeforeCreation(final String newID, final Map<String, Object> initAttributes) throws ApiOperationException {
		final String kuerzel = JSONMapper.convertToString(initAttributes.get("kuerzel"), false, false, Schema.tab_Floskeln.col_Kuerzel.datenlaenge(), "kuerzel");
		final boolean alreadyUsed = this.conn
				.queryAll(DTOFloskeln.class).stream()
				.anyMatch(f -> kuerzel.equalsIgnoreCase(f.Kuerzel));

		if (alreadyUsed)
			throw new ApiOperationException(Response.Status.BAD_REQUEST, "Das Kürzel %s wird bereits verwendet.".formatted(kuerzel));
	}

	@Override
	protected void initDTO(final DTOFloskeln dto, final String newID, final Map<String, Object> initAttributes) {
		dto.Kuerzel = newID;
	}

	@Override
	protected Floskel map(final DTOFloskeln dto) {
		final Floskel floskel = new Floskel();
		floskel.kuerzel = dto.Kuerzel;
		floskel.text = dto.FloskelText;
		floskel.kuerzelFloskelgruppe = dto.FloskelGruppe;
		floskel.idFach = mapIdFach(dto.FloskelFach);
		floskel.niveau = dto.FloskelNiveau;
		floskel.idJahrgang = mapIdJahrgang(dto.FloskelJahrgang);
		return floskel;
	}

	@Override
	public Floskel getById(final String kuerzel) throws ApiOperationException {
		if (kuerzel == null)
			throw new ApiOperationException(Response.Status.BAD_REQUEST, "Das Kürzel der Floskel darf nicht null sein.");

		final DTOFloskeln dto = this.conn.queryByKey(DTOFloskeln.class, kuerzel);
		if (dto == null)
			throw new ApiOperationException(Response.Status.NOT_FOUND, "Es wurde keine Floskel mit dem Kürzel %s gefunden.".formatted(kuerzel));

		return map(dto);
	}

	@Override
	public List<Floskel> getAll() {
		return conn.queryAll(DTOFloskeln.class).stream()
				.map(this::map)
				.collect(Collectors.toList());
	}

	@Override
	protected void mapAttribute(final DTOFloskeln dto, final String name, final Object value, final Map<String, Object> map)
			throws ApiOperationException {
		switch (name) {
			case "kuerzel" -> {
				// nicht patchbar - wird beim neu erstellen in der Methode initDto gesetzt
			}
			case "text" -> dto.FloskelText = JSONMapper.convertToString(value, true, true, Schema.tab_Floskeln.col_FloskelText.datenlaenge(), name);
			case "kuerzelFloskelgruppe" -> updateFloskelgruppe(dto, name, value);
			case "idFach" -> updateFach(dto, name, value);
			case "niveau" -> dto.FloskelNiveau = JSONMapper.convertToString(value, true, true, Schema.tab_Floskeln.col_FloskelNiveau.datenlaenge(), name);
			case "idJahrgang" -> updateJahrgang(dto, name, value);
			default -> throw new ApiOperationException(Response.Status.BAD_REQUEST, "Die Daten des Patches enthalten das unbekannte Attribut %s.".formatted(name));
		}
	}

	// --- constructor ---

	private Map<String, DTOFach> getFaecherByKuerzel() {
		return this.conn
				.queryAll(DTOFach.class).stream()
				.collect(Collectors.toMap(f -> f.Kuerzel, f -> f));
	}

	// --- map ---

	private Long mapIdFach(final String kuerzelFach) {
		final DTOFach fach = faecherByKuerzel.get(kuerzelFach);
		return (fach == null) ? null : fach.ID;
	}

	private Long mapIdJahrgang(final String kuerzelJahrgang) {
		final int schuljahr = this.conn.getUser().schuleGetSchuljahresabschnitt().schuljahr;
		final Jahrgaenge jahrgang = Jahrgaenge.data().getWertByKuerzel(kuerzelJahrgang);
		return (jahrgang == null) ? null : jahrgang.daten(schuljahr).id;
	}

	// --- mapAttribute ---

	private void updateFloskelgruppe(final DTOFloskeln dto, final String name, final Object value) throws ApiOperationException {
		final String kuerzelFloskelgruppe = JSONMapper.convertToString(value, false, false, Schema.tab_Floskeln.col_FloskelGruppe.datenlaenge(), name);
		if (keineFloskelgruppeZumKuerzelVorhanden(kuerzelFloskelgruppe))
			throw new ApiOperationException(Response.Status.NOT_FOUND, "Keine Floskelgruppe mit dem Kürzel %s vorhanden.".formatted(kuerzelFloskelgruppe));

		dto.FloskelGruppe = kuerzelFloskelgruppe;
	}

	private boolean keineFloskelgruppeZumKuerzelVorhanden(final String kuerzelFloskelgruppe) {
		return (this.conn.queryByKey(DTOFloskelgruppen.class, kuerzelFloskelgruppe) == null);
	}

	private void updateFach(final DTOFloskeln dto, final String name, final Object value) throws ApiOperationException {
		final Long idFach = JSONMapper.convertToLong(value, true, name);
		if (idFach == null) {
			dto.FloskelFach = null;
			return;
		}

		final DTOFach fach = this.conn.queryByKey(DTOFach.class, idFach);
		if (fach == null)
			throw new ApiOperationException(Response.Status.NOT_FOUND, "Kein Fach mit der ID %d gefunden.".formatted(idFach));

		dto.FloskelFach = fach.Kuerzel;
	}

	private void updateJahrgang(final DTOFloskeln dto, final String name, final Object value) throws ApiOperationException {
		final Long idJahrgang = JSONMapper.convertToLong(value, true, name);
		if (idJahrgang == null) {
			dto.FloskelJahrgang = null;
			return;
		}

		final JahrgaengeKatalogEintrag jahrgang = Jahrgaenge.data().getEintragByID(idJahrgang);
		if (jahrgang == null)
			throw new ApiOperationException(Response.Status.NOT_FOUND, "Kein Jahrgang mit der ID %d gefunden.".formatted(idJahrgang));

		dto.FloskelJahrgang = jahrgang.kuerzel;
	}

}
