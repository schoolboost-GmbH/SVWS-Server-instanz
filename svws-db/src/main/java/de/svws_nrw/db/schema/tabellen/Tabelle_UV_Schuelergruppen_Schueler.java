package de.svws_nrw.db.schema.tabellen;

import de.svws_nrw.asd.adt.Pair;
import de.svws_nrw.db.schema.Schema;
import de.svws_nrw.db.schema.SchemaDatentypen;
import de.svws_nrw.db.schema.SchemaFremdschluesselAktionen;
import de.svws_nrw.db.schema.SchemaRevisionen;
import de.svws_nrw.db.schema.SchemaTabelle;
import de.svws_nrw.db.schema.SchemaTabelleFremdschluessel;
import de.svws_nrw.db.schema.SchemaTabelleSpalte;

/**
 * Diese Klasse beinhaltet die Schema-Definition für die Tabelle UV_Schuelergruppen_Schueler.
 */
public class Tabelle_UV_Schuelergruppen_Schueler extends SchemaTabelle {

	/** Die Definition der Tabellenspalte Schuelergruppe_ID */
	public SchemaTabelleSpalte col_Schuelergruppe_ID = add("Schuelergruppe_ID", SchemaDatentypen.BIGINT, true)
			.setNotNull()
			.setJavaComment("Die ID der Schülergruppe als Fremdschlüssel auf die Tabelle UV_Schuelergruppen");

	/** Die Definition der Tabellenspalte Schueler_ID */
	public SchemaTabelleSpalte col_Schueler_ID = add("Schueler_ID", SchemaDatentypen.BIGINT, true)
			.setNotNull()
			.setJavaComment("Die ID des Schülers als Fremdschlüssel auf die Tabelle Schueler");

	/** Die Definition der Tabellenspalte Planungsabschnitt_ID */
	public SchemaTabelleSpalte col_Planungsabschnitt_ID = add("Planungsabschnitt_ID", SchemaDatentypen.BIGINT, false)
			.setNotNull()
			.setJavaComment("Die ID des Planungsabschnitts als Fremdschlüssel auf die Tabelle UV_Planungsabschnitte");

	/** Die Definition des Fremdschlüssels auf UV_Schuelergruppen */
	public SchemaTabelleFremdschluessel fk_UVSchuelergruppenSchueler_UVSchuelergruppe_FK = addForeignKey(
			"UVSchuelergruppenSchueler_UVSchuelergruppe_FK",
			/* OnUpdate: */ SchemaFremdschluesselAktionen.CASCADE,
			/* OnDelete: */ SchemaFremdschluesselAktionen.CASCADE,
			new Pair<>(col_Schuelergruppe_ID, Schema.tab_UV_Schuelergruppen.col_ID),
			new Pair<>(col_Planungsabschnitt_ID, Schema.tab_UV_Schuelergruppen.col_Planungsabschnitt_ID)
	);

	/** Die Definition des Fremdschlüssels auf Planungsabschnitte_Schueler */
	public SchemaTabelleFremdschluessel fk_UVSchuelergruppenSchueler_UVSchuelerPlanungsabschnitte_FK = addForeignKey(
			"UVSchuelergruppenSchueler_UVSchuelerPlanungsabschnitte_FK",
			/* OnUpdate: */ SchemaFremdschluesselAktionen.CASCADE,
			/* OnDelete: */ SchemaFremdschluesselAktionen.CASCADE,
			new Pair<>(col_Planungsabschnitt_ID, Schema.tab_UV_Planungsabschnitte_Schueler.col_Planungsabschnitt_ID),
			new Pair<>(col_Schueler_ID, Schema.tab_UV_Planungsabschnitte_Schueler.col_Schueler_ID)
	);

	/**
	 * Erstellt die Schema-Definition für die Tabelle UV_Schuelergruppen_Schueler.
	 */
	public Tabelle_UV_Schuelergruppen_Schueler() {
		super("UV_Schuelergruppen_Schueler", SchemaRevisionen.REV_48);
		setMigrate(false);
		setImportExport(true);
		setJavaSubPackage("uv");
		setJavaClassName("DTOUvSchuelergruppeSchueler");
		setJavaComment("Tabelle für die Zuordnung von Schülern zu Schülergruppen eines Planungsabschnitts der Unterrichtsverteilung (UV)");
	}

}
