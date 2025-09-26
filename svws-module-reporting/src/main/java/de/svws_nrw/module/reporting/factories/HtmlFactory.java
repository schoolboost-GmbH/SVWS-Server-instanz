package de.svws_nrw.module.reporting.factories;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.svws_nrw.base.ResourceUtils;
import de.svws_nrw.core.data.reporting.ReportingParameter;
import de.svws_nrw.core.logger.LogLevel;
import de.svws_nrw.core.types.reporting.ReportingReportvorlage;
import de.svws_nrw.db.utils.ApiOperationException;
import de.svws_nrw.module.reporting.filterung.ReportingFilterDataType;
import de.svws_nrw.module.reporting.html.HtmlBuilder;
import de.svws_nrw.module.reporting.html.HtmlTemplateDefinition;
import de.svws_nrw.module.reporting.html.contexts.HtmlContext;
import de.svws_nrw.module.reporting.html.contexts.HtmlContextGostLaufbahnplanungAbiturjahrgangFachwahlstatistiken;
import de.svws_nrw.module.reporting.html.contexts.HtmlContextGostKlausurplanungKlausurplan;
import de.svws_nrw.module.reporting.html.contexts.HtmlContextGostKursplanungBlockungsergebnis;
import de.svws_nrw.module.reporting.html.contexts.HtmlContextKlassen;
import de.svws_nrw.module.reporting.html.contexts.HtmlContextKurse;
import de.svws_nrw.module.reporting.html.contexts.HtmlContextLehrer;
import de.svws_nrw.module.reporting.html.contexts.HtmlContextSchueler;
import de.svws_nrw.module.reporting.html.contexts.HtmlContextSchule;
import de.svws_nrw.module.reporting.html.contexts.HtmlContextStundenplanungFachStundenplan;
import de.svws_nrw.module.reporting.html.contexts.HtmlContextStundenplanungKlassenStundenplan;
import de.svws_nrw.module.reporting.html.contexts.HtmlContextStundenplanungLehrerStundenplan;
import de.svws_nrw.module.reporting.html.contexts.HtmlContextStundenplanungRaumStundenplan;
import de.svws_nrw.module.reporting.html.contexts.HtmlContextStundenplanungSchuelerStundenplan;
import de.svws_nrw.module.reporting.repositories.ReportingRepository;
import de.svws_nrw.module.reporting.validierung.ReportingValidierung;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;


/**
 * <p>Diese Klasse erstellt HTML-Inhalte auf Basis des in den Reporting-Parametern übergebenen HTML-Templates und der übergebenen Daten.</p>
 * <p>Dabei erstellt die Factory bei der Initialisierung zunächst die Contexts mit den Daten gemäß dem HTML-Template.
 * Zum Erstellen der HTML-Inhalte generiert die Factory einen oder mehrere HTML-Builder, die aus dem Template das fertige HTML erzeugen.</p>
 * <p>Die HTML-Builder können extern weiter verarbeitet werden oder es kann intern eine Response im HTML-Format erzeugt werden.</p>
 */
public class HtmlFactory {

	/** Repository mit Parametern, Logger und Daten-Cache zur Report-Generierung. */
	private final ReportingRepository reportingRepository;

	/** Einstellungen und Daten zum Steuern der Report-Generierung. */
	private final ReportingParameter reportingParameter;

	/** Die Template-Definition für die Erstellung der HTML-Datei. */
	private final HtmlTemplateDefinition htmlTemplateDefinition;

	/** Eine Map zum Sammeln der erstellten HTML-Contexts. */
	final Map<String, HtmlContext<?>> mapHtmlContexts = new HashMap<>();


	/**
	 * Erzeugt eine neue HTML-Factory, um eine HTML-Datei aus einem HTML-Template zu erzeugen.
	 *
	 * @param reportingRepository		Repository für das Reporting, welches verschiedene Daten aus der Datenbank zwischenspeichert.
	 *
	 * @throws ApiOperationException	Im Fehlerfall wird eine ApiOperationException ausgelöst und Log-Daten zusammen mit dieser zurückgegeben.
	 */
	protected HtmlFactory(final ReportingRepository reportingRepository)
			throws ApiOperationException {

		this.reportingRepository = reportingRepository;
		this.reportingParameter = this.reportingRepository.reportingParameter();

		this.reportingRepository.logger().logLn(LogLevel.DEBUG, 0,
				">>> Beginn der Initialisierung der HTML-Factory und der Validierung der übergebenen Daten.");

		// Validiere die Angaben zur HTML-Vorlage.
		this.htmlTemplateDefinition = HtmlTemplateDefinition.getByType(ReportingReportvorlage.getByBezeichnung(this.reportingParameter.reportvorlage));
		if (this.htmlTemplateDefinition == null) {
			this.reportingRepository.logger()
					.logLn(LogLevel.ERROR, 4, "FEHLER: Die Template-Definitionen für die HTML-Factory sind inkonsistent.");
			throw new ApiOperationException(Status.INTERNAL_SERVER_ERROR, "FEHLER: Die Template-Definitionen für die HTML-Factory sind inkonsistent.");
		}

		// Prüfe, ob die Rechte des Benutzers zu den in der TemplateDefinition hinterlegten Rechten passen.
		this.reportingRepository.logger().logLn(LogLevel.DEBUG, 4,
				"Prüfe die Berechtigungen des Benutzers für den Zugriff auf die für die Ausgabe notwendigen Daten.");
		if (!this.reportingRepository.conn().getUser().pruefeKompetenz(new HashSet<>(htmlTemplateDefinition.getBenutzerKompetenzen()))) {
			this.reportingRepository.logger()
					.logLn(LogLevel.ERROR, 4,
							"FEHLER: Der Benutzer hat nicht die erforderlichen Rechte, um auf die Daten für die Erstellung der Ausgabe zu zugreifen.");
			throw new ApiOperationException(Status.FORBIDDEN,
					"FEHLER: Der Benutzer hat nicht die erforderlichen Rechte, um auf die Daten für die Erstellung der Ausgabe zu zugreifen.");
		}

		this.reportingRepository.logger().logLn(LogLevel.DEBUG, 0, "<<< Ende der Initialisierung der HTML-Factory und der Validierung der übergebenen Daten.");

		getContexts();
	}


	/**
	 * Erzeugte die notwendigen Contexts für die HTML-Erstellung auf Basis des angegebenen HTML-Templates.
	 *
	 * @throws ApiOperationException	Im Fehlerfall wird eine ApiOperationException ausgelöst und Log-Daten zusammen mit dieser zurückgegeben.
	 */
	private void getContexts() throws ApiOperationException {

		reportingRepository.logger().logLn(LogLevel.DEBUG, 0, ">>> Beginn der Erzeugung der Datenkontexte für die HTML-Generierung.");
		reportingRepository.logger().logLn(LogLevel.DEBUG, 4, "Erzeuge Datenkontext Schule für die HTML-Generierung.");

		final HtmlContextSchule htmlContextSchule = new HtmlContextSchule(reportingRepository);
		mapHtmlContexts.put("Schule", htmlContextSchule);

		// Betrachte die HTML-Template-Definition und erzeuge damit die korrekten Contexts der Hauptdaten
		switch (htmlTemplateDefinition.name().substring(0, htmlTemplateDefinition.name().indexOf("_v_"))) {
			case "SCHUELER":
				// Schüler-Context ist Hauptdatenquelle
				initContextSchueler();
				break;
			case "KLASSEN":
				// Klassen-Context ist Hauptdatenquelle
				initContextKlassen();
				break;
			case "KURSE":
				// Kurse-Context ist Hauptdatenquelle
				initContextKurse();
				break;
			case "LEHRER":
				// Lehrer-Context ist Hauptdatenquelle
				initContextLehrer();
				break;
			case "GOST_KURSPLANUNG":
				// GOSt-Kursplanung-Blockungsergebnis-Context ist Hauptdatenquelle
				initContextGostKursplanung();
				break;
			case "GOST_KLAUSURPLANUNG":
				// GOSt-Klausurplanung-Klausurplan-Context ist Hauptdatenquelle
				initContextGostKlausurplanung();
				break;
			case "GOST_LAUFBAHNPLANUNG_ABITURJAHRGANG":
				// GOSt-Laufbahnplanung-Abiturjahrgang-Fachwahlstatistiken-Context ist Hauptdatenquelle
				initContextGostLaufbahnplanungAbiturjahrgangFachwahlstatistiken();
				break;
			case "STUNDENPLANUNG":
				// Stundenplan-Context ist Hauptdatenquelle
				initContextStundenplanung();
				break;
			default:
				break;
		}

		reportingRepository.logger().logLn(LogLevel.DEBUG, 0, "<<< Ende der Erzeugung der Datenkontexte für die HTML-Generierung.");
	}

	/**
	 * Initialisiert den Context für Schüler.
	 *
	 * @throws ApiOperationException	Im Fehlerfall wird eine ApiOperationException ausgelöst und Log-Daten zusammen mit dieser zurückgegeben.
	 */
	public void initContextSchueler() throws ApiOperationException {
		reportingRepository.logger().logLn(LogLevel.DEBUG, 4, "Validiere die Daten für Schüler für die HTML-Generierung.");

		final boolean istGostLaufbahnplanung =
				((htmlTemplateDefinition == HtmlTemplateDefinition.SCHUELER_v_GOST_LAUFBAHNPLANUNG_WAHLBOGEN)
						|| (htmlTemplateDefinition == HtmlTemplateDefinition.SCHUELER_v_GOST_LAUFBAHNPLANUNG_ERGEBNISUEBERSICHT));
		final boolean istGostAbitur =
				((htmlTemplateDefinition == HtmlTemplateDefinition.SCHUELER_v_GOST_ABITUR_APO_ANLAGE_12_A3)
						|| (htmlTemplateDefinition == HtmlTemplateDefinition.SCHUELER_v_GOST_ABITUR_APO_ANLAGE_12_A4));

		ReportingValidierung.validiereDatenFuerSchueler(reportingRepository, reportingParameter.idsHauptdaten, istGostLaufbahnplanung, istGostAbitur);
		reportingRepository.logger().logLn(LogLevel.DEBUG, 4,
				("Erzeuge Datenkontext Schüler für die HTML-Generierung - %d IDs von Schülern wurden übergeben für Template %s.")
						.formatted(reportingParameter.idsHauptdaten.size(), htmlTemplateDefinition.name()));
		final HtmlContextSchueler htmlContextSchueler = new HtmlContextSchueler(reportingRepository);
		mapHtmlContexts.put("Schueler", htmlContextSchueler);
	}

	/**
	 * Initialisiert den Context für Klassen.
	 *
	 * @throws ApiOperationException	Im Fehlerfall wird eine ApiOperationException ausgelöst und Log-Daten zusammen mit dieser zurückgegeben.
	 */
	public void initContextKlassen() throws ApiOperationException {
		reportingRepository.logger().logLn(LogLevel.DEBUG, 4, "Validiere die Daten für Klassen für die HTML-Generierung.");
		ReportingValidierung.validiereDatenFuerKlassen(reportingRepository, reportingParameter.idsHauptdaten);
		reportingRepository.logger().logLn(LogLevel.DEBUG, 4,
				("Erzeuge Datenkontext Klassen für die HTML-Generierung - %d IDs von Klassen wurden übergeben für Template %s.")
						.formatted(reportingParameter.idsHauptdaten.size(), htmlTemplateDefinition.name()));
		final HtmlContextKlassen htmlContextKlassen = new HtmlContextKlassen(reportingRepository);
		mapHtmlContexts.put("Klassen", htmlContextKlassen);
	}

	/**
	 * Initialisiert den Context für Kurse.
	 *
	 * @throws ApiOperationException	Im Fehlerfall wird eine ApiOperationException ausgelöst und Log-Daten zusammen mit dieser zurückgegeben.
	 */
	public void initContextKurse() throws ApiOperationException {
		reportingRepository.logger().logLn(LogLevel.DEBUG, 4, "Validiere die Daten für Kurse für die HTML-Generierung.");
		ReportingValidierung.validiereDatenFuerKurse(reportingRepository, reportingParameter.idsHauptdaten);
		reportingRepository.logger().logLn(LogLevel.DEBUG, 4,
				("Erzeuge Datenkontext Kurse für die HTML-Generierung - %d IDs von Kursen wurden übergeben für Template %s.")
						.formatted(reportingParameter.idsHauptdaten.size(), htmlTemplateDefinition.name()));
		final HtmlContextKurse htmlContextKurse = new HtmlContextKurse(reportingRepository);
		mapHtmlContexts.put("Kurse", htmlContextKurse);
	}

	/**
	 * Initialisiert den Context für Lehrer.
	 *
	 * @throws ApiOperationException	Im Fehlerfall wird eine ApiOperationException ausgelöst und Log-Daten zusammen mit dieser zurückgegeben.
	 */
	public void initContextLehrer() throws ApiOperationException {
		reportingRepository.logger().logLn(LogLevel.DEBUG, 4, "Validiere die Daten für Lehrer für die HTML-Generierung.");
		ReportingValidierung.validiereDatenFuerLehrer(reportingRepository, reportingParameter.idsHauptdaten);
		reportingRepository.logger().logLn(LogLevel.DEBUG, 4,
				("Erzeuge Datenkontext Lehrer für die HTML-Generierung - %d IDs von Lehrern wurden übergeben für Template %s.")
						.formatted(reportingParameter.idsHauptdaten.size(), htmlTemplateDefinition.name()));
		final HtmlContextLehrer htmlContextLehrer = new HtmlContextLehrer(reportingRepository);
		mapHtmlContexts.put("Lehrer", htmlContextLehrer);
	}

	/**
	 * Initialisiert die Fachwahlstatistiken für den Context der GOSt-Laufbahnplanung eines Abiturjahrgangs.
	 *
	 * @throws ApiOperationException	Im Fehlerfall wird eine ApiOperationException ausgelöst und Log-Daten zusammen mit dieser zurückgegeben.
	 */
	public void initContextGostLaufbahnplanungAbiturjahrgangFachwahlstatistiken() throws ApiOperationException {
		reportingRepository.logger().logLn(LogLevel.DEBUG, 4, "Validiere die Daten für einen Gost-Laufbahnplan eines Abiturjahrgangs und dessen "
				+ "Fachwahlstatistiken für die HTML-Generierung.");
		ReportingValidierung.validiereDatenFuerGostLaufbahnplanungAbiturjahrgangFachwahlstatistiken(reportingRepository);
		reportingRepository.logger().logLn(LogLevel.DEBUG, 4,
				"Erzeuge Datenkontext Gost-Laufbahnplan-Abiturjahrgang-Fachwahlstatistiken für die HTML-Generierung mit Template %s."
						.formatted(htmlTemplateDefinition.name()));
		final HtmlContextGostLaufbahnplanungAbiturjahrgangFachwahlstatistiken htmlContextGostLaufbahnplanungAbiturjahrgangFachwahlstatistiken =
				new HtmlContextGostLaufbahnplanungAbiturjahrgangFachwahlstatistiken(reportingRepository);
		mapHtmlContexts.put("GostLaufbahnplanungAbiturjahrgangFachwahlStatistiken", htmlContextGostLaufbahnplanungAbiturjahrgangFachwahlstatistiken);
	}

	/**
	 * Initialisiert den Context für die GOSt-Kursplanung.
	 *
	 * @throws ApiOperationException	Im Fehlerfall wird eine ApiOperationException ausgelöst und Log-Daten zusammen mit dieser zurückgegeben.
	 */
	public void initContextGostKursplanung() throws ApiOperationException {
		reportingRepository.logger().logLn(LogLevel.DEBUG, 4, "Validiere die Daten für ein Gost-Blockungsergebnis für die HTML-Generierung.");
		ReportingValidierung.validiereDatenFuerGostKursplanungBlockungsergebnis(reportingRepository);
		reportingRepository.logger().logLn(LogLevel.DEBUG, 4,
				"Erzeuge Datenkontext Gost-Kursplanung-Blockungsergebnis für die HTML-Generierung mit ID %s für Template %s."
						.formatted(reportingParameter.idsHauptdaten.getFirst(), htmlTemplateDefinition.name()));
		final List<Long> idsFilter = this.reportingRepository.reportingParameter().idsDetaildaten;
		final ReportingFilterDataType idsFilterDataType = switch (htmlTemplateDefinition) {
			case GOST_KURSPLANUNG_v_KURS_MIT_KURSSCHUELERN -> ReportingFilterDataType.KURSE;
			case GOST_KURSPLANUNG_v_SCHUELER_MIT_KURSEN, GOST_KURSPLANUNG_v_SCHUELER_MIT_SCHIENEN_KURSEN -> ReportingFilterDataType.SCHUELER;
			default -> ReportingFilterDataType.UNDEFINED;
		};
		final HtmlContextGostKursplanungBlockungsergebnis htmlContextGostBlockung =
				new HtmlContextGostKursplanungBlockungsergebnis(reportingRepository, idsFilter, idsFilterDataType);
		mapHtmlContexts.put("GostBlockungsergebnis", htmlContextGostBlockung);
	}

	/**
	 * Initialisiert den Context für die GOSt-Klausurplanung.
	 *
	 * @throws ApiOperationException	Im Fehlerfall wird eine ApiOperationException ausgelöst und Log-Daten zusammen mit dieser zurückgegeben.
	 */
	public void initContextGostKlausurplanung() throws ApiOperationException {
		reportingRepository.logger().logLn(LogLevel.DEBUG, 4, "Validiere die Daten für einen Gost-Klausurplan für die HTML-Generierung.");
		ReportingValidierung.validiereDatenFuerGostKlausurplanungKlausurplan(reportingRepository);
		reportingRepository.logger().logLn(LogLevel.DEBUG, 4,
				"Erzeuge Datenkontext Gost-Klausurplanung für die HTML-Generierung mit Template %s.".formatted(htmlTemplateDefinition.name()));
		final List<Long> idsFilter = this.reportingRepository.reportingParameter().idsDetaildaten;
		final ReportingFilterDataType idsFilterDataType = switch (htmlTemplateDefinition) {
			case GOST_KLAUSURPLANUNG_v_SCHUELER_MIT_KLAUSUREN -> ReportingFilterDataType.SCHUELER;
			default -> ReportingFilterDataType.UNDEFINED;
		};
		final HtmlContextGostKlausurplanungKlausurplan htmlContextGostKlausurplan = new HtmlContextGostKlausurplanungKlausurplan(reportingRepository,
				idsFilter, idsFilterDataType);
		mapHtmlContexts.put("GostKlausurplan", htmlContextGostKlausurplan);
	}

	/**
	 * Initialisiert den Context zur Stundenplanung.
	 *
	 * @throws ApiOperationException	Im Fehlerfall wird eine ApiOperationException ausgelöst und Log-Daten zusammen mit dieser zurückgegeben.
	 */
	public void initContextStundenplanung() throws ApiOperationException {
		reportingRepository.logger().logLn(LogLevel.DEBUG, 4, "Validiere die Daten für einen Stundenplan für die HTML-Generierung.");
		ReportingValidierung.validiereDatenFuerStundenplanung(reportingRepository);
		reportingRepository.logger().logLn(LogLevel.DEBUG, 4,
				"Erzeuge Datenkontext Stundenplan für die HTML-Generierung mit Template %s.".formatted(htmlTemplateDefinition.name()));
		switch (htmlTemplateDefinition) {
			case STUNDENPLANUNG_v_FACH_STUNDENPLAN -> {
				final HtmlContextStundenplanungFachStundenplan htmlContextFachStundenplan =
						new HtmlContextStundenplanungFachStundenplan(reportingRepository,
								reportingRepository.stundenplan(reportingParameter.idsHauptdaten.getFirst()),
								reportingParameter.idsDetaildaten);
				mapHtmlContexts.put("FaecherStundenplaene", htmlContextFachStundenplan);
			}
			case STUNDENPLANUNG_v_KLASSEN_STUNDENPLAN -> {
				reportingRepository.logger().logLn(LogLevel.DEBUG, 4, "Validiere die Daten der Klassen für einen Stundenplan für die HTML-Generierung.");
				ReportingValidierung.validiereDatenFuerKlassen(reportingRepository, reportingParameter.idsDetaildaten);
				final HtmlContextStundenplanungKlassenStundenplan htmlContextKlassenStundenplan =
						new HtmlContextStundenplanungKlassenStundenplan(reportingRepository,
								reportingRepository.stundenplan(reportingParameter.idsHauptdaten.getFirst()),
								reportingParameter.idsDetaildaten);
				mapHtmlContexts.put("KlassenStundenplaene", htmlContextKlassenStundenplan);
			}
			case STUNDENPLANUNG_v_LEHRER_STUNDENPLAN, STUNDENPLANUNG_v_LEHRER_STUNDENPLAN_KOMBINIERT -> {
				reportingRepository.logger().logLn(LogLevel.DEBUG, 4, "Validiere die Daten der Lehrkräfte für einen Stundenplan für die HTML-Generierung.");
				ReportingValidierung.validiereDatenFuerLehrer(reportingRepository, reportingParameter.idsDetaildaten);
				final HtmlContextStundenplanungLehrerStundenplan htmlContextLehrerStundenplan =
						new HtmlContextStundenplanungLehrerStundenplan(reportingRepository,
								reportingRepository.stundenplan(reportingParameter.idsHauptdaten.getFirst()),
								reportingParameter.idsDetaildaten);
				mapHtmlContexts.put("LehrerStundenplaene", htmlContextLehrerStundenplan);
			}
			case STUNDENPLANUNG_v_RAUM_STUNDENPLAN -> {
				final HtmlContextStundenplanungRaumStundenplan htmlContextRaeumeStundenplan =
						new HtmlContextStundenplanungRaumStundenplan(reportingRepository,
								reportingRepository.stundenplan(reportingParameter.idsHauptdaten.getFirst()),
								reportingParameter.idsDetaildaten);
				mapHtmlContexts.put("RaeumeStundenplaene", htmlContextRaeumeStundenplan);
			}
			case STUNDENPLANUNG_v_SCHUELER_STUNDENPLAN -> {
				reportingRepository.logger().logLn(LogLevel.DEBUG, 4, "Validiere die Daten der Schüler für einen Stundenplan für die HTML-Generierung.");
				ReportingValidierung.validiereDatenFuerSchueler(reportingRepository, reportingParameter.idsDetaildaten, false, false);
				final HtmlContextStundenplanungSchuelerStundenplan htmlContextSchuelerStundenplan =
						new HtmlContextStundenplanungSchuelerStundenplan(reportingRepository,
								reportingRepository.stundenplan(reportingParameter.idsHauptdaten.getFirst()),
								reportingParameter.idsDetaildaten);
				mapHtmlContexts.put("SchuelerStundenplaene", htmlContextSchuelerStundenplan);
			}
			default -> {
				// Weitere Formate müssen bei der Stundenplanung nicht initiiert werden.
			}
		}
	}


	/**
	 * Erzeugt auf Basis des gegebenen HTML-Templates und der übergebenen Daten die HTML-Builder, aus denen die HTML-Inhalte erzeugt werden können.
	 *
	 * @return Eine Liste mit htmlBuilder.
	 *
	 * @throws ApiOperationException	Im Fehlerfall wird eine ApiOperationException ausgelöst und Log-Daten zusammen mit dieser zurückgegeben.
	 */
	protected List<HtmlBuilder> createHtmlBuilders() throws ApiOperationException {
		return getHtmlBuilders();
	}


	/**
	 * Erstellt eine Response in Form einer einzelnen HTML-Datei oder Z eine einzelne ZIP-Datei, die mehrere generierte HTML-Dateien enthält.
	 *
	 * @return Im Falle eines Success enthält die HTTP-Response das HTML-Dokument oder die ZIP-Datei.
	 *
	 * @throws ApiOperationException	Im Fehlerfall wird eine ApiOperationException ausgelöst und Log-Daten zusammen mit dieser zurückgegeben.
	 */
	protected Response createHtmlResponse() throws ApiOperationException {
		try {
			reportingRepository.logger().logLn(LogLevel.DEBUG, 0, ">>> Beginn der Erzeugung der Response einer API-Anfrage für eine HTML-Generierung.");
			final List<HtmlBuilder> htmlBuilders = getHtmlBuilders();
			if (!htmlBuilders.isEmpty()) {
				if (htmlBuilders.size() == 1) {
					final String encodedFilename = "filename*=UTF-8''" + URLEncoder.encode(htmlBuilders.getFirst().getDateiname(), StandardCharsets.UTF_8);

					reportingRepository.logger().logLn(LogLevel.DEBUG, 0, "<<< Ende der Erzeugung der Response einer API-Anfrage für eine HTML-Generierung.");
					return Response.ok(htmlBuilders.getFirst().getHtml(), "text/html").header("Content-Disposition", "attachment; " + encodedFilename).build();
				}
				if (htmlTemplateDefinition.getDateiname().isEmpty()) {
					reportingRepository.logger().logLn(LogLevel.ERROR, 4, "FEHLER: Die gewählte Vorlage kann nicht einzelne HTML-Inhalte erstellen.");
					throw new ApiOperationException(Status.INTERNAL_SERVER_ERROR, "FEHLER: Die gewählte Vorlage kann nicht einzelne HTML-Inhalte erstellen.");
				}

				final byte[] zipData = createZIP(htmlBuilders);
				final String encodedFilename = "filename*=UTF-8''" + URLEncoder.encode(htmlTemplateDefinition.getDateiname() + ".zip", StandardCharsets.UTF_8);

				reportingRepository.logger().logLn(LogLevel.DEBUG, 0, "<<< Ende der Erzeugung der Response einer API-Anfrage für eine HTML-Generierung.");
				return Response.ok(zipData, "application/zip").header("Content-Disposition", "attachment; " + encodedFilename).build();
			}
			reportingRepository.logger().logLn(LogLevel.ERROR, 0,
					"### Fehler bei der Erzeugung der Response einer API-Anfrage für eine HTML-Generierung. Es sind keine HTML-Inhalte generiert worden.");
			throw new ApiOperationException(Status.INTERNAL_SERVER_ERROR,
					"Fehler bei der Erzeugung der Response einer API-Anfrage für eine HTML-Generierung. Es sind keine HTML-Inhalte generiert worden.");
		} catch (final Exception e) {
			reportingRepository.logger().logLn(LogLevel.ERROR, 0,
					"### Fehler bei der Erzeugung der Response einer API-Anfrage für eine HTML-Generierung.");
			throw e;
		}
	}


	/**
	 * Erzeugt auf Basis der übergebenen HTML-Vorlage und Daten die HTML-Inhalte der Dateien und legt diese Inhalte in einer Map zum Dateinamen ab.
	 *
	 * @return Eine Map mit den Dateinamen und HTML-Dateiinhalten.
	 *
	 * @throws ApiOperationException	Im Fehlerfall wird eine ApiOperationException ausgelöst und Log-Daten zusammen mit dieser zurückgegeben.
	 */
	private List<HtmlBuilder> getHtmlBuilders() throws ApiOperationException {

		reportingRepository.logger().logLn(LogLevel.DEBUG, 0, ">>> Beginn der Erzeugung der HTML-Builder.");
		final List<HtmlBuilder> htmlBuilders = new ArrayList<>();

		// Lade den Inhalt des HTML-Codes aus dem Template.
		final String htmlTemplateCode = ResourceUtils.text(htmlTemplateDefinition.getRootPfadHtmlTemplate());

		if (!reportingParameter.einzelausgabeHauptdaten && !reportingParameter.einzelausgabeDetaildaten) {
			// Dateiname der Dateien aus den Daten erzeugen.
			final String dateiname = getDateiname(mapHtmlContexts);

			// HTML-Builder erstellen und damit das HTML mit Daten für die HTML-Datei erzeugen
			reportingRepository.logger()
					.logLn(LogLevel.DEBUG, 4,
							"Verarbeite Template (%s) und Daten aus den Kontexten zum finalen HTML-Dateiinhalt.".formatted(htmlTemplateDefinition.name()));
			htmlBuilders.add(new HtmlBuilder(htmlTemplateCode, mapHtmlContexts.values().stream().toList(), dateiname).getBuilderMitIds(getContextsIds()));
		} else if (reportingParameter.einzelausgabeHauptdaten) {
			// Die Hauptdatenquelle soll in einzelne Kontexte für Einzeldateien zerlegt werden.
			erzeugeHauptEinzelContexts(htmlBuilders, htmlTemplateCode);
		} else {
			erzeugeDetailEinzelContexts(htmlBuilders, htmlTemplateCode);
		}

		reportingRepository.logger().logLn(LogLevel.DEBUG, 0, "<<< Ende der Erzeugung der HTML-Builder.");
		return htmlBuilders;
	}

	/**
	 * Erstellt einzelne Haupt-Kontexte auf Basis der gegebenen Hauptdatenquelle, um separate HTML-Dateien zu generieren.
	 *
	 * @param htmlBuilders     Eine Liste von {@code HtmlBuilder}-Objekten, in die die erzeugten HTML-Inhalte gespeichert werden.
	 * @param htmlTemplateCode Der HTML-Template-Code, der beim Generieren der HTML-Inhalte verwendet wird.
	 *
	 * @throws ApiOperationException Falls ein Fehler bei der Verarbeitung der Kontexte oder bei der Generierung der HTML-Inhalte auftritt.
	 */
	private void erzeugeHauptEinzelContexts(final List<HtmlBuilder> htmlBuilders, final String htmlTemplateCode) throws ApiOperationException {
		if (htmlTemplateDefinition.name().startsWith("SCHUELER_v_")) {
			// Zerlege den Gesamt-Schüler-Context in einzelne Contexts mit jeweils einem Schüler
			reportingRepository.logger().logLn(LogLevel.DEBUG, 4, "Erzeuge einzelne Haupt-Kontexte für jeden Schüler, da einzelne Dateien angefordert "
					+ "wurden.");
			final List<HtmlContextSchueler> schuelerContexts = ((HtmlContextSchueler) mapHtmlContexts.get("Schueler")).getEinzelContexts();

			reportingRepository.logger().logLn(LogLevel.DEBUG, 4,
					"Verarbeite Template (%s) und Daten aus den einzelnen Kontexten zu finalen HTML-Dateiinhalten.".formatted(
							htmlTemplateDefinition.name()));
			for (final HtmlContextSchueler schuelerContext : schuelerContexts) {
				mapHtmlContexts.put("Schueler", schuelerContext);

				// Dateiname der Dateien aus den Daten erzeugen.
				final String dateiname = getDateiname(mapHtmlContexts);

				// HTML-Builder erstellen und damit das HTML mit Daten für die HTML-Datei erzeugen
				htmlBuilders.add(new HtmlBuilder(htmlTemplateCode, mapHtmlContexts.values().stream().toList(), dateiname).getBuilderMitIds(getContextsIds()));
			}
		}
		if (htmlTemplateDefinition.name().startsWith("KLASSEN_v_")) {
			// Zerlege den Gesamt-Klassen-Context in einzelne Contexts mit jeweils einer Klasse
			reportingRepository.logger().logLn(LogLevel.DEBUG, 4, "Erzeuge einzelne Haupt-Kontexte für jede Klasse, da einzelne Dateien angefordert "
					+ "wurden.");
			final List<HtmlContextKlassen> klassenContexts = ((HtmlContextKlassen) mapHtmlContexts.get("Klassen")).getEinzelContexts();

			reportingRepository.logger().logLn(LogLevel.DEBUG, 4,
					"Verarbeite Template (%s) und Daten aus den einzelnen Kontexten zu finalen HTML-Dateiinhalten.".formatted(
							htmlTemplateDefinition.name()));
			for (final HtmlContextKlassen klasseContext : klassenContexts) {
				mapHtmlContexts.put("Klassen", klasseContext);

				// Dateiname der Dateien aus den Daten erzeugen.
				final String dateiname = getDateiname(mapHtmlContexts);

				// HTML-Builder erstellen und damit das HTML mit Daten für die HTML-Datei erzeugen
				htmlBuilders.add(new HtmlBuilder(htmlTemplateCode, mapHtmlContexts.values().stream().toList(), dateiname).getBuilderMitIds(getContextsIds()));
			}
		}
		if (htmlTemplateDefinition.name().startsWith("KURSE_v_")) {
			// Zerlege den Gesamt-Kurse-Context in einzelne Contexts mit jeweils einem Kurs
			reportingRepository.logger().logLn(LogLevel.DEBUG, 4, "Erzeuge einzelne Haupt-Kontexte für jeden Kurs, da einzelne Dateien angefordert "
					+ "wurden.");
			final List<HtmlContextKurse> kurseContexts = ((HtmlContextKurse) mapHtmlContexts.get("Kurse")).getEinzelContexts();

			reportingRepository.logger().logLn(LogLevel.DEBUG, 4,
					"Verarbeite Template (%s) und Daten aus den einzelnen Kontexten zu finalen HTML-Dateiinhalten.".formatted(
							htmlTemplateDefinition.name()));
			for (final HtmlContextKurse kursContext : kurseContexts) {
				mapHtmlContexts.put("Kurse", kursContext);

				// Dateiname der Dateien aus den Daten erzeugen.
				final String dateiname = getDateiname(mapHtmlContexts);

				// HTML-Builder erstellen und damit das HTML mit Daten für die HTML-Datei erzeugen
				htmlBuilders.add(new HtmlBuilder(htmlTemplateCode, mapHtmlContexts.values().stream().toList(), dateiname).getBuilderMitIds(getContextsIds()));
			}
		}
	}

	/**
	 * Ermittelt die IDs aus den in der HTML-Factory vorhandenen HTML-Kontexten. Die Methode iteriert über die Einträge der Map, sammelt die IDs aus den
	 * Kontexten, entfernt Duplikate und gibt die eindeutigen IDs zurück.
	 *
	 * @return Eine Liste von eindeutigen {@code Long}-IDs, die aus den HTML-Kontexten gesammelt wurden.
	 */
	private List<Long> getContextsIds() {
		final List<Long> ids = new ArrayList<>();

		for (final Map.Entry<String, HtmlContext<?>> entry : mapHtmlContexts.entrySet()) {
			final HtmlContext<?> context = entry.getValue();
			if (context == null)
				continue;
			ids.addAll(context.getIds());
		}

		return ids.stream().filter(Objects::nonNull).distinct().toList();
	}


	/**
	 * Erzeugt einzelne Detail-Kontexte basierend auf der definierten Darstellungsvorlage und den verfügbaren Datenquellen. Die Methode verarbeitet
	 * verschiedene Vorlagentypen und erstellt individuelle HTML-Kontexte entsprechend den Anforderungen des Vorlagentyps.
	 *
	 * @param htmlBuilders     Liste der HtmlBuilder, die zur Erstellung der Inhalte verwendet werden
	 * @param htmlTemplateCode Der Code der HTML-Vorlage, der die Art der zu erstellenden Kontexte definiert
	 *
	 * @throws ApiOperationException Wird ausgelöst, wenn ein Fehler bei der Verarbeitung der Vorlagentypen oder der verfügbaren Kontexte auftritt.
	 */
	private void erzeugeDetailEinzelContexts(final List<HtmlBuilder> htmlBuilders, final String htmlTemplateCode) throws ApiOperationException {
		switch (htmlTemplateDefinition) {
			case GOST_KLAUSURPLANUNG_v_SCHUELER_MIT_KLAUSUREN -> splitteDetailContexts("GostKlausurplan",
					((HtmlContextGostKlausurplanungKlausurplan) mapHtmlContexts.get("GostKlausurplan"))::getEinzelContexts,
					htmlBuilders, htmlTemplateCode, "Erzeuge einzelne Detail-Kontexte des Klausurplans für jeden Schüler.");
			case GOST_KURSPLANUNG_v_KURS_MIT_KURSSCHUELERN -> splitteDetailContexts("GostBlockungsergebnis",
					((HtmlContextGostKursplanungBlockungsergebnis) mapHtmlContexts.get("GostBlockungsergebnis"))::getEinzelContexts,
					htmlBuilders, htmlTemplateCode, "Erzeuge einzelne Detail-Kontexte der Kursplanung für jeden Kurs.");
			case STUNDENPLANUNG_v_FACH_STUNDENPLAN -> splitteDetailContexts("FaecherStundenplaene",
					((HtmlContextStundenplanungFachStundenplan) mapHtmlContexts.get("FaecherStundenplaene"))::getEinzelContexts,
					htmlBuilders, htmlTemplateCode, "Erzeuge einzelne Detail-Kontexte der Fachstundenpläne für jedes Fach.");
			case STUNDENPLANUNG_v_KLASSEN_STUNDENPLAN -> splitteDetailContexts("KlassenStundenplaene",
					((HtmlContextStundenplanungKlassenStundenplan) mapHtmlContexts.get("KlassenStundenplaene"))::getEinzelContexts,
					htmlBuilders, htmlTemplateCode, "Erzeuge einzelne Detail-Kontexte der Klassenstundenpläne für jede Klasse.");
			case STUNDENPLANUNG_v_LEHRER_STUNDENPLAN, STUNDENPLANUNG_v_LEHRER_STUNDENPLAN_KOMBINIERT -> splitteDetailContexts("LehrerStundenplaene",
					((HtmlContextStundenplanungLehrerStundenplan) mapHtmlContexts.get("LehrerStundenplaene"))::getEinzelContexts,
					htmlBuilders, htmlTemplateCode, "Erzeuge einzelne Detail-Kontexte der Lehrerstundenpläne für jeden Lehrer.");
			case STUNDENPLANUNG_v_RAUM_STUNDENPLAN -> splitteDetailContexts("RaeumeStundenplaene",
					((HtmlContextStundenplanungRaumStundenplan) mapHtmlContexts.get("RaeumeStundenplaene"))::getEinzelContexts,
					htmlBuilders, htmlTemplateCode, "Erzeuge einzelne Detail-Kontexte der Raumstundenpläne für jeden Raum.");
			case STUNDENPLANUNG_v_SCHUELER_STUNDENPLAN -> splitteDetailContexts("SchuelerStundenplaene",
					((HtmlContextStundenplanungSchuelerStundenplan) mapHtmlContexts.get("SchuelerStundenplaene"))::getEinzelContexts,
					htmlBuilders, htmlTemplateCode, "Erzeuge einzelne Detail-Kontexte der Schülerstundenpläne für jeden Schüler.");
			default -> throw new ApiOperationException(Status.BAD_REQUEST,
					"FEHLER: Es wurden Einzeldaten-Kontexte für eine Vorlage angefordert, die dies nicht Unterstützung. Erstellung wird abgebrochen.");
		}
	}

	/**
	 * Teilt die Detailkontexte anhand der bereitgestellten Kontextspezifikationen auf und erstellt HTML-Inhalte für jeden Kontext basierend auf dem
	 * angegebenen Template.
	 *
	 * @param <C>                        Der Typ des HTML-Kontextes, der erzeugt werden soll.
	 * @param bezeichnungContext         Der Name des Kontextes, der verarbeitet wird.
	 * @param functionErmittleEinzelContexts  Eine Funktion, die eine Liste von Einzelkontexten bereitstellt.
	 * @param htmlBuilders               Die HTML-Builder-Liste, in die die erzeugten Inhalte eingefügt werden.
	 * @param htmlTemplateCode           Der Template-Code, der für die HTML-Generierung verwendet wird.
	 * @param logText                    Ein Text, der zur Protokollierung des Prozesses verwendet wird.
	 *
	 * @throws ApiOperationException     Wenn während der Verarbeitung Fehler auftreten, wird diese Ausnahme ausgelöst.
	 */
	private <C extends HtmlContext<?>> void splitteDetailContexts(final String bezeichnungContext, final Supplier<List<C>> functionErmittleEinzelContexts,
			final List<HtmlBuilder> htmlBuilders, final String htmlTemplateCode, final String logText) throws ApiOperationException {

		reportingRepository.logger().logLn(LogLevel.DEBUG, 4, logText);
		final List<C> einzelContexts = functionErmittleEinzelContexts.get();

		reportingRepository.logger().logLn(LogLevel.DEBUG, 4,
				"Verarbeite Template (%s) und Daten aus den einzelnen Kontexten zu finalen HTML-Dateiinhalten.".formatted(htmlTemplateDefinition.name()));

		for (final C einzelContext : einzelContexts) {
			mapHtmlContexts.put(bezeichnungContext, einzelContext);
			final String dateiname = getDateiname(mapHtmlContexts);
			htmlBuilders.add(new HtmlBuilder(htmlTemplateCode, mapHtmlContexts.values().stream().toList(), dateiname).getBuilderMitIds(getContextsIds()));
		}
	}

	/**
	 * Erstellt den Dateinamen gemäß der in der Template-Definition hinterlegten Vorlage für den Dateinamen. Dabei können die Daten den Contexts entnommen werden.
	 *
	 * @param mapHtmlContexts 			Map mit den bereits erzeugten HTML-Datenkontexten, um daraus Daten für den Dateinamen entnehmen zu können.
	 *
	 * @return Der fertige Dateiname.
	 *
	 * @throws ApiOperationException	Im Fehlerfall wird eine ApiOperationException ausgelöst und Log-Daten zusammen mit dieser zurückgegeben.
	 */
	private String getDateiname(final Map<String, HtmlContext<?>> mapHtmlContexts) throws ApiOperationException {

		reportingRepository.logger().logLn(LogLevel.DEBUG, 4, "Erzeuge den Dateinamen zum Template %s.".formatted(htmlTemplateDefinition.name()));

		String dateiname = htmlTemplateDefinition.getDateiname();

		if (!htmlTemplateDefinition.getDateinamensvorlage().isEmpty() && !htmlTemplateDefinition.getDateinamensvorlage().isBlank()) {
			final HtmlBuilder htmlBuilder =
					new HtmlBuilder(htmlTemplateDefinition.getDateinamensvorlage(), mapHtmlContexts.values().stream().toList(), dateiname);
			final String html = htmlBuilder.getHtml();

			if ((html == null) || html.isEmpty() || html.isBlank() || !html.contains("<p>") || !html.contains("</p>")
					|| ((html.indexOf("<p>") + 3) >= html.indexOf("</p>"))) {
				reportingRepository.logger().logLn(LogLevel.ERROR, 4,
						("FEHLER: Erzeugung des Dateinamens zum Template %s. fehlgeschlagen. Der Dateiname konnte nicht gemäß des angegebenen Musters aus den "
								+ "Daten generiert werden.")
								.formatted(htmlTemplateDefinition.name()));
				throw new ApiOperationException(Status.INTERNAL_SERVER_ERROR,
						("FEHLER: Erzeugung des Dateinamens zum Template %s. fehlgeschlagen. Der Dateiname konnte nicht gemäß des angegebenen Musters aus den "
								+ "Daten generiert werden.")
								.formatted(htmlTemplateDefinition.name()));
			}
			dateiname = html.substring(html.indexOf("<p>") + 3, html.indexOf("</p>"));
		}

		try {
			//noinspection ResultOfMethodCallIgnored
			(new File(dateiname + ".html")).getCanonicalFile();
		} catch (@SuppressWarnings("unused") final Exception e) {
			// Rückgabewert von getCanonicalFile wird ignoriert. Diese Funktion prüft aber den Dateinamen und erzeugt eine Exception, wenn der Name ungültig
			// ist.
			reportingRepository.logger().logLn(LogLevel.ERROR, 4, "FEHLER: Der generierte HTML-Dateiname enthält ungültige Zeichen.");
			throw new ApiOperationException(Status.INTERNAL_SERVER_ERROR, "FEHLER: Der generierte HTML-Dateiname enthält ungültige Zeichen.");
		}

		return dateiname;
	}


	/**
	 * Erstellt eine ZIP-Datei, die alle HTML-Dateien aus der übergebenen Map enthält.
	 *
	 * @param htmlBuilders 				Eine Liste mit den htmlBuilders, die die HTML-Inhalte erzeugen.
	 *
	 * @return Gibt das ZIP in Form eines ByteArrays zurück.
	 *
	 * @throws ApiOperationException	Im Fehlerfall wird eine ApiOperationException ausgelöst und Log-Daten zusammen mit dieser zurückgegeben.
	 */
	private byte[] createZIP(final List<HtmlBuilder> htmlBuilders) throws ApiOperationException {
		final byte[] zipData;
		try {
			try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
				try (ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream)) {
					for (final HtmlBuilder htmlBuilder : htmlBuilders) {
						zos.putNextEntry(new ZipEntry(htmlBuilder.getDateinameMitEndung()));
						zos.write(htmlBuilder.getHtmlByteArray());
						zos.closeEntry();
					}
					byteArrayOutputStream.flush();
				}
				zipData = byteArrayOutputStream.toByteArray();
			}
		} catch (final IOException e) {
			reportingRepository.logger().logLn(LogLevel.ERROR, 4, "FEHLER: Die erzeugten HTML-Inhalte konnten nicht als ZIP-Datei zusammengestellt werden.");
			throw new ApiOperationException(Status.INTERNAL_SERVER_ERROR, e,
					"FEHLER: Die erzeugten HTML-Inhalte konnten nicht als ZIP-Datei zusammengestellt werden.");
		}
		return zipData;
	}

}
