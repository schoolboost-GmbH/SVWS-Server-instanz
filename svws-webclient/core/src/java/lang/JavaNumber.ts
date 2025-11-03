import { JavaObject } from './JavaObject';


export abstract class JavaNumber extends JavaObject {

	public intValue(n: number): number {
		return Math.trunc(n);
	}

	public longValue(n: number): number {
		return Math.trunc(n);
	}

	public floatValue(n: number): number {
		return n;
	}

	public doubleValue(n: number): number {
		return n;
	}

	public byteValue(n: number): number {
		return n;
	}

	public shortValue(n: number): number {
		return n;
	}

	transpilerCanonicalName(): string {
		return 'java.lang.Number';
	}

	isTranspiledInstanceOf(name: string): boolean {
		return [
			'java.lang.Number',
			'java.lang.Object',
		].includes(name);
	}

}


export function cast_java_lang_Number(obj: unknown): JavaNumber {
	return obj as JavaNumber;
}
