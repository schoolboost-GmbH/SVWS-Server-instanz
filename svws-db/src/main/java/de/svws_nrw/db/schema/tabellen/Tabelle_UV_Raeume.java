package de.svws_nrw.db.schema.tabellen;

import de.svws_nrw.db.converter.current.DatumConverter;
import de.svws_nrw.db.schema.SchemaDatentypen;
import de.svws_nrw.db.schema.SchemaRevisionen;
import de.svws_nrw.db.schema.SchemaTabelle;
import de.svws_nrw.db.schema.SchemaTabelleSpalte;

/**
 * Diese Klasse beinhaltet die Schema-Definition für die Tabelle UV_Raeume.
 */
public class Tabelle_UV_Raeume extends SchemaTabelle {

	/** Die Definition der Tabellenspalte ID */
	public final SchemaTabelleSpalte col_ID = add("ID", SchemaDatentypen.BIGINT, true)
			.setNotNull()
			.setJavaComment("ID des UV-Raums (generiert, planungsspezifisch)");

	/** Die Definition der Tabellenspalte Kuerzel */
	public final SchemaTabelleSpalte col_Kuerzel = add("Kuerzel", SchemaDatentypen.VARCHAR, false).setDatenlaenge(20)
			.setNotNull()
			.setJavaComment("Das Kürzel des Raums");

	/** Die Definition der Tabellenspalte GueltigAb */
	public final SchemaTabelleSpalte col_GueltigAb = add("GueltigAb", SchemaDatentypen.DATE, false)
			.setDefault("1899-01-01")
			.setNotNull()
			.setConverter(DatumConverter.class)
			.setJavaComment("Das Datum, ab dem der Raum gültig ist");

	/** Die Definition der Tabellenspalte GueltigBis */
	public final SchemaTabelleSpalte col_GueltigBis = add("GueltigBis", SchemaDatentypen.DATE, false)
			.setConverter(DatumConverter.class)
			.setJavaComment("Das Datum, bis wann der Raum gültig ist. Ist kein Datum gesetzt, gilt der Raum unbegrenzt weiter.");


	/**
	 * Erstellt die Schema-Defintion für die Tabelle UV_Raeume.
	 */
	public Tabelle_UV_Raeume() {
		super("UV_Raeume", SchemaRevisionen.REV_48);
		setMigrate(false);
		setImportExport(true);
		setPKAutoIncrement();
		setJavaSubPackage("uv");
		setJavaClassName("DTOUvRaum");
		setJavaComment("Tabelle für die konkreten Räume der UV");
	}

}
