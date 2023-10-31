import { AbstractForm } from '../models';
/**
 * Get an address book of persisting fields to the actual form path.
 * Example:
 * {
 *   'property': ['tab', 'property']
 *   ...
 * }
 * Where tab is not a persisting property of the model.
 */
export declare const getPersistenceMap: (form: AbstractForm, map?: {
    [s: string]: string[];
}, from?: string[], to?: string[]) => {
    [path: string]: string[];
};
