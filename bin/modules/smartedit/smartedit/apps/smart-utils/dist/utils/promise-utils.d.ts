import { LogService } from '../services/log.service';
export interface Deferred<T> {
    promise: Promise<T>;
    resolve: (value?: T) => void;
    reject: (value?: any, silenceError?: boolean) => void;
}
/**
 * @ngdoc service
 * @name @smartutils.services:PromiseUtils
 *
 * @description
 * utility service around ES6 Promises.
 */
export declare class PromiseUtils {
    static logService: LogService;
    private WAIT_TIMEOUT;
    private FAILURE_TIMEOUT;
    toPromise<T>(method: (...args: any[]) => T, context?: any): (...innerArgs: any[]) => Promise<T>;
    promise<T>(executor: (resolve: (value?: T | PromiseLike<T>) => void, reject: (reason?: any, silenceError?: boolean) => void) => void): Promise<T>;
    defer<T>(): Deferred<T>;
    sleep(ms: number): Promise<any>;
    handlePromiseRejections: <T>(promise: Promise<T>) => Promise<T>;
    isAjaxError(error: any): boolean;
    waitOnCondition(condition: () => boolean, callback: () => any, errorMessage: string, elapsedTime?: number): void;
    resolveToCallbackWhenCondition<T>(condition: () => boolean, callback: () => T, errorMessage?: string): Promise<T>;
    attempt<T>(promise: Promise<T>): Promise<{
        error: any;
        data: T;
    }>;
    private defaultFailureCallback;
}
export declare const promiseUtils: PromiseUtils;
