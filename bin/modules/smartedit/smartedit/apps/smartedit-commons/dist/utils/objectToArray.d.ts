import { TypedMap } from '@smart/utils';
export declare function objectToArray<T = string>(obj: TypedMap<T>): {
    key: string;
    value: T;
}[];
