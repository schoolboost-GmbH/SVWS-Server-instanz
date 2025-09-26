package de.svws_nrw.module.reporting.factories;

import de.svws_nrw.base.email.MailSmtpSession;
import de.svws_nrw.base.email.MailSmtpSessionConfig;
import de.svws_nrw.core.data.SimpleOperationResponse;
import de.svws_nrw.core.data.benutzer.BenutzerEMailDaten;
import de.svws_nrw.core.data.reporting.ReportingParameter;
import de.svws_nrw.core.logger.LogLevel;
import de.svws_nrw.core.types.reporting.ReportingEMailEmpfaengerTyp;
import de.svws_nrw.data.benutzer.DataBenutzerEMailDaten;
import de.svws_nrw.data.email.DBEmailUtils;
import de.svws_nrw.db.utils.ApiOperationException;
import de.svws_nrw.module.reporting.pdf.PdfBuilder;
import de.svws_nrw.module.reporting.repositories.ReportingRepository;
import de.svws_nrw.module.reporting.types.gost.kursplanung.ReportingGostKursplanungKurs;
import de.svws_nrw.module.reporting.types.klasse.ReportingKlasse;
import de.svws_nrw.module.reporting.types.kurs.ReportingKurs;
import de.svws_nrw.module.reporting.types.lehrer.ReportingLehrer;
import de.svws_nrw.module.reporting.types.person.ReportingPerson;
import de.svws_nrw.module.reporting.types.schueler.ReportingSchueler;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Versendet Report-Ergebnisse in Form von PDF-Dateien per E-Mail an die zugehörigen Personen.
 */
public final class EmailFactory {

	/**
	 * Blacklist mit E-Mail-Domains, an die kein Versand stattfinden soll. Die Domains werden in Kleinbuchstaben eingetragen.
	 * Die Domains smail.de und lmail.de sind real existierende Domains, die vom Anonymisierung-Tool für SchILD-NRW verwendet wurden und damit in vielen
	 * Beispieldatenbanken vorhanden sind.
	 */
	private static final List<String> BLOCKED_EMAIL_DOMAINS = new ArrayList<>(List.of("smail.de", "lmail.de"));

	/** Repository mit Parametern, Logger und Daten-Cache zur Report-Generierung. */
	private final ReportingRepository reportingRepository;

	/**
	 * Erstelle eine neue Instanz von EmailFactory.
	 *
	 * @param reportingRepository das Repository für Reporting-Daten
	 */
	public EmailFactory(final ReportingRepository reportingRepository) {
		this.reportingRepository = reportingRepository;
	}

	/**
	 * Versendet E-Mails mit individuellen PDF-Anhängen an Schüler basierend auf den bereitgestellten Daten.
	 * Die Methode ermittelt Empfängerdaten, erstellt die E-Mails und versendet diese über eine SMTP-Sitzung.
	 * Dabei wird für jeden Schüler geprüft, ob eine gültige E-Mail-Adresse und Anhänge vorliegen.
	 * Es wird ein Bericht über die Anzahl der erfolgreichen, übersprungenen und fehlgeschlagenen Versendungen zurückgegeben.
	 *
	 * @param pdfFactory Eine Instanz von PdfFactory, die die zu versendenden PDF-Dokumente bereitstellt
	 *
	 * @return Eine Response mit der Operationsergebniszusammenfassung, inklusive Erfolgszustand, Logs und möglicher Fehler.
	 *
	 * @throws ApiOperationException Falls ein schwerwiegender Fehler beim E-Mail-Versand auftritt.
	 */
	public Response sendEmails(final PdfFactory pdfFactory) throws ApiOperationException {
		try {
			reportingRepository.logger().logLn(LogLevel.DEBUG, 0, ">>> Beginn des E-Mail-Versands.");

			final ReportingParameter parameter = pruefeUndLiesParameter();

			final MailSmtpSession session = createSession();

			final String absenderEmail = ermittleAbsenderEmail();
			final ReportingEMailEmpfaengerTyp empfaengerTyp = ermittleEmpfaengerTyp();

			final Map<Long, List<PdfBuilder>> mapGruppiertePdfs = pdfFactory.getPdfBuildersById();

			final List<String> listUebersprungen = new ArrayList<>();
			final List<String> listFehler = new ArrayList<>();

			final Map<String, EmpfaengerEmailAnhaenge> mapEmpfaengerEmailAnhaenge =
					sammleEmpfaengerUndAnhaenge(parameter, empfaengerTyp, mapGruppiertePdfs, listUebersprungen);

			final int erfolgreich = versendeAnAlleEmpfaenger(session, absenderEmail, parameter, mapEmpfaengerEmailAnhaenge, listUebersprungen, listFehler);

			final Response response = baueResponse(erfolgreich, listUebersprungen, listFehler);

			reportingRepository.logger().logLn(LogLevel.DEBUG, 0, "<<< Ende E-Mail-Versand von Report-PDFs pro Schüler.");

			return response;
		} catch (final ApiOperationException e) {
			throw e;
		} catch (final Exception e) {
			reportingRepository.logger().logLn(LogLevel.ERROR, 4, "### FEHLER: Der E-Mail-Versand wurde aufgrund eines Fehlers abgebrochen.");
			throw new ApiOperationException(Status.INTERNAL_SERVER_ERROR, e, "### FEHLER: Fehler beim E-Mail-Versand.");
		}
	}

	/**
	 * Ermittelt die E-Mail-Adresse des aktuellen Benutzers. Falls keine gültige E-Mail-Adresse vorliegt oder ein Fehler auftritt, wird eine Ausnahme
	 * ausgelöst und der Prozess abgebrochen.
	 *
	 * @return Die ermittelte, gültige E-Mail-Adresse des aktuellen Benutzers
	 *
	 * @throws ApiOperationException Falls keine gültige E-Mail-Adresse gefunden wird oder ein Fehler beim Abrufen der Benutzerdaten auftritt.
	 */
	private String ermittleAbsenderEmail() throws ApiOperationException {
		String emailAdresse = "";
		final BenutzerEMailDaten benutzerEMailDaten;
		// E-Mail-Daten des aktuellen Benutzers ermitteln. Tritt dabei ein Fehler auf, so wird eine leere Adresse zurückgegeben.
		try {
			benutzerEMailDaten = new DataBenutzerEMailDaten(reportingRepository.conn()).getById(reportingRepository.conn().getUser().getId());
			if ((benutzerEMailDaten != null) && (benutzerEMailDaten.address != null) && !benutzerEMailDaten.address.isBlank())
				emailAdresse = benutzerEMailDaten.address.trim();
		} catch (final Exception ignore) {
			emailAdresse = "";
		}
		emailAdresse = valideEmail(emailAdresse);

		if (emailAdresse.isBlank()) {
			reportingRepository.logger().logLn(LogLevel.ERROR, 4,
					"### FEHLER: Der E-Mail-Versand wurde abgebrochen, da für den aktuellen Benutzer keine gültige E-Mail-Adresse "
							+ "ermittelt werden konnte. Bitte überprüfen Sie die E-Mail-Einstellungen des Benutzers.");
			throw new ApiOperationException(Status.BAD_REQUEST, null, null, MediaType.APPLICATION_JSON);
		}
		return emailAdresse;
	}

	/**
	 * Ermittelt den aktuellen E-Mail-Empfänger-Typ aus den Reporting-Parametern.
	 * Falls kein gültiger Empfängertyp definiert ist, wird eine ApiOperationException ausgelöst.
	 *
	 * @return Der ermittelte Empfänger-Typ vom Typ {@link ReportingEMailEmpfaengerTyp}.
	 *
	 * @throws ApiOperationException Wenn kein gültiger Empfängertyp definiert ist, wird ein Fehler geworfen.
	 */
	private ReportingEMailEmpfaengerTyp ermittleEmpfaengerTyp() throws ApiOperationException {
		final ReportingParameter parameter = this.reportingRepository.reportingParameter();
		if ((parameter != null) && (parameter.eMailDaten != null)
				&& (ReportingEMailEmpfaengerTyp.getByID(parameter.eMailDaten.empfaengerTyp) != ReportingEMailEmpfaengerTyp.UNDEFINED)) {
			reportingRepository.logger().logLn(LogLevel.DEBUG, 4,
					"Der E-Mail-Empfänger-Typ wurde ermittelt: " + ReportingEMailEmpfaengerTyp.getByID(parameter.eMailDaten.empfaengerTyp).name());
			return ReportingEMailEmpfaengerTyp.getByID(parameter.eMailDaten.empfaengerTyp);
		} else {
			reportingRepository.logger().logLn(LogLevel.ERROR, 4, "### FEHLER: Es wurde kein gültiger Empfängertyp festgelegt");
			throw new ApiOperationException(Status.BAD_REQUEST, "### FEHLER: Es wurde kein gültiger Empfängertyp festgelegt.");
		}
	}

	/**
	 * Prüft, ob die notwendigen Parameter und E-Mail-Daten für den E-Mail-Versand vorhanden und korrekt gesetzt sind, und liest diese aus. Sollte eine der
	 * notwendigen Informationen fehlen oder nicht valide sein, wird eine Ausnahme ausgelöst.
	 *
	 * @return Das ReportingParameter-Objekt, das die nötigen Parameter und E-Mail-Daten enthält
	 *
	 * @throws ApiOperationException Fehler werfen, falls die benötigten Parameter oder E-Mail-Daten nicht vorhanden sind, unvollständig oder ungültig sind.
	 */
	private ReportingParameter pruefeUndLiesParameter() throws ApiOperationException {
		if ((this.reportingRepository.reportingParameter() == null) || (this.reportingRepository.reportingParameter().eMailDaten == null)) {
			reportingRepository.logger().logLn(LogLevel.ERROR, 4,
					"### FEHLER: Der E-Mail-Versand wurde abgebrochen, da die notwendigen Parameter und E-Mail-Daten nicht übergeben wurden.");
			throw new ApiOperationException(Status.BAD_REQUEST, null, null, MediaType.APPLICATION_JSON);
		}
		final ReportingParameter parameter = this.reportingRepository.reportingParameter();
		if ((parameter.eMailDaten.betreff == null) || parameter.eMailDaten.betreff.isBlank()
				|| (parameter.eMailDaten.text == null) || parameter.eMailDaten.text.isBlank()) {
			reportingRepository.logger().logLn(LogLevel.ERROR, 4,
					"### FEHLER: Der E-Mail-Versand wurde abgebrochen, da kein Betreff oder Text für die E-Mail angegeben wurde.");
			throw new ApiOperationException(Status.BAD_REQUEST, null, null, MediaType.APPLICATION_JSON);
		}
		return parameter;
	}

	/**
	 * Erstellt eine neue SMTP-Sitzung basierend auf der SMTP-Konfiguration, die aus den Schul- und Benutzereinstellungen ermittelt wird.
	 *
	 * @return Eine Instanz von MailSmtpSession, die die SMTP-Sitzung repräsentiert.
	 *
	 * @throws ApiOperationException Falls keine gültige SMTP-Konfiguration erstellt werden konnte oder ein anderer Fehler auftritt.
	 */
	private MailSmtpSession createSession() throws ApiOperationException {
		// SMTP-Konfiguration aus Schul- und Benutzer-Einstellungen laden
		final MailSmtpSessionConfig smtpConfig = DBEmailUtils.getSMTPConfig(reportingRepository.conn());
		if (smtpConfig == null) {
			reportingRepository.logger().logLn(LogLevel.ERROR, 4, "### FEHLER: Es konnte keine gültige SMTP-Konfiguration erstellt werden.");
			throw new ApiOperationException(Status.BAD_REQUEST, "### FEHLER: Es konnte keine gültige SMTP-Konfiguration erstellt werden.");
		}
		return new MailSmtpSession(smtpConfig);
	}

	/**
	 * Sammelt E-Mail-Empfänger und ihre zugehörigen Anhänge anhand der bereitgestellten Parameter. Diese Methode verarbeitet eine Gruppierung von PDFs und
	 * weist sie den jeweiligen Empfängern zu, um so die vollständigen Daten für den E-Mail-Versand vorzubereiten. Nicht zuordenbare oder ungültige Einträge
	 * werden in einer separaten Liste aufgeführt.
	 *
	 * @param parameter Die Reporting-Parameter, die unter anderem Informationen zu E-Mail-Daten enthalten.
	 * @param empfaengerTyp Der Typ der Empfänger, der für die Ermittlung der E-Mail-Adressen benutzt wird.
	 * @param mapGruppiertePdfs Eine Zuordnung von IDs zu einer Liste von PdfBuilder-Objekten, die die PDFs repräsentieren.
	 * @param listUebersprungen Die Liste, in der Informationen über übersprungene Datensätze gesammelt werden.
	 *
	 * @return Eine Zuordnung von E-Mail-Adressen zu Empfänger-Objekten und deren zugehörigen Anhängen.
	 *
	 * @throws ApiOperationException Wenn beim Prozessieren der Daten ein Fehler auftritt, wird diese Exception geworfen.
	 */
	private Map<String, EmpfaengerEmailAnhaenge> sammleEmpfaengerUndAnhaenge(final ReportingParameter parameter,
			final ReportingEMailEmpfaengerTyp empfaengerTyp, final Map<Long, List<PdfBuilder>> mapGruppiertePdfs, final List<String> listUebersprungen)
			throws ApiOperationException {

		final Map<String, EmpfaengerEmailAnhaenge> mapEmpfaengerEmailAnhaenge = new HashMap<>();

		for (final Map.Entry<Long, List<PdfBuilder>> entry : mapGruppiertePdfs.entrySet()) {

			final Long id = entry.getKey();
			final List<PdfBuilder> pdfBuilders = entry.getValue();

			if (id == null)
				continue;

			if (id < 0) {
				listUebersprungen.add("- HINWEIS: Es gab PDF-Dateien, die keinem Empfänger zugeordnet werden konnten. Diese werden nicht versendet.");
				continue;
			}

			if ((pdfBuilders == null) || pdfBuilders.isEmpty()) {
				listUebersprungen.add("- Für die ID " + id + " konnten keine PDFs gefunden werden und damit kein E-Mail-Versand erfolgen.");
				continue;
			}

			final List<ReportingPerson> empfaengerPersonen = ermittleEmpfaengerPersonen(id, empfaengerTyp);
			if (empfaengerPersonen.isEmpty()) {
				listUebersprungen.add("- Für die ID " + id + " konnten keine Empfänger ermittelt werden. Sie wird beim E-Mail-Versand übersprungen.");
				continue;
			}

			final List<byte[]> attachmentsData = new ArrayList<>();
			final List<String> attachmentsMime = new ArrayList<>();
			final List<String> attachmentsNames = new ArrayList<>();
			for (final PdfBuilder pdfBuilder : pdfBuilders) {
				attachmentsData.add(pdfBuilder.getPdfByteArray());
				attachmentsMime.add("application/pdf");
				attachmentsNames.add(pdfBuilder.getDateinameMitEndung());
			}

			for (final ReportingPerson empfaengerPerson : empfaengerPersonen) {
				final String empfaengerEmail = ermittleEmpfaengerEmail(empfaengerPerson, parameter.eMailDaten.istPrivateEmailAlternative);

				if (empfaengerEmail.isBlank()) {
					listUebersprungen.add("- Für die ID " + id + " konnte keine gültige E-Mail-Adresse des Empfängers ermittelt werden. Sie wird beim "
							+ "E-Mail-Versand übersprungen.");
					continue;
				}

				if (BLOCKED_EMAIL_DOMAINS.contains(empfaengerEmail.toLowerCase().substring(empfaengerEmail.indexOf('@') + 1))) {
					listUebersprungen.add("- Die E-Mail an " + empfaengerEmail + " konnte nicht versendet werden, da die Domain als unzulässig markiert "
							+ "wurde.");
					continue;
				}

				final EmpfaengerEmailAnhaenge empfaengerEmailAnhaenge =
						mapEmpfaengerEmailAnhaenge.computeIfAbsent(empfaengerEmail, k -> new EmpfaengerEmailAnhaenge(empfaengerPerson));
				empfaengerEmailAnhaenge.attachmentsData.addAll(attachmentsData);
				empfaengerEmailAnhaenge.attachmentsMime.addAll(attachmentsMime);
				empfaengerEmailAnhaenge.attachmentsNames.addAll(attachmentsNames);
			}
		}
		return mapEmpfaengerEmailAnhaenge;
	}

	/**
	 * Ermittelt die E-Mail-Adresse eines Empfängers basierend auf den bereitgestellten Daten. Standardmäßig wird die schulische E-Mail-Adresse verwendet.
	 * Ist diese nicht verfügbar und ist die Verwendung der privaten E-Mail-Adresse zulässig, wird alternativ die private Adresse verwendet.
	 *
	 * @param reportingPerson Die Person, deren E-Mail-Adresse ermittelt werden soll. Kann nicht null sein.
	 * @param istPrivateEmailAlternative Ein Indikator, ob die private E-Mail-Adresse genutzt werden darf, falls die schulische Adresse nicht verfügbar ist.
	 *
	 * @return Die gültige E-Mail-Adresse des Empfängers als String. Falls keine Adresse verfügbar ist, wird ein leerer String zurückgegeben.
	 */
	private String ermittleEmpfaengerEmail(final ReportingPerson reportingPerson, final boolean istPrivateEmailAlternative) {
		if (reportingPerson == null)
			return "";

		// Im Regelfall wird die schulische E-Mail-Adresse verwendet.
		String emailAdresse = reportingPerson.emailSchule();
		if ((emailAdresse != null) && !emailAdresse.isBlank())
			emailAdresse = valideEmail(emailAdresse);
		else
			emailAdresse = "";

		// Ist keine schulische E-Mail-Adresse vorhanden und wurde die private E-Mail-Adresse als Empfänger erlaubt, dann wird die private E-Mail-Adresse
		// verwendet.
		if ((emailAdresse.isBlank()) && istPrivateEmailAlternative) {
			emailAdresse = reportingPerson.emailPrivat();
			if ((emailAdresse != null) && !emailAdresse.isBlank())
				emailAdresse = valideEmail(emailAdresse);
			else
				emailAdresse = "";
		}

		return emailAdresse;
	}

	/**
	 * Ermittelt eine Liste von Empfänger-Personen basierend auf der angegebenen ID und dem spezifizierten Typ.
	 * Die Methode verarbeitet verschiedene Modi und gibt die entsprechenden Daten aus dem Reporting-Repository zurück.
	 *
	 * @param id Die ID, anhand der die zu ermittelnden Personen identifiziert werden
	 * @param typ Der Typ, der den Typ der zu ermittelnden Empfänger vorgibt.
	 *
	 * @return Eine Liste von ReportingPerson-Objekten, die die ermittelten Empfänger darstellen.
	 */
	private List<ReportingPerson> ermittleEmpfaengerPersonen(final long id, final ReportingEMailEmpfaengerTyp typ) throws ApiOperationException {
		return switch (typ) {
			case SCHUELER -> {
				final ReportingSchueler schueler = reportingRepository.schueler(id);
				yield (schueler == null) ? new ArrayList<>() : List.of(schueler);
			}
			case LEHRER -> {
				final ReportingLehrer lehrer = reportingRepository.lehrer(id);
				yield (lehrer == null) ? new ArrayList<>() : List.of(lehrer);
			}
			case KLASSENLEHRER -> {
				final ReportingKlasse klasse = reportingRepository.klasse(id);
				if (klasse == null)
					yield new ArrayList<>();
				final List<ReportingLehrer> lehrer = (klasse.klassenleitungen() == null) ? new ArrayList<>() : klasse.klassenleitungen();
				yield new ArrayList<>(lehrer);
			}
			case KURSLEHRER -> {
				final ReportingKurs kurs = reportingRepository.mapKurse().get(id);
				if (kurs == null)
					yield new ArrayList<>();
				final List<ReportingLehrer> lehrer = (kurs.lehrkraefte() == null) ? new ArrayList<>() : kurs.lehrkraefte();
				yield new ArrayList<>(lehrer);
			}
			case GOSTKURSPLANUNG_KURSLEHRER -> {
				final ReportingGostKursplanungKurs kurs = reportingRepository.mapGostKursplanungKurse().get(id);
				if (kurs == null)
					yield new ArrayList<>();
				final List<ReportingLehrer> lehrer = (kurs.lehrkraefte() == null) ? new ArrayList<>() : kurs.lehrkraefte();
				yield new ArrayList<>(lehrer);
			}
			default -> {
				reportingRepository.logger().logLn(LogLevel.ERROR, 4,
						"### FEHLER: Es wurde kein gültiger Empfängertyp festgelegt. Der Versand der E-Mails wurde abgebrochen.");
				throw new ApiOperationException(Status.NOT_FOUND, null, null, MediaType.APPLICATION_JSON);
			}
		};
	}

	/**
	 * Sendet E-Mails an alle Empfänger, die in der angegebenen Map definiert sind, mit den entsprechenden Anhängen und Parametern. Es wird der Erfolg der
	 * gesendeten E-Mails gezählt.
	 *
	 * @param session Die SMTP-Sitzung, die für den E-Mail-Versand verwendet wird.
	 * @param absenderEmail Die E-Mail-Adresse des Absenders.
	 * @param parameter Die Parameter, die spezifische Einstellungen für den E-Mail-Versand enthalten.
	 * @param mapEmpfaengerEmailAnhaenge Eine Map, die die Empfänger-E-Mails als Schlüssel und die korrespondierenden
	 *                                   E-Mail-Anhänge als Wert enthält.
	 * @param listUebersprungen Eine Liste, die benutzt wird, um die E-Mail-Adressen der Empfänger zu protokollieren,
	 *                          deren E-Mails übersprungen wurden.
	 * @param listFehler Eine Liste, die Fehlertexte protokolliert, die beim E-Mail-Versand aufgetreten sind.
	 *
	 * @return Die Anzahl der erfolgreich gesendeten E-Mails.
	 *
	 * @throws ApiOperationException Wenn ein Fehler während der Operation auftritt, wird dies Exception geworfen.
	 */
	private int versendeAnAlleEmpfaenger(final MailSmtpSession session, final String absenderEmail, final ReportingParameter parameter,
			final Map<String, EmpfaengerEmailAnhaenge> mapEmpfaengerEmailAnhaenge, final List<String> listUebersprungen, final List<String> listFehler)
			throws ApiOperationException {

		int erfolgreich = 0;

		for (final Map.Entry<String, EmpfaengerEmailAnhaenge> anhaengeEntry : mapEmpfaengerEmailAnhaenge.entrySet()) {
			final String empfaengerEmail = anhaengeEntry.getKey();
			final EmpfaengerEmailAnhaenge anhaenge = anhaengeEntry.getValue();

			final String subject = buildEMailBetreff(parameter);
			final String body = buildEMailHTMLBody(parameter);

			final int maxAnhangGesamtgroesseInKB = parameter.eMailDaten.maxAnhangGesamtgroesseInKB;
			final boolean istMaxAnhangGesamtgroesseInKBAbsolut = parameter.eMailDaten.istMaxAnhangGesamtgroesseInKBAbsolut;

			if (maxAnhangGesamtgroesseInKB > 0) {
				final List<List<Integer>> pakete = bildePaketeMitAnhaengenNachMaxGroesse(
						anhaenge.attachmentsData, maxAnhangGesamtgroesseInKB, istMaxAnhangGesamtgroesseInKBAbsolut, empfaengerEmail, listFehler);

				if (pakete.isEmpty()) {
					listUebersprungen.add("- Für Empfänger " + empfaengerEmail + " konnten keine versendbaren Anhänge ermittelt werden. Er wird beim "
							+ "Versand übersprungen.");
					continue;
				}

				for (final List<Integer> paket : pakete) {
					final List<byte[]> paketData = new ArrayList<>(paket.size());
					final List<String> paketMime = new ArrayList<>(paket.size());
					final List<String> paketNames = new ArrayList<>(paket.size());
					for (final Integer index : paket) {
						paketData.add(anhaenge.attachmentsData.get(index));
						paketMime.add(anhaenge.attachmentsMime.get(index));
						paketNames.add(anhaenge.attachmentsNames.get(index));
					}
					erfolgreich += sendeEmail(session, absenderEmail, empfaengerEmail, subject, body, paketData, paketMime, paketNames, listFehler) ? 1 : 0;
				}
			} else {
				erfolgreich += sendeEmail(session, absenderEmail, empfaengerEmail, subject, body,
						anhaenge.attachmentsData, anhaenge.attachmentsMime, anhaenge.attachmentsNames, listFehler) ? 1 : 0;
			}
		}
		return erfolgreich;
	}

	/**
	 * Bildet Pakete aus einer Liste von Anhängen basierend auf einer maximalen Paketgröße. Anhänge, die größer sind als die Paketgröße, können entweder
	 * verworfen oder in separate Pakete aufgenommen werden, abhängig von der Konfiguration für absolute Maximalgrößen.
	 *
	 * @param attachmentsData Eine Liste von Byte-Arrays, wobei jedes Element die Daten eines Anhangs darstellt.
	 *                        Diese Anhänge werden in Pakete gruppiert.
	 * @param maxPaketgroesseInKB Die maximale Größe eines Pakets in Kilobyte. Wenn 0 oder negativ, werden keine Pakete gebildet.
	 * @param istMaxPaketgroesseInKBAbsolut Gibt an, ob die maximale Paketgröße absolut ist (true, Anhänge, die zu groß sind,
	 *                                      werden verworfen) oder flexibel (false, zu große Anhänge werden in eigenen Paketen gespeichert).
	 * @param empfaengerEmail Die E-Mail-Adresse des Empfängers, die bei Fehlern in der Fehlerliste referenziert wird.
	 * @param listFehler Eine Liste, in die Fehlerprotokolle eingetragen werden können, falls Anhänge verworfen oder nicht erfolgreich verarbeitet werden.
	 *
	 * @return Eine Liste von Listen, wobei jede innere Liste die Indizes der Anhänge enthält, die in einem Paket gruppiert wurden.
	 *         Die Reihenfolge der Indizes entspricht der ursprünglichen Reihenfolge der Anhänge in der Eingabeliste.
	 */
	private static List<List<Integer>> bildePaketeMitAnhaengenNachMaxGroesse(final List<byte[]> attachmentsData, final int maxPaketgroesseInKB,
			final boolean istMaxPaketgroesseInKBAbsolut, final String empfaengerEmail, final List<String> listFehler) {

		// Die Liste für die Rückgabe führt nachher Listen von Indices von Anhängen aus der Anhänge-Liste. Jeder dieser List<Integer> enthält die Indices
		// der Anhänge für eine E-Mail.
		final List<List<Integer>> pakete = new ArrayList<>();

		// Die Anhänge sind als Byte-Arrays abgelegt. Bestimme daher die max. Paketgröße in Bytes (1 Kilobyte entspricht 1 024 Byte).
		final long maxPaketgroesseInByte = Math.max(0, (long) maxPaketgroesseInKB) * 1024L;

		if ((attachmentsData == null) || attachmentsData.isEmpty() || (maxPaketgroesseInByte <= 0))
			return pakete;

		// Ein Array anlegen, das die Größen der Anhänge in Byte sammelt.
		final int anzahlAnhaenge = attachmentsData.size();
		final int[] groessenAnhaenge = new int[anzahlAnhaenge];
		for (int i = 0; i < anzahlAnhaenge; i++) {
			final byte[] anhangsdaten = attachmentsData.get(i);
			groessenAnhaenge[i] = (anhangsdaten == null) ? 0 : anhangsdaten.length;
		}

		// Erstelle eine Liste von Indices der Anhänge und sortiere dann die Liste der Indizes absteigend nach der Größe der Anhänge.
		final List<Integer> indices = new ArrayList<>(anzahlAnhaenge);
		for (int i = 0; i < anzahlAnhaenge; i++)
			indices.add(i);
		indices.sort((a, b) -> Integer.compare(groessenAnhaenge[b], groessenAnhaenge[a]));

		// Fülle nun die Pakete nach dem Prinzip "First-Fit": Packe jeden Index in das erste Paket, das noch Platz hat, ansonsten eröffne ein neues Paket.
		// Anhänge, die größer sind als die Paketgröße, werden in eigene Pakete gepackt, deren Größe damit größer ist als das Limit.
		final List<Long> aktuellePaketgroessen = new ArrayList<>();
		for (final int index : indices) {
			final long groesseZumAktuellenIndex = groessenAnhaenge[index];

			// Wenn die Größe über dem Limit liegt, wird ein eigenständiges Paket erstellt oder der Anhang verworfen, je nach Einstellung.
			if (groesseZumAktuellenIndex > maxPaketgroesseInByte) {
				if (istMaxPaketgroesseInKBAbsolut) {
					// Die maximale Paketgröße darf nicht überschritten werden, verwerfe daher den Anhang und logge das Problem.
					if (listFehler != null) {
						listFehler.add("- Fehler: Anhang wurde nicht an " + empfaengerEmail + " versendet, da er das maximale Größenlimit des Anhangs "
								+ "überschreitet.");
					}
				} else {
					final List<Integer> einzelanhang = new ArrayList<>(1);
					einzelanhang.add(index);
					pakete.add(einzelanhang);
					aktuellePaketgroessen.add(groesseZumAktuellenIndex);
				}
				continue;
			}

			// Versuche den aktuellen Anhang in einem bestehenden Paket zu platzieren.
			boolean platziert = false;
			for (int paket = 0; paket < pakete.size(); paket++) {
				if ((aktuellePaketgroessen.get(paket) + groesseZumAktuellenIndex) <= maxPaketgroesseInByte) {
					pakete.get(paket).add(index);
					aktuellePaketgroessen.set(paket, aktuellePaketgroessen.get(paket) + groesseZumAktuellenIndex);
					platziert = true;
					break;
				}
			}

			// Wenn der Anhang nicht in einem Paket platziert wurde, wird ein neues Paket erstellt.
			if (!platziert) {
				final List<Integer> newBin = new ArrayList<>();
				newBin.add(index);
				pakete.add(newBin);
				aktuellePaketgroessen.add(groesseZumAktuellenIndex);
			}
		}
		return pakete;
	}

	/**
	 * Versendet eine E-Mail mit optionalen Anhängen und behandelt eventuelle Fehler.
	 *
	 * @param session Die SMTP-Session, die für den Versand der E-Mail verwendet wird.
	 * @param absenderEmail Die E-Mail-Adresse des Absenders.
	 * @param empfaengerEmail Die E-Mail-Adresse des Empfängers.
	 * @param subject Der Betreff der E-Mail.
	 * @param body Der Textinhalt der E-Mail.
	 * @param attachmentsData Eine Liste mit den Byte-Daten der Anhänge.
	 * @param attachmentsMime Eine Liste mit den MIME-Typen der Anhänge.
	 * @param attachmentsNames Eine Liste mit den Dateinamen der Anhänge.
	 * @param listFehler Eine Liste zur Speicherung von Fehlernachrichten, falls der Versand fehlschlägt.
	 *
	 * @return True, wenn die E-Mail erfolgreich versendet wurde, andernfalls false.
	 */
	private static boolean sendeEmail(final MailSmtpSession session, final String absenderEmail, final String empfaengerEmail, final String subject,
			final String body, final List<byte[]> attachmentsData, final List<String> attachmentsMime, final List<String> attachmentsNames,
			final List<String> listFehler) {
		try {
			session.sendTextMessageWithAttachments(absenderEmail, empfaengerEmail, subject, body, attachmentsData, attachmentsMime, attachmentsNames);
		} catch (final Exception e) {
			listFehler.add("- Fehler beim Versand an Empfänger " + empfaengerEmail + ": " + e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Baut eine HTTP-Response basierend auf den Ergebnissen eines E-Mail-Versands.
	 *
	 * @param erfolgreich Die Anzahl der erfolgreich versendeten E-Mails.
	 * @param listUebersprungen Eine Liste mit E-Mails, die übersprungen wurden.
	 * @param listFehler Eine Liste mit E-Mails, bei deren Versand Fehler aufgetreten sind.
	 *
	 * @return Eine Response mit den entsprechenden Statusinformationen und Log-Einträgen.
	 */
	private Response baueResponse(final int erfolgreich, final List<String> listUebersprungen, final List<String> listFehler) {
		final SimpleOperationResponse sop = new SimpleOperationResponse();
		sop.success = listFehler.isEmpty();
		sop.log = new ArrayList<>();
		sop.log.add("E-Mail-Versand abgeschlossen. Erfolgreich: " + erfolgreich + ", Übersprungen: " + listUebersprungen.size() + ", Fehler: "
				+ listFehler.size() + ".");
		if (!listUebersprungen.isEmpty()) {
			sop.log.add("Übersprungen beim E-Mail-Versand:");
			sop.log.addAll(listUebersprungen);
		}
		if (!listFehler.isEmpty()) {
			sop.log.add("Fehler während des E-Mail-Versands:");
			sop.log.addAll(listFehler);
		}
		return Response.status(listFehler.isEmpty() ? Status.OK : Status.ACCEPTED).type(MediaType.APPLICATION_JSON).entity(sop).build();
	}


	// ##### Hilfsmethoden

	/**
	 * Überprüft, ob die angegebene E-Mail-Adresse gültig ist.
	 *
	 * @param emailAddress Die E-Mail-Adresse, die überprüft werden soll.
	 *
	 * @return True, wenn die E-Mail-Adresse gültig ist, andernfalls false.
	 */
	private boolean istValideEmail(final String emailAddress) {
		if ((emailAddress == null) || emailAddress.isBlank())
			return false;
		try {
			final InternetAddress address = new InternetAddress(emailAddress);
			address.validate();
			return true;
		} catch (final AddressException addressException) {
			return false;
		}
	}

	/**
	 * Validiert eine gegebene E-Mail-Adresse. Falls die E-Mail-Adresse gültig ist,
	 * wird sie zurückgegeben, andernfalls wird ein leerer String zurückgegeben.
	 *
	 * @param emailAddress Die E-Mail-Adresse, die validiert werden soll. Kann {@code null} oder leer sein.
	 *
	 * @return Die validierte E-Mail-Adresse, wenn sie gültig ist, ansonsten ein leerer String.
	 */
	private String valideEmail(final String emailAddress) {
		if ((emailAddress == null) || emailAddress.isBlank())
			return "";
		// E-Mail-Adresse bearbeiten ...
		final String resultEmailAddress = emailAddress.trim().toLowerCase();
		// ... und dann validieren.
		if (istValideEmail(resultEmailAddress))
			return resultEmailAddress;
		else
			return "";
	}

	/**
	 * Erstellt den Betreff einer E-Mail basierend auf den übergebenen Reporting-Parametern und validiert, ob ein gültiger Betreff vorliegt. Falls kein
	 * Betreff angegeben ist, wird ein Fehler geloggt und eine ApiOperationException ausgelöst.
	 *
	 * @param parameter Die ReportingParameter, die die relevanten Daten für die E-Mail enthalten. Insbesondere wird das Feld {@code eMailDaten.betreff}
	 *                  verwendet.
	 *
	 * @return Der generierte Betreff der E-Mail als String.
	 *
	 * @throws ApiOperationException Falls kein gültiger Betreff definiert ist.
	 */
	private String buildEMailBetreff(final ReportingParameter parameter) throws ApiOperationException {
		if ((parameter != null) && (parameter.eMailDaten != null) && (parameter.eMailDaten.betreff != null) && (!parameter.eMailDaten.betreff.isBlank())) {
			return parameter.eMailDaten.betreff;
		} else {
			reportingRepository.logger().logLn(LogLevel.ERROR, 4,
					"### FEHLER: Der E-Mail-Versand wurde abgebrochen, da kein Betreff für die E-Mail angegeben wurde.");
			throw new ApiOperationException(Status.BAD_REQUEST, null, null, MediaType.APPLICATION_JSON);
		}
	}

	/**
	 * Erzeugt den HTML-Body für die E-Mail basierend auf den übergebenen Parametern.
	 * Der Text wird aus einem Plain-Text-Feld entnommen, normalisiert, in HTML umgewandelt und strukturiert.
	 * Leerzeilen und Zeilenumbrüche werden dabei entsprechend formatiert.
	 *
	 * @param parameter die Parameter, die die E-Mail-Daten, einschließlich des Textes, enthalten
	 *
	 * @return der generierte E-Mail-Text als HTML-String
	 *
	 * @throws ApiOperationException falls die übergebenen Parameter ungültig sind oder der Text fehlt
	 */
	private String buildEMailHTMLBody(final ReportingParameter parameter) throws ApiOperationException {
		if ((parameter != null) && (parameter.eMailDaten != null) && (parameter.eMailDaten.text != null) && (!parameter.eMailDaten.text.isBlank())) {
			// Der Text kommt als Plain-Text aus dem Client. Normalisiere zunächst den übergebenen Text.
			final String emailBodyPlainText = normalisierterPlainText(parameter.eMailDaten.text);

			// Plain-Text in HTML transformieren. Dazu HTML-escaped	Text erstellen.
			final String escaped = htmlEscape(emailBodyPlainText);

			// Einen StringBuilder für den HTML-Code erzeugen und ein minimales HTML-Grundgerüst einfügen, damit Clients korrekt als HTML rendern.
			final StringBuilder html = new StringBuilder(escaped.length() + 128);

			html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head><body>");

			// Teile den Text in Absätze. Doppel-CRLF werden als Absatz (<p>) behandelt, Einzel-CRLF als Zeilenumbruch.
			final String[] absaetze = escaped.split("\\r\\n\\r\\n");
			for (final String absatz : absaetze) {
				// Ersetze verbleibende Einzelzeilenumbrüche im Absatz durch <br>
				final String absatzMitBr = absatz.replace("\r\n", "<br>");
				// Leere Absätze als echte Leerzeile darstellen
				if (absatzMitBr.isBlank()) {
					html.append("<p><br></p>");
				} else {
					html.append("<p>").append(absatzMitBr).append("</p>");
				}
			}

			html.append("</body></html>");

			return html.toString();
		} else {
			reportingRepository.logger().logLn(LogLevel.ERROR, 4,
					"### FEHLER: Der E-Mail-Versand wurde abgebrochen, da kein Text für die E-Mail angegeben wurde.");
			throw new ApiOperationException(Status.BAD_REQUEST, null, null, MediaType.APPLICATION_JSON);
		}
	}

	/**
	 * Normalisiert den gegebenen Text, indem Zeilenumbrüche vereinheitlicht, überflüssige Leerzeilen reduziert,
	 * Tabs durch Leerzeichen ersetzt und abschließende Leerzeilen sicherstellt werden.
	 * Der Rückgabewert enthält abschließend Zeilenumbrüche im CRLF-Format zur maximalen Kompatibilität.
	 *
	 * @param plainText Der ursprüngliche Text, der normalisiert werden soll.
	 *
	 * @return Der normalisierte Text mit angepassten Zeilenumbrüchen und Leerzeilen.
	 */
	private static String normalisierterPlainText(final String plainText) {
		// 1) Normalisiere Zeilenumbrüche auf \n
		String result = plainText.replace("\r\n", "\n").replace("\r", "\n");

		// 2) Trimme führende/abschließende Spaces pro Zeile und reduziere doppelte Leerzeilen
		final String[] zeilen = result.split("\n", -1);
		final StringBuilder normalisierterText = new StringBuilder(result.length());
		boolean warLetzteZeileLeer = false;
		for (final String zeile : zeilen) {
			// Leerzeichen und white spaces entfernen.
			final String neueZeile = (zeile == null) ? "" : zeile.strip();
			if (neueZeile.isEmpty()) {
				// Reduziere mehrere Leerzeilen auf eine Leerzeile.
				if (!warLetzteZeileLeer) {
					normalisierterText.append('\n');
					warLetzteZeileLeer = true;
				}
			} else {
				// Ersetze Tabs durch 4 Spaces
				final String zeileOhneTabs = neueZeile.replace("\t", "    ");
				normalisierterText.append(zeileOhneTabs).append('\n');
				warLetzteZeileLeer = false;
			}
		}
		result = normalisierterText.toString();

		// 3) Sicherstellen, dass eine abschließende Leerzeile existiert.
		if (!result.endsWith("\n\n")) {
			if (result.endsWith("\n")) {
				result = result + "\n";
			} else {
				result = result + "\n\n";
			}
		}

		// 4) Konvertiere die Zeilenumbrüche final auf CRLF für maximale Client-Kompatibilität
		result = result.replace("\n", "\r\n");
		return result;
	}

	/**
	 * Wandelt ein übergebenes Text-String-Objekt in einen HTML-escaped String um. Zeichen wie `&`, `<`, `>` und `"` werden durch HTML-Entities ersetzt.
	 *
	 * @param text Der Eingabetext, der HTML-maskiert werden soll. Wenn der Text null oder leer ist, wird ein leerer String zurückgegeben.
	 *
	 * @return Der HTML-escaped String, der für die Verwendung in HTML-Inhalten geeignet ist.
	 */
	private static String htmlEscape(final String text) {
		if ((text == null) || text.isEmpty())
			return "";
		final StringBuilder stringBuilder = new StringBuilder(text.length());
		for (int i = 0; i < text.length(); i++) {
			final char c = text.charAt(i);
			switch (c) {
				case '&' -> stringBuilder.append("&amp;");
				case '<' -> stringBuilder.append("&lt;");
				case '>' -> stringBuilder.append("&gt;");
				case '"' -> stringBuilder.append("&quot;");
				case '\'' -> stringBuilder.append("&#39;");
				default -> stringBuilder.append(c);
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * Diese Klasse repräsentiert eine Sammlung von E-Mail-Anhängen, die einer bestimmten empfangenden Person zugeordnet sind. Sie dient dazu, die
	 * Informationen des Empfängers sowie die zugehörigen Dateianhänge und deren Eigenschaften zu speichern und zu verwalten. Die Klasse steht im Kontext
	 * einer internen Verarbeitung und ist daher als `private` und `static` deklariert.
	 */
	private static final class EmpfaengerEmailAnhaenge {

		/**
		 * Eine Instanz von ReportingPerson, die den Empfänger dieser Aggregation repräsentiert. Dieses Feld enthält Informationen über die Person, die die
		 * zugehörigen Daten und Anhänge erhalten soll.
		 * Die ReportingPerson wird verwendet, um den Empfänger eindeutig zu identifizieren und eventuell weitere, mit der E-Mail oder den Daten
		 * zusammenhängende Informationen darzustellen.
		 */
		final ReportingPerson reportingPerson;

		/**
		 * Eine Liste, die die binären Daten von Dateianhängen speichert.
		 * Jeder Eintrag in der Liste repräsentiert die Rohdaten eines Dateianhangs in Form eines Byte-Arrays.
		 */
		final List<byte[]> attachmentsData = new ArrayList<>();

		/**
		 * Eine Liste, die die MIME-Typen der Dateianhänge speichert.
		 * Jeder Eintrag in der Liste repräsentiert den MIME-Typ (z. B. "application/pdf" oder "image/png") eines entsprechenden Dateianhangs.
		 * Diese Liste wird verwendet, um die Art der angehängten Dateien zu kennzeichnen, damit der Empfänger die Dateien korrekt interpretieren oder
		 * verarbeiten kann.
		 */
		final List<String> attachmentsMime = new ArrayList<>();

		/**
		 * Eine Liste, die die Namen der Dateianhänge speichert. Jeder Eintrag in der Liste repräsentiert den Dateinamen eines entsprechenden Anhangs.
		 * Diese Liste wird verwendet, um die Namen der angehängten Dateien bereitzustellen, die dem Empfänger angezeigt werden sollen.
		 */
		final List<String> attachmentsNames = new ArrayList<>();

		EmpfaengerEmailAnhaenge(final ReportingPerson reportingPerson) {
			this.reportingPerson = reportingPerson;
		}

		/**
		 * Normalisiert die E-Mail-Adresse eines ReportingPerson-Objekts. Dabei wird versucht, zuerst die schulische E-Mail-Adresse zu verwenden. Ist diese
		 * nicht vorhanden oder leer, wird stattdessen die private E-Mail-Adresse verwendet. Die gefundene E-Mail-Adresse wird getrimmt, in Kleinbuchstaben
		 * umgewandelt und zurückgegeben. Falls keine gültige E-Mail-Adresse verfügbar ist, wird ein leerer String zurückgegeben.
		 *
		 * @param reportingPerson Das ReportingPerson-Objekt, dessen E-Mail-Adresse normalisiert werden soll.
		 *          Wenn null übergeben wird, wird ein leerer String zurückgegeben.
		 *
		 * @return Die normalisierte E-Mail-Adresse als String. Wenn weder eine schulische noch eine private
		 *         E-Mail-Adresse verfügbar ist, wird ein leerer String zurückgegeben.
		 */
		private static String normalisierteEmail(final ReportingPerson reportingPerson) {
			if (reportingPerson == null)
				return "";
			final String schulMail = reportingPerson.emailSchule();
			if ((schulMail != null) && !schulMail.isBlank())
				return schulMail.trim().toLowerCase();
			final String privatMail = reportingPerson.emailPrivat();
			if ((privatMail != null) && !privatMail.isBlank())
				return privatMail.trim().toLowerCase();
			return "";
		}

		@Override
		public int hashCode() {
			return Objects.hash(normalisierteEmail(this.reportingPerson));
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if ((obj == null) || (getClass() != obj.getClass()))
				return false;
			final EmpfaengerEmailAnhaenge other = (EmpfaengerEmailAnhaenge) obj;
			return Objects.equals(normalisierteEmail(this.reportingPerson), normalisierteEmail(other.reportingPerson));
		}
	}
}
