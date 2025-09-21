<template>
	<div class="page page-grid-cards">
		<svws-ui-input-wrapper class="flex flex-col gap-4">
			<ui-card v-if="hatKompetenzDruckenStundenplan && (mapStundenplaene.size > 0)" icon="i-ri-printer-line" title="Stundenplan drucken oder versenden" subtitle="Drucke oder versende die Stundenpläne der ausgewählten Lehrkräfte."
				:is-open="currentAction === 'druckLehrerStundenplan'" @update:is-open="isOpen => setCurrentAction('druckLehrerStundenplan', isOpen)">
				<svws-ui-input-wrapper :grid="2" class="p-2">
					<div class="text-left col-span-2">
						<ui-select :disabled="!lehrerListeManager().liste.auswahlExists()" label="Stundenplan" v-model="stundenplanAuswahl"
							:manager="stundenplanSelectManager" />
					</div>
					<div class="text-left col-span-2 mb-2">
						<svws-ui-checkbox v-model="option2">Pausenzeiten anzeigen</svws-ui-checkbox><br>
						<svws-ui-checkbox v-model="option4">Fach- statt Kursbezeichnung verwenden (nicht Sek-II)</svws-ui-checkbox><br>
						<svws-ui-checkbox v-model="option8">Fachkürzel statt Fachbezeichnung verwenden</svws-ui-checkbox><br>
					</div>
					<div class="text-left mb-2">
						<br><p class="font-bold underline mb-2">Optionen zur Druckausgabe:</p>
						<svws-ui-radio-group>
							<svws-ui-radio-option :value="1" v-model="druckoptionLehrerStundenplan" name="druckoptionLehrerStundenplanGesamtausdruckEinseitig" label="Gesamtausdruck einseitig" />
							<svws-ui-radio-option :value="2" v-model="druckoptionLehrerStundenplan" name="druckoptionLehrerStundenplanEinzelausdruckEinseitig" label="Einzelausdruck einseitig" />
						</svws-ui-radio-group>
					</div>
					<div class="text-left mb-2">
						<br><p class="font-bold underline mb-2">Sortierung:</p>
						<svws-ui-radio-group>
							<svws-ui-radio-option :value="1" v-model="sortieroptionLehrerStundenplan" name="sortieroptionLehrerStundenplanName" label="Standard (Nachname, Vorname(n))" />
							<svws-ui-radio-option :value="2" v-model="sortieroptionLehrerStundenplan" name="sortieroptionLehrerStundenplanKuerzel" label="Kürzel, Nachname, Vorname(n)" />
						</svws-ui-radio-group>
					</div>
					<div class="text-left col-span-2">
						<svws-ui-button :disabled="isPrintStundenplanDisabled" @click="downloadPDF" :is-loading="loading" class="mt-4">
							<svws-ui-spinner v-if="loading" spinning />
							<span v-else class="icon i-ri-printer-line" />
							Drucken
						</svws-ui-button>
					</div>
					<!-- E-Mail-Eingabefelder -->
					<div class="text-left col-span-2 mb-2">
						<br><p class="font-bold mb-2">Den individuellen Stundenplan per E-Mail an die Lehrkräfte versenden.</p>
						<div class="flex flex-col gap-2">
							<input type="text" v-model="emailBetreff" placeholder="Betreff eingeben" class="w-full border rounded px-2 py-1">
							<textarea v-model="emailText" rows="5" placeholder="E-Mail-Text eingeben" class="w-full border rounded px-2 py-1" />
							<svws-ui-checkbox v-model="istPrivateEmailAlternative" name="istPrivateEmailAlternative">
								Private E-Mail verwenden, wenn keine schulische E-Mail-Adresse vorhanden ist.
							</svws-ui-checkbox>
						</div>
					</div>
					<div class="text-left col-span-2">
						<svws-ui-button :disabled="isEmailStundenplanDisabled" @click="sendPdfByEmail" :is-loading="loading" class="mt-4">
							<svws-ui-spinner v-if="loading" spinning />
							<span v-else class="icon i-ri-mail-send-line" />
							E-Mail senden
						</svws-ui-button>
					</div>
					<!-- Ende: E-Mail-Eingabefelder -->
					<div v-if="!lehrerListeManager().liste.auswahlExists()">
						<span class="text-ui-danger">Es ist keine Lehrkraft ausgewählt.</span>
					</div>
				</svws-ui-input-wrapper>
			</ui-card>
			<ui-card v-if="hatKompetenzLoeschen" icon="i-ri-delete-bin-line" title="Löschen"
				subtitle="Setze einen Löschvermerk bei den ausgewählten Lehrkräften." :is-open="currentAction === 'delete'"
				@update:is-open="(isOpen) => setCurrentAction('delete', isOpen)">
				<div>
					<span v-if="deleteLehrerCheck().success">Bereit zum Löschen.</span>
					<template v-else v-for="message in deleteLehrerCheck().logs" :key="message">
						<span class="text-ui-danger"> {{ message }} <br> </span>
					</template>
				</div>
				<template #buttonFooterLeft>
					<svws-ui-button :disabled="isDeleteDisabled" title="Löschen" @click="entferneLehrer" :is-loading="loading" class="mt-4">
						<svws-ui-spinner v-if="loading" spinning />
						<span v-else class="icon i-ri-play-line" />
						Löschen
					</svws-ui-button>
				</template>
			</ui-card>
		</svws-ui-input-wrapper>
		<log-box :logs :status="statusAction">
			<template #button>
				<svws-ui-button v-if="statusAction !== undefined" type="transparent" @click="clearLog" title="Log verwerfen">Log verwerfen</svws-ui-button>
			</template>
		</log-box>
	</div>
</template>

<script setup lang="ts">

	import { ref, computed } from "vue";
	import type { SLehrerAllgemeinesGruppenprozesseProps } from "./SLehrerAllgemeinesGruppenprozesseProps";
	import type { StundenplanListeEintrag, List } from "@core";
	import { DateUtils, ReportingParameter, ReportingReportvorlage, ListUtils, ArrayList, BenutzerKompetenz, ReportingSortierungDefinition, ReportingEMailDaten, ReportingEMailEmpfaengerTyp, ReportingAusgabeformat } from "@core";
	import { SelectManager } from "@ui";

	type Action = 'druckLehrerStundenplan' | 'delete' | '';

	const props = defineProps<SLehrerAllgemeinesGruppenprozesseProps>();

	const hatKompetenzDrucken = computed(() => (props.benutzerKompetenzen.has(BenutzerKompetenz.BERICHTE_ALLE_FORMULARE_DRUCKEN) || props.benutzerKompetenzen.has(BenutzerKompetenz.BERICHTE_STANDARDFORMULARE_DRUCKEN)));
	const hatKompetenzDruckenStundenplan = computed(() => (props.benutzerKompetenzen.has(BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ANSEHEN) && hatKompetenzDrucken.value));
	const hatKompetenzLoeschen = computed(() => props.benutzerKompetenzen.has(BenutzerKompetenz.SCHUELER_LOESCHEN));

	const isPrintDisabled = computed<boolean>(() => !props.lehrerListeManager().liste.auswahlExists() || loading.value)
	const isPrintStundenplanDisabled = computed<boolean>(() => isPrintDisabled.value || stundenplanAuswahl.value === undefined)
	const isEmailStundenplanDisabled = computed<boolean>(() => isPrintStundenplanDisabled.value || ((emailBetreff.value.trim().length) === 0) || ((emailText.value.trim().length) === 0))
	const isDeleteDisabled = computed<boolean>(() => !hatKompetenzLoeschen.value || !props.lehrerListeManager().liste.auswahlExists() || !props.deleteLehrerCheck().success || loading.value)

	const stundenplanAuswahl = ref<StundenplanListeEintrag>();
	const currentAction = ref<Action>('');
	const loading = ref<boolean>(false);
	const logs = ref<List<string | null> | undefined>();
	const statusAction = ref<boolean | undefined>();

	const stundenplanDisplayText = (eintrag: StundenplanListeEintrag) => {
		return eintrag.bezeichnung.replace('Stundenplan ', '') + ': '
			+ toDateStr(eintrag.gueltigAb) + '—'
			+ toDateStr(eintrag.gueltigBis)
			+ ' (KW ' + toKW(eintrag.gueltigAb) + '—'
			+ toKW(eintrag.gueltigBis) + ')'
	}

	const stundenplaene = computed<Array<StundenplanListeEintrag>>(() => [...props.mapStundenplaene.values()])
	const stundenplanSelectManager = new SelectManager({
		options: stundenplaene, optionDisplayText: stundenplanDisplayText, selectionDisplayText: stundenplanDisplayText,
	})

	const option1 = ref(false);
	const option2 = ref(false);
	const option4 = ref(false);
	const option8 = ref(false);
	const option16 = ref(false);
	const option32 = ref(false);
	const option64 = ref(false);
	const option128 = ref(false);
	const option256 = ref(false);
	const option512 = ref(false);
	const option1024 = ref(false);
	const option2048 = ref(false);
	const option4096 = ref(false);

	const druckoptionLehrerStundenplan = ref(1);
	const sortieroptionLehrerStundenplan = ref(1);

	const emailBetreff = ref<string>("");
	const emailText = ref<string>("");
	const istPrivateEmailAlternative = ref<boolean>(false);

	async function downloadPDF() {
		if (stundenplanAuswahl.value === undefined)
			return;

		const listeIdsLehrer = new ArrayList<number>();
		for (const lehrer of props.lehrerListeManager().liste.auswahl())
			listeIdsLehrer.add(lehrer.id);

		const reportingParameter = new ReportingParameter();
		reportingParameter.reportvorlage = ReportingReportvorlage.STUNDENPLANUNG_v_LEHRER_STUNDENPLAN.getBezeichnung();
		reportingParameter.idsHauptdaten = ListUtils.create1(stundenplanAuswahl.value.id);
		reportingParameter.idsDetaildaten = listeIdsLehrer;
		reportingParameter.einzelausgabeHauptdaten = false;
		reportingParameter.einzelausgabeDetaildaten = (druckoptionLehrerStundenplan.value === 2);
		reportingParameter.sortierungDetaildaten = new ReportingSortierungDefinition();
		reportingParameter.sortierungDetaildaten.verwendeStandardsortierung = (sortieroptionLehrerStundenplan.value === 1);
		if (sortieroptionLehrerStundenplan.value === 2) {
			const attribute = new ArrayList<string>();
			attribute.add("lehrer.kuerzel");
			attribute.add("lehrer.nachname");
			attribute.add("lehrer.vorname");
			attribute.add("lehrer.vornamen");
			attribute.add("lehrer.geburtsdatum");
			attribute.add("lehrer.id");
			reportingParameter.sortierungDetaildaten.attribute = attribute;
		}
		reportingParameter.detailLevel = ((option1.value ? 1 : 0) + (option2.value ? 2 : 0) + (option4.value ? 4 : 0)
			+ (option8.value ? 8 : 0) + (option16.value ? 16 : 0) + (option32.value ? 32 : 0) + (option64.value ? 64 : 0)
			+ (option128.value ? 128 : 0) + (option256.value ? 256 : 0) + (option512.value ? 512 : 0)
			+ (option1024.value ? 1024 : 0) + (option2048.value ? 2048 : 0) + (option4096.value ? 4096 : 0));

		loading.value = true;
		const { data, name } = await props.getPDF(reportingParameter, stundenplanAuswahl.value.id);
		const link = document.createElement("a");
		link.href = URL.createObjectURL(data);
		link.download = name;
		link.target = "_blank";
		link.click();
		URL.revokeObjectURL(link.href);
		loading.value = false;
	}

	async function sendPdfByEmail() {
		if (stundenplanAuswahl.value === undefined)
			return;

		const listeIdsLehrer = new ArrayList<number>();
		for (const lehrer of props.lehrerListeManager().liste.auswahl())
			listeIdsLehrer.add(lehrer.id);

		const reportingParameter = new ReportingParameter();
		reportingParameter.reportvorlage = ReportingReportvorlage.STUNDENPLANUNG_v_LEHRER_STUNDENPLAN.getBezeichnung();
		reportingParameter.ausgabeformat = ReportingAusgabeformat.EMAIL.getId();
		reportingParameter.idsHauptdaten = ListUtils.create1(stundenplanAuswahl.value.id);
		reportingParameter.idsDetaildaten = listeIdsLehrer;
		reportingParameter.einzelausgabeHauptdaten = false;
		reportingParameter.einzelausgabeDetaildaten = true;

		const emailDaten = new ReportingEMailDaten();
		emailDaten.empfaengerTyp = ReportingEMailEmpfaengerTyp.LEHRER.getId();
		emailDaten.istPrivateEmailAlternative = istPrivateEmailAlternative.value;
		emailDaten.betreff = (((emailBetreff.value.trim().length) !== 0) ? emailBetreff.value : ("Stundenplan " + stundenplanAuswahl.value.bezeichnung));
		emailDaten.text = (((emailText.value.trim().length) !== 0) ? emailText.value : ("Im Anhang dieser E-Mail ist der Stundenplan " + stundenplanAuswahl.value.bezeichnung + " enthalten."));
		reportingParameter.eMailDaten = emailDaten;

		reportingParameter.detailLevel = ((option1.value ? 1 : 0) + (option2.value ? 2 : 0) + (option4.value ? 4 : 0)
			+ (option8.value ? 8 : 0) + (option16.value ? 16 : 0) + (option32.value ? 32 : 0) + (option64.value ? 64 : 0)
			+ (option128.value ? 128 : 0) + (option256.value ? 256 : 0) + (option512.value ? 512 : 0)
			+ (option1024.value ? 1024 : 0) + (option2048.value ? 2048 : 0) + (option4096.value ? 4096 : 0));

		loading.value = true;
		const result = await props.sendEMail(reportingParameter);
		statusAction.value = result.success;
		logs.value = result.log;
		loading.value = false;
	}

	async function entferneLehrer() {
		loading.value = true;
		[statusAction.value, logs.value] = await props.deleteLehrer();
		loading.value = false;
	}

	function setCurrentAction(newAction: Action, open: boolean) {
		if ((newAction !== currentAction.value) && !open)
			return;

		option1.value = false;
		option2.value = false;
		option4.value = false;
		option8.value = false;
		option16.value = false;
		option32.value = false;
		option64.value = false;
		option128.value = false;
		option256.value = false;
		option512.value = false;
		option1024.value = false;
		option2048.value = false;
		option4096.value = false;
		druckoptionLehrerStundenplan.value = 1;
		sortieroptionLehrerStundenplan.value = 1;
		emailBetreff.value = '';
		emailText.value = '';
		istPrivateEmailAlternative.value = false;

		currentAction.value = open ? newAction : "";
	}

	function clearLog() {
		loading.value = false;
		logs.value = undefined;
		statusAction.value = undefined;
	}

	const wochentag = ['So.', 'Mo.', 'Di.', 'Mi.', 'Do.', 'Fr.', 'Sa.', 'So.' ];

	function toDateStr(iso: string) : string {
		const date = DateUtils.extractFromDateISO8601(iso);
		return wochentag[date[3] % 7] + " " + date[2] + "." + date[1] + "." + date[0];
	}

	function toKW(iso: string) : string {
		const date = DateUtils.extractFromDateISO8601(iso);
		return "" + date[5];
	}

</script>
