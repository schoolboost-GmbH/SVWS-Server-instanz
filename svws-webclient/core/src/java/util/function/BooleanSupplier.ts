
export interface BooleanSupplier {

	getAsBoolean(): boolean;

}


export function cast_java_util_function_BooleanSupplier(obj: unknown): BooleanSupplier {
	return obj as BooleanSupplier;
}
