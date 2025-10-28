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
 * Diese Klasse beinhaltet die Schema-Definition für die Tabelle UV_StundenplanRegelparameter.
 */
public class Tabelle_UV_StundenplanRegelparameter extends SchemaTabelle {

	/** Die Definition der Tabellenspalte Regel_ID */
	public final SchemaTabelleSpalte col_Regel_ID = add("Regel_ID", SchemaDatentypen.BIGINT, true)
			.setNotNull()
			.setJavaComment("ID der Regel des Parameters");

	/** Die Definition der Tabellenspalte Nummer */
	public final SchemaTabelleSpalte col_Nummer = add("Nummer", SchemaDatentypen.INT, true)
			.setNotNull()
			.setJavaComment("Die Nummer des Parameters der Regel, beginnend bei 1");

	/** Die Definition der Tabellenspalte Parameter */
	public final SchemaTabelleSpalte col_Parameter = add("Parameter", SchemaDatentypen.BIGINT, false)
			.setNotNull()
			.setJavaComment("Der Wert des Parameters der Regel (hängt vom Typ der Regel ab)");


	/** Die Definition des Fremdschlüssels UV_StundenplanRegelparameter_UV_StundenplanRegel_FK */
	public final SchemaTabelleFremdschluessel fk_UV_StundenplanRegelparameter_UV_StundenplanRegel_FK = addForeignKey(
			"UV_StundenplanRegelparameter_UV_StundenplanRegel_FK",
			/* OnUpdate: */ SchemaFremdschluesselAktionen.CASCADE,
			/* OnDelete: */ SchemaFremdschluesselAktionen.CASCADE,
			new Pair<>(col_Regel_ID, Schema.tab_UV_StundenplanRegeln.col_ID)
	);


	/** Die Definition des Non-Unique-Index UV_StundenplanRegelnparameter_IDX_StundenplanRegel_ID_Nummer_Parameter */
	public final SchemaTabelleIndex index_UV_StundenplanRegelnparameter_IDX_StundenplanRegel_ID_Nummer_Parameter =
			addIndex("UV_StundenplanRegelnparameter_IDX_StundenplanRegel_ID_NrParam",
					col_Regel_ID,
					col_Nummer,
					col_Parameter
			);


	/**
	 * Erstellt die Schema-Definition für die Tabelle UV_StundenplanRegelparameter.
	 */
	public Tabelle_UV_StundenplanRegelparameter() {
		super("UV_StundenplanRegelparameter", SchemaRevisionen.REV_48);
		setMigrate(false);
		setImportExport(true);
		setJavaSubPackage("uv");
		setJavaClassName("DTOUvStundenplanRegelparameter");
		setJavaComment("Tabelle für die Regelparameter der Stundenplankonfigurationen der UV");
	}

}
