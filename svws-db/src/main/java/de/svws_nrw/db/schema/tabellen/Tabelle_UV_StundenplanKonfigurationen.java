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
 * Diese Klasse beinhaltet die Schema-Definition für die Tabelle UV_StundenplanKonfigurationen.
 */
public class Tabelle_UV_StundenplanKonfigurationen extends SchemaTabelle {

	/** Die Definition der Tabellenspalte ID */
	public SchemaTabelleSpalte col_ID = add("ID", SchemaDatentypen.BIGINT, true)
			.setNotNull()
			.setJavaComment("ID der Stundenplankonfiguration (generiert)");

	/** Die Definition der Tabellenspalte Planungsabschnitt_ID */
	public SchemaTabelleSpalte col_Planungsabschnitt_ID = add("Planungsabschnitt_ID", SchemaDatentypen.BIGINT, false)
			.setNotNull()
			.setJavaComment("ID des Planungsabschnitts");


	/** Die Definition des Fremdschlüssels UVStundenplanKonfigurationen_UVPlanungsabschnitt_FK */
	public SchemaTabelleFremdschluessel fk_UVStundenplanKonfigurationen_UVPlanungsabschnitt_FK = addForeignKey(
			"UVStundenplanKonfigurationen_UVPlanungsabschnitt_FK",
			/* OnUpdate: */ SchemaFremdschluesselAktionen.CASCADE,
			/* OnDelete: */ SchemaFremdschluesselAktionen.CASCADE,
			new Pair<>(col_Planungsabschnitt_ID, Schema.tab_UV_Planungsabschnitte.col_ID));


	/**
	 * Erstellt die Schema-Definition für die Tabelle UV_StundenplanKonfigurationen.
	 */
	public Tabelle_UV_StundenplanKonfigurationen() {
		super("UV_StundenplanKonfigurationen", SchemaRevisionen.REV_48);
		setMigrate(false);
		setImportExport(true);
		setPKAutoIncrement();
		setJavaSubPackage("uv");
		setJavaClassName("DTOUvStundenplanKonfiguration");
		setJavaComment("Tabelle für die Stundenplankonfigurationen der UV");
	}

}
