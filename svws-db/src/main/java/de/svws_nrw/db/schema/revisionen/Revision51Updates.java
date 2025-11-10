package de.svws_nrw.db.schema.revisionen;

import de.svws_nrw.db.schema.Schema;
import de.svws_nrw.db.schema.SchemaRevisionUpdateSQL;
import de.svws_nrw.db.schema.SchemaRevisionen;

/**
 * Diese Klasse enthält die SQL-Befehle für Revisions-Updates
 * auf Revision 46.
 */
public final class Revision51Updates extends SchemaRevisionUpdateSQL {

	/**
	 * Erzeugt eine Instanz für die Revisions-Updates für Revision 46.
	 */
	public Revision51Updates() {
		super(SchemaRevisionen.REV_51);
		add("K_Vermerkart: Hinzufügen des Vermerktyps 'Sportbefreiung', falls dieser noch nicht existiert.",
				"""
				INSERT INTO K_Vermerkart (ID, Bezeichnung, Sortierung, Sichtbar, Aenderbar)
				SELECT
				    (SELECT COALESCE(MAX(ID), 0) + 1 FROM K_Vermerkart) AS ID,
				    'Sportbefreiung',
				    1,
				    '+',
				    '+'
				FROM DUAL
				WHERE NOT EXISTS (SELECT 1 FROM K_Vermerkart WHERE Bezeichnung = 'Sportbefreiung');
				""",
				Schema.tab_K_Vermerkart
		);
		add("SchuelerVermerke: Einfügen von Vermerken für Sportbefreiungen bei Schülern, die eine Sportbefreiung haben.",
				"""
				INSERT INTO SchuelerVermerke (ID, Schueler_ID, VermerkArt_ID, Datum, Bemerkung, AngelegtVon, GeaendertVon)
				SELECT
				    (SELECT COALESCE(MAX(ID), 0) FROM SchuelerVermerke) + ROW_NUMBER() OVER (),
				    s.ID,
				    (SELECT ID FROM K_Vermerkart WHERE Bezeichnung = 'Sportbefreiung'),
				    NULL,
				    k.Bezeichnung,
				    NULL,
				    NULL
				FROM Schueler s
				JOIN K_Sportbefreiung k ON s.Sportbefreiung_ID = k.ID
				WHERE s.Sportbefreiung_ID IS NOT NULL
				  AND NOT EXISTS (
				    SELECT 1 FROM SchuelerVermerke v
				    WHERE v.Schueler_ID = s.ID
				      AND v.VermerkArt_ID = (SELECT ID FROM K_Vermerkart WHERE Bezeichnung = 'Sportbefreiung')
				      AND v.Bemerkung = k.Bezeichnung
				);
				""",
				Schema.tab_SchuelerVermerke, Schema.tab_K_Sportbefreiung, Schema.tab_K_Vermerkart, Schema.tab_Schueler
		);
	}

}
