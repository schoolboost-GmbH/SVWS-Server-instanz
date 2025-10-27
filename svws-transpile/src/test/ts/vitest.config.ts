import { defineConfig } from "vitest/config";
import { resolve } from 'node:path';

export default defineConfig({
	test: {
		globals: true,
		pool: "threads",
		reporters: ["default", "junit"],
		outputFile: "../../../build/coverage/junit.xml",
		coverage: {
			provider: "v8",
			reportsDirectory: "../../../build/coverage",
		},
		include: [
			"src/**/*.test.ts",
		],
	},
	resolve: {
		alias: {
			"~": resolve(__dirname, "src"),
		},
	},
});
