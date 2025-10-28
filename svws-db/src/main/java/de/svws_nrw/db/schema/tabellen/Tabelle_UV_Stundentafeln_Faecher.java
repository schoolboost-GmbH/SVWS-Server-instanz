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
 * Diese Klasse beinhaltet die Schema-Definition für die Tabelle UV_Stundentafeln_Faecher.
 */
public class Tabelle_UV_Stundentafeln_Faecher extends SchemaTabelle {

	/** Die Definition der Tabellenspalte Stundentafel_ID */
	public final SchemaTabelleSpalte col_Stundentafel_ID = add("Stundentafel_ID", SchemaDatentypen.BIGINT, true)
			.setNotNull()
			.setJavaComment("Fremdschlüssel auf die Stundentafel (Tabelle UV_Stundentafeln)");

	/** Die Definition der Tabellenspalte Abschnitt */
	public final SchemaTabelleSpalte col_Abschnitt = add("Abschnitt", SchemaDatentypen.INT, false)
			.setNotNull()
			.setJavaComment("Abschnitt des Schuljahres");

	/** Die Definition der Tabellenspalte Fach_ID */
	public final SchemaTabelleSpalte col_Fach_ID = add("Fach_ID", SchemaDatentypen.BIGINT, true)
			.setNotNull()
			.setJavaComment("Fremdschlüssel auf das Fach der UV (Tabelle UV_Faecher)");

	/** Die Definition der Tabellenspalte Wochenstunden */
	public final SchemaTabelleSpalte col_Wochenstunden = add("Wochenstunden", SchemaDatentypen.FLOAT, false)
			.setDefault("3")
			.setNotNull()
			.setJavaComment("Die Anzahl der Wochenstunden für das Fach");

	/** Die Definition der Tabellenspalte DavonErgaenzungsstunden */
	public final SchemaTabelleSpalte col_DavonErgaenzungsstunden = add("DavonErgaenzungsstunden", SchemaDatentypen.FLOAT, false)
			.setDefault("0")
			.setNotNull()
			.setJavaComment("Die Anzahl der Ergänzungsstunden für das Fach (in Wochenstunden enthalten)");



	/** Die Definition des Fremdschlüssels auf UV_Stundentafeln */
	public final SchemaTabelleFremdschluessel fk_UVStundentafelnFaecher_UVStundentafeln_FK = addForeignKey(
			"UVStundentafelnFaecher_UVStundentafeln_FK",
			/* OnUpdate: */ SchemaFremdschluesselAktionen.CASCADE,
			/* OnDelete: */ SchemaFremdschluesselAktionen.CASCADE,
			new Pair<>(col_Stundentafel_ID, Schema.tab_UV_Stundentafeln.col_ID)
	);

	/** Die Definition des Fremdschlüssels auf UV_Faecher */
	public final SchemaTabelleFremdschluessel fk_UVStundentafelnFaecher_UVFaecher_FK = addForeignKey(
			"UVStundentafelnFaecher_UVFaecher_FK",
			/* OnUpdate: */ SchemaFremdschluesselAktionen.CASCADE,
			/* OnDelete: */ SchemaFremdschluesselAktionen.CASCADE,
			new Pair<>(col_Fach_ID, Schema.tab_UV_Faecher.col_ID)
	);


	/**
	 * Erstellt die Schema-Definition für die Tabelle UV_Stundentafeln_Faecher.
	 */
	public Tabelle_UV_Stundentafeln_Faecher() {
		super("UV_Stundentafeln_Faecher", SchemaRevisionen.REV_48);
		setMigrate(false);
		setImportExport(true);
		setJavaSubPackage("uv");
		setJavaClassName("DTOUvStundentafelFach");
		setJavaComment("Tabelle für die Zuordnung von Fächern zu Stundentafeln der Unterrichtsverteilung (UV)");
	}

}
