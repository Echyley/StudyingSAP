import { StringUtils } from '@smart/utils';
import { IPrioritized } from './interfaces/IPrioritized';
/**
 * The PriorityService handles arrays of {@link IPrioritized} elements.
 */
export declare class PriorityService {
    private readonly stringUtils;
    constructor(stringUtils: StringUtils);
    /**
     * Will sort the candidate array by ascendign or descending priority.
     * Even if the priority is not defined for a number of elements, the sorting will still be consistent over invocations
     * @param candidate Elements to be sorted
     * @param ascending If true, candidate will be sorted by ascending priority.
     * @returns The sorted candidate array.
     */
    sort<T extends IPrioritized>(candidate: T[], ascending?: boolean): T[];
}
