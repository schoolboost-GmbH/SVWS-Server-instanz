<template>
	<div class="flex flex-col w-full h-full overflow-hidden">
		<header class="svws-ui-header">
			<div class="svws-ui-header--title">
				<div class="svws-headline-wrapper">
					<h2 class="svws-headline">
						<span>Reporting</span>
					</h2>
				</div>
			</div>
			<div class="svws-ui-header--actions" />
		</header>
		<div class="page page-grid-cards">
			<svws-ui-content-card title="Reporting">
				<svws-ui-input-wrapper>
					<svws-ui-select v-model="report" :items="ReportingReportvorlage.values()" :item-text="i => i.getBezeichnung()" title="Eine Reportvorlage wählen" />
					<svws-ui-textarea-input :disabled="report === undefined" placeholder="Vorgaben ReportingParameter" :model-value="parameter" @change="value => parameter = value ?? ''" resizeable="vertical" autoresize :rows="12" />
					<svws-ui-button :disabled="report === undefined" @click="run">Report erzeugen</svws-ui-button>
				</svws-ui-input-wrapper>
			</svws-ui-content-card>
		</div>
	</div>
</template>

<script setup lang="ts">

	import { computed, ref, watch } from "vue";
	import { ReportingParameter, ReportingReportvorlage } from "@core";
	import type { SchuleReportingProps } from "./SSchuleReportingProps";

	const props = defineProps<SchuleReportingProps>();

	const report = ref<ReportingReportvorlage>();
	const parameter = ref<string>("");

	const template = computed(() => {
		const t = new ReportingParameter();
		t.idSchuljahresabschnitt = props.schuljahresabschnitt().id;
		t.reportvorlage = report.value?.getBezeichnung() ?? '';
		return JSON.stringify(JSON.parse(ReportingParameter.transpilerToJSON(t)), null, 4);
	});

	watch(report, () => (parameter.value = template.value));

	async function run() {
		const params = ReportingParameter.transpilerFromJSON(parameter.value);
		const { data, name } = await props.createReport(params);
		const link = document.createElement("a");
		link.href = URL.createObjectURL(data);
		link.download = name;
		link.target = "_blank";
		link.click();
		URL.revokeObjectURL(link.href);
	}

</script>
