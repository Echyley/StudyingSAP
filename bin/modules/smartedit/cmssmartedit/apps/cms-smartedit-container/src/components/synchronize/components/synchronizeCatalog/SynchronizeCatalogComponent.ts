/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Input,
    OnDestroy,
    OnInit,
    ViewRef
} from '@angular/core';
import { SynchronizationService, ISyncJob, JOB_STATUS } from 'cmscommons';
import { take } from 'rxjs/operators';
import {
    EVENT_CONTENT_CATALOG_UPDATE,
    ICatalog,
    ICatalogVersion,
    IConfirmationModalService,
    L10nPipe,
    MultiNamePermissionContext,
    Nullable,
    SystemEventService,
    TypedMap,
    USER_TRACKING_FUNCTIONALITY,
    IUserTrackingService
} from 'smarteditcommons';

interface SyncJobStatus {
    syncStartTime: string;
    syncEndTime: string;
    status: JOB_STATUS;
    source: string;
    target: string;
}

@Component({
    selector: 'se-synchronize-catalog',
    templateUrl: './SynchronizeCatalogComponent.html',
    styleUrls: ['./SynchronizeCatalogComponent.scss'],
    providers: [L10nPipe],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SynchronizeCatalogComponent implements OnInit, OnDestroy {
    @Input() catalog: ICatalog;
    @Input() catalogVersion: ICatalogVersion;
    @Input() activeCatalogVersion: ICatalogVersion;

    public syncJobStatus: SyncJobStatus;
    public targetCatalogVersion: string;
    public sourceCatalogVersion: Nullable<string>;
    public syncCatalogPermission: MultiNamePermissionContext[];
    public catalogDto: ISyncJob;

    constructor(
        private synchronizationService: SynchronizationService,
        private confirmationModalService: IConfirmationModalService,
        private systemEventService: SystemEventService,
        private l10nPipe: L10nPipe,
        private cdr: ChangeDetectorRef,
        private userTrackingService: IUserTrackingService
    ) {}

    async ngOnInit(): Promise<void> {
        this.syncJobStatus = {
            syncStartTime: '',
            syncEndTime: '',
            status: null,
            source: '',
            target: ''
        };

        this.targetCatalogVersion = this.activeCatalogVersion.version;

        this.sourceCatalogVersion = !this.catalogVersion.active
            ? this.catalogVersion.version
            : null;

        this.syncCatalogPermission = [
            {
                names: ['se.sync.catalog'],
                context: {
                    catalogId: this.catalog.catalogId,
                    catalogVersion: this.sourceCatalogVersion,
                    targetCatalogVersion: this.targetCatalogVersion
                }
            }
        ];

        // Catalog works as a DTO. Thus it needs the target and source catalog versions.
        this.catalogDto = {
            catalogId: this.catalog.catalogId,
            targetCatalogVersion: this.targetCatalogVersion,
            sourceCatalogVersion: this.sourceCatalogVersion
        } as ISyncJob;

        // start auto updating synchronization data
        this.synchronizationService.startAutoGetSyncData(this.catalogDto, (job: ISyncJob) =>
            this.updateSyncStatusData(job)
        );

        // call the update for the first time.
        await this.callGetSyncData();
    }

    ngOnDestroy(): void {
        this.synchronizationService.stopAutoGetSyncData(this.catalogDto);
    }

    public isSyncJobFinished(): boolean {
        return this.syncJobStatus.status === JOB_STATUS.FINISHED;
    }

    public isSyncJobInProgress(): boolean {
        return (
            this.syncJobStatus.status === JOB_STATUS.RUNNING ||
            this.syncJobStatus.status === JOB_STATUS.UNKNOWN
        );
    }

    public isSyncJobFailed(): boolean {
        return (
            this.syncJobStatus.status === JOB_STATUS.ERROR ||
            this.syncJobStatus.status === JOB_STATUS.FAILURE
        );
    }

    public isSyncButtonEnabled(): boolean {
        return !this.isSyncJobInProgress();
    }

    public async syncCatalog(): Promise<void> {
        this.userTrackingService.trackingUserAction(USER_TRACKING_FUNCTIONALITY.NAVIGATION, 'Sync');
        const catalogName = await this.translateCatalogName(this.catalog.name);
        await this.confirmationModalService
            .confirm({
                title: 'se.sync.confirmation.title',
                description: 'se.sync.confirm.msg',
                descriptionPlaceholders: {
                    catalogName
                }
            })
            .catch(() => Promise.reject());

        // when confirmed
        const job = await this.synchronizationService.updateCatalogSync(this.catalogDto);
        this.updateSyncStatusData(job);

        this.systemEventService.publishAsync(EVENT_CONTENT_CATALOG_UPDATE, job);
    }

    private async translateCatalogName(catalogName: TypedMap<string>): Promise<string> {
        return this.l10nPipe.transform(catalogName).pipe(take(1)).toPromise();
    }

    // Auto Get
    private async callGetSyncData(): Promise<void> {
        const syncStatus = await this.synchronizationService.getCatalogSyncStatus(this.catalogDto);
        this.updateSyncStatusData(syncStatus);
    }

    private updateSyncStatusData(syncStatus: ISyncJob): void {
        this.syncJobStatus = {
            syncStartTime: syncStatus.creationDate,
            syncEndTime: syncStatus.endDate,
            status: syncStatus.syncStatus,
            source: syncStatus.sourceCatalogVersion ? syncStatus.sourceCatalogVersion : '',
            target: syncStatus.targetCatalogVersion ? syncStatus.targetCatalogVersion : ''
        };

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }
}
