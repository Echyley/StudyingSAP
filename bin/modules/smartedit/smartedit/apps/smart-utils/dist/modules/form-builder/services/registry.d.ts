/**
 * @internal
 *
 * Generic registry for mapping keys to items.
 */
export declare abstract class Registry<T> {
    private _map;
    constructor(items?: {
        [name: string]: T;
    });
    /**
     * Adds a item to the registry.
     *
     * @param {string} name
     * @param {T} item
     */
    add(name: string, item: T): void;
    /**
     * Gets an a item in the registry.
     *
     * @param {string} name
     * @returns {T}
     */
    get(name: string): T | undefined;
    /**
     * @internal
     */
    private get _service();
}
