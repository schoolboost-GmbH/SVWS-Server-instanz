<template>
	<div class="page gap-6 overflow-y-auto">
		<svws-ui-button>Daten importieren</svws-ui-button>
		<svws-ui-table class="mt-4" :columns :items="[]" :selectable="false" :model-value="selected" @update:model-value="selected=$event" count>
			<template #cell(anrede)="{ rowData }">
				<svws-ui-text-input :model-value="null" headless required />
			</template>
			<template #cell(name)="{ rowData }">
				<svws-ui-text-input :model-value="null" headless required />
			</template>
			<template #cell(vorname)="{ rowData }">
				<svws-ui-text-input :model-value="null" headless required />
			</template>
			<template #cell(telefon)="{ rowData }">
				<svws-ui-text-input :model-value="null" headless required :max-len="20" />
			</template>
			<template #cell(email)="{ rowData }">
				<svws-ui-text-input :model-value="null" headless required />
			</template>
			<template #actions>
				<svws-ui-button :disabled="!selected.length">Datensätze anlegen</svws-ui-button>
			</template>
		</svws-ui-table>
	</div>
</template>

<script setup lang="ts">

	import { ref } from "vue";
	import { SSchuleDatenaustauschSchulwechselZugaengeProps } from "~/components/schule/datenaustausch/schulwechsel/SSchuleDatenaustauschSchulwechselZugaengeProps";

	const props = defineProps<SSchuleDatenaustauschSchulwechselZugaengeProps>()

	const columns = [
		{key: 'anrede', label: 'Anrede', span: 1},
		{key: 'name', label: 'Name', span: 2},
		{key: 'vorname', label: 'Rufname', span: 2},
		{key: 'telefon', label: 'Telefon', span: 2},
		{key: 'email', label: 'Email', span: 3},
		{key: 'id', label: 'ID', span: 0.5},
	]

	const selected = ref<String[]>([]);

</script>
