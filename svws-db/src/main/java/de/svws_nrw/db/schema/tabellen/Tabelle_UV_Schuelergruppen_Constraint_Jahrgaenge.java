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
 * Diese Klasse beinhaltet die Schema-Definition für die Tabelle UV_Schuelergruppen_Constraint_Jahrgaenge.
 */
public class Tabelle_UV_Schuelergruppen_Constraint_Jahrgaenge extends SchemaTabelle {

	/** Die Definition der Tabellenspalte Schuelergruppe_ID */
	public SchemaTabelleSpalte col_Schuelergruppe_ID = add("Schuelergruppe_ID", SchemaDatentypen.BIGINT, true)
			.setNotNull()
			.setJavaComment("ID der Schülergruppe");

	/** Die Definition der Tabellenspalte Jahrgang_ID */
	public SchemaTabelleSpalte col_Jahrgang_ID = add("Jahrgang_ID", SchemaDatentypen.BIGINT, true)
			.setNotNull()
			.setJavaComment("ID des zugeordneten Jahrgangs");

	/** Die Definition der Tabellenspalte Planungsabschnitt_ID */
	public SchemaTabelleSpalte col_Planungsabschnitt_ID = add("Planungsabschnitt_ID", SchemaDatentypen.BIGINT, false)
			.setNotNull()
			.setJavaComment("Die ID des Planungsabschnitts als Fremdschlüssel auf die Tabelle UV_Planungsabschnitte");


	/** Die Definition des Fremdschlüssels auf UV_Schuelergruppen */
	public SchemaTabelleFremdschluessel fk_UVSchuelergruppenJahrgaenge_UVSchuelergruppen_FK = addForeignKey(
			"UVSchuelergruppenJahrgaenge_UVSchuelergruppen_FK",
			/* OnUpdate: */ SchemaFremdschluesselAktionen.CASCADE,
			/* OnDelete: */ SchemaFremdschluesselAktionen.CASCADE,
			new Pair<>(col_Schuelergruppe_ID, Schema.tab_UV_Schuelergruppen.col_ID),
			new Pair<>(col_Planungsabschnitt_ID, Schema.tab_UV_Schuelergruppen.col_Planungsabschnitt_ID)
	);

    /** Fremdschlüssel auf die Tabelle EigeneSchule_Jahrgaenge */
    public SchemaTabelleFremdschluessel fk_UVSchuelergruppenJahrgaenge_EigeneSchuleJahrgaenge_FK = addForeignKey(
            "UVSchuelergruppenJahrgaenge_EigeneSchuleJahrgaenge_FK",
            /* OnUpdate: */ SchemaFremdschluesselAktionen.CASCADE,
            /* OnDelete: */ SchemaFremdschluesselAktionen.CASCADE,
            new Pair<>(col_Jahrgang_ID, Schema.tab_EigeneSchule_Jahrgaenge.col_ID)
    );


	/**
	 * Erstellt die Schema-Definition für die Tabelle UV_Schuelergruppen_Constraint_Jahrgaenge.
	 */
	public Tabelle_UV_Schuelergruppen_Constraint_Jahrgaenge() {
		super("UV_Schuelergruppen_Constraint_Jahrgaenge", SchemaRevisionen.REV_48);
		setMigrate(false);
		setImportExport(true);
		setJavaSubPackage("uv");
		setJavaClassName("DTOUvSchuelergruppeConstraintJahrgang");
		setJavaComment("Tabelle für die Zuordnung von Jahrgängen zu Schülergruppen eines Planungsabschnitts der Unterrichtsverteilung (UV)");
	}

}
