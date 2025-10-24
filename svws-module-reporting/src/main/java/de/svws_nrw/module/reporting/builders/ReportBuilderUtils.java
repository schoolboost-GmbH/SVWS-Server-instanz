package de.svws_nrw.module.reporting.builders;

import java.io.File;
import java.util.List;

import de.svws_nrw.module.reporting.html.contexts.HtmlContext;
import de.svws_nrw.module.reporting.html.dialects.ConvertExpressionDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

/**
 * Hilfsklasse für wiederverwendbare Logik rund um Report-Builder:
 * - Generierung eines Dateinamens aus einer Thymeleaf-Vorlage mit Fallback.
 * - Zusammenführen mehrerer HtmlContext-Objekte zu einem Gesamt-Context.
 */
public final class ReportBuilderUtils {

	private ReportBuilderUtils() {
		// Utility
	}

	/**
	 * Erstellt eine minimal konfigurierte TemplateEngine für String-Templates im HTML-Modus.
	 *
	 * @return TemplateEngine
	 */
	public static TemplateEngine createDefaultHtmlTemplateEngine() {
		final StringTemplateResolver resolver = new StringTemplateResolver();
		resolver.setTemplateMode(TemplateMode.HTML);
		final TemplateEngine engine = new TemplateEngine();
		engine.setTemplateResolver(resolver);
		engine.addDialect(new ConvertExpressionDialect());
		return engine;
	}

	/**
	 * Führt mehrere HtmlContext-Objekte zu einem einzigen Thymeleaf-Context zusammen.
	 *
	 * @param contexts Liste der HtmlContext-Objekte (kann null oder leer sein)
	 *
	 * @return Ein kombinierter Thymeleaf-Context oder ein neuer, leerer Context, falls keine Contexts vorhanden sind.
	 */
	public static Context mergeHtmlContexts(final List<HtmlContext<?>> contexts) {
		final Context finalContext = new Context();
		if ((contexts == null) || contexts.isEmpty())
			return finalContext;

		for (final HtmlContext<?> htmlCtx : contexts) {
			if (htmlCtx == null)
				continue;
			final Context ctx = htmlCtx.getContext();
			if (ctx == null)
				continue;
			for (final String variable : ctx.getVariableNames()) {
				finalContext.setVariable(variable, ctx.getVariable(variable));
			}
		}
		return finalContext;
	}

	/**
	 * Generiert einen Dateinamen basierend auf einer Vorlage unter Verwendung einer Template-Engine und zusätzlicher Kontextinformationen.
	 * Wenn die Vorlage leer ist, wird ein leerer String zurückgegeben. Tritt ein Fehler während der Dateinamensgenerierung auf,
	 * wird ebenfalls ein leerer String zurückgegeben.
	 *
	 * @param templateEngine   Die zu verwendende Template-Engine. Wird eine null-Referenz übergeben, wird eine Standard-Template-Engine verwendet.
	 * @param dateinamensvorlage Die Vorlage für den zu generierenden Dateinamen. Darf nicht null oder leer sein.
	 * @param contexts          Eine Liste von HtmlContext-Objekten, die zusätzliche Variablen für die Vorlage enthalten. Darf null oder leer sein.
	 *
	 * @return Der generierte Dateiname. Ist die Vorlage leer oder tritt ein Fehler auf, wird ein leerer String zurückgegeben.
	 */
	public static String generiereDateinameAusVorlage(final TemplateEngine templateEngine, final String dateinamensvorlage,
			final List<HtmlContext<?>> contexts) {

		if ((dateinamensvorlage == null) || dateinamensvorlage.isBlank())
			return "";

		final Context context = mergeHtmlContexts(contexts);
		final TemplateEngine engine = (templateEngine != null) ? templateEngine : createDefaultHtmlTemplateEngine();
		final String html = engine.process(dateinamensvorlage, context);

		// Extrahiere Name aus <p>...</p>
		final String dateiname = extrahiereDateinameAusAbsatz(html);
		if (dateiname.isBlank())
			return "";

		if (istValiderDateiname(dateiname))
			return dateiname;

		return "";
	}

	/**
	 * Extrahiert den Dateinamen aus einem HTML-Absatz-Tag ("<p>...</p>").
	 * Wenn das übergebene HTML null, leer oder kein vollständiges Absatz-Tag enthält,
	 * wird ein leerer String zurückgegeben.
	 *
	 * @param html der HTML-String, aus dem der Dateiname extrahiert werden soll. Muss ein Absatz-Tag enthalten, um erfolgreich einen Dateinamen zu extrahieren.
	 *
	 * @return der aus dem Absatz-Tag extrahierte Inhalt als String. Gibt einen leeren String zurück, wenn kein entsprechender Inhalt gefunden wird.
	 */
	private static String extrahiereDateinameAusAbsatz(final String html) {
		if ((html == null) || html.isBlank())
			return "";
		final int startTag = html.indexOf("<p>");
		final int endTag = html.indexOf("</p>");
		if ((startTag < 0) || (endTag < 0) || ((startTag + 3) >= endTag))
			return "";
		return html.substring(startTag + 3, endTag).trim();
	}

	/**
	 * Überprüft, ob ein übergebener String einen gültigen Dateinamen darstellt.
	 *
	 * @param dateiname Der zu überprüfende Dateiname als String. Darf nicht null oder leer sein.
	 *
	 * @return true, wenn der Dateiname gültig ist; false, wenn er null, leer oder ungültig ist.
	 */
	private static boolean istValiderDateiname(final String dateiname) {
		if ((dateiname == null) || dateiname.isBlank())
			return false;
		try {
			//noinspection ResultOfMethodCallIgnored
			new File(dateiname).getCanonicalFile();
			return true;
		} catch (@SuppressWarnings("unused") final Exception e) {
			return false;
		}
	}
}
