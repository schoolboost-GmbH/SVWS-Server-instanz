package de.svws_nrw.db.schema.tabellen;

import de.svws_nrw.asd.adt.Pair;
import de.svws_nrw.db.schema.Schema;
import de.svws_nrw.db.schema.SchemaDatentypen;
import de.svws_nrw.db.schema.SchemaFremdschluesselAktionen;
import de.svws_nrw.db.schema.SchemaRevisionen;
import de.svws_nrw.db.schema.SchemaTabelle;
import de.svws_nrw.db.schema.SchemaTabelleFremdschluessel;
import de.svws_nrw.db.schema.SchemaTabelleIndex;
import de.svws_nrw.db.schema.SchemaTabelleSpalte;

/**
 * Diese Klasse beinhaltet die Schema-Definition für die Tabelle UV_StundenplanErgebnisse.
 */
public class Tabelle_UV_StundenplanErgebnisse extends SchemaTabelle {

	/** Die Definition der Tabellenspalte ID */
	public final SchemaTabelleSpalte col_ID = add("ID", SchemaDatentypen.BIGINT, true)
			.setNotNull()
			.setJavaComment("ID der Stundenplanergebnisses (generiert)");

	/** Die Definition der Tabellenspalte StundenplanKonfiguration_ID */
	public final SchemaTabelleSpalte col_StundenplanKonfiguration_ID = add("StundenplanKonfiguration_ID", SchemaDatentypen.BIGINT, false)
			.setNotNull()
			.setJavaComment("ID der Stundenplankonfiguration");


	/** Die Definition des Fremdschlüssels UVStundenplanErgebnisse_UVStundenplanKonfiguration_FK */
	public final SchemaTabelleFremdschluessel fk_UVStundenplanErgebnisse_UVStundenplanKonfiguration_FK = addForeignKey(
			"UVStundenplanErgebnisse_UVStundenplanKonfiguration_FK",
			/* OnUpdate: */ SchemaFremdschluesselAktionen.CASCADE,
			/* OnDelete: */ SchemaFremdschluesselAktionen.CASCADE,
			new Pair<>(col_StundenplanKonfiguration_ID, Schema.tab_UV_StundenplanKonfigurationen.col_ID));


	/** Die Definition des Non-Unique-Index UV_StundenplanErgebnisse_IDX_StundenplanKonfiguration_ID */
	public final SchemaTabelleIndex index_UV_StundenplanErgebnisse_IDX_StundenplanKonfiguration_ID = addIndex("UV_StundenplanErgebnisse_IDX_StundenplanKonfiguration_ID",
			col_StundenplanKonfiguration_ID
	);


	/**
	 * Erstellt die Schema-Definition für die Tabelle UV_StundenplanErgebnisse.
	 */
	public Tabelle_UV_StundenplanErgebnisse() {
		super("UV_StundenplanErgebnisse", SchemaRevisionen.REV_48);
		setMigrate(false);
		setImportExport(true);
		setPKAutoIncrement();
		setJavaSubPackage("uv");
		setJavaClassName("DTOUvStundenplanErgebnis");
		setJavaComment("Tabelle für die Stundenplanergebnisse der UV");
	}

}
