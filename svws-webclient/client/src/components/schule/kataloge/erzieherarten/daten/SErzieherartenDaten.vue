<template>
	<div class="page page-grid-cards">
		<svws-ui-content-card title="Erzieherart" class="w-full">
			<svws-ui-input-wrapper>
				<svws-ui-text-input class="contentFocusField w-5/5" placeholder="Bezeichnung" :model-value="manager().auswahl().bezeichnung"
					@change="bezeichnung => patch({ bezeichnung: bezeichnung ?? undefined })" :readonly />
			</svws-ui-input-wrapper>
		</svws-ui-content-card>
	</div>
</template>

<script setup lang="ts">

	import type { ErzieherartenDatenProps } from "~/components/schule/kataloge/erzieherarten/daten/SErzieherartenDatenProps";
	import { computed } from "vue";
	import { BenutzerKompetenz } from "@core";

	const props = defineProps<ErzieherartenDatenProps>();

	const hatKompetenzUpdate = computed<boolean>(() => props.benutzerKompetenzen.has(BenutzerKompetenz.KATALOG_EINTRAEGE_AENDERN));
	// patching these entries is not aloud according to SchILDzentral
	const idsOfNonPatchableEntries = new Set([1,2,3,4,5])
	const readonly = computed(() => (!hatKompetenzUpdate.value) || (idsOfNonPatchableEntries.has(props.manager().auswahl().id)));

</script>
