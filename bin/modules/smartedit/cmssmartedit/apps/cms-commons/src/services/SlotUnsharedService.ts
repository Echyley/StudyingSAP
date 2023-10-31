/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { CmsitemsRestService, PageContentSlotsService } from 'smarteditcommons';

/**
 * SlotUnsharedService provides methods to interact with the backend for unshared slot information.
 */
@Injectable()
export class SlotUnsharedService {
    private readonly slotUnsharedStatus = 'OVERRIDE';

    constructor(
        private cmsItemsRestService: CmsitemsRestService,
        private pageContentSlotsService: PageContentSlotsService
    ) {}

    /**
     * Checks if the slot is unshared and returns true in case slot is unshared and returns false if it is not.
     * Based on this service method the slot unshared button is shown or hidden for a particular slotId.
     *
     * @param slotId The uid of the slot
     *
     * @returns promise that resolves to true if slot is unshared; Otherwise false.
     */
    public async isSlotUnshared(slotId: string): Promise<boolean> {
        const slotStatus = await this.pageContentSlotsService.getSlotStatus(slotId);

        return slotStatus === this.slotUnsharedStatus;
    }

    /**
     * Checks if the slot is shared and returns true in case slot is shared and returns false if it is not.
     * Based on this service method the slot shared button is shown or hidden for a particular slotId
     *
     * @param slotId of the slot
     *
     * @returns promise that resolves to true if the slot is shared; Otherwise false.
     */
    public isSlotShared(slotId: string): Promise<boolean> {
        return this.pageContentSlotsService.isSlotShared(slotId);
    }

    /**
     * This method is used to revert an unshared slot to a shared slot.
     * This operation is immutable.
     *
     * @param slotUuid The uuid of the slot
     */
    public revertToSharedSlot(slotUuid: string): Promise<void> {
        return this.cmsItemsRestService.delete(slotUuid);
    }
}
