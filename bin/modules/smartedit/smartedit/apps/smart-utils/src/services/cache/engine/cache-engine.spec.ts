/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { promiseUtils, WindowUtils } from '../../../utils';
import { LogService } from '../../log.service';
import { CacheEngine } from './cache-engine';
import { DefaultCacheTiming } from './default-cache-timing';
import { ICacheItem } from './interfaces';

describe('cacheEngine', () => {
    let cacheEngine: CacheEngine;

    let MOCK_ITEM: ICacheItem<{ items: any[] }>;
    const MOCK_DEFAULT_CACHE_TIMING = new DefaultCacheTiming(30000, 15000);
    const MOCK_TAGS: string[] = ['TAG1', 'TAG2'];
    let baseTime: Date;
    let intervalCallback: () => Promise<void>;
    const logService: jasmine.SpyObj<LogService> = jasmine.createSpyObj<LogService>('logService', [
        'warn',
        'debug'
    ]);

    beforeEach(() => {
        baseTime = new Date(2000, 0, 1);

        jasmine.clock().uninstall();
        jasmine.clock().install();
        jasmine.clock().mockDate(baseTime);
        const windowUtils = new WindowUtils();
        spyOn(windowUtils, 'runIntervalOutsideAngular').and.callFake(((
            _callback: () => Promise<void>,
            interval: number
        ) => {
            if (interval === CacheEngine.BACKGROUND_REFRESH_INTERVAL) {
                intervalCallback = _callback;
            }
        }) as any);

        cacheEngine = new CacheEngine(windowUtils, promiseUtils, logService);

        MOCK_ITEM = {
            cache: {
                items: [1234, 'any_value', { id: 1 }]
            },
            expirationAge: 60000,
            evictionTags: MOCK_TAGS,
            id: '==itemId1==',
            refreshAge: 30000,
            timestamp: baseTime.getTime()
        };
    });
    afterEach(function () {
        jasmine.clock().uninstall();
    });

    it('should be able to store and get an item', () => {
        cacheEngine.addItem(MOCK_ITEM, MOCK_DEFAULT_CACHE_TIMING, () => Promise.resolve({} as any));

        expect(cacheEngine.getItemById(MOCK_ITEM.id)).toEqual(MOCK_ITEM);
    });

    it('should not store the same item twice', () => {
        cacheEngine.addItem(MOCK_ITEM, MOCK_DEFAULT_CACHE_TIMING, () => Promise.resolve({} as any));
        cacheEngine.addItem(MOCK_ITEM, MOCK_DEFAULT_CACHE_TIMING, () => Promise.resolve({} as any));

        expect(logService.warn).toHaveBeenCalledWith(
            `CacheEngine - item already exist for id: ${MOCK_ITEM.id}`
        );
    });

    it("GIVEN an item is handled twice and it's cache is not expired, THEN it should return the cache without refreshing it", (done) => {
        const refresh: jasmine.Spy = jasmine
            .createSpy('refresh')
            .and.returnValue(Promise.resolve(null));

        cacheEngine.addItem(MOCK_ITEM, MOCK_DEFAULT_CACHE_TIMING, refresh);

        // first handle, will cache the value
        const promise1 = cacheEngine.handle(MOCK_ITEM);

        const promise2 = cacheEngine.handle(MOCK_ITEM);

        promise1.then((value1) => {
            expect(value1).toEqual(MOCK_ITEM.cache);
            promise2.then((value2) => {
                expect(value2).toEqual(MOCK_ITEM.cache);
                expect(refresh).toHaveBeenCalledTimes(1);
                done();
            });
        });
    });

    it('GIVEN a item does not have a cache and is handled twice, THEN it should refresh the item only once AND return the expected cache', (done) => {
        const expectedReturnCache: string[] = ['item 1', 'item 2'];
        const MOCK_ITEM_NO_CACHE: ICacheItem<any> = {
            cache: null,
            expirationAge: 60000,
            evictionTags: [],
            id: '~~itemId~~',
            refreshAge: 30000,
            timestamp: baseTime.getTime()
        };

        const refresh: jasmine.Spy = jasmine
            .createSpy('refresh')
            .and.returnValue(Promise.resolve(expectedReturnCache));

        cacheEngine.addItem(MOCK_ITEM_NO_CACHE, MOCK_DEFAULT_CACHE_TIMING, refresh);

        const promise1 = cacheEngine.handle(MOCK_ITEM_NO_CACHE);
        const promise2 = cacheEngine.handle(MOCK_ITEM_NO_CACHE);

        promise1.then((value1) => {
            expect(value1).toEqual(expectedReturnCache);
            promise2.then((value2) => {
                expect(value2).toEqual(expectedReturnCache);
                expect(MOCK_ITEM_NO_CACHE.cache).toEqual(expectedReturnCache);
                done();
            });
        });
    });

    it('GIVEN an item has an expired cache, THEN it should refresh the item AND return the expected cache', (done) => {
        const EXPIRATION_AGE = 60000;

        intervalCallback();

        const expectedReturnCache: string[] = ['item 1', 'item 2'];
        const ANY_MOCK_ITEM: ICacheItem<{ randomValue: string }> = {
            cache: {
                randomValue: '4 is a guaranteed random value'
            },
            expirationAge: EXPIRATION_AGE,
            evictionTags: [],
            id: 'skidimarink ❤',
            refreshAge: 30000,
            timestamp: baseTime.getTime()
        };

        const refresh: jasmine.Spy = jasmine
            .createSpy('refresh')
            .and.returnValue(Promise.resolve(expectedReturnCache));

        cacheEngine.addItem(ANY_MOCK_ITEM, MOCK_DEFAULT_CACHE_TIMING, refresh);

        cacheEngine.handle(ANY_MOCK_ITEM).then((value) => {
            expect(value).toEqual(expectedReturnCache);
            expect(refresh).toHaveBeenCalled();
            done();
        });
    });

    it('should be able to evict an item that match a tag', () => {
        cacheEngine.addItem(MOCK_ITEM, MOCK_DEFAULT_CACHE_TIMING, () => Promise.resolve({} as any));

        cacheEngine.evict(...[MOCK_TAGS[0]]);

        expect(cacheEngine.getItemById(MOCK_ITEM.id)).toBeNull();
    });

    it('should be able to evict an item by tags', () => {
        cacheEngine.addItem(MOCK_ITEM, MOCK_DEFAULT_CACHE_TIMING, () => Promise.resolve({} as any));

        cacheEngine.evict(...MOCK_TAGS);

        expect(cacheEngine.getItemById(MOCK_ITEM.id)).toBeNull();
    });

    it('should not evict an item when tags do not match', () => {
        cacheEngine.addItem(MOCK_ITEM, MOCK_DEFAULT_CACHE_TIMING, () => Promise.resolve({} as any));

        cacheEngine.evict(...['ANY_TAG', 'ANOTHER_TAG']);

        expect(cacheEngine.getItemById(MOCK_ITEM.id)).toEqual(MOCK_ITEM);
    });

    it('should automatically refresh an item when required', (done) => {
        const REFRESH_AGE = 10000;
        const expectedReturnCache: { key: string } = {
            key: 'ANY_REFRESHED_DATA'
        };

        jasmine.clock().tick(REFRESH_AGE);

        const ANY_MOCK_ITEM: ICacheItem<{ randomValue: string }> = {
            cache: {
                randomValue: 'random value...'
            },
            expirationAge: REFRESH_AGE * 2,
            evictionTags: [],
            id: 'ANY_ID',
            refreshAge: REFRESH_AGE,
            timestamp: baseTime.getTime()
        };

        const refresh: jasmine.Spy = jasmine
            .createSpy('refresh')
            .and.returnValue(Promise.resolve(expectedReturnCache));
        spyOn(MOCK_DEFAULT_CACHE_TIMING, 'setAge');

        cacheEngine.addItem(ANY_MOCK_ITEM, MOCK_DEFAULT_CACHE_TIMING, refresh);

        intervalCallback().then(() => {
            expect(refresh).toHaveBeenCalled();
            expect(MOCK_DEFAULT_CACHE_TIMING.setAge).toHaveBeenCalledWith(ANY_MOCK_ITEM);
            const item = cacheEngine.getItemById(ANY_MOCK_ITEM.id);
            expect(item).toBeDefined();
            item && expect(item.cache).toEqual(expectedReturnCache);
            expect(ANY_MOCK_ITEM.timestamp).toEqual(new Date().getTime());
            done();
        });
    });

    it('GIVEN an item is automatically refreshed, THEN it should log an Error if the refresh action fail', (done) => {
        const REFRESH_AGE = 10000;

        jasmine.clock().tick(REFRESH_AGE);

        const error = 'error message here';
        const ANY_MOCK_ITEM: ICacheItem<{ randomValue: string }> = {
            cache: {
                randomValue: 'random value...'
            },
            expirationAge: REFRESH_AGE * 2,
            evictionTags: [],
            id: 'ANY_ID',
            refreshAge: REFRESH_AGE,
            timestamp: baseTime.getTime()
        };

        const refresh: jasmine.Spy = jasmine
            .createSpy('refresh')
            .and.returnValue(Promise.reject(error));

        cacheEngine.addItem(ANY_MOCK_ITEM, MOCK_DEFAULT_CACHE_TIMING, refresh);
        cacheEngine.handle(ANY_MOCK_ITEM).catch((reason) => {
            expect(reason).toEqual(error);
        });

        intervalCallback().then(() => {
            expect(refresh).toHaveBeenCalled();
            expect(logService.debug).toHaveBeenCalledWith(
                `CacheEngine - unable to refresh cache for id: ${ANY_MOCK_ITEM.id}`,
                `${error}`
            );
            expect(ANY_MOCK_ITEM.cache).toBeUndefined();
            done();
        });
    });
});
