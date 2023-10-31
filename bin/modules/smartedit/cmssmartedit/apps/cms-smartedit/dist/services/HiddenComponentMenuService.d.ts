import { ICMSComponent } from 'cmscommons';
import { IHiddenComponentMenu } from 'cmssmartedit/services/IHiddenComponentMenu';
import { SlotContainerService } from 'cmssmartedit/services/SlotContainerService';
import { IContextualMenuService } from 'smarteditcommons';
/**
 * This service is used to retrieve menu items that are available to be used with hidden components.
//  */
export declare class HiddenComponentMenuService {
    private contextualMenuService;
    private slotContainerService;
    private readonly MENU_ITEM_EXTERNAL;
    private readonly MENU_ITEM_CLONE;
    private readonly MENU_ITEM_REMOVE;
    private allowedItems;
    constructor(contextualMenuService: IContextualMenuService, slotContainerService: SlotContainerService);
    /**
     * This method is used to set the list of items that can be used with hidden components.
     *
     * @param itemsToAllow The ID of the menu items that can be used with hidden components.
     *
     */
    allowItemsInHiddenComponentMenu(itemsToAllow: string[]): void;
    /**
     * This method removes a provided set of allowed menu items if previously allowed.
     *
     * @param itemsToDisallow An array containing the ID's of the menu items that cannot be used any longer with hidden
     * components.
     *
     */
    removeAllowedItemsInHiddenComponentMenu(itemsToDisallow: string[]): void;
    /**
     * This method retrieves the list of IDs of the menu items that can be used with hidden components.
     *
     * @returns The list of IDs of the menu items that can be used with hidden components.
     *
     */
    getAllowedItemsInHiddenComponentMenu(): string[];
    /**
     * This method is used to retrieve the menu items available to be used in the provided component. To do so,
     * this method retrieves contextual menu items available for the provided component and filters out the ones that cannot
     * be used in hidden components. For example, assuming that a visible component has 'drag and drop' and 'remove'
     * contextual menu items, if the component is hidden it should only have the remove button available, since the
     * drag and drop operation is meaningless if the component is hidden. Hence, this service will retrieve only
     * the remove item.
     *
     * @param component The hidden component for which to retrieve its menu items.
     * @param slotId The SmartEdit id of the slot where the component is located.
     *
     * @returns Promise that resolves to an array of contextual menu items available for the component
     * provided.
     */
    getItemsForHiddenComponent(component: ICMSComponent, slotId: string): Promise<IHiddenComponentMenu>;
    private validateComponent;
    private setDefaultItemsAllowed;
    private buildComponentInfo;
    private getAllowedItemsForComponent;
}
