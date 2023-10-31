/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { ISyncJob, ISyncStatus } from 'cmscommons';
import { IUriContext, Nullable, TypedMap, ISyncPollingService } from 'smarteditcommons';

@Injectable()
export class SlotSynchronizationService {
    constructor(private syncPollingService: ISyncPollingService) {}

    /**
     * Returns the sync status for the slot.
     * @param pageUUID - the page where the slot is situated.
     * @param slotId - the slot id for which to retrieve the sync status.
     * @returns the sync status object, or null if not found.
     */
    public async getSyncStatus(pageUUID: string, slotId: string): Promise<Nullable<ISyncStatus>> {
        const syncStatus = await this.syncPollingService.getSyncStatus(pageUUID);
        const syncFromSelected = this.findSlotStatus(syncStatus.selectedDependencies || [], slotId);
        if (syncFromSelected !== null) {
            syncFromSelected.fromSharedDependency = false;
            return syncFromSelected;
        } else {
            const syncFromShared = this.findSlotStatus(syncStatus.sharedDependencies || [], slotId);
            if (syncFromShared !== null) {
                syncFromShared.fromSharedDependency = true;
                return syncFromShared;
            }
        }
        return null;
    }

    public performSync(array: TypedMap<string>[], uriContext: IUriContext): Promise<ISyncJob> {
        return this.syncPollingService.performSync(array, uriContext);
    }

    /**
     * Verifies whether the sync status exists. The sync status for the slot does not exists when
     * the slot comes from a parent catalog in multicountry environment.
     * @param syncStatus - the object to verify.
     * @returns true if the sync status exists, false otherwise.
     */
    public syncStatusExists(syncStatus: ISyncStatus): boolean {
        return !!syncStatus;
    }

    /**
     * Returns the slot sync status from the list of dependencies by slot id.
     * @param dependencies - the list of dependencies to verify
     * @param slotId - the slot for which to find a sync status
     * @returns the sync status or null if cannot be find.
     */
    private findSlotStatus(dependencies: ISyncStatus[], slotId: string): Nullable<ISyncStatus> {
        return dependencies.find((slot) => slot.name === slotId) || null;
    }
}
