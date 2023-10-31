/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { LogService, TypedMap } from '@smart/utils';

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
@Injectable()
export class DiscardablePromiseUtils {
    private _map = {} as TypedMap<DiscardablePromise<any>>;

    constructor(private logService: LogService) {}

    /**
     * Selects a new promise as candidate for invoking a given callback
     * each invocation of this method for a given key discards the previously selected promise
     * @param key The string key identifying the discardable promise
     * @param promise The discardable promise instance once a new candidate is called with this method
     * @param successCallback The success callback to ultimately apply on the last promise not discarded
     * @param failureCallback The failure callback to ultimately apply on the last promise not discarded. Optional.
     */
    public apply<T>(
        key: string,
        promise: Promise<T>,
        successCallback: (arg: T) => any,
        failureCallback?: (arg: Error) => any
    ): void {
        if (!this._map[key]) {
            this._map[key] = {
                promise,
                successCallback,
                failureCallback
            };
        } else {
            this.logService.debug(`competing promise for key ${key}`);
            delete this._map[key].discardableHolder.successCallback;
            delete this._map[key].discardableHolder.failureCallback;
            this._map[key].promise = promise;
        }

        this._map[key].discardableHolder = {
            successCallback: this._map[key].successCallback,
            failureCallback: this._map[key].failureCallback
        };

        const self = this;
        const p = this._map[key].promise;
        (p as Promise<T>).then(
            function (response: T): void {
                if (this.successCallback) {
                    delete self._map[key];
                    this.successCallback.apply(undefined, [response]);
                } else {
                    self.logService.debug(
                        `aborted successCallback for promise identified by ${key}`
                    );
                }
            }.bind(this._map[key].discardableHolder),
            function (error: Error): void {
                if (this.failureCallback) {
                    delete self._map[key];
                    this.failureCallback.apply(undefined, [error]);
                } else {
                    self.logService.debug(
                        `aborted failureCallback for promise identified by ${key}`
                    );
                }
            }.bind(this._map[key].discardableHolder)
        );
    }

    /**
     * Removes callbacks of promise if exists.
     *
     * Used to remove any pending callbacks when a component is destroyed to prevent memory leaks.
     */
    public clear(key: string): void {
        if (this.exists(key)) {
            delete this._map[key].discardableHolder.successCallback;
            delete this._map[key].discardableHolder.failureCallback;
        }
    }

    private exists(key: string): boolean {
        return this._map[key] ? true : false;
    }
}
