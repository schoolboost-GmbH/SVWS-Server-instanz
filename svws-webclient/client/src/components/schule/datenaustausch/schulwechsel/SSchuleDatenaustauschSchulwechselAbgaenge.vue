<template>
	<div class="page gap-6 overflow-y-auto">
		<svws-ui-table :columns :items="[]" :selectable="false" :model-value="selected" @update:model-value="selected=$event" count>
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
				<svws-ui-button :disabled="!selected.length">Datensätze senden</svws-ui-button>
			</template>
		</svws-ui-table>
	</div>
</template>

<script setup lang="ts">

	import { ref } from "vue";
	import { SSchuleDatenaustauschSchulwechselAbgaengeProps } from "~/components/schule/datenaustausch/schulwechsel/SSchuleDatenaustauschSchulwechselAbgaengeProps";

	const props = defineProps<SSchuleDatenaustauschSchulwechselAbgaengeProps>()

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
