package de.svws_nrw.asd.validate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import jakarta.validation.constraints.NotNull;

/**
 * Diese Klasse ist die Basisklasse für Validatoren.
 */
public abstract class Validator extends BasicValidator {

	/** Der vom Validator genutzte Kontext */
	private final @NotNull ValidatorKontext _kontext;

	/** Eine Liste von Validatoren, die bei diesem Validator mitgeprüft werden. */
	protected final @NotNull List<Validator> _validatoren = new ArrayList<>();



	/**
	 * Erstellt einen neuen Validator in dem übegebenen Kontext
	 *
	 * @param kontext   der Kontext, in dem der Validator ausgeführt wird
	 */
	protected Validator(final @NotNull ValidatorKontext kontext) {
		super(ValidatorFehlerart.UNGENUTZT);
		this._kontext = kontext;
		this._defaultValidatorFehlerart = this.getValidatorFehlerart(-1);
	}


	/**
	 * Gibt den Kontext des Validators zurück.
	 *
	 * @return der Kontext des Validators
	 */
	public @NotNull ValidatorKontext kontext() {
		return _kontext;
	}

	/**
	 * Gibt den zugehörigen ValidatorManager zurück.
	 *
	 * @return der ValidatorManager
	 */
	public @NotNull ValidatorManager getValidatorManager() {
		return _kontext.getValidatorManager();
	}


	/**
	 * Führt die Prüfungen des Validators aus. Dabei wird zunächst die Fehlerliste
	 * geleert und durch die ausführenden Prüfroutinen befüllt.
	 *
	 * @return true, falls alle Prüfroutinen erfolgreich waren, und ansonsten false
	 */
	@Override
	public final boolean run() {
		boolean success = true;
		_fehler.clear();
		if (_kontext.getValidatorManager().isValidatorActiveInSchuljahr(_kontext.getSchuljahr(), this.getClass().getCanonicalName())) {
			for (final @NotNull Validator validator : _validatoren) {
				if (!validator.run())
					success = false;
				_fehler.addAll(validator._fehler);
				updateFehlerart(validator.getFehlerart());
			}
			// Berücksichtige auch Exceptions bei der Prüfung dieses Validators
			try {
				if (!this.pruefe())
					success = false;
			} catch (final Exception e) {
				addFehler(-1, "Unerwarteter Fehler bei der Validierung: " + e.getMessage());
			}
		}
		return success;
	}


	/**
	 * Diese Methode führt einen Prüfschritt aus, genau dann, wenn der Validator selbst und dieser explixite Schritt aktiv sind.
	 * Das Lambda stellt die Fehlerbedingung da, die TRUE liefert, wenn ein Fehler vorliegt.
	 *
	 * @param schrittNummer     die Nummer des Prüfschrittes. Startet in der Regel mit 0
	 * @param fehlerbedingung   die Prüfschrittbedingung als Lambda
	 * @param error             die Fehlermeldung, falls der Prüfschritt fehlschlägt
	 *
	 * @return true, wenn der Prüfschritt erfolgreich ausgeführt wurde oder nicht aktiv ist und false, wenn ein Fehler beim Prüfschritt auftritt
	 */
	protected final boolean exec(final int schrittNummer, final @NotNull BooleanSupplier fehlerbedingung, final @NotNull String error) {
		if (schrittNummer < 0)
			throw new ValidatorException(
					"Ein negativer Wert als Nummer für einen Validator-Prüfschritt ist nicht zulässig. Die -1 wird in Fehlercodes nur für interne Fehler verwendet.");
		final boolean isActive =
				_kontext.getValidatorManager().isPruefschrittActiveInSchuljahr(_kontext.getSchuljahr(), this.getClass().getCanonicalName(), schrittNummer);
		if (!isActive)
			return true;
		final boolean result = fehlerbedingung.getAsBoolean();
		if (result) {
			this.addFehler(schrittNummer, error);
			return false;
		}
		return true;
	}


	/**
	 * Gibt die Fehler des Validators als unmodifiable List zurück.
	 *
	 * @return die Liste der Fehler als unmodifiable List
	 */
	@Override
	public @NotNull List<ValidatorFehler> getFehler() {
		return new ArrayList<>(_fehler);
	}


	/**
	 * Die Fehlerart, welche diesem speziellen Validator zugeordnet ist.
	 *
	 * @param pruefschritt   der Prüfschritt, bei welchem der Fehler aufgetreten ist
	 *
	 * @return die Fehlerart
	 */
	@Override
	public @NotNull ValidatorFehlerart getValidatorFehlerart(final int pruefschritt) {
		return _kontext.getValidatorManager().getFehlerartBySchuljahrAndValidatorClassAndPruefschritt(_kontext.getSchuljahr(), this.getClass(), pruefschritt);
	}


	/**
	 * Gibt das Fehlercode-Präfix zurück, welcher diesem speziellen Validator zugeordnet ist.
	 *
	 * @return das Fehlercode-Präfix
	 */
	@Override
	public @NotNull String getFehlercodePraefix() {
		return _kontext.getValidatorManager().getFehlercodePraefixBySchuljahrAndValidatorClass(_kontext.getSchuljahr(), this.getClass());
	}


}
