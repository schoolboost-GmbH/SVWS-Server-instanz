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
 * Diese Klasse beinhaltet die Schema-Definition für die Tabelle UV_LehrerAnrechnungsstunden.
 */
public class Tabelle_UV_LehrerAnrechnungsstunden extends SchemaTabelle {

	/** Die Definition der Tabellenspalte ID */
	public SchemaTabelleSpalte col_ID = add("ID", SchemaDatentypen.BIGINT, true)
			.setNotNull()
			.setJavaComment("ID des Anrechnungsstunden-Eintrags (generiert)");

	/** Die Definition der Tabellenspalte Lehrer_ID */
	public SchemaTabelleSpalte col_Lehrer_ID = add("Lehrer_ID", SchemaDatentypen.BIGINT, false)
			.setNotNull()
			.setJavaComment("Die ID des UV-Lehrers des Planungsabschnitts als Fremdschlüssel auf die Tabelle UV_Lehrer");

	/** Die Definition der Tabellenspalte AnrechnungsgrundKrz */
	public SchemaTabelleSpalte col_AnrechnungsgrundKrz = add("AnrechnungsgrundKrz", SchemaDatentypen.VARCHAR, false).setDatenlaenge(10)
			.setJavaComment("Anrechnungsstundentext für die Anrechnungsstunden");

	/** Die Definition der Tabellenspalte AnrechnungStd */
	public SchemaTabelleSpalte col_AnrechnungStd = add("AnrechnungStd", SchemaDatentypen.FLOAT, false)
			.setJavaComment("Anzahl der Anrechnungsstunden");

	/** Die Definition der Tabellenspalte GueltigAb */
	public SchemaTabelleSpalte col_GueltigAb = add("GueltigAb", SchemaDatentypen.DATE, false)
			.setDefault("1899-01-01")
			.setNotNull()
			.setConverter(DatumConverter.class)
			.setJavaComment("Das Datum, ab dem die Anrechnungsstunde gültig ist");

	/** Die Definition der Tabellenspalte GueltigBis */
	public SchemaTabelleSpalte col_GueltigBis = add("GueltigBis", SchemaDatentypen.DATE, false)
			.setNotNull()
			.setConverter(DatumConverter.class)
			.setJavaComment("Das Datum, bis wann die Anrechnungsstunde gültig ist");



	/** Die Definition des Fremdschlüssels auf UV_Lehrer */
	public SchemaTabelleFremdschluessel fk_UV_LehrerAnrechnungsstunden_UVLehrer_FK = addForeignKey(
			"UV_LehrerAnrechnungsstunden_UVLehrer_FK",
			/* OnUpdate: */ SchemaFremdschluesselAktionen.CASCADE,
			/* OnDelete: */ SchemaFremdschluesselAktionen.CASCADE,
			new Pair<>(col_Lehrer_ID, Schema.tab_UV_Lehrer.col_ID)
	);

	/**
	 * Erstellt die Schema-Definition für die Tabelle UV_LehrerAnrechnungsstunden.
	 */
	public Tabelle_UV_LehrerAnrechnungsstunden() {
		super("UV_LehrerAnrechnungsstunden", SchemaRevisionen.REV_48);
		setMigrate(false);
		setImportExport(true);
		setPKAutoIncrement();
		setJavaSubPackage("uv");
		setJavaClassName("DTOUvLehrerAnrechnungsstunden");
		setJavaComment("Tabelle für die konkreten Anrechnungsstunden der UV-Lehrer");
	}

}
