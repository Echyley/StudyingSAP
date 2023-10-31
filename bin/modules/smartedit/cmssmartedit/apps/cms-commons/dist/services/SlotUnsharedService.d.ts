import { CmsitemsRestService, PageContentSlotsService } from 'smarteditcommons';
/**
 * SlotUnsharedService provides methods to interact with the backend for unshared slot information.
 */
export declare class SlotUnsharedService {
    private cmsItemsRestService;
    private pageContentSlotsService;
    private readonly slotUnsharedStatus;
    constructor(cmsItemsRestService: CmsitemsRestService, pageContentSlotsService: PageContentSlotsService);
    /**
     * Checks if the slot is unshared and returns true in case slot is unshared and returns false if it is not.
     * Based on this service method the slot unshared button is shown or hidden for a particular slotId.
     *
     * @param slotId The uid of the slot
     *
     * @returns promise that resolves to true if slot is unshared; Otherwise false.
     */
    isSlotUnshared(slotId: string): Promise<boolean>;
    /**
     * Checks if the slot is shared and returns true in case slot is shared and returns false if it is not.
     * Based on this service method the slot shared button is shown or hidden for a particular slotId
     *
     * @param slotId of the slot
     *
     * @returns promise that resolves to true if the slot is shared; Otherwise false.
     */
    isSlotShared(slotId: string): Promise<boolean>;
    /**
     * This method is used to revert an unshared slot to a shared slot.
     * This operation is immutable.
     *
     * @param slotUuid The uuid of the slot
     */
    revertToSharedSlot(slotUuid: string): Promise<void>;
}
