package de.svws_nrw.db.schema.tabellen;

import de.svws_nrw.asd.adt.Pair;
import de.svws_nrw.db.converter.current.Boolean01Converter;
import de.svws_nrw.db.schema.Schema;
import de.svws_nrw.db.schema.SchemaDatentypen;
import de.svws_nrw.db.schema.SchemaFremdschluesselAktionen;
import de.svws_nrw.db.schema.SchemaRevisionen;
import de.svws_nrw.db.schema.SchemaTabelle;
import de.svws_nrw.db.schema.SchemaTabelleFremdschluessel;
import de.svws_nrw.db.schema.SchemaTabelleIndex;
import de.svws_nrw.db.schema.SchemaTabelleSpalte;

/**
 * Diese Klasse beinhaltet die Schema-Definition für die Tabelle Gost_Jahrgang_Faecher.
 */
public class Tabelle_Gost_Jahrgang_Faecher extends SchemaTabelle {

	/** Die Definition der Tabellenspalte Abi_Jahrgang */
	public final SchemaTabelleSpalte col_Abi_Jahrgang = add("Abi_Jahrgang", SchemaDatentypen.INT, true)
			.setNotNull()
			.setJavaComment("Schuljahr, in welchem der Jahrgang das Abitur macht");

	/** Die Definition der Tabellenspalte Fach_ID */
	public final SchemaTabelleSpalte col_Fach_ID = add("Fach_ID", SchemaDatentypen.BIGINT, true)
			.setNotNull()
			.setJavaComment("ID des Faches in der Fächertabelle");

	/** Die Definition der Tabellenspalte WaehlbarEF1 */
	public final SchemaTabelleSpalte col_WaehlbarEF1 = add("WaehlbarEF1", SchemaDatentypen.INT, false)
			.setDefault("1")
			.setNotNull()
			.setConverter(Boolean01Converter.class)
			.setJavaComment("Belegung des Faches in der EF.1 möglich: 1 - true, 0 - false ");

	/** Die Definition der Tabellenspalte WaehlbarEF2 */
	public final SchemaTabelleSpalte col_WaehlbarEF2 = add("WaehlbarEF2", SchemaDatentypen.INT, false)
			.setDefault("1")
			.setNotNull()
			.setConverter(Boolean01Converter.class)
			.setJavaComment("Belegung des Faches in der EF.2 möglich: 1 - true, 0 - false");

	/** Die Definition der Tabellenspalte WaehlbarQ11 */
	public final SchemaTabelleSpalte col_WaehlbarQ11 = add("WaehlbarQ11", SchemaDatentypen.INT, false)
			.setDefault("1")
			.setNotNull()
			.setConverter(Boolean01Converter.class)
			.setJavaComment("Belegung des Faches in der Q1.1 möglich: 1 - true, 0 - false");

	/** Die Definition der Tabellenspalte WaehlbarQ12 */
	public final SchemaTabelleSpalte col_WaehlbarQ12 = add("WaehlbarQ12", SchemaDatentypen.INT, false)
			.setDefault("1")
			.setNotNull()
			.setConverter(Boolean01Converter.class)
			.setJavaComment("Belegung des Faches in der Q1.2 möglich: 1 - true, 0 - false");

	/** Die Definition der Tabellenspalte WaehlbarQ21 */
	public final SchemaTabelleSpalte col_WaehlbarQ21 = add("WaehlbarQ21", SchemaDatentypen.INT, false)
			.setDefault("1")
			.setNotNull()
			.setConverter(Boolean01Converter.class)
			.setJavaComment("Belegung des Faches in der Q2.1 möglich: 1 - true, 0 - false");

	/** Die Definition der Tabellenspalte WaehlbarQ22 */
	public final SchemaTabelleSpalte col_WaehlbarQ22 = add("WaehlbarQ22", SchemaDatentypen.INT, false)
			.setDefault("1")
			.setNotNull()
			.setConverter(Boolean01Converter.class)
			.setJavaComment("Belegung des Faches in der Q2.2 möglich: 1 - true, 0 - false");

	/** Die Definition der Tabellenspalte WaehlbarAbiGK */
	public final SchemaTabelleSpalte col_WaehlbarAbiGK = add("WaehlbarAbiGK", SchemaDatentypen.INT, false)
			.setDefault("1")
			.setNotNull()
			.setConverter(Boolean01Converter.class)
			.setJavaComment("Eintrag, ob das Fach als 3. oder 4. Abiturfach gewählt werden kann: 1 - true, 0 - false");

	/** Die Definition der Tabellenspalte WaehlbarAbiLK */
	public final SchemaTabelleSpalte col_WaehlbarAbiLK = add("WaehlbarAbiLK", SchemaDatentypen.INT, false)
			.setDefault("1")
			.setNotNull()
			.setConverter(Boolean01Converter.class)
			.setJavaComment("Eintrag, ob das Fach als 1. oder 2. Abiturfach (LK) gewählt werden kann: 1 - true, 0 - false");

	/** Die Definition der Tabellenspalte WochenstundenQPhase */
	public final SchemaTabelleSpalte col_WochenstundenQPhase = add("WochenstundenQPhase", SchemaDatentypen.INT, false)
			.setJavaComment("Anzahl der Unterrichtsstunden in 45-minuten-Einheiten des Faches in der Qualifikationsphase");


	/** Die Definition des Fremdschlüssels Gost_Jahrgang_Faecher_Abi_Jahrgang_FK */
	public final SchemaTabelleFremdschluessel fk_Gost_Jahrgang_Faecher_Abi_Jahrgang_FK = addForeignKey(
			"Gost_Jahrgang_Faecher_Abi_Jahrgang_FK",
			/* OnUpdate: */ SchemaFremdschluesselAktionen.CASCADE,
			/* OnDelete: */ SchemaFremdschluesselAktionen.CASCADE,
			new Pair<>(col_Abi_Jahrgang, Schema.tab_Gost_Jahrgangsdaten.col_Abi_Jahrgang)
	);

	/** Die Definition des Fremdschlüssels Gost_Jahrgang_Faecher_Fach_ID_FK */
	public final SchemaTabelleFremdschluessel fk_Gost_Jahrgang_Faecher_Fach_ID_FK = addForeignKey(
			"Gost_Jahrgang_Faecher_Fach_ID_FK",
			/* OnUpdate: */ SchemaFremdschluesselAktionen.CASCADE,
			/* OnDelete: */ SchemaFremdschluesselAktionen.CASCADE,
			new Pair<>(col_Fach_ID, Schema.tab_EigeneSchule_Faecher.col_ID)
	);


	/** Die Definition des Non-Unique-Index Gost_Jahrgang_Faecher_IDX_Abi_Jahrgang */
	public final SchemaTabelleIndex index_Gost_Jahrgang_Faecher_IDX_Abi_Jahrgang = addIndex("Gost_Jahrgang_Faecher_IDX_Abi_Jahrgang",
			col_Abi_Jahrgang
	);

	/** Die Definition des Non-Unique-Index Gost_Jahrgang_Faecher_IDX_Abi_Jahrgang_Fach_ID */
	public final SchemaTabelleIndex index_Gost_Jahrgang_Faecher_IDX_Abi_Jahrgang_Fach_ID = addIndex("Gost_Jahrgang_Faecher_IDX_Abi_Jahrgang_Fach_ID",
			col_Abi_Jahrgang,
			col_Fach_ID
	);


	/**
	 * Erstellt die Schema-Defintion für die Tabelle Gost_Jahrgang_Faecher.
	 */
	public Tabelle_Gost_Jahrgang_Faecher() {
		super("Gost_Jahrgang_Faecher", SchemaRevisionen.REV_4);
		setMigrate(false);
		setImportExport(true);
		setJavaSubPackage("gost");
		setJavaClassName("DTOGostJahrgangFaecher");
		setJavaComment("Gymnasiale Oberstufe - Jahrgangsdaten: Informationen zu den wählbaren Fächern der einzelnen Jahrgänge");
	}

}
