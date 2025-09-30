package de.svws_nrw.db.schema.tabellen;

import de.svws_nrw.asd.adt.Pair;
import de.svws_nrw.db.schema.Schema;
import de.svws_nrw.db.schema.SchemaDatentypen;
import de.svws_nrw.db.schema.SchemaFremdschluesselAktionen;
import de.svws_nrw.db.schema.SchemaRevisionen;
import de.svws_nrw.db.schema.SchemaTabelle;
import de.svws_nrw.db.schema.SchemaTabelleFremdschluessel;
import de.svws_nrw.db.schema.SchemaTabelleSpalte;
import de.svws_nrw.db.schema.SchemaTabelleUniqueIndex;

/**
 * Diese Klasse beinhaltet die Schema-Definition für die Tabelle UV_Schuelergruppen.
 */
public class Tabelle_UV_Schuelergruppen extends SchemaTabelle {

	/** Die Definition der Tabellenspalte ID */
	public SchemaTabelleSpalte col_ID = add("ID", SchemaDatentypen.BIGINT, true)
			.setNotNull()
			.setJavaComment("ID der Schülergruppe (generiert, planungsspezifisch)");

	/** Die Definition der Tabellenspalte Planungsabschnitt_ID */
	public SchemaTabelleSpalte col_Planungsabschnitt_ID = add("Planungsabschnitt_ID", SchemaDatentypen.BIGINT, false)
			.setNotNull()
			.setJavaComment("Die ID des Planungsabschnitts als Fremdschlüssel auf die Tabelle UV_Planungsabschnitte");

	/** Die Definition der Tabellenspalte Bezeichnung */
	public SchemaTabelleSpalte col_Bezeichnung = add("Bezeichnung", SchemaDatentypen.VARCHAR, false).setDatenlaenge(50)
			.setNotNull()
			.setJavaComment("Eine Beschreibung / Kommentar zu dieser Schülergruppe");


	/** Die Definition des Fremdschlüssels auf UV_Planungsabschnitte */
	public SchemaTabelleFremdschluessel fk_UVSchuelergruppen_UVPlanungsabschnitte_FK = addForeignKey(
			"UVSchuelergruppen_UVPlanungsabschnitte_FK",
			/* OnUpdate: */ SchemaFremdschluesselAktionen.CASCADE,
			/* OnDelete: */ SchemaFremdschluesselAktionen.CASCADE,
			new Pair<>(col_Planungsabschnitt_ID, Schema.tab_UV_Planungsabschnitte.col_ID)
	);

	/** Unique-Index für die Kombination ID und Planungsabschnitt_ID, benötigt durch 2-teiligen FK */
	public SchemaTabelleUniqueIndex unique_UVSchuelergruppen_UC1 = addUniqueIndex("UVSchuelergruppen_UC1",
			col_ID, col_Planungsabschnitt_ID
	 );


	/**
	 * Erstellt die Schema-Definition für die Tabelle UV_Schuelergruppen.
	 */
	public Tabelle_UV_Schuelergruppen() {
		super("UV_Schuelergruppen", SchemaRevisionen.REV_48);
		setMigrate(false);
		setImportExport(true);
		setPKAutoIncrement();
		setJavaSubPackage("uv");
		setJavaClassName("DTOUvSchuelergruppe");
		setJavaComment("Tabelle für die Schülergruppen eines Planungsabschnitts der Unterrichtsverteilung (UV)");
	}

}
