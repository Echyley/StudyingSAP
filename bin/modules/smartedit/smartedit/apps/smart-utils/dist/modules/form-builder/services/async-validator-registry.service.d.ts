import { AsyncValidatorFn } from '@angular/forms';
import { Registry } from './registry';
export interface AsyncValidatorMap {
    [name: string]: (...args: any[]) => AsyncValidatorFn;
}
/**
 * A registry for asynchronous validators.
 */
export declare class AsyncValidatorRegistryService extends Registry<(...args: any[]) => AsyncValidatorFn> {
    constructor(asyncValidators: AsyncValidatorMap);
}
