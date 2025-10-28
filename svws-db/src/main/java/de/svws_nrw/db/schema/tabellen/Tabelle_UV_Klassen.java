package de.svws_nrw.db.schema.tabellen;

import de.svws_nrw.asd.adt.Pair;
import de.svws_nrw.db.schema.Schema;
import de.svws_nrw.db.schema.SchemaDatentypen;
import de.svws_nrw.db.schema.SchemaFremdschluesselAktionen;
import de.svws_nrw.db.schema.SchemaRevisionen;
import de.svws_nrw.db.schema.SchemaTabelle;
import de.svws_nrw.db.schema.SchemaTabelleFremdschluessel;
import de.svws_nrw.db.schema.SchemaTabelleSpalte;
import de.svws_nrw.db.schema.SchemaTabelleUniqueIndex;

/**
 * Diese Klasse beinhaltet die Schema-Definition für die Tabelle UV_Klassen.
 */
public class Tabelle_UV_Klassen extends SchemaTabelle {

	/** Die Definition der Tabellenspalte ID */
	public final SchemaTabelleSpalte col_ID = add("ID", SchemaDatentypen.BIGINT, true)
			.setNotNull()
			.setJavaComment("ID der Klasse (generiert, planungsspezifisch)");

	/** Die Definition der Tabellenspalte Planungsabschnitt_ID */
	public final SchemaTabelleSpalte col_Planungsabschnitt_ID = add("Planungsabschnitt_ID", SchemaDatentypen.BIGINT, false)
			.setNotNull()
			.setJavaComment("Die ID des Planungsabschnitts als Fremdschlüssel auf die Tabelle UV_Planungsabschnitte");

	/** Die Definition der Tabellenspalte Schuljahresabschnitts_ID */
	public final SchemaTabelleSpalte col_Schuljahresabschnitts_ID = add("Schuljahresabschnitts_ID", SchemaDatentypen.BIGINT, false)
			.setNotNull()
			.setJavaComment("ID des Schuljahresabschnittes aus der Tabelle Schuljahresabschnitte");

	/** Die Definition der Tabellenspalte Bezeichnung */
	public final SchemaTabelleSpalte col_Bezeichnung = add("Bezeichnung", SchemaDatentypen.VARCHAR, false).setDatenlaenge(150)
			.setJavaComment("Bezeichnender Text für die Klasse");

	/** Die Definition der Tabellenspalte Klasse */
	public final SchemaTabelleSpalte col_Klasse = add("Klasse", SchemaDatentypen.VARCHAR, false).setDatenlaenge(15)
			.setNotNull()
			.setJavaComment("Kürzel der Klasse");

	/** Die Definition der Tabellenspalte Parallelitaet */
	public final SchemaTabelleSpalte col_Parallelitaet = add("Parallelitaet", SchemaDatentypen.VARCHAR, false).setDatenlaenge(2)
			.setNotNull()
			.setJavaComment("Parallelitaet der Klasse (a/b/c/...)");

	/** Fremdschlüssel auf die Stundentafel (Tabelle UV_Stundentafeln) */
	public final SchemaTabelleSpalte col_Stundentafel_ID = add("Stundentafel_ID", SchemaDatentypen.BIGINT, false)
			.setJavaComment("Fremdschlüssel auf die Stundentafel (Tabelle UV_Stundentafeln)");

	/** Fremdschlüssel auf die Schülergruppe (Tabelle UV_Schuelergruppen) */
	public final SchemaTabelleSpalte col_Schuelergruppe_ID = add("Schuelergruppe_ID", SchemaDatentypen.BIGINT, false)
			.setNotNull()
			.setJavaComment("Fremdschlüssel auf die Schülergruppe (Tabelle UV_Schuelergruppen)");

	/** Die Definition der Tabellenspalte OrgFormKrz */
	public final SchemaTabelleSpalte col_OrgFormKrz = add("OrgFormKrz", SchemaDatentypen.VARCHAR, false).setDatenlaenge(1)
			.setJavaComment("Organisationsform der Klasse Kürzel IT.NRW");

	/** Die Definition der Tabellenspalte Fachklasse_ID */
	public final SchemaTabelleSpalte col_Fachklasse_ID = add("Fachklasse_ID", SchemaDatentypen.BIGINT, false)
			.setJavaComment("FID des Fachklasse nur BK SBK");

	/** Die Definition der Tabellenspalte ASDSchulformNr */
	public final SchemaTabelleSpalte col_ASDSchulformNr = add("ASDSchulformNr", SchemaDatentypen.VARCHAR, false).setDatenlaenge(3)
			.setJavaComment("Schulgliederung in der Klasse");


	/** Fremdschlüssel auf die Tabelle Schuljahresabschnitte */
	public final SchemaTabelleFremdschluessel fk_UVKlassen_Schuljahresabschnitte_FK = addForeignKey(
			"UVKlassen_Schuljahresabschnitte_FK",
			/* OnUpdate: */ SchemaFremdschluesselAktionen.CASCADE,
			/* OnDelete: */ SchemaFremdschluesselAktionen.CASCADE,
			new Pair<>(col_Schuljahresabschnitts_ID, Schema.tab_Schuljahresabschnitte.col_ID)
	);

	/** Fremdschlüssel auf die Tabelle UV_Stundentafeln */
	public final SchemaTabelleFremdschluessel fk_UVKlassen_UVStundentafeln_FK = addForeignKey(
			"UVKlassen_UVStundentafeln_FK",
			/* OnUpdate: */ SchemaFremdschluesselAktionen.CASCADE,
			/* OnDelete: */ SchemaFremdschluesselAktionen.SET_NULL,
			new Pair<>(col_Stundentafel_ID, Schema.tab_UV_Stundentafeln.col_ID)
	);

	/** Fremdschlüssel auf die Tabelle UV_Schuelergruppen */
	public final SchemaTabelleFremdschluessel fk_UVKlassen_UVSchuelergruppen_FK = addForeignKey(
			"UVKlassen_UVSchuelergruppen_FK",
			/* OnUpdate: */ SchemaFremdschluesselAktionen.CASCADE,
			/* OnDelete: */ SchemaFremdschluesselAktionen.RESTRICT,
			new Pair<>(col_Schuelergruppe_ID, Schema.tab_UV_Schuelergruppen.col_ID),
			new Pair<>(col_Planungsabschnitt_ID, Schema.tab_UV_Schuelergruppen.col_Planungsabschnitt_ID)
	);

	/** Unique-Index für die Kombination ID und Planungsabschnitt_ID, benötigt durch 2-teiligen FK */
	public final SchemaTabelleUniqueIndex unique_UVKlassen_UC1 = addUniqueIndex("UVKlassen_UC1",
			col_ID, col_Planungsabschnitt_ID
	);


	/**
	 * Erstellt die Schema-Definition für die Tabelle UV_Klassen.
	 */
	public Tabelle_UV_Klassen() {
		super("UV_Klassen", SchemaRevisionen.REV_48);
		setMigrate(false);
		setImportExport(true);
		setPKAutoIncrement();
		setJavaSubPackage("uv");
		setJavaClassName("DTOUvKlasse");
		setJavaComment("Tabelle für die Klassen eines Planungsabschnitts der Unterrichtsverteilung (UV)");
	}

}
