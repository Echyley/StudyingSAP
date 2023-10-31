import { ISyncJob, ISyncStatus } from 'cmscommons';
import { IUriContext, Nullable, TypedMap, ISyncPollingService } from 'smarteditcommons';
export declare class SlotSynchronizationService {
    private syncPollingService;
    constructor(syncPollingService: ISyncPollingService);
    /**
     * Returns the sync status for the slot.
     * @param pageUUID - the page where the slot is situated.
     * @param slotId - the slot id for which to retrieve the sync status.
     * @returns the sync status object, or null if not found.
     */
    getSyncStatus(pageUUID: string, slotId: string): Promise<Nullable<ISyncStatus>>;
    performSync(array: TypedMap<string>[], uriContext: IUriContext): Promise<ISyncJob>;
    /**
     * Verifies whether the sync status exists. The sync status for the slot does not exists when
     * the slot comes from a parent catalog in multicountry environment.
     * @param syncStatus - the object to verify.
     * @returns true if the sync status exists, false otherwise.
     */
    syncStatusExists(syncStatus: ISyncStatus): boolean;
    /**
     * Returns the slot sync status from the list of dependencies by slot id.
     * @param dependencies - the list of dependencies to verify
     * @param slotId - the slot for which to find a sync status
     * @returns the sync status or null if cannot be find.
     */
    private findSlotStatus;
}
