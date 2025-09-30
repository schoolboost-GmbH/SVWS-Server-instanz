package de.svws_nrw.db.schema.tabellen;

import de.svws_nrw.asd.adt.Pair;
import de.svws_nrw.db.converter.current.DatumConverter;
import de.svws_nrw.db.schema.Schema;
import de.svws_nrw.db.schema.SchemaDatentypen;
import de.svws_nrw.db.schema.SchemaFremdschluesselAktionen;
import de.svws_nrw.db.schema.SchemaRevisionen;
import de.svws_nrw.db.schema.SchemaTabelle;
import de.svws_nrw.db.schema.SchemaTabelleFremdschluessel;
import de.svws_nrw.db.schema.SchemaTabelleSpalte;

/**
 * Diese Klasse beinhaltet die Schema-Definition für die Tabelle UV_Faecher.
 */
public class Tabelle_UV_Faecher extends SchemaTabelle {

	/** Die Definition der Tabellenspalte ID */
	public SchemaTabelleSpalte col_ID = add("ID", SchemaDatentypen.BIGINT, true)
			.setNotNull()
			.setJavaComment("ID des UV-Faches (generiert, planungsspezifisch)");

	/** Die Definition der Tabellenspalte Fach_ID */
	public SchemaTabelleSpalte col_Fach_ID = add("Fach_ID", SchemaDatentypen.BIGINT, false)
			.setNotNull()
			.setJavaComment("Die ID des Faches als Fremdschlüssel auf die Tabelle EigeneSchule_Faecher");

	/** Die Definition der Tabellenspalte GueltigAb */
	public SchemaTabelleSpalte col_GueltigAb = add("GueltigAb", SchemaDatentypen.DATE, false)
			.setDefault("1899-01-01")
			.setNotNull()
			.setConverter(DatumConverter.class)
			.setJavaComment("Datum, ab dem das Fach gültig ist");

	/** Die Definition der Tabellenspalte GueltigBis */
	public SchemaTabelleSpalte col_GueltigBis = add("GueltigBis", SchemaDatentypen.DATE, false)
			.setConverter(DatumConverter.class)
			.setJavaComment("Datum, bis zu dem das Fach gültig ist. Ist kein Datum gesetzt, gilt das Fach unbegrenzt weiter.");


	/** Die Definition des Fremdschlüssels auf EigeneSchule_Faecher */
	public SchemaTabelleFremdschluessel fk_UVFaecher_EigeneSchuleFaecher_FK = addForeignKey(
			"UVFaecher_EigeneSchuleFaecher_FK",
			/* OnUpdate: */ SchemaFremdschluesselAktionen.CASCADE,
			/* OnDelete: */ SchemaFremdschluesselAktionen.CASCADE,
			new Pair<>(col_Fach_ID, Schema.tab_EigeneSchule_Faecher.col_ID)
	);

	/**
	 * Erstellt die Schema-Definition für die Tabelle UV_Faecher.
	 */
	public Tabelle_UV_Faecher() {
		super("UV_Faecher", SchemaRevisionen.REV_48);
		setMigrate(false);
		setImportExport(true);
		setPKAutoIncrement();
		setJavaSubPackage("uv");
		setJavaClassName("DTOUvFach");
		setJavaComment("Tabelle für die konkreten Fächer der UV");
	}

}
