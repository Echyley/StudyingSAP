/**
 * Constant containing the different sync statuses.
 */
export declare const DEFAULT_SYNCHRONIZATION_STATUSES: {
    UNAVAILABLE: string;
    IN_SYNC: string;
    NOT_SYNC: string;
    IN_PROGRESS: string;
    SYNC_FAILED: string;
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
export declare const DEFAULT_SYNCHRONIZATION_POLLING: {
    SLOW_POLLING_TIME: number;
    FAST_POLLING_TIME: number;
    SPEED_UP: string;
    SLOW_DOWN: string;
    FAST_FETCH: string;
    FETCH_STATUS: string;
    FETCH_SYNC_STATUS_ONCE: string;
};
/**
 * Constant containing synchronization events.
 */
export declare const DEFAULT_SYNCHRONIZATION_EVENT: {
    CATALOG_SYNCHRONIZED: string;
};
