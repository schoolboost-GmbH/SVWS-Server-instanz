import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch, type ComputedRef, type Ref } from 'vue';
import { useWindowSize } from "@vueuse/core";
import type { UiSelectProps } from "./UiSelectProps";
import type { Validator } from "../../../../../../core/src/asd/validate/Validator";
import { ValidatorFehlerart } from "../../../../../../core/src/asd/validate/ValidatorFehlerart";
import { ArrayList } from "../../../../../../core/src/java/util/ArrayList";
import type { List } from "../../../../../../core/src/java/util/List";

export function useUiSelectUtils<T, V extends Validator>(
	multi: boolean,
	props: UiSelectProps<T, V>,
	attrs: Record<string, any>,
	isValid: ComputedRef<boolean>,
	isValidatorValid: ComputedRef<boolean>,
	uiSelect: Ref<HTMLElement | null>,
	uiSelectCombobox: Ref<HTMLElement | null>,
	uiSelectSearch: Ref<HTMLElement | null>,
	uiSelectDropdown: Ref<HTMLDivElement | null>,
	search: Ref<string>,
	selectOption: (option: T) => void,
	deselectOption: (option: T) => void,
	hasSelection: () => boolean,
	isSelected: (option: T) => boolean,
	deselectAllowed: () => boolean
) {

	// ID zur Eindeutigen Kennzeichnung der Komponente. Wird für die HTML IDs benötigt
	const instanceId = crypto.randomUUID();

	const dropdownIsOpen = ref(false);

	// Definiert, ob das Inputfeld gerade den Fokus hat (im Falle von searchable === true)
	const domFocusOnInput = ref(false);

	// Größen und Positionen zur Berechnung des Dropdowns
	const top = ref(0);
	const left = ref(0);
	const width = ref(0);
	const height = ref(0);
	const { height: windowHeight } = useWindowSize();

	// Index des visuell hervorghobenen Dropdownlistenelements bei Tastennavigation
	const highlightedIndex = ref(-1);

	// duch die Suche gefilterte Optionenliste
	const searchFilteredOptions = computed(() => {
		return props.searchable! ? getMatchingOptions(search.value) : props.manager!.filteredOptions;
	});



	onMounted(() => {
		const result = { ...attrs };
		const stringClass = result.class;
		if (typeof stringClass === 'string')
			if (props.searchable! && stringClass.includes('contentFocusField'))
				uiSelectSearch.value?.classList.add('contentFocusField');
			else if (!props.searchable! && stringClass.includes('contentFocusField'))
				uiSelectCombobox.value?.classList.add('contentFocusField');

	});

	/**
	 * Farben und Attribute
	 */

	/**
	 * Extrahiert alle gesetzten Klassen von außerhalb und setzt diese an den Rootknoten. Icon-Farben werden jedoch herausgefiltert, da diese den Hintergrund
	 * beeinflussen. Ebenso Fokusklassen, da diese an die Combobox bzw. das SearchInput weitergeleitet werden müssen.
	 */
	const filteredAttributes = computed (() => {
		const result = { ...attrs };

		const stringClass = result.class;

		if (typeof stringClass === 'string')
			result.class = stringClass.split(' ').filter(c => !c.startsWith('icon-ui') && (c !== 'contentFocusField')
				&& (c !== 'subNavigationFocusField')).join(' ');

		return result;
	});

	/**
	 * Berechnet die Hintergrundfarbe der Combobox. Wird von außen eine gesetzt, dann wird diese verwendet. Andernfalls bg-ui.
	 */
	const backgroundColorClass = computed(() => {
		const classString = attrs.class;
		if (typeof classString !== "string")
			return props.headless! ? "" : "bg-ui";
		const classes = classString.split(' ');

		// Nach erster bg-ui* Klasse suchen
		const match = classes.find(c => c.startsWith('bg-ui'));
		return match ?? (props.headless! ? "" : "bg-ui");
	});

	/**
	 * Berechnet die Textfarbe. Wird von außen eine gesetzt, dann wird diese verwendet. Andernfalls text-ui. Für Sekundärfarben wird dann automatisch
	 * die sekundäre Variante der übergebenen Farbe verwendet.
	 */
	const textColorClass = computed(() => {
		if (props.disabled!)
			return "text-ui-disabled";
		if (!isValid.value)
			return "text-ui-danger";
		if (!isValidatorValid.value)
			switch (props.validator!().getFehlerart()) {
				case (ValidatorFehlerart.HINWEIS):
					return "text-ui-warning";
				case (ValidatorFehlerart.KANN):
					return "text-ui-caution";
				case (ValidatorFehlerart.MUSS):
					return "text-ui-danger";
			}

		if (props.required! && !hasSelection())
			return "text-ui-danger";
		const classString = attrs.class;
		if (typeof classString !== "string")
			return "text-ui";
		const classes = classString.split(' ');

		// Nach erster text-ui* Klasse suchen
		const match = classes.find(c => c.startsWith('text-ui'));
		return match ?? 'text-ui';
	});

	/**
	 * Berechnet die Iconfarbe. Wird von außen eine gesetzt, dann wird diese verwendet. Andernfalls icon-ui.
	 */
	const iconColorClass = computed(() => {
		if (props.disabled!)
			return "icon-ui-disabled";
		if (!isValid.value)
			return "icon-ui-danger";
		if (!isValidatorValid.value)
			switch (props.validator!().getFehlerart()) {
				case (ValidatorFehlerart.HINWEIS):
					return "icon-ui-warning";
				case (ValidatorFehlerart.KANN):
					return "icon-ui-caution";
				case (ValidatorFehlerart.MUSS):
					return "icon-ui-danger";
			}
		const classString = attrs.class;
		if (typeof classString !== "string")
			return "icon-ui";
		const classes = classString.split(' ');

		// Nach erster icon-ui* Klasse suchen
		const match = classes.find(c => c.startsWith('icon-ui'));
		return match ?? 'icon-ui';
	});

	/**
	 * Berechnet die Borderfarbe der Combobox. Wird von außen eine gesetzt, dann wird diese verwendet. Andernfalls bg-ui-neutral.
	 */
	const borderColorClass = computed(() => {
		if (!isValid.value)
			return "border-ui-danger";
		if (!isValidatorValid.value)
			switch (props.validator!().getFehlerart()) {
				case (ValidatorFehlerart.HINWEIS):
					return "border-ui-warning";
				case (ValidatorFehlerart.KANN):
					return "border-ui-caution";
				case (ValidatorFehlerart.MUSS):
					return "border-ui-danger";
			}

		const classString = attrs.class;
		if (typeof classString !== "string")
			return "border-ui";
		const classes = classString.split(' ');

		// Nach erster border-ui* Klasse suchen
		const match = classes.find(c => c.startsWith('border-ui'));
		return match ?? 'border-ui';
	});

	/**
	 * Berechnet die Textfarbe abhängig vom Fokus. Ist das Select searchable und der Fokus ist im Input, dann wird die aktuelle Selektion nur in
	 * Sekundärfarbe angezeigt.
	 */
	const singleSelectionTextColor = computed((): string =>
		((uiSelectSearch.value !== null) && (domFocusOnInput.value)) ? getSecondaryTextColor(textColorClass.value) : textColorClass.value
	);

	/**
	 * Aria Attribute der Combobox
	 */
	const comboboxAriaAttrs = computed(() => {
		const isEditable = !props.readonly! || !props.disabled!;
		if (props.searchable! && !isEditable)
			return {};
		return {
			'aria-labelledby': `uiSelectLabel_${instanceId}`,
			'aria-controls': isEditable? `uiSelectDropdown_${instanceId}` : undefined,
			'aria-autocomplete': isEditable ? 'none' as const : undefined,
			'aria-expanded': isEditable ? dropdownIsOpen.value : undefined,
			'aria-disabled': props.disabled! ? true : undefined,
			'aria-activedescendant': (isEditable && hasHighlightedOption()) ? `uiSelectOption_${highlightedIndex.value}_${instanceId}` : undefined,
		}
	});

	/**
	 * Aria Attribute des SearchInputs
	 */
	const searchAriaAttrs = computed(() => ({
		'aria-labelledby': `uiSelectLabel_${instanceId}`,
		'aria-controls': `uiSelectDropdown_${instanceId}`,
		'aria-autocomplete': 'none' as const,
		'aria-expanded': dropdownIsOpen.value,
		'aria-activedescendant': (hasHighlightedOption()) ? `uiSelectOption_${highlightedIndex.value}_${instanceId}` : undefined,
	}));

	/**
	 * Berechnet den Tabindex der Combobox. Nur wenn kein SearchInput generiert wird (searchable = false) darf die Combobox vie Tastatur erreicht werden können.
	 */
	const comboboxTabindex = computed((): number =>
		(props.searchable! || props.disabled! || props.readonly!) ? -1 : 0
	);

	/**
	 * Berechnet den Tabindex des Searchinputs.
	 */
	const searchInputTabindex = computed((): number =>
		(props.disabled! || props.readonly!) ? -1 : 0
	);

	/**
	 * Berechnet die Role der Combobox. Wird ein SearchInput generiert (searchable = true), dann erhält die Combobox keine Rolle, da diese am SearchInput
	 * gesetzt wird.
	 */
	const comboboxRole = computed((): string | undefined =>
		(!props.searchable! || props.disabled! || props.readonly!) ? 'combobox' : undefined
	);

	/**
	 * Dropdown Position und Funktionalität
	 */

	/**
	 * Berechnet die Position-Styles des Dropdowns
	 */
	const dropdownPositionStyles = computed(() => ({
		top: topPosition.value,
		left: left.value + 'px',
		width: width.value + 'px',
		maxHeight: maxHeight.value + 'px',
	}));

	/**
	 * Berechnet die top Position des Dropdowns abhängig von Position und Größe der Combobox sowie des Flipflags.
	 */
	const topPosition = computed (() => {
		if (flip.value) {
			const dropdownHeight = uiSelectDropdown.value?.scrollHeight !== undefined
				? Math.min(maxHeight.value, uiSelectDropdown.value.scrollHeight)
				: maxHeight.value;
			return `${top.value - dropdownHeight - 2}px`;
		} else
			return `${top.value + height.value + 3}px`;

	});

	/**
	 * Berechnet, ab wann das Dropdown über der Combobox positioniert wird, statt darunter
	 */
	const flip = computed (() => {
		if (!dropdownIsOpen.value)
			return false;
		const below = windowHeight.value - (top.value + height.value);
		const above = top.value;
		if ((uiSelectDropdown.value?.clientHeight !== undefined) && (below > 100))
			return false;
		return (below < above);
	});

	/**
	 * Berechnet die maximale Höhe des Dropdowns, sodass dieses nie aus dem Viewport verschwindet. Sollte diese 235 übersteigen, dann wird die maxHeight auf
	 * 235px gesetzt
	 */
	const maxHeight = computed(() => {
		let maxHeight = 0;
		if (flip.value)
			maxHeight = top.value - 5;
		else
			maxHeight = windowHeight.value - (top.value + height.value) - 5;
		return (maxHeight > 235) ? 235 : maxHeight;
	});

	/**
	 * Öffnet oder schließt das Dropdown
	 */
	function toggleDropdown() {
		if (dropdownIsOpen.value)
			closeDropdown();
		else
			openDropdown();
	}

	/**
	 * Aktualisiert die aktuelle Position und Größe des Dropdowns
	 */
	function updatePosition() {
		const rect = uiSelectCombobox.value?.getBoundingClientRect();
		if (!rect)
			return;
		top.value = rect.top;
		left.value = rect.left;
		width.value = rect.width;
		height.value = rect.height;
	}

	/**
	 * Öffnet das Dropdown
	 */
	function openDropdown() {
		if ((uiSelectDropdown.value === null) || dropdownIsOpen.value)
			return;
		updatePosition();
		handleDomFocus();
		dropdownIsOpen.value = true;
		uiSelectDropdown.value.showPopover();
		if (!hasHighlightedOption())
			uiSelectDropdown.value.scrollTop = 0;
		window.addEventListener('resize', () => closeDropdown());
	}

	/**
	 * Schließt das Dropdown. Es kann außerdem angegeben werden, ob dabei die Combobox wieder dokussiert werden soll.
	 *
	 * @param comboBoxFocus   true, wenn die Combobox fokussiert werden soll. Default ist true
	 */
	function closeDropdown(comboBoxFocus: boolean = true) {
		if (!dropdownIsOpen.value)
			return;
		dropdownIsOpen.value = false;
		uiSelectDropdown.value?.hidePopover();
		if (comboBoxFocus)
			handleDomFocus();
		removeOptionHighlighting();
		window.removeEventListener('resize', () => closeDropdown());
	}

	// Passt die Scrollposition des Dropdowns an, falls der visuelle Fokus außerhalb des Sichtfelds landet
	watch(highlightedIndex, async () => {
		await nextTick(() => {
			if (!hasHighlightedOption()) {
				if (uiSelectDropdown.value)
					uiSelectDropdown.value.scrollTop = 0;
				return;
			}

			const highlightedElement = document.getElementById(`uiSelectOption_${highlightedIndex.value}_${instanceId}`);

			if (uiSelectDropdown.value && highlightedElement) {
				const listRect = uiSelectDropdown.value.getBoundingClientRect();
				const itemRect = highlightedElement.getBoundingClientRect();
				const ringSize = 6; // Zusätzlicher Abstand für den Auswahlring

				// Falls das Element unten rausgeht → Liste nach unten scrollen
				if (itemRect.bottom + ringSize > listRect.bottom) {
					uiSelectDropdown.value.scrollTop += (itemRect.bottom + ringSize) - listRect.bottom;
				}
				// Falls das Element oben rausgeht → Liste nach oben scrollen
				else if (itemRect.top - ringSize < listRect.top) {
					uiSelectDropdown.value.scrollTop -= listRect.top - (itemRect.top - ringSize);
				}
			}
		});
	}, {deep: true});

	/**
	 * Dropdown Tastatursteuerung
	 */

	/**
	 * Tastaturbedinung des Komponente. Sie orierntiert sich an den Vorgaben von https://www.w3.org/WAI/ARIA/apg/patterns/combobox/
	 *
	 * @param event das Keyboardevent, das die gedrückte Taste enthält
	 */
	async function onKeyDown(event: KeyboardEvent) {
		if (props.disabled! || props.readonly!)
			return;

		// Nur bei geöffnetem Dropdown, oder wenn Navigation ausgelöst wird. Verhindert, dass die Seite beim Navigieren des Dropdowns gescrollt wird.
		const isNavigationKey = ['ArrowUp', 'ArrowDown', 'PageUp', 'PageDown'].includes(event.key);

		if (isNavigationKey)
			event.preventDefault();
		await handleKeyEvent(event);
	}

	async function handleKeyEvent(event: KeyboardEvent) {

		const handlers: Record<string, () => Promise<void> | void> = {
			"Enter": () => handleEnter(),
			" ": () => handleSpace(),
			"Tab": () => handleTab(event),
			"ArrowDown": async () => await handleArrowDown(),
			"ArrowUp": async () => await handleArrowUp(event),
			"PageUp": async () => await handlePageUp(),
			"PageDown": async () => await handlePageDown(),
			"Home": () => handleHome(),
			"End": () => handleEnd(),
			"Escape": () => handleEscape(),
			"*": () => handleDefault(event),
		};
		const handler = handlers[event.key] ?? handlers["*"];
		await handler();
	}

	function handleEnter() {
		if (!dropdownIsOpen.value) {
			openDropdown();
			return;
		}
		selectHighlightedOption();
		if (!multi || !hasHighlightedOption())
			closeDropdown();
	}

	function handleSpace() {
		if (!dropdownIsOpen.value) {
			openDropdown();
			return;
		}
		if (props.searchable!) {
			return;
		}
		selectHighlightedOption();
		if (!multi || !hasHighlightedOption())
			closeDropdown();
	}

	function handleTab(event: KeyboardEvent) {
		if (!event.shiftKey) {
			selectHighlightedOption(false);
		}
		closeDropdown();
	}

	async function handleArrowDown() {
		if (!dropdownIsOpen.value) {
			openDropdown();
			return;
		}
		await navigateList(1);
	}

	async function handleArrowUp(event: KeyboardEvent) {
		if (!dropdownIsOpen.value) {
			openDropdown();
			return;
		}
		if (event.altKey) {
			selectHighlightedOption();
			closeDropdown();
			return;
		}
		await navigateList(-1);
	}

	function handleHome() {
		openDropdown();
		if (!props.searchable!) {
			highlightFirstOption();
		}
	}

	function handleEnd() {
		openDropdown();
		if (!props.searchable!) {
			highlightLastOption();
		}
	}

	function handleEscape() {
		closeDropdown()
		resetSearch();
	}

	async function handlePageUp() {
		if (!props.searchable!) {
			await navigateList(-10);
		}
	}

	async function handlePageDown() {
		if (!props.searchable!) {
			await navigateList(10);
		}
	}

	/**
	 * KeyboardEvent Handler für alle restlichen Keys.
	 *
	 * @param event   das Event mit der grdrückten Taste
	 */
	function handleDefault(event: KeyboardEvent) {
		// Wenn searchable = true -> Suchfeld befüllen -> keine gesonderte Behandlung notwendig
		// Wenn serchable = false -> visueller Fokus der Liste verändern
		if (props.searchable! || !isPrintableChar(event.key)) {
			return;
		}
		openDropdown();
		handlePrintableKeyInput(event);
	}

	/**
	 * Ermittelt die Option im Dropdown, die fokussiert werden soll basierend auf dem eingegebenen druckbaren Zeichen. Falls das Zeichen innerhalb von 0,5s
	 * wiederholt eingegeben wird, dann wird bei jeder Eingabe davon der nächste passende Eintrag fokussiert. Passend bedeutet dabei, dass der Eintrag mit dem
	 * eingebenen Zeichen beginnt. Wird innerhalb kurzer Zeit (0,5s) mehr als ein Zeichen eingebeben und zudem auch unterschiedliche Zeichen, dann wird nach
	 * einem Eintrag gesucht, der mit dem gesamten eingegebenen Begriff beginnt.
	 *
	 * @param event   das Keyboardevent, welches das eingebene Zeichen enthält.
	 */
	let timer: any = null;
	let lastInput: any = null;
	function handlePrintableKeyInput(event: KeyboardEvent) {
		if ((lastInput === event.key) && hasHighlightedOption()) {
			if ((highlightedIndex.value + 1) >= searchFilteredOptions.value.size()) {
				focusOptionThatStartsWith(lastInput);
				return;
			}

			const nextOption = props.manager!.filteredOptions.get(highlightedIndex.value + 1);
			if (props.manager!.getOptionText(nextOption).toLowerCase().startsWith(event.key))
				highlightedIndex.value += 1;
			else
				focusOptionThatStartsWith(lastInput);
			return;
		}

		if (!props.searchable!) {
			lastInput = event.key;
			// Wenn der Timer bereits läuft, füge den neuen Buchstaben an `search.value` an
			if (timer !== null)
				search.value += event.key;
			else {
				// Setzt den Timer, wenn noch keiner läuft und setzt `search.value` auf den aktuellen Buchstaben
				search.value = event.key;
				timer = setTimeout(() => {
					// Reset des Timers nach 0,5 Sekunden
					timer = null;
					resetSearch();
				}, 500);
			}
		}

		focusOptionThatStartsWith(search.value);
	}

	/**
	 * Prüft, ob das Zeichen ein druckbares Zeichen ist
	 *
	 * @param char   das zu prüfende Zeichen
	 */
	function isPrintableChar(char: string) {
		return /^[\x20-\x7E]$/.test(char);
	}

	/**
	 * Ermittelt die Option, die mit dem übergebenen String beginnt und fokussiert diese visuell. Falls keine gefunden wurde, wird nichts fokussiert bzw. der
	 * vorhandene Fokus wird entfernt.
	 *
	 * @param start   String, mit dem die Option beginnen muss
	 */
	function focusOptionThatStartsWith(start: string) {
		const char = start.toLowerCase();
		const startIndex = (hasHighlightedOption()) ? 0 : highlightedIndex.value + 1;
		const optionsSize = searchFilteredOptions.value.size();

		// Suche nach einer Option, die mit 'char' beginnt
		for (let i = startIndex; i < optionsSize + startIndex; i++) {
			const index = i % optionsSize; // Um auf den Index am Anfang der Liste zurückzukommen
			const option = props.manager!.filteredOptions.get(index);
			if (props.manager!.getOptionText(option).toLowerCase().startsWith(char)) {
				highlightedIndex.value = index;
				return;
			}
		}
		// Wurde keine passende Option gefunden, dann wird der visuelle Fokus entfernt
		removeOptionHighlighting();
	}

	/**
	 * Fokusberechnungen
	 */

	/**
	 * Generiert die focusClass für die Combobox, falls eine gesetzt ist. Diese wird nur an die Combobox gesetzt, wenn das Select nicht searchable ist.
	 */
	const comboboxFocusClass = computed(() => {
		const result = { ...attrs };
		const stringClass = result.class;
		if (typeof stringClass === 'string')
			if (!props.searchable! && stringClass.includes('contentFocusField'))
				return 'contentFocusField';
			else if (!props.searchable! && stringClass.includes('subNavigationFocusField'))
				return 'subNavigationFocusField';
		return '';
	});

	/**
	 * Generiert die focusClass für das Searchinput, falls eine gesetzt ist. Diese wird nur an das Searchinput gesetzt, wenn das Select searchable ist.
	 */
	const searchInputFocusClass = computed(() => {
		const result = { ...attrs };
		const stringClass = result.class;
		if (typeof stringClass === 'string')
			if (props.searchable! && stringClass.includes('contentFocusField'))
				return 'contentFocusField';
			else if (props.searchable! && stringClass.includes('subNavigationFocusField'))
				return 'contentFocusField';
		return '';
	});

	/**
	 * Je nachdem, ob das Select durchsuchbar ist (searchable), wird der Dom-Fokus auf die Combobox oder das darin befindliche Suchfeld gesetzt. Auch die Flags
	 * für den visuellen Fokus (visualFocusOnCombobox) und den Dom-Fokus (domFocusOnCombobox) werden auf true gesetzt.
	 */
	function handleDomFocus() {
		if (props.searchable!) {
			uiSelectSearch.value?.focus();
			domFocusOnInput.value = true;
		} else
			uiSelectCombobox.value?.focus();
		document.addEventListener('click', handleClickOutside);
	}

	/**
	 * Entfernt die Flags für den Fokus von dem Input.
	 */
	function handleBlur() {
		domFocusOnInput.value = false;
	}

	/**
	 * Löscht den aktuellen SearchInput, wenn der Fokus außerhalb der Combobox landet und schließt das Dropdown
	 */
	function onFocusOut() {
		// Bei einem Fokuswechsel ist der Fokus kurz auf dem Body. Das muss hiermit abgewartet werden
		requestAnimationFrame(() => {
			if (!((uiSelectCombobox.value?.contains(document.activeElement)) ?? false)) {
				closeDropdown();
				resetSearch();
				document.removeEventListener('click', handleClickOutside);
			}
		});
	}

	/**
	 * Navigiert den visuellen Fokus im Dropdown.
	 *
	 * @param steps   Wenn negativ, dann wird rückwärts navigiert. Die Zahl gibt die Anzahl der Schritte an.
	 */
	async function navigateList(steps: number) {
		let newIndex = highlightedIndex.value + steps;
		if (newIndex >= searchFilteredOptions.value.size())
			newIndex = 0;
		else if (newIndex < 0)
			newIndex = searchFilteredOptions.value.size() - 1;
		highlightedIndex.value = newIndex;
	}

	/**
	 * Schließt das Dropdown bei einem Klick außerhalb der Komponente und resettet den Suchtext und den Fokus.
	 *
	 * @param event   das Klickevent
	 */
	function handleClickOutside(event: MouseEvent) {
		if ((uiSelect.value !== null) && !uiSelect.value.contains(event.target as Node)) {
			closeDropdown(false);
			resetSearch();
			document.removeEventListener('click', handleClickOutside);
		}
	}

	/**
	 * Wird die Komponente geklickt, dann wird automatisch das richtige Element fokussiert (Combobox oder Suchfeld je nach Wert von searchable). Dabei wird
	 * außerdem auch das Dropdown geöffnet oder geschlossen. Das ist insbesondere relavant, wenn der Benutzer in das Suchfeld schreiben möchte, aber dieses
	 * nicht beim Klick getroffen hat, weil es nicht die gesamte Combobox ausfüllt.
	 */
	function handleComponentClick() {
		if (props.readonly!)
			return;
		handleDomFocus();
		toggleDropdown();
	}

	/**
	 * Highlighting der Optionen im Dropdown für die Navigation durch Tastensteuerung darin.
	 */

	function hasHighlightedOption() {
		return highlightedIndex.value !== -1;
	}

	function highlightFirstOption() {
		highlightedIndex.value = 0;
	}

	function highlightLastOption() {
		highlightedIndex.value = searchFilteredOptions.value.size() - 1;
	}

	function removeOptionHighlighting() {
		if (hasHighlightedOption()) {
			highlightedIndex.value = -1;
		}
	}

	/**
	 * CSS- und Tailwindklassen
	 */

	/**
	 * CSS- und Tailwindklassen der Combobox
	 */
	const comboboxClasses = computed((): (string | object)[] => [
		props.headless! ? 'pl-1 min-h-6' : 'border mt-[0.8em] pl-3 pr-1 min-h-9',
		props.disabled! ? 'border-ui-disabled' : borderColorClass.value,
		backgroundColorClass.value,
		props.readonly! ? 'cursor-not-allowed' : props.searchable! ? 'cursor-text' : 'cursor-pointer',
		{
			'pointer-events-none': props.disabled!,
		},
		comboboxFocusClass.value,
	]);

	/**
	 * CSS- und Tailwindklassen für die Abstände beim headless-Design
	 */
	const headlessPadding = computed((): string =>
		props.headless! ? 'py-0' : 'py-1'
	);

	/**
	 * CSS- und Tailwindklassen für das Label
	 */
	const labelClasses = computed((): (string | object)[] => [
		moveLabel.value ? 'absolute -top-0.5 text-xs' : 'absolute top-1/2 font-normal',
		props.removable! ? (moveLabel.value ? 'right-2' : 'right-11') : (moveLabel.value ? 'right-2' : 'right-6'),
		props.headless! ? (props.removable! ? 'left-10' : 'left-6') : '',
		backgroundColorClass.value,
		textColorClass.value,
	]);

	/**
	 * CSS- und Tailwindklassen für den Labeltext
	 */
	const labelTextColorClass = computed((): string =>
		moveLabel.value ? textColorClass.value : getSecondaryTextColor(textColorClass.value)
	);

	const labelIconClass = computed((): string =>
		moveLabel.value ? iconColorClass.value : getSecondaryIconColor(iconColorClass.value)
	);

	/**
	 * CSS- und Tailwindklassen einer Option im Dropdown
	 */
	const optionClasses = computed(() => (option: T, optionIndex: number) => [
		isSelected(option) ? 'bg-ui-selected text-ui-onselected font-medium border border-ui-selected' : 'text-ui',
		{ 'bg-ui-hover inset-ring-2 inset-ring-ui-neutral ': highlightedIndex.value === optionIndex },
	]);

	/**
	 * CSS- und Tailwindklassen für Validatorfehlericons
	 */
	const validatorErrorIcon = computed(() => {
		if (props.validator === undefined)
			return null;
		const fehlerart = props.validator().getFehlerart();
		if (fehlerart === ValidatorFehlerart.MUSS)
			return ['i-ri-alert-fill', props.disabled! ? 'icon-ui-disabled' : 'icon-ui-danger'];
		if (fehlerart === ValidatorFehlerart.KANN)
			return ['i-ri-error-warning-fill', props.disabled! ? 'icon-ui-disabled' : 'icon-ui-caution'];
		if (fehlerart === ValidatorFehlerart.HINWEIS)
			return ['i-ri-question-fill', props.disabled! ? 'icon-ui-disabled' : 'icon-ui-warning'];
		return null;
	});


	/**
	 * Anzeige und Darstellung
	 */

	/**
	 * Definiert, wann das Label der Combobox nach oben rutscht
	 */
	const moveLabel = computed(() => hasSelection() || (search.value !== '' && props.searchable!));

	/**
	 * Berechnet, ob ein Label angezeigt wird
	 */
	const showLabel = computed((): boolean =>
		props.label !== '' && ((props.headless! && !moveLabel.value) || !props.headless!)
	);

	/**
	 * Berechnet, ob ein Validatorfehler angezeigt werden soll
	 */
	const showValidatorError = computed((): boolean =>
		!isValid.value && (!props.required! || hasSelection())
	);

	/**
	 * Berechnet, ob eine Validatorfehlermeldung angezeigt werden soll
	 */
	const showValidatorErrorMessage = computed((): boolean =>
		(props.validator !== undefined) &&
		(!props.validator().getFehler().isEmpty()) &&
		(props.validator().getFehlerart() !== ValidatorFehlerart.UNGENUTZT)
	);

	/**
	 * Berechnet die Hintergrundfdarbe des Validatorfehlers
	 */
	function validatorErrorBgClasses(fehler: ValidatorFehlerart): {} {
		if (props.validator === undefined)
			return {};

		switch (fehler) {
			case ValidatorFehlerart.MUSS:
				return { 'bg-ui-danger': true };
			case ValidatorFehlerart.KANN:
				return { 'bg-ui-caution': true };
			case ValidatorFehlerart.HINWEIS:
				return { 'bg-ui-warning': true };
			default:
				return {};
		}
	}

	/**
	 * Berechnet, ob eine aktuelle Selektion angezeigt werden soll
	 */
	const showSelection = computed((): boolean =>
		hasSelection() && (search.value === '')
	);

	/**
	 * Teilt den Text in Teile auf, die mit der Suchanfrage übereinstimmen. Dies wird dazu verwendet, die übereinstimmenden Teile farblich hervorzuheben.
	 *
	 * @param text   der Suchtext
	 */
	function splitText(text: string) {
		if (search.value === "" || !props.searchable!)
			return [{ text, hit: false }];

		const escapedSearch = search.value.replace(/[-/\\^$*+?.()|[\]{}]/g, '\\$&');
		const regex = new RegExp(`(${escapedSearch})`, 'gi');

		return text.split(regex).map((part) => ({
			text: part,
			hit: part.toLowerCase() === search.value.toLowerCase(),
		}));
	}

	/**
	 * Generiert die passende sekundäre Textfarbe für gesetzte Textfarben von außen.
	 *
	 * @param color    die Textfarbe, dessen Sekundärfarbe ermittlet werden soll.
	 */
	function getSecondaryTextColor (color: string) {
		if (color.startsWith("text-uistatic"))
			return "text-uistatic-25";
		switch (color) {
			case "text-ui":
				return "text-ui-secondary";
			case "text-ui-brand":
				return "text-ui-brand-secondary"
			case "text-ui-statistic":
				return "text-ui-statistic-secondary"
			case "text-ui-selected":
				return "text-ui-selected-secondary"
			case "text-ui-danger":
				return "text-ui-danger-secondary"
			case "text-ui-success":
				return "text-ui-success-secondary"
			case "text-ui-warning":
				return "text-ui-warning-secondary"
			case "text-ui-caution":
				return "text-ui-caution-secondary"
			case "text-ui-neutral":
				return "text-ui-neutral-secondary"
			case "text-ui-disabled":
				return "text-ui-disabled-secondary"
			case "text-ui-onbrand":
				return "text-ui-onbrand-secondary"
			case "text-ui-onstatistic":
				return "text-ui-onstatistic-secondary"
			case "text-ui-onselected":
				return "text-ui-onselected-secondary"
			case "text-ui-ondanger":
				return "text-ui-ondanger-secondary"
			case "text-ui-onsuccess":
				return "text-ui-onsuccess-secondary"
			case "text-ui-onwarning":
				return "text-ui-onwarning-secondary"
			case "text-ui-oncaution":
				return "text-ui-oncaution-secondary"
			case "text-ui-onneutral":
				return "text-ui-onneutral-secondary"
			case "text-ui-ondisabled":
				return "text-ui-ondisabled-secondary"
			default:
				return "text-ui-secondary";
		}
	}

	/**
	 * Generiert die passende sekundäre Iconfarbe für gesetzte Iconfarben von außen.
	 *
	 * @param color    die Iconfarbe, dessen Sekundärfarbe ermittlet werden soll.
	 */
	function getSecondaryIconColor (color: string) {
		if (color.startsWith("icon-uistatic"))
			return "icon-uistatic-25";
		switch (color) {
			case "icon-ui":
				return "icon-ui-secondary";
			case "icon-ui-brand":
				return "icon-ui-brand-secondary"
			case "icon-ui-statistic":
				return "icon-ui-statistic-secondary"
			case "icon-ui-selected":
				return "icon-ui-selected-secondary"
			case "icon-ui-danger":
				return "icon-ui-danger-secondary"
			case "icon-ui-success":
				return "icon-ui-success-secondary"
			case "icon-ui-warning":
				return "icon-ui-warning-secondary"
			case "icon-ui-caution":
				return "icon-ui-caution-secondary"
			case "icon-ui-neutral":
				return "icon-ui-neutral-secondary"
			case "icon-ui-disabled":
				return "icon-ui-disabled-secondary"
			case "icon-ui-onbrand":
				return "icon-ui-onbrand-secondary"
			case "icon-ui-onstatistic":
				return "icon-ui-onstatistic-secondary"
			case "icon-ui-onselected":
				return "icon-ui-onselected-secondary"
			case "icon-ui-ondanger":
				return "icon-ui-ondanger-secondary"
			case "icon-ui-onsuccess":
				return "icon-ui-onsuccess-secondary"
			case "icon-ui-onwarning":
				return "icon-ui-onwarning-secondary"
			case "icon-ui-oncaution":
				return "icon-ui-oncaution-secondary"
			case "icon-ui-onneutral":
				return "icon-ui-onneutral-secondary"
			case "icon-ui-ondisabled":
				return "icon-ui-ondisabled-secondary"
			default:
				return "icon-ui-secondary";
		}
	}

	/**
	 * Suche und Selektion
	 */

	/**
	 * Beginnt eine Suche bei der Eingabe von Zeichen in das Suchfeld. Dabei wird außerdem auch das Dropdown geöffnet, falls dies nicht bereits der Fall ist.
	 */
	function handleInput() {
		removeOptionHighlighting();
		openDropdown();
	}

	/**
	 * Setzt die Eingabe im Suchfeld zurück und aktualisiert die gefilterte Liste.
	 */
	function resetSearch() {
		search.value = "";
	}

	/**
	 * Wendet den Suchbegriff auf die Optionen an und gibt ein Array der passenden Optionen zurück, die dann im Dropdown angezeigt werden.
	 * Dabei werden auch deepSearchArttibute berücksichtigt
	 *
	 * @param searchText   der Suchbegriff
	 * @returns ein Array mit validen Optionen
	 */
	function getMatchingOptions(searchText: string): List<T> {
		const filteredOptions = new ArrayList<T>();

		for (const option of props.manager!.filteredOptions) {
			// Suche im Optionentext
			if (searchText === "" || props.manager!.getOptionText(option).toLocaleLowerCase("de-DE").includes(searchText.toLocaleLowerCase("de-DE"))) {
				filteredOptions.add(option);
				continue;
			}

			// Suche in den DeepSearchAttributen der Option
			let matches = false;
			for (const attr of props.deepSearchAttributes?? []) {
				const value = option[attr as keyof T];
				const stringValue = (value !== undefined && value !== null) ? value.toString() : '';
				if (stringValue.toLocaleLowerCase("de-DE").includes(searchText.toLocaleLowerCase("de-DE"))) {
					matches = true;
					break;
				}
			}

			if (matches)
				filteredOptions.add(option);
		}

		return filteredOptions;
	}

	/**
	 * Toggelt die aktuelle Selektion. Bei searchable = true wird auch das Suchfeld zurückgesetzt
	 *
	 * @param option   die Option, die selektiert bzw. deselektiert werden soll
	 */
	function toggleSelection(option: T, comboboxFocus: boolean = true) {
		if (!multi)
			closeDropdown(comboboxFocus);

		if (isSelected(option)) {
			if (deselectAllowed())
				deselectOption(option);
		} else
			selectOption(option);
		resetSearch();
		// Bei der Selektion landet der Fokus auf dem Body und muss ggf. wieder auf die Combobox gesetzt werden
		if (comboboxFocus) {
			requestAnimationFrame(() => {
				handleDomFocus();
			});
		}
	}

	/**
	 * Selektiert die hervorghobene Option
	 */
	function selectHighlightedOption(comboboxFocus: boolean = true) {
		if (!hasHighlightedOption()) {
			return;
		}
		const option = searchFilteredOptions.value.get(highlightedIndex.value);
		toggleSelection(option, comboboxFocus);
	}

	return {
		instanceId, search, filteredAttributes, textColorClass, iconColorClass, focusBasedTextColorClass: singleSelectionTextColor, comboboxAriaAttrs,
		searchAriaAttrs, comboboxTabindex, searchInputTabindex, comboboxRole, dropdownPositionStyles, onKeyDown, searchInputFocusClass, handleDomFocus,
		handleBlur, handleComponentClick, comboboxClasses, headlessPadding, labelClasses, labelTextColorClass, labelIconClass, optionClasses, validatorErrorIcon, showLabel,
		showValidatorError, showValidatorErrorMessage, validatorErrorBgClasses, showSelection, splitText, getSecondaryTextColor, handleInput, onFocusOut,
		toggleSelection, searchFilteredOptions, resetSearch,
	}

}