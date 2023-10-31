import { LogService } from '@smart/utils';
export interface DiscardablePromise<T> {
    promise: Promise<T>;
    successCallback: (...args: any[]) => any;
    failureCallback: (...args: any[]) => any;
    discardableHolder?: {
        successCallback: (...args: any[]) => any;
        failureCallback: (...args: any[]) => any;
    };
}
/**
 * Helper to handle competing promises
 */
export declare class DiscardablePromiseUtils {
    private logService;
    private _map;
    constructor(logService: LogService);
    /**
     * Selects a new promise as candidate for invoking a given callback
     * each invocation of this method for a given key discards the previously selected promise
     * @param key The string key identifying the discardable promise
     * @param promise The discardable promise instance once a new candidate is called with this method
     * @param successCallback The success callback to ultimately apply on the last promise not discarded
     * @param failureCallback The failure callback to ultimately apply on the last promise not discarded. Optional.
     */
    apply<T>(key: string, promise: Promise<T>, successCallback: (arg: T) => any, failureCallback?: (arg: Error) => any): void;
    /**
     * Removes callbacks of promise if exists.
     *
     * Used to remove any pending callbacks when a component is destroyed to prevent memory leaks.
     */
    clear(key: string): void;
    private exists;
}
