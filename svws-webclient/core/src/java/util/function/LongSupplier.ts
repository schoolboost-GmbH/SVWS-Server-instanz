
export interface LongSupplier {

	getAsLong(): number;

}


export function cast_java_util_function_LongSupplier(obj: unknown): LongSupplier {
	return obj as LongSupplier;
}
