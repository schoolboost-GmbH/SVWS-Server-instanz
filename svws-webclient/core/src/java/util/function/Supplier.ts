
export interface Supplier<T> {

	get(): T;

}


export function cast_java_util_function_Supplier<T>(obj: unknown): Supplier<T> {
	return obj as Supplier<T>;
}
