/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Constant containing the different sync statuses.
 */
export const DEFAULT_SYNCHRONIZATION_STATUSES = {
    UNAVAILABLE: 'UNAVAILABLE',
    IN_SYNC: 'IN_SYNC',
    NOT_SYNC: 'NOT_SYNC',
    IN_PROGRESS: 'IN_PROGRESS',
    SYNC_FAILED: 'SYNC_FAILED'
};

/**
 * Constant containing polling related values
 * * `SLOW_POLLING_TIME` : the slow polling time in milliseconds
 * * `FAST_POLLING_TIME` : the slow polling time in milliseconds
 * * `SPEED_UP` : event used to speed up polling (`syncPollingSpeedUp`)
 * * `SLOW_DOWN` : event used to slow down polling (`syncPollingSlowDown`)
 * * `FAST_FETCH` : event used to trigger a sync fetch (`syncFastFetch`)
 * * `FETCH_SYNC_STATUS_ONCE`: event used to trigger a one time sync (`fetchSyncStatusOnce`)
 *
 */
export const DEFAULT_SYNCHRONIZATION_POLLING = {
    SLOW_POLLING_TIME: 20000,
    FAST_POLLING_TIME: 4000,
    SPEED_UP: 'syncPollingSpeedUp',
    SLOW_DOWN: 'syncPollingSlowDown',
    FAST_FETCH: 'syncFastFetch',
    FETCH_STATUS: 'fetchStatus',
    FETCH_SYNC_STATUS_ONCE: 'fetchSyncStatusOnce'
};

/**
 * Constant containing synchronization events.
 */
export const DEFAULT_SYNCHRONIZATION_EVENT = {
    CATALOG_SYNCHRONIZED: 'CATALOG_SYNCHRONIZED_EVENT'
};
