/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Injectable } from '@angular/core';
import { Deferred, PromiseUtils, WindowUtils } from '../../../utils';
import { LogService } from '../../log.service';

import { ICacheItem, ICacheTiming, IMetadata } from './interfaces';

/** @internal */
export interface ICacheItemRegistry {
    item: ICacheItem<any>;
    cacheTiming: ICacheTiming;
    completed: boolean;
    processing: boolean;
    defer: Deferred<any>;
    refresh<T>(): Promise<T>;
}

/** @internal */
@Injectable()
export class CacheEngine {
    public static readonly BACKGROUND_REFRESH_INTERVAL: number = 10000;
    private cachedItemsRegistry: ICacheItemRegistry[] = [];

    constructor(
        private windowUtils: WindowUtils,
        private promiseUtils: PromiseUtils,
        private logService: LogService
    ) {
        this.startBackgroundMonitoringJob();
    }

    public addItem(
        item: ICacheItem<any>,
        cacheTiming: ICacheTiming,
        refresh: <T>() => Promise<T>
    ): void {
        if (this.getItemIndex(item) === -1) {
            this.cachedItemsRegistry.push({
                item,
                cacheTiming,
                refresh,
                completed: false,
                processing: false,
                defer: this.promiseUtils.defer()
            });
        } else {
            this.logService.warn(`CacheEngine - item already exist for id: ${item.id}`);
        }
    }

    public getItemById(id: string): ICacheItem<any> | null {
        const match = this.cachedItemsRegistry.find((obj) => obj.item.id === id);
        return match ? match.item : null;
    }

    public handle<T>(item: ICacheItem<any>): Promise<T> {
        const obj = this.cachedItemsRegistry[this.getItemIndex(item)];
        if (obj.completed && !this.hasExpired(item)) {
            obj.defer.resolve(item.cache);
        } else if (!obj.processing) {
            obj.processing = true;
            this.refreshCache(obj);
        }
        return obj.defer.promise;
    }

    public evict(...tags: string[]): void {
        tags.forEach((tag) => {
            this.cachedItemsRegistry
                .filter((obj) => obj.item.evictionTags.indexOf(tag) > -1)
                .forEach((obj) => this.cachedItemsRegistry.splice(this.getItemIndex(obj.item), 1));
        });
    }

    // regularly go though cache data and call prebound methods to refresh data when needed.
    protected startBackgroundMonitoringJob(): void {
        this.windowUtils.runIntervalOutsideAngular(
            () =>
                Promise.all(
                    this.cachedItemsRegistry
                        .filter((obj) => this.needRefresh(obj.item))
                        .map((obj) => this.refreshCache(obj))
                ),
            CacheEngine.BACKGROUND_REFRESH_INTERVAL
        );
    }

    protected refreshCache<T>(obj: ICacheItemRegistry): Promise<T | void> {
        return obj.refresh<IMetadata>().then(
            (value: IMetadata) => {
                // TODO: read value.metadata to refresh expiry/refresh ages.
                obj.cacheTiming.setAge(obj.item);
                obj.item.cache = value;
                obj.item.timestamp = new Date().getTime();
                obj.completed = true;
                obj.processing = false;
                obj.defer.resolve(value);
            },
            (e: any) => {
                this.logService.debug(
                    `CacheEngine - unable to refresh cache for id: ${obj.item.id}`,
                    e
                );
                delete obj.item.cache;
                obj.defer.reject(e);
            }
        );
    }

    private hasExpired(item: ICacheItem<any>): boolean {
        return item.timestamp + item.expirationAge <= new Date().getTime();
    }

    private needRefresh(item: ICacheItem<any>): boolean {
        return item.timestamp + item.refreshAge <= new Date().getTime();
    }

    private getItemIndex(item: ICacheItem<any>): number {
        return this.cachedItemsRegistry.findIndex((o) => o.item.id === item.id);
    }
}
