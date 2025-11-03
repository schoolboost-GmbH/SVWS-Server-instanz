<template>
	<div v-if="code.length > 0" class="w-full h-full overflow-x-auto overflow-y-hidden mt-4">
		<div>
			<slot name="button" />
		</div>
		<div class="border-ui-neutral border rounded-xl group relative">
			<div class="absolute top-4 right-4 group-hover:visible invisible">
				<svws-ui-button type="secondary" @click="copyToClipboard">
					<template v-if="copied === null">
						<span class="icon i-ri-file-copy-line" />
						<span>Kopieren</span>
					</template>
					<template v-else-if="copied === false">
						<span>Kopieren fehlgeschlagen</span>
						<span class="icon i-ri-error-warning-fill" />
					</template>
					<template v-else>
						<span>Kopiert</span>
						<span class="icon i-ri-check-line icon-ui-success" />
					</template>
				</svws-ui-button>
			</div>
			<div class="max-h-96 w-full overflow-auto">
				<pre class="py-2 px-3 w-px">{{ code }}</pre>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">

	import { ref } from "vue";

	const props = withDefaults(defineProps<{
		code: string;
		hfull?: boolean;
		backticks?: boolean;
	}>(), {
		hfull: false,
		backticks: true,
	});

	const copied = ref<boolean | null>(null);
	defineSlots();

	async function copyToClipboard() {
		const copy = props.backticks ? "```\n" + props.code + "\n```" : props.code;
		try {
			await navigator.clipboard.writeText(copy);
		} catch (e) {
			console.log(e);
			copied.value = false;
		}
		copied.value = true;
	}
</script>