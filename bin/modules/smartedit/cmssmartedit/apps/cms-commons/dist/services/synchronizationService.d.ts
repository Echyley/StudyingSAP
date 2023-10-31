import { TranslateService } from '@ngx-translate/core';
import { CrossFrameEventService, IAlertService, IAuthenticationService, IRestServiceFactory, Timer, TimerService } from 'smarteditcommons';
import { ISyncJob } from '../dtos';
export declare const CATALOG_SYNC_INTERVAL_IN_MILLISECONDS = 5000;
export interface DiscardableTimer extends Timer {
    discardWhenNextSynced?: boolean;
}
export declare class SynchronizationService {
    private restServiceFactory;
    private timerService;
    private translateService;
    private alertService;
    private authenticationService;
    private crossFrameEventService;
    private readonly BASE_URL;
    private readonly SYNC_JOB_INFO_BY_TARGET_URI;
    private readonly SYNC_JOB_INFO_BY_SOURCE_AND_TARGET_URI;
    private intervalHandle;
    private syncRequested;
    private syncJobInfoByTargetRestService;
    private syncJobInfoBySourceAndTargetRestService;
    constructor(restServiceFactory: IRestServiceFactory, timerService: TimerService, translateService: TranslateService, alertService: IAlertService, authenticationService: IAuthenticationService, crossFrameEventService: CrossFrameEventService);
    /**
     * This method is used to synchronize a catalog between two catalog versions.
     * It sends the SYNCHRONIZATION_EVENT.CATALOG_SYNCHRONIZED event if successful.
     */
    updateCatalogSync(catalog: ISyncJob): Promise<ISyncJob>;
    /**
     * This method is used to get the status of the last synchronization job between two catalog versions.
     */
    getCatalogSyncStatus(catalog: ISyncJob): Promise<ISyncJob>;
    /**
     * This method is used to get the status of the last synchronization job between two catalog versions.
     */
    getSyncJobInfoBySourceAndTarget(catalog: ISyncJob): Promise<ISyncJob>;
    /**
     * This method is used to get the status of the last synchronization job.
     */
    getLastSyncJobInfoByTarget(catalog: ISyncJob): Promise<ISyncJob>;
    /**
     * This method starts the auto synchronization status update in a catalog between two given catalog versions.
     */
    startAutoGetSyncData(catalog: ISyncJob, callback: (job: ISyncJob) => void): void;
    /**
     * This method stops the auto synchronization status update in a catalog between two given catalog versions
     * or it marks the job with discardWhenNextSynced = true if there is a synchronization in progress. If the job is
     * marked with discardWhenNextSynced = true then it will be discarded when the synchronization process is finished or aborted.
     */
    stopAutoGetSyncData(catalog: ISyncJob): void;
    private _autoSyncCallback;
    /**
     * Method sends SYNCHRONIZATION_EVENT.CATALOG_SYNCHRONIZED event when synchronization process is finished.
     * It also stops polling if the job is not needed anymore (i.e. was marked with discardWhenNextSynced = true).
     */
    private syncRequestedCallback;
    private catalogSyncInProgress;
    private catalogSyncFinished;
    private catalogSyncAborted;
    private removeCatalogSyncRequest;
    private addCatalogSyncRequest;
    private _getJobKey;
}
