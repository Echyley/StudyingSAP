/**
 * Interface of entities aimed at being sorted by priority
 */
export interface IPrioritized {
    /**
     * Number ranging from 0 to 1000 used for sorting
     */
    priority?: number;
}
