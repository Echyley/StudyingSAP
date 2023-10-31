import { ChangeDetectorRef, OnDestroy, OnInit } from '@angular/core';
import { SynchronizationService, ISyncJob, JOB_STATUS } from 'cmscommons';
import { ICatalog, ICatalogVersion, IConfirmationModalService, L10nPipe, MultiNamePermissionContext, Nullable, SystemEventService, IUserTrackingService } from 'smarteditcommons';
interface SyncJobStatus {
    syncStartTime: string;
    syncEndTime: string;
    status: JOB_STATUS;
    source: string;
    target: string;
}
export declare class SynchronizeCatalogComponent implements OnInit, OnDestroy {
    private synchronizationService;
    private confirmationModalService;
    private systemEventService;
    private l10nPipe;
    private cdr;
    private userTrackingService;
    catalog: ICatalog;
    catalogVersion: ICatalogVersion;
    activeCatalogVersion: ICatalogVersion;
    syncJobStatus: SyncJobStatus;
    targetCatalogVersion: string;
    sourceCatalogVersion: Nullable<string>;
    syncCatalogPermission: MultiNamePermissionContext[];
    catalogDto: ISyncJob;
    constructor(synchronizationService: SynchronizationService, confirmationModalService: IConfirmationModalService, systemEventService: SystemEventService, l10nPipe: L10nPipe, cdr: ChangeDetectorRef, userTrackingService: IUserTrackingService);
    ngOnInit(): Promise<void>;
    ngOnDestroy(): void;
    isSyncJobFinished(): boolean;
    isSyncJobInProgress(): boolean;
    isSyncJobFailed(): boolean;
    isSyncButtonEnabled(): boolean;
    syncCatalog(): Promise<void>;
    private translateCatalogName;
    private callGetSyncData;
    private updateSyncStatusData;
}
export {};
