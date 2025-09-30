package de.svws_nrw.db.dto.current.uv;

import java.io.Serializable;

/**
 * Diese Klasse dient als DTO für den Primärschlüssel der Datenbanktabelle UV_Stundentafeln_Faecher.
 * Sie wurde automatisch per Skript generiert und sollte nicht verändert werden,
 * da sie aufgrund von Änderungen am DB-Schema ggf. neu generiert und überschrieben wird.
 */
public final class DTOUvStundentafelFachPK implements Serializable {

	/** Die UID für diese Klasse */
	private static final long serialVersionUID = 1L;

	/** Fremdschlüssel auf die Stundentafel (Tabelle UV_Stundentafeln) */
	public long Stundentafel_ID;

	/** Fremdschlüssel auf das Fach der UV (Tabelle UV_Faecher) */
	public long Fach_ID;

	/**
	 * Erstellt ein neues Objekt der Klasse DTOUvStundentafelFachPK ohne eine Initialisierung der Attribute.
	 */
	@SuppressWarnings("unused")
	private DTOUvStundentafelFachPK() {
	}

	/**
	 * Erstellt ein neues Objekt der Klasse DTOUvStundentafelFachPK.
	 * @param Stundentafel_ID   der Wert für das Attribut Stundentafel_ID
	 * @param Fach_ID   der Wert für das Attribut Fach_ID
	 */
	public DTOUvStundentafelFachPK(final long Stundentafel_ID, final long Fach_ID) {
		this.Stundentafel_ID = Stundentafel_ID;
		this.Fach_ID = Fach_ID;
	}


	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DTOUvStundentafelFachPK other = (DTOUvStundentafelFachPK) obj;
		if (Stundentafel_ID != other.Stundentafel_ID)
			return false;
		return Fach_ID == other.Fach_ID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Long.hashCode(Stundentafel_ID);

		result = prime * result + Long.hashCode(Fach_ID);
		return result;
	}
}
