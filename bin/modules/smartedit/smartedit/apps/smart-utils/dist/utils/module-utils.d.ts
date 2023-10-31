/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Provider, ValueProvider } from '@angular/core';
import { Cloneable, TypedMap } from '../dtos';
/** @internal */
export declare class ModuleUtils {
    initialize(useFactory: (...args: any[]) => void, deps?: any[]): Provider;
    bootstrap(useFactory: (...args: any[]) => void, deps?: any[]): Provider;
    provideValues(_constants?: TypedMap<Cloneable | RegExp>): ValueProvider[];
}
export declare const moduleUtils: ModuleUtils;
