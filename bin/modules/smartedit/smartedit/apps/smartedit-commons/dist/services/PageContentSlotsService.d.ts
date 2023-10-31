import { CrossFrameEventService } from './crossFrame/CrossFrameEventService';
import { IPageInfoService, IRestServiceFactory } from './interfaces';
export interface IPageContentSlot {
    pageId: string;
    position: string;
    slotId: string;
    slotShared?: boolean;
    slotStatus?: string;
}
export interface IPageContentSlotsResponse {
    pageContentSlotList: IPageContentSlot[];
}
/**
 * PageContentSlotsServiceModule provides methods to load and act on the contentSlots for the page loaded in the storefront.
 */
export declare class PageContentSlotsService {
    private readonly crossFrameEventService;
    private readonly pageInfoService;
    private readonly resource;
    private pageContentSlots;
    constructor(restServiceFactory: IRestServiceFactory, crossFrameEventService: CrossFrameEventService, pageInfoService: IPageInfoService);
    /**
     * This function fetches all the slots of the loaded page.
     *
     * @returns promise that resolves to a collection of content slots information for the loaded page.
     */
    getPageContentSlots(): Promise<IPageContentSlot[]>;
    /**
     * Retrieves the slot status of the proved slotId. It can be one of TEMPLATE, PAGE and OVERRIDE.
     *
     * @param slotId of the slot
     *
     * @returns promise that resolves to the status of the slot.
     */
    getSlotStatus(slotId: string): Promise<string | null>;
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
     * Fetches content slot list from API
     */
    private reloadPageContentSlots;
}
