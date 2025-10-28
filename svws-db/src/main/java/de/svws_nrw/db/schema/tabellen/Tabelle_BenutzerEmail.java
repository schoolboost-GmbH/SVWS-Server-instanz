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
 * Diese Klasse beinhaltet die Schema-Definition für die Tabelle BenutzerEmail.
 */
public class Tabelle_BenutzerEmail extends SchemaTabelle {

	/** Die Definition der Tabellenspalte Benutzer_ID */
	public final SchemaTabelleSpalte col_Benutzer_ID = add("Benutzer_ID", SchemaDatentypen.BIGINT, true)
			.setNotNull()
			.setJavaComment("Die ID des Benutzers, zu dem der Datensatz gehört ");

	/** Die Definition der Tabellenspalte Email */
	public final SchemaTabelleSpalte col_Email = add("Email", SchemaDatentypen.VARCHAR, false).setDatenlaenge(255)
			.setNotNull()
			.setJavaComment("Die EMail-Adresse des Benutzers, zu dem der Datensatz gehört ");

	/** Die Definition der Tabellenspalte EmailName */
	public final SchemaTabelleSpalte col_EmailName = add("EmailName", SchemaDatentypen.VARCHAR, false).setDatenlaenge(255)
			.setNotNull()
			.setJavaComment("??? ");

	/** Die Definition der Tabellenspalte SMTPUsername */
	public final SchemaTabelleSpalte col_SMTPUsername = add("SMTPUsername", SchemaDatentypen.VARCHAR, false).setDatenlaenge(255)
			.setJavaComment("??? ");

	/** Die Definition der Tabellenspalte SMTPPassword */
	public final SchemaTabelleSpalte col_SMTPPassword = add("SMTPPassword", SchemaDatentypen.VARCHAR, false).setDatenlaenge(255)
			.setJavaComment("??? ");

	/** Die Definition der Tabellenspalte EMailSignature */
	public final SchemaTabelleSpalte col_EMailSignature = add("EMailSignature", SchemaDatentypen.VARCHAR, false).setDatenlaenge(2047)
			.setJavaComment("??? ");

	/** Die Definition der Tabellenspalte HeartbeatDate */
	public final SchemaTabelleSpalte col_HeartbeatDate = add("HeartbeatDate", SchemaDatentypen.BIGINT, false)
			.setJavaComment("??? ");

	/** Die Definition der Tabellenspalte ComputerName */
	public final SchemaTabelleSpalte col_ComputerName = add("ComputerName", SchemaDatentypen.VARCHAR, false).setDatenlaenge(255)
			.setJavaComment("??? ");


	/** Die Definition des Fremdschlüssels BenutzerEmail_Benutzer_FK */
	public final SchemaTabelleFremdschluessel fk_BenutzerEmail_Benutzer_FK = addForeignKey(
			"BenutzerEmail_Benutzer_FK",
			/* OnUpdate: */ SchemaFremdschluesselAktionen.CASCADE,
			/* OnDelete: */ SchemaFremdschluesselAktionen.CASCADE,
			new Pair<>(col_Benutzer_ID, Schema.tab_Benutzer.col_ID))
			.setRevision(SchemaRevisionen.REV_2);


	/**
	 * Erstellt die Schema-Defintion für die Tabelle BenutzerEmail.
	 */
	public Tabelle_BenutzerEmail() {
		super("BenutzerEmail", SchemaRevisionen.REV_0);
		setMigrate(false);
		setImportExport(true);
		setJavaSubPackage("schild.benutzer");
		setJavaClassName("DTOBenutzerMail");
		setJavaComment("Die Informationen zu einem E-Mail-Zugang des Benutzers, damit Emails versendet werden können.");
	}

}
