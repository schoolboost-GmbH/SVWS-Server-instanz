package de.svws_nrw.db.dto.current.uv;

import java.io.Serializable;

/**
 * Diese Klasse dient als DTO für den Primärschlüssel der Datenbanktabelle UV_Lerngruppen_Schienen.
 * Sie wurde automatisch per Skript generiert und sollte nicht verändert werden,
 * da sie aufgrund von Änderungen am DB-Schema ggf. neu generiert und überschrieben wird.
 */
public final class DTOUvLerngruppeSchienePK implements Serializable {

	/** Die UID für diese Klasse */
	private static final long serialVersionUID = 1L;

	/** Eindeutige ID der Lerngruppe im Planungsabschnitt */
	public long Lerngruppe_ID;

	/** Fremdschlüssel auf die Schiene (Tabelle UV_Schienen) */
	public Long Schiene_ID;

	/**
	 * Erstellt ein neues Objekt der Klasse DTOUvLerngruppeSchienePK ohne eine Initialisierung der Attribute.
	 */
	@SuppressWarnings("unused")
	private DTOUvLerngruppeSchienePK() {
	}

	/**
	 * Erstellt ein neues Objekt der Klasse DTOUvLerngruppeSchienePK.
	 * @param Lerngruppe_ID   der Wert für das Attribut Lerngruppe_ID
	 * @param Schiene_ID   der Wert für das Attribut Schiene_ID
	 */
	public DTOUvLerngruppeSchienePK(final long Lerngruppe_ID, final Long Schiene_ID) {
		this.Lerngruppe_ID = Lerngruppe_ID;
		if (Schiene_ID == null) {
			throw new NullPointerException("Schiene_ID must not be null");
		}
		this.Schiene_ID = Schiene_ID;
	}


	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DTOUvLerngruppeSchienePK other = (DTOUvLerngruppeSchienePK) obj;
		if (Lerngruppe_ID != other.Lerngruppe_ID)
			return false;
		if (Schiene_ID == null) {
			if (other.Schiene_ID != null)
				return false;
		} else if (!Schiene_ID.equals(other.Schiene_ID))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Long.hashCode(Lerngruppe_ID);

		result = prime * result + ((Schiene_ID == null) ? 0 : Schiene_ID.hashCode());
		return result;
	}
}
