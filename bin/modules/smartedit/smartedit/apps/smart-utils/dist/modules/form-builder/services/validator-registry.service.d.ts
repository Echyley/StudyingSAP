import { ValidatorFn } from '@angular/forms';
import { Registry } from './registry';
export interface ValidatorMap {
    [name: string]: (...args: any[]) => ValidatorFn;
}
/**
 * A registry for synchronous validators.
 */
export declare class ValidatorRegistryService extends Registry<(...args: any[]) => ValidatorFn> {
    constructor(validators: ValidatorMap);
}
