import { BehaviorSubject } from 'rxjs';
import { ContextualMenu } from 'smartedit/services/ContextualMenu';
import { IContextualMenuButton, IContextualMenuConfiguration, IContextualMenuService, PriorityService, SystemEventService, TypedMap } from 'smarteditcommons';
/**
 * The ContextualMenuService is used to add contextual menu items for each component.
 *
 * To add items to the contextual menu, you must call the addItems method of the contextualMenuService and pass a map
 * of the component-type array of contextual menu items mapping. The component type names are the keys in the mapping.
 * The component name can be the full name of the component type, an ant-like wildcard (such as  *middle*Suffix), or a
 * valid regex that includes or excludes a set of component types.
 *
 */
export declare class ContextualMenuService implements IContextualMenuService {
    private priorityService;
    private systemEventService;
    onContextualMenuItemsAdded: BehaviorSubject<string>;
    private _contextualMenus;
    private _featuresList;
    constructor(priorityService: PriorityService, systemEventService: SystemEventService);
    /**
     * The method called to add contextual menu items to component types in the SmartEdit application.
     * The contextual menu items are then retrieved by the contextual menu decorator to wire the set of menu items to the specified component.
     *
     * ### Example:
     *
     *
     *      contextualMenuService.addItems({
     *          '.*Component': [{
     *              key: 'itemKey',
     *              i18nKey: 'CONTEXTUAL_MENU',
     *              condition: function(componentType, componentId) {
     *                  return componentId === 'ComponentType';
     *              },
     *              callback: function(componentType, componentId) {
     *                  alert('callback for ' + componentType + "_" + componentId);
     *              },
     *              displayClass: 'democlass',
     *              iconIdle: '.../icons/icon.png',
     *              iconNonIdle: '.../icons/icon.png',
     *              }]
     *          });
     *
     *
     * @param  contextualMenuItemsMap A map of componentType regular expressions to list of IContextualMenuButton contextual menu items
     *
     * The object contains a list that maps component types to arrays of IContextualMenuButton contextual menu items. The mapping is a key-value pair.
     * The key is the name of the component type, for example, Simple Responsive Banner Component, and the value is an array of IContextualMenuButton contextual menu items, like add, edit, localize, etc.
     */
    addItems(contextualMenuItemsMap: TypedMap<IContextualMenuButton[]>): void;
    /**
     * This method removes the menu items identified by the provided key.
     *
     * @param itemKey The key that identifies the menu items to remove.
     */
    removeItemByKey(itemKey: string): void;
    /**
     * Verifies whether the itemKey has already been added to contextual menu list.
     *
     * @param itemKey The item key to verify.
     *
     * @returns Return true if itemKey exists in the contextual menu list, false otherwise.
     */
    containsItem(itemKey: string): boolean;
    /**
     * Will return an array of contextual menu items for a specific component type.
     * For each key in the contextual menus' object, the method converts each component type into a valid regex using the regExpFactory of the function module and then compares it with the input componentType and, if matched, will add it to an array and returns the array.
     *
     * @param componentType The type code of the selected component
     *
     * @returns An array of contextual menu items assigned to the type.
     *
     */
    getContextualMenuByType(componentType: string): IContextualMenuButton[];
    /**
     * Returns an object that contains a list of contextual menu items that are displayed in the menu and menu items that are added to the More … options.
     *
     * The returned object contains two arrays. The first array contains the menu items that are displayed in the menu. The display limit size (iLeftBtns) specifies
     * the maximum number of items that can be displayed in the menu. The other array contains the menu items that are available under the More... options.
     * This method decides which items to send to each array based on their priority. Items with the lowest priority are displayed in the menu. The remaining
     * items are added to the More... menu. Items that do not have a priority are automatically assigned a default priority.
     *
     * @param configuration
     * @returns A promise that resolves to an array of contextual menu items assigned to the component type.
     *
     * The returned object contains the following properties
     * - leftMenuItems : An array of menu items that can be displayed on the component.
     * - moreMenuItems : An array of menu items that are available under the more menu items action.
     *
     */
    getContextualMenuItems(configuration: IContextualMenuConfiguration): Promise<ContextualMenu>;
    /**
     * This method can be used to ask SmartEdit to retrieve again the list of items in the enabled contextual menus.
     */
    refreshMenuItems(): void;
    private _getFeaturesList;
    private _validateItem;
    private _getUniqueItemArray;
    private _initContextualMenuItems;
}
