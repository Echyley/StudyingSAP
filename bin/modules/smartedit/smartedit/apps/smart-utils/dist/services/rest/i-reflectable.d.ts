/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
export interface IReflectable<T> {
    setMethod?(name: string, methodInstance: (...params: any[]) => Promise<void | T> | Promise<T[] | null>): void;
    getMethodForSingleInstance?(name: string): (...params: any[]) => Promise<T | null>;
    getMethodForArray?(name: string): (...params: any[]) => Promise<T[] | null>;
}
