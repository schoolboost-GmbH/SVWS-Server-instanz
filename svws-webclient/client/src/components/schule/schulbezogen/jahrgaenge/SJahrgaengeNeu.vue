<template>
	<div class="page page-grid-cards">
		<svws-ui-content-card title="Allgemein">
			<svws-ui-input-wrapper :grid="2">
				<svws-ui-text-input placeholder="Kürzel" :min-len="1" :max-len="20" v-model="data.kuerzel" :disabled :valid="fieldIsValid('kuerzel')"
					required />
				<svws-ui-text-input placeholder="Bezeichnung" :max-len="100" :min-len="1" required v-model="data.bezeichnung" :disabled :valid="fieldIsValid('bezeichnung')" />
				<div class="flex flex-col my-auto space-y-1">
					<div v-if="!isUniqueInList(data.kuerzel, props.manager().liste.list(), 'kuerzel')" class="flex items-center">
						<span class="icon i-ri-alert-line mx-0.5 mr-1" />
						<p>Dieses Kürzel wird bereits verwendet.</p>
					</div>
					<div v-if="kuerzelIsTooLong" class="flex items-center">
						<span class="icon i-ri-alert-line mx-0.5 mr-1" />
						<p>Dieses Kürzel verwendet zu viele Zeichen.</p>
					</div>
				</div>
				<div class="flex flex-col my-auto space-y-1">
					<div v-if="!isUniqueInList(data.bezeichnung, props.manager().liste.list(), 'bezeichnung')" class="flex items-center">
						<div />
						<span class="icon i-ri-alert-line mx-0.5 mr-1" />
						<p>Diese Bezeichnung wird bereits verwendet.</p>
					</div>
					<div v-if="data.bezeichnung.length > 100" class="flex items-center">
						<div />
						<span class="icon i-ri-alert-line mx-0.5 mr-1" />
						<p>Diese Bezeichnung verwendet zu viele Zeichen.</p>
					</div>
				</div>
				<svws-ui-text-input placeholder="Kurzbezeichnung" :max-len="2" v-model="data.kurzbezeichnung" :disabled :valid="fieldIsValid('kurzbezeichnung')" />
				<svws-ui-select title="Schulgliederung" :items="Schulgliederung.getBySchuljahrAndSchulform(schuljahr, schulform)" :item-text="textSchulgliederung"
					statistics :disabled v-model="schulgliederung" :valid="fieldIsValid('kuerzelSchulgliederung')" />
				<svws-ui-select title="Jahrgang" :items="Jahrgaenge.getListBySchuljahrAndSchulform(schuljahr, schulform)" :item-text="textStatistikJahrgang"
					statistics :disabled v-model="statistikJahrgang" :valid="fieldIsValid('kuerzelStatistik')" required />
				<svws-ui-select title="Folgejahrgang" :items="folgejahrgaenge" :item-text="textFolgejahrgang"
					:disabled="!hatKompetenzAdd || !data.kuerzelStatistik" v-model="folgejahrgang" :valid="fieldIsValid('idFolgejahrgang')" />
				<svws-ui-input-number placeholder="Anzahl der Restabschnitte" :disabled :min="0" :max="40" v-model="data.anzahlRestabschnitte"
					:valid="fieldIsValid('anzahlRestabschnitte')" />
				<svws-ui-input-number placeholder="Sortierung" v-model="data.sortierung" :disabled :min="0" :max="32000" :valid="fieldIsValid('sortierung')" />
				<svws-ui-spacing />
				<svws-ui-checkbox v-model="data.istSichtbar" :disabled>
					Sichtbar
				</svws-ui-checkbox>
				<div class="mt-7 flex flex-row gap-4 justify end">
					<svws-ui-button type="secondary" @click="cancel">Abbrechen</svws-ui-button>
					<svws-ui-button @click="add" :disabled="!formIsValid || !hatKompetenzAdd">Speichern</svws-ui-button>
				</div>
			</svws-ui-input-wrapper>
		</svws-ui-content-card>
		<svws-ui-checkpoint-modal :checkpoint :continue-routing="props.continueRoutingAfterCheckpoint" />
	</div>
</template>

<script setup lang="ts">
	import type { SchuleJahrgangNeuProps } from "./SJahrgaengeNeuProps";
	import { computed, ref, watch } from "vue";
	import { BenutzerKompetenz, Jahrgaenge, JahrgangsDaten, Schulgliederung } from "@core";
	import { isUniqueInList, mandatoryInputIsValid, optionalInputIsValid } from "~/util/validation/Validation";

	const props = defineProps<SchuleJahrgangNeuProps>();
	const data = ref<JahrgangsDaten>(Object.assign(new JahrgangsDaten(), { istSichtbar: true, sortierung: 1, anzahlRestabschnitte: 0 }));
	const folgejahrgaenge = computed<JahrgangsDaten[]>(() => [...props.manager().liste.list()].filter((j: JahrgangsDaten) => j.kuerzelStatistik !== data.value.kuerzelStatistik));
	const isLoading = ref<boolean>(false);
	const hatKompetenzAdd = computed<boolean>(() => props.benutzerKompetenzen.has(BenutzerKompetenz.KATALOG_EINTRAEGE_AENDERN));
	const disabled = computed(() => !hatKompetenzAdd.value);

	const kuerzelIsTooLong = computed(() => {
		if (data.value.kuerzel === null)
			return false;

		return data.value.kuerzel.length > 20;
	});

	const schulgliederung = computed<Schulgliederung | null>({
		get: () => {
			const kuerzel = data.value.kuerzelSchulgliederung;
			if (kuerzel === null)
				return null;

			return Schulgliederung.data().getWertByKuerzel(kuerzel);
		},
		set: (value: Schulgliederung | null) => {
			const kuerzel = value?.daten(props.schuljahr)?.kuerzel;
			if (kuerzel !== undefined)
				data.value.kuerzelSchulgliederung = kuerzel;
		},
	});

	const statistikJahrgang = computed<Jahrgaenge | null>({
		get: () => {
			const kuerzel = data.value.kuerzelStatistik;
			return (kuerzel === null) ? null : Jahrgaenge.data().getWertByKuerzel(kuerzel);
		},
		set: (value: Jahrgaenge | null) => {
			data.value.kuerzelStatistik = value?.daten(props.schuljahr)?.kuerzel ?? null;
		},
	});

	const folgejahrgang = computed<JahrgangsDaten | null>({
		get: () => {
			return props.manager().liste.get(data.value.idFolgejahrgang ?? -1);
		},
		set: (value: JahrgangsDaten | null) => {
			data.value.idFolgejahrgang = value?.id ?? null;
		},
	});

	// --- Validierung ---
	function fieldIsValid(field: keyof JahrgangsDaten | null): (v: string | number | null) => boolean {
		return (v: string | number | null) => {
			switch (field) {
				case 'kuerzel':
					return kuerzelIsValid(data.value.kuerzel);
				case 'bezeichnung':
					return bezeichnungIsValid(data.value.bezeichnung);
				case 'kurzbezeichnung':
					return optionalInputIsValid(data.value.kurzbezeichnung, 2);
				case 'kuerzelSchulgliederung':
					return kuerzelSchulgliederungIsValid(data.value.kuerzelSchulgliederung);
				case 'kuerzelStatistik':
					return kuerzelStatistikIsValid(data.value.kuerzelStatistik);
				case 'idFolgejahrgang':
					return idFolgejahrgangIsValid(data.value.idFolgejahrgang);
				case 'anzahlRestabschnitte':
					return anzahlRestabschnitteIsValid(data.value.anzahlRestabschnitte);
				case 'sortierung':
					return data.value.sortierung >= 0 && data.value.sortierung <= 32000;
				default:
					return true;
			}
		};
	}

	function kuerzelIsValid(kuerzel: string | null): boolean {
		if (!mandatoryInputIsValid(kuerzel, 20))
			return false;

		return isUniqueInList(kuerzel, props.manager().liste.list(), "kuerzel");
	}

	function bezeichnungIsValid(bezeichnung: string | null): boolean {
		if (!mandatoryInputIsValid(bezeichnung, 100))
			return false;

		return isUniqueInList(bezeichnung, props.manager().liste.list(), "bezeichnung");
	}

	function kuerzelSchulgliederungIsValid(kuerzelSchulgliederung: string | null): boolean {
		if (kuerzelSchulgliederung === null)
			return true;

		const result = Schulgliederung.data().getWertByKuerzel(kuerzelSchulgliederung);
		return (result !== null);
	}

	function kuerzelStatistikIsValid(kuerzelStatistik: string | null): boolean {
		if (kuerzelStatistik === null)
			return false;

		const result = Jahrgaenge.data().getWertByKuerzel(kuerzelStatistik);
		return (result !== null);
	}

	function idFolgejahrgangIsValid(idFolgejahrgang: number | null): boolean {
		if (idFolgejahrgang === null)
			return true;

		const jahrgang = props.manager().liste.get(idFolgejahrgang);
		// false, falls es keinen Folgejahrgang mit dieser id gibt oder falls sich dieser nicht vom Statistik-Jahrgang unterscheidet
		return !((jahrgang === null) || (jahrgang.kuerzelStatistik === data.value.kuerzelStatistik));
	}

	function anzahlRestabschnitteIsValid(anzahlRestabschnitte: number | null) {
		if (anzahlRestabschnitte === null)
			return true;

		return ((anzahlRestabschnitte >= 0) && (anzahlRestabschnitte <= 40));
	}

	const formIsValid = computed(() => {
		// alle Felder auf validity prüfen
		return Object.keys(data.value).every(field => {
			const validateField = fieldIsValid(field as keyof JahrgangsDaten);
			const fieldValue = data.value[field as keyof JahrgangsDaten] as string | null;
			return validateField(fieldValue);
		});
	});

	// --- Bezeichnungen ---
	function textSchulgliederung(schulgliederung: Schulgliederung): string {
		const eintrag = schulgliederung.daten(props.schuljahr);
		if (eintrag === null)
			return '_';
		return eintrag.kuerzel + ' - ' + eintrag.text;
	}

	function textStatistikJahrgang(jahrgang: Jahrgaenge | null) {
		const jahrgangEintrag = (jahrgang === null) ? null : jahrgang.daten(props.schuljahr);
		if (jahrgangEintrag === null)
			return 'JU - Jahrgangsübergreifend';

		return jahrgangEintrag.kuerzel + " - " + jahrgangEintrag.text;
	}

	const textFolgejahrgang = (jahrgang: JahrgangsDaten | null) => {
		if (jahrgang === null)
			return '-';
		if (jahrgang.kuerzel === null)
			return jahrgang.bezeichnung;
		return jahrgang.kuerzel + ' : ' + jahrgang.bezeichnung;
	};

	// --- util ---
	async function add() {
		if (isLoading.value)
			return;

		props.checkpoint.active = false;
		isLoading.value = true;
		const { id, referenziertInAnderenTabellen, ...partialData } = data.value;
		await props.addJahrgang(partialData);
		isLoading.value = false;
	}

	async function cancel() {
		props.checkpoint.active = false;
		await props.goToDefaultView(null);
	}

	watch(() => data.value.kuerzelStatistik, () => {
		data.value.idFolgejahrgang = null;
	});

	watch(() => data.value, async () => {
		if (isLoading.value)
			return;

		props.checkpoint.active = true;
	}, { immediate: false, deep: true });

</script>
