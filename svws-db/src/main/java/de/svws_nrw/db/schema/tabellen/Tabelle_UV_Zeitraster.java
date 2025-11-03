package de.svws_nrw.db.schema.tabellen;

import de.svws_nrw.db.converter.current.DatumConverter;
import de.svws_nrw.db.schema.SchemaDatentypen;
import de.svws_nrw.db.schema.SchemaRevisionen;
import de.svws_nrw.db.schema.SchemaTabelle;
import de.svws_nrw.db.schema.SchemaTabelleSpalte;

/**
 * Diese Klasse beinhaltet die Schema-Definition für die Tabelle UV_Zeitraster.
 */
public class Tabelle_UV_Zeitraster extends SchemaTabelle {

    /** Eindeutige ID des Zeitrasters (automatisch generiert) */
    public final SchemaTabelleSpalte col_ID = add("ID", SchemaDatentypen.BIGINT, true)
            .setNotNull()
            .setJavaComment("Eindeutige ID des Zeitrasters (automatisch generiert)");

    /** Datum, ab dem das Zeitraster gültig ist */
    public final SchemaTabelleSpalte col_GueltigVon = add("GueltigVon", SchemaDatentypen.DATE, false)
            .setDefault("1899-01-01")
            .setNotNull()
            .setConverter(DatumConverter.class)
            .setJavaComment("Datum, ab dem das Zeitraster gültig ist");

    /** Datum, bis zu dem das Zeitraster gültig ist. Ist kein Datum gesetzt, gilt das Zeitraster unbegrenzt weiter. */
    public final SchemaTabelleSpalte col_GueltigBis = add("GueltigBis", SchemaDatentypen.DATE, false)
            .setConverter(DatumConverter.class)
            .setJavaComment("Datum, bis zu dem das Zeitraster gültig ist. Ist kein Datum gesetzt, gilt das Zeitraster unbegrenzt weiter.");

    /** Bezeichnung des Zeitrasters */
    public final SchemaTabelleSpalte col_Bezeichnung = add("Bezeichnung", SchemaDatentypen.VARCHAR, false).setDatenlaenge(100)
            .setNotNull()
            .setJavaComment("Bezeichnung des Zeitrasters");

    /**
     * Erstellt die Schema-Definition für die Tabelle UV_Zeitraster.
     */
    public Tabelle_UV_Zeitraster() {
        super("UV_Zeitraster", SchemaRevisionen.REV_48);
        setMigrate(false);
        setImportExport(true);
        setPKAutoIncrement();
        setJavaSubPackage("uv");
        setJavaClassName("DTOUvZeitraster");
        setJavaComment("Tabelle für die Zeitraster der Unterrichtsverteilung (UV)");
    }
}
