import { ICMSComponent, IPageContentSlotsComponentsRestService, ISlotVisibilityService } from 'cmscommons';
import { CrossFrameEventService, IPageInfoService, LogService, TypedMap, IComponentHandlerService } from 'smarteditcommons';
/**
 * The slot visibility service provides methods to manage all backend calls and loads an internal
 * structure that provides the necessary data to the slot visibility button and slot visibility component.
 */
export declare class SlotVisibilityService extends ISlotVisibilityService {
    private crossFrameEventService;
    private componentHandlerService;
    private logService;
    private pageInfoService;
    private pageContentSlotsComponentsRestService;
    constructor(crossFrameEventService: CrossFrameEventService, componentHandlerService: IComponentHandlerService, logService: LogService, pageInfoService: IPageInfoService, pageContentSlotsComponentsRestService: IPageContentSlotsComponentsRestService);
    /**
     * Returns the list of hidden components for a given slotId
     *
     * @param slotId the slot id
     *
     * @returns A promise that resolves to a list of hidden components for the slotId
     */
    getHiddenComponents(slotId: string): Promise<ICMSComponent[]>;
    /**
     * Reloads and cache's the pagesContentSlotsComponents for the current page in context.
     * this method can be called when ever a component is added or modified to the slot so that the pagesContentSlotsComponents is re-evaluated.
     *
     * @returns A promise that resolves to the contentSlot - Components [] map for the page in context.
     */
    reloadSlotsInfo(): Promise<TypedMap<ICMSComponent[]>>;
    /**
     * Function that filters the given SlotsToComponentsMap to return only those components that are hidden in the storefront.
     * @param allSlotsToComponentsMap object containing slotId - components list.
     *
     * @return allSlotsToComponentsMap object containing slotId - components list.
     */
    private filterVisibleComponents;
    /**
     * Function to load slot to component map for the current page in context
     */
    private getSlotToComponentsMap;
    private clearComponentsCache;
}
