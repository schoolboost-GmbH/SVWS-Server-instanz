
export interface IntSupplier {

    getAsInt() : number;

}


export function cast_java_util_function_IntSupplier(obj : unknown) : IntSupplier {
	return obj as IntSupplier;
}
