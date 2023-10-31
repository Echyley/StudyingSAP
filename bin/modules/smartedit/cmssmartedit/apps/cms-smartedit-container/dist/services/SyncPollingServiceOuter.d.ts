import { ISyncJob, ISyncStatus, SynchronizationResourceService } from 'cmscommons';
import { CrossFrameEventService, ICatalogService, IExperienceService, IPageInfoService, IUriContext, SystemEventService, TimerService, TypedMap, LogService, ISyncPollingService, SmarteditBootstrapGateway } from 'smarteditcommons';
export declare class SyncPollingService extends ISyncPollingService {
    private logService;
    private pageInfoService;
    private experienceService;
    private catalogService;
    private synchronizationResourceService;
    private crossFrameEventService;
    private systemEventService;
    private timerService;
    private readonly smarteditBootstrapGateway;
    SYNC_POLLING_THROTTLE: number;
    private syncStatus;
    private triggers;
    private syncPollingTimer;
    private refreshInterval;
    private syncPageObservableMap;
    constructor(logService: LogService, pageInfoService: IPageInfoService, experienceService: IExperienceService, catalogService: ICatalogService, synchronizationResourceService: SynchronizationResourceService, crossFrameEventService: CrossFrameEventService, systemEventService: SystemEventService, timerService: TimerService, smarteditBootstrapGateway: SmarteditBootstrapGateway);
    performSync(array: TypedMap<string>[], uriContext: IUriContext): Promise<ISyncJob>;
    getSyncStatus(pageUUID?: string, uriContext?: IUriContext, forceGetSynchronization?: boolean): Promise<ISyncStatus>;
    fetchSyncStatus(_pageUUID?: string, uriContext?: IUriContext): Promise<ISyncStatus>;
    changePollingSpeed(eventId: string, key?: string): void;
    private fetchPageSynchronization;
    private getPageSlotSyncStatus;
    private stopSync;
    private startSync;
    private initSyncPolling;
    private clearSyncPageObservableMap;
    private clearSyncStatus;
    private getPageUUID;
    private isCurrentPageFromActiveCatalog;
    private getSyncPollingTypeFromInterval;
}
