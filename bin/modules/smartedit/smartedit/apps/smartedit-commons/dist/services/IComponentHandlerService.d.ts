/// <reference types="jquery" />
/**
 * Handles all get/set component related operations
 */
export declare abstract class IComponentHandlerService {
    private readonly yjQuery;
    constructor(yjQuery: JQueryStatic);
    reloadInner(): Promise<void>;
    /**
     * Determines whether the component identified by the provided smarteditComponentId and smarteditComponentType
     * resides in a different catalog version to the one of the current page.
     *
     * @param smarteditComponentId the component id as per the smartEdit contract with the storefront
     * @param smarteditComponentType the component type as per the smartEdit contract with the storefront
     *
     * @returns flag that evaluates to true if the component resides in a catalog version different to
     * the one of the current page.  False otherwise.
     */
    isExternalComponent(smarteditComponentId: string, smarteditComponentType: string): Promise<boolean>;
    /**
     * Retrieves a handler on the smartEdit overlay div
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @returns The #smarteditoverlay JQuery Element
     */
    getOverlay(): JQuery;
    /**
     * determines whether the overlay is visible
     * This method can only be invoked from the smartEdit application and not the smartEdit iframe.
     *
     * @returns  true if the overlay is visible
     */
    isOverlayOn(): boolean;
    /**
     * Retrieves the yjQuery wrapper around a smartEdit component identified by its smartEdit id, smartEdit type and an optional class
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @param smarteditComponentId the component id as per the smartEdit contract with the storefront
     * @param smarteditComponentType the component type as per the smartEdit contract with the storefront
     * @param smarteditSlotId the slot id of the slot containing the component as per the smartEdit contract with the storefront
     * @param cssClass the css Class to further restrict the search on. This parameter is optional.
     *
     * @returns  a yjQuery object wrapping the searched component
     */
    getComponentUnderSlot(smarteditComponentId: string, smarteditComponentType: string, smarteditSlotId: string, cssClass?: string): JQuery;
    /**
     * Retrieves the yjQuery wrapper around a smartEdit component identified by its smartEdit id, smartEdit type and an optional class
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @param smarteditComponentId the component id as per the smartEdit contract with the storefront
     * @param smarteditComponentType the component type as per the smartEdit contract with the storefront
     * @param cssClass the css Class to further restrict the search on. This parameter is optional.
     *
     * @returns a yjQuery object wrapping the searched component
     */
    getComponent(smarteditComponentId: string, smarteditComponentType: string, cssClass?: string): JQuery;
    /**
     * Retrieves the yjQuery wrapper around a smartEdit component of the original storefront layer identified by its smartEdit id, smartEdit type and slot ID
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @param smarteditComponentId the component id as per the smartEdit contract with the storefront
     * @param smarteditComponentType the component type as per the smartEdit contract with the storefront
     * @param slotId the ID of the slot within which the component resides
     *
     * @returns a yjQuery object wrapping the searched component
     */
    getOriginalComponentWithinSlot(smarteditComponentId: string, smarteditComponentType: string, slotId: string): JQuery;
    /**
     * Retrieves the yjQuery wrapper around a smartEdit component of the original storefront layer identified by its smartEdit id, smartEdit type
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @param smarteditComponentId the component id as per the smartEdit contract with the storefront
     * @param smarteditComponentType the component type as per the smartEdit contract with the storefront
     *
     * @returns a yjQuery object wrapping the searched component
     */
    getOriginalComponent(smarteditComponentId: string, smarteditComponentType: string): JQuery;
    /**
     * Retrieves the yjQuery wrapper around a smartEdit component of the overlay layer identified by its smartEdit id, smartEdit type and slot ID
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @param smarteditComponentId the component id as per the smartEdit contract with the storefront
     * @param smarteditComponentType the component type as per the smartEdit contract with the storefront
     * @param slotId the ID of the slot within which the component resides
     *
     * @returns a yjQuery object wrapping the searched component
     */
    getOverlayComponentWithinSlot(smarteditComponentId: string, smarteditComponentType: string, slotId: string): JQuery;
    /**
     * Retrieves the yjQuery wrapper around the smartEdit component of the overlay layer corresponding to the storefront layer component passed as argument
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @param originalComponent the DOM element in the storefront layer
     *
     * @returns a yjQuery object wrapping the searched component
     */
    getOverlayComponent(originalComponent: JQuery): JQuery;
    /**
     * Retrieves the yjQuery wrapper around a smartEdit component of the overlay div identified by its smartEdit id, smartEdit type
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @param smarteditComponentId the component id as per the smartEdit contract with the storefront
     * @param smarteditComponentType the component type as per the smartEdit contract with the storefront
     *
     * @returns a yjQuery object wrapping the searched component
     *
     */
    getComponentInOverlay(smarteditComponentId: string, smarteditComponentType: string): JQuery;
    /**
     * Retrieves the the slot ID for a given element
     *
     * @param component the yjQuery component for which to search the parent
     *
     * @returns the slot ID for that particular component
     */
    getParentSlotForComponent(component: HTMLElement | JQuery): string;
    /**
     * Retrieves the position of a component within a slot based on visible components in the given slotId.
     *
     * @param slotId the slot id as per the smartEdit contract with the storefront
     * @param componentId the component id as per the smartEdit contract with the storefront
     *
     * @returns the position of the component within a slot
     */
    getComponentPositionInSlot(slotId: string, componentId: string): number;
    /**
     * Retrieves the yjQuery wrapper around a list of smartEdit components contained in the slot identified by the given slotId.
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @param slotId the ID of the slot within which the component resides
     *
     * @returns The list of searched components yjQuery objects
     */
    getOriginalComponentsWithinSlot(slotId: string): JQuery[];
    /**
     * Gets the id that is relevant to be able to perform slot related operations for this components
     * It typically is CONTAINER_ID_ATTRIBUTE when applicable and defaults to ID_ATTRIBUTE
     *
     * @param component the yjQuery component for which to get the id
     *
     * @returns the slot operations related id
     */
    getSlotOperationRelatedId(component: HTMLElement | JQuery): string;
    /**
     * Gets the id that is relevant to be able to perform slot related operations for this components
     * It typically is {@link seConstantsModule.CONTAINER_ID_ATTRIBUTE} when applicable and defaults to {@link seConstantsModule.ID_ATTRIBUTE}
     *
     * @param component the yjQuery component for which to get the Uuid
     *
     * @returns the slot operations related Uuid
     */
    getSlotOperationRelatedUuid(component: HTMLElement | JQuery): string;
    /**
     * Retrieves the direct smartEdit component parent of a given component.
     * The parent is fetched in the same layer (original storefront or smartEdit overlay) as the child
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @param component the yjQuery component for which to search a parent
     *
     * @returns a yjQuery object wrapping the smae-layer parent component
     */
    getParent(component: HTMLElement | JQuery): JQuery;
    /**
     * Returns the closest parent (or self) being a smartEdit component
     *
     * @param component the DOM/yjQuery element for which to search a parent
     *
     * @returns The closest closest parent (or self) being a smartEdit component
     */
    getClosestSmartEditComponent(component: HTMLElement | JQuery): JQuery;
    /**
     * Determines whether a DOM/yjQuery element is a smartEdit component
     *
     * @param component the DOM/yjQuery element for which to check if it's a SmartEdit component
     *
     * @returns true if DOM/yjQuery element is a smartEdit component
     */
    isSmartEditComponent(component: HTMLElement | JQuery): boolean;
    /**
     * Sets the smartEdit component id of a given component
     *
     * @param component the yjQuery component for which to set the id
     * @param id the id to be set
     *
     * @returns component the yjQuery component
     */
    setId(component: HTMLElement | JQuery, id: string): JQuery;
    /**
     * Gets the smartEdit component id of a given component
     *
     * @param component the yjQuery component for which to get the id
     *
     * @returns the component id
     */
    getId(component: HTMLElement | JQuery): string;
    /**
     * Gets the smartEdit component id of a given component
     *
     * @param component the yjQuery component for which to get the id
     *
     * @returns the component id
     */
    getUuid(component: HTMLElement | JQuery): string;
    /**
     * Gets the smartEdit component id of a given component
     *
     * @param component the yjQuery component for which to get the id
     *
     * @returns the component id
     */
    getCatalogVersionUuid(component: HTMLElement | JQuery): string;
    /**
     * Sets the smartEdit component type of a given component
     *
     * @param component the yjQuery component for which to set the type
     * @param type the type to be set
     *
     * @returns component the yjQuery component
     */
    setType(component: HTMLElement | JQuery, type: string): JQuery;
    /**
     * Gets the smartEdit component type of a given component
     *
     * @param component the yjQuery component for which to get the type
     *
     * @returns the component type
     */
    getType(component: HTMLElement | JQuery): string;
    /**
     * Gets the type that is relevant to be able to perform slot related operations for this components
     * It typically is CONTAINER_TYPE_ATTRIBUTE when applicable and defaults to TYPE_ATTRIBUTE
     *
     * @param component the yjQuery component for which to get the type
     *
     * @returns the slot operations related type
     */
    getSlotOperationRelatedType(component: HTMLElement | JQuery): string;
    /**
     * Retrieves the DOM selector matching all smartEdit components that are not of type ContentSlot
     *
     * @returns components selector
     */
    getAllComponentsSelector(): string;
    /**
     * Retrieves the DOM selector matching all smartEdit components that are of type ContentSlot
     *
     * @returns the slots selector
     */
    getAllSlotsSelector(): string;
    /**
     * Retrieves the the slot Uuid for a given element
     *
     * @param the DOM element which represents the component
     *
     * @returns the slot Uuid for that particular component
     */
    getParentSlotUuidForComponent(component: HTMLElement | JQuery): string;
    /**
     * @param pattern Pattern of class names to search for
     *
     * @returns Class attributes from the body element of the storefront
     */
    getBodyClassAttributeByRegEx(pattern: RegExp): string;
    /**
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     * Get first level smartEdit component children for a given node, regardless how deep they are found.
     * The returned children may have different depths relatively to the parent:
     *
     * ### Example
     *
     * 	    <body>
     * 		    <div>
     * 			    <component smartedit-component-id="1">
     * 				    <component smartedit-component-id="1_1"></component>
     * 			    </component>
     * 			    <component smartedit-component-id="2">
     * 			    	<component smartedit-component-id="2_1"></component>
     * 		    	</component>
     * 		    </div>
     * 		    <component smartedit-component-id="3">
     * 			    <component smartedit-component-id="3_1"></component>
     * 		    </component>
     * 		    <div>
     * 			    <div>
     * 				    <component smartedit-component-id="4">
     * 					    <component smartedit-component-id="4_1"></component>
     * 				    </component>
     * 			    </div>
     * 		    </div>
     * 	    </body>
     *
     *
     * @param node any HTML/yjQuery Element
     *
     * @returns The list of first level smartEdit component children for a given node, regardless how deep they are found.
     */
    getFirstSmartEditComponentChildren(htmlElement: HTMLElement | JQuery): JQuery[];
    /**
     * Get component clone in overlay
     *
     * @param the DOM element which represents the component
     *
     * @returns The component clone in overlay
     */
    getComponentCloneInOverlay(component: JQuery): JQuery;
    /**
     * Get all the slot uids from the DOM
     *
     * @returns An array of slot ids in the DOM
     */
    getAllSlotUids(): string[];
    private buildComponentQuery;
    private buildComponentsInSlotQuery;
}
