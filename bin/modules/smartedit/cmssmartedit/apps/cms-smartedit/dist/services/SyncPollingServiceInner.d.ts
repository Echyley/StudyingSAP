import { SystemEventService, ISyncPollingService } from 'smarteditcommons';
export declare class SyncPollingService extends ISyncPollingService {
    private systemEventService;
    constructor(systemEventService: SystemEventService);
    registerSyncPollingEvents(): void;
}
