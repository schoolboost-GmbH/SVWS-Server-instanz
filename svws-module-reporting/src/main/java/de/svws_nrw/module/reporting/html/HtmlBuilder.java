package de.svws_nrw.module.reporting.html;

import de.svws_nrw.module.reporting.html.contexts.HtmlContext;
import de.svws_nrw.module.reporting.html.dialects.ConvertExpressionDialect;
import jakarta.ws.rs.core.Response;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Erzeugt aus einem Thymeleaf-HTML-Template (HTML-Vorlage) und den zugehörigen Daten in den Contexts den finalen HTML-Code inklusive der Daten.
 * Die Rückgabe des HTML-Codes kann in Form eines Strings, eines ByteArrays oder einer Response erfolgen.
 */
public class HtmlBuilder {

	/** Der HTML-Inhalt des Templates. */
	private final String htmlVorlage;

	/** Der HTML-Code der finalen HTML-Datei. */
	private String htmlFinal;

	/** Liste mit Daten-Contexts, die zu einem finalen Context zusammengefügt werden, um damit das HTML-Template zu füllen. */
	private final List<HtmlContext<?>> contexts;

	/** Die IDs der Hauptdaten (z. B. Schüler, Klassen), für die dieser Builder erzeugt wurde. Kann für spätere Gruppierungen genutzt werden, z. B. für E-Mail-Versand. */
	private final Set<Long> builderIds = new LinkedHashSet<>();

	/** Dateiname der HTML-Datei. */
	private final String dateiname;



	/**
	 * Erstellt einen neuen HTML-Builder und initialisiert die Variablen
	 *
	 * @param htmlVorlage   Der Inhalt einer HTML-Vorlagendatei, die mit Daten gefüllt werden soll.
	 * @param contexts 	    Liste mit Daten-Contexts, die zu einem finalen Context zusammengefügt werden, um damit das HTML-Template zu füllen.
	 * @param dateiname 	Dateiname der HTML-Datei ohne Dateiendung.
	 */
	public HtmlBuilder(final String htmlVorlage, final List<HtmlContext<?>> contexts, final String dateiname) {
		this.htmlVorlage = htmlVorlage;
		this.contexts = contexts;
		this.dateiname = dateiname;
		this.htmlFinal = "";
	}

	/**
	 * Erstellt einen neuen HTML-Builder und initialisiert die Variablen, inklusive der IDs, für die der Builder erzeugt wird.
	 *
	 * @param htmlVorlage   Der Inhalt einer HTML-Vorlagendatei, die mit Daten gefüllt werden soll.
	 * @param contexts 	    Liste mit Daten-Contexts, die zu einem finalen Context zusammengefügt werden, um damit das HTML-Template zu füllen.
	 * @param ids			Liste der IDs, für die der Builder erzeugt wird.
	 * @param dateiname 	Dateiname der HTML-Datei ohne Dateiendung.
	 */
	public HtmlBuilder(final String htmlVorlage, final List<HtmlContext<?>> contexts, final List<Long> ids, final String dateiname) {
		this.htmlVorlage = htmlVorlage;
		this.contexts = contexts;
		if (ids != null)
			this.builderIds.addAll(ids.stream().filter(Objects::nonNull).toList());
		this.dateiname = dateiname;
		this.htmlFinal = "";
	}

	/**
	 * Liefert die IDs, für die dieser Builder erzeugt wurde.
	 *
	 * @return Ein unveränderliches Set der IDs des Builders.
	 */
	public Set<Long> getIds() {
		return Collections.unmodifiableSet(builderIds);
	}

	/**
	 * Die übergebenen IDs (die für die Erstellung genutzt wurden) werden dem Builder hinzugefügt und dieser wird anschließend zurückgegeben.
	 *
	 * @param ids Die IDs, die dem Builder als IDs der Erstellung hinzugefügt werden sollen.
	 *
	 * @return Dieser HTML-Builder mit den ergänzten IDs.
	 */
	public HtmlBuilder getBuilderMitIds(final List<Long> ids) {
		if (ids != null)
			this.builderIds.addAll(ids.stream().filter(Objects::nonNull).toList());
		return this;
	}

	/**
	 * Gibt den Dateinamen der HTML-Datei zurück.
	 *
	 * @return Dateiname der HTML-Datei.
	 */
	public String getDateiname() {
		return dateiname;
	}

	/**
	 * Gibt den Dateinamen der HTML-Datei mit Dateiendung zurück.
	 *
	 * @return Dateiname der HTML-Datei mit Endung.
	 */
	public String getDateinameMitEndung() {
		return dateiname + ".html";
	}

	/**
	 * Gibt den finalen HTML-Inhalt als String zurück.
	 *
	 * @return String des finalen HTML-Inhaltes.
	 */
	public String getHtml() {
		return erzeugeHtml();
	}

	/**
	 * Gibt den finalen HTML-Inhalt in Form eines Byte-Arrays.
	 *
	 * @return 	das Byte-Array des finalen HTML-Inhaltes im UTF-8-Format.
	 */
	public byte[] getHtmlByteArray() {
		return erzeugeHtml().getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * Erzeugt eine Response mit einer HTML-Datei als Content
	 *
	 * @return Response mit der HTML-Datei als Content
	 */
	public Response getHtmlResponse() {
		final String encodedFilename = "filename*=UTF-8''" + URLEncoder.encode(dateiname, StandardCharsets.UTF_8);
		final String html = erzeugeHtml();
		return Response.ok(html, "text/html")
				.header("Content-Disposition", "attachment; " + encodedFilename)
				.build();
	}

	/**
	 * Erstellt das finale HTML-Dokument mit den Daten.
	 * Hierzu werden die Variablen in der HTML-Vorlage durch Daten ersetzt.
	 *
	 * @return 	Das finale Html mit den Daten
	 */
	private String erzeugeHtml() {

		// Wurde das finale HTML bereits erzeugt, gebe dieses zurück.
		if (!htmlFinal.isEmpty())
			return htmlFinal;

		final StringTemplateResolver resolver = new StringTemplateResolver();
		resolver.setTemplateMode(TemplateMode.HTML);

		final TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(resolver);

		// Dialects des SVWS-Servers für weitere Funktionen hinzufügen.
		templateEngine.addDialect(new ConvertExpressionDialect());

		// Füge die übergebenen Contexts zu einem Context zusammen.
		final Context finalContext = new Context();
		if ((contexts != null) && !contexts.isEmpty()) {
			for (final HtmlContext<?> htmlCtx : contexts) {
				for (final String variable : htmlCtx.getContext().getVariableNames()) {
					finalContext.setVariable(variable, htmlCtx.getContext().getVariable(variable));
				}
			}
		}

		htmlFinal = (!(finalContext.getVariableNames().isEmpty() || (htmlVorlage == null))) ? templateEngine.process(htmlVorlage, finalContext) : "";
		return htmlFinal;
	}
}
