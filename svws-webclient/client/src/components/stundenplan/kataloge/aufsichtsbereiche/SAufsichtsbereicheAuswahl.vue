<template>
	<svws-ui-table :clicked="auswahl" clickable @update:clicked="gotoEintrag" :items="stundenplanManager().aufsichtsbereichGetMengeAsList()" :columns="cols" :selectable="!readonly" v-model="selected" class="max-w-128 min-w-96" scroll>
		<template v-if="!readonly" #actions>
			<svws-ui-button @click="doDeleteEintraege()" type="trash" :disabled="selected.length === 0" />
			<s-aufsichtsbereich-neu-modal v-slot="{ openModal }" :add-aufsichtsbereich="addEintrag">
				<svws-ui-button type="icon" @click="openModal()">
					<span class="icon i-ri-add-line" />
				</svws-ui-button>
			</s-aufsichtsbereich-neu-modal>
		</template>
	</svws-ui-table>
</template>

<script setup lang="ts">

	import { computed, ref } from "vue";
	import type { AufsichtsbereicheAuswahlProps } from "./SAufsichtsbereicheAuswahlProps";
	import { BenutzerKompetenz, type Aufsichtsbereich } from "@core";

	const props = defineProps<AufsichtsbereicheAuswahlProps>();
	const readonly = computed<boolean>(() => !(props.benutzerKompetenzen.has(BenutzerKompetenz.STUNDENPLAN_AENDERN)));
	const selected = ref<Aufsichtsbereich[]>([]);

	const cols = [
		{ key: "kuerzel", label: "Kürzel", sortable: true, defaultSort: 'asc', span: 0.5 },
		{ key: "beschreibung", label: "Beschreibung", sortable: true },
	];

	async function doDeleteEintraege() {
		await props.deleteEintraege(selected.value);
		selected.value = [];
	}

</script>
