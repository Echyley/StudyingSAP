/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Inject } from '@angular/core';
import { stringUtils } from '@smart/utils';
import * as lodash from 'lodash';
import {
    CATALOG_VERSION_UUID_ATTRIBUTE,
    COMPONENT_CLASS,
    CONTAINER_ID_ATTRIBUTE,
    CONTAINER_TYPE_ATTRIBUTE,
    CONTENT_SLOT_TYPE,
    ELEMENT_UUID_ATTRIBUTE,
    ID_ATTRIBUTE,
    OVERLAY_COMPONENT_CLASS,
    OVERLAY_ID,
    TYPE_ATTRIBUTE,
    UUID_ATTRIBUTE
} from '../utils/smarteditconstants';
import { YJQUERY_TOKEN } from './vendors/YjqueryModule';

/**
 * Handles all get/set component related operations
 */

export abstract class IComponentHandlerService {
    constructor(@Inject(YJQUERY_TOKEN) private readonly yjQuery: JQueryStatic) {}

    /*
     * Reload inner page
     * */
    reloadInner(): Promise<void> {
        'proxyFunction';
        return;
    }

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
    isExternalComponent(
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        smarteditComponentId: string,
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        smarteditComponentType: string
    ): Promise<boolean> {
        'proxyFunction';
        return Promise.resolve(true);
    }

    /**
     * Retrieves a handler on the smartEdit overlay div
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @returns The #smarteditoverlay JQuery Element
     */
    getOverlay(): JQuery {
        return this.yjQuery('#' + OVERLAY_ID);
    }

    /**
     * determines whether the overlay is visible
     * This method can only be invoked from the smartEdit application and not the smartEdit iframe.
     *
     * @returns  true if the overlay is visible
     */
    isOverlayOn(): boolean {
        return this.getOverlay().length && this.getOverlay()[0].style.display !== 'none';
    }

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
    getComponentUnderSlot(
        smarteditComponentId: string,
        smarteditComponentType: string,
        smarteditSlotId: string,
        cssClass?: string
    ): JQuery {
        const slotQuery: string = this.buildComponentQuery(smarteditSlotId, CONTENT_SLOT_TYPE);
        const componentQuery: string = this.buildComponentQuery(
            smarteditComponentId,
            smarteditComponentType,
            cssClass
        );
        const selector = `${slotQuery} ${componentQuery}`;

        return this.yjQuery(selector);
    }

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
    getComponent(
        smarteditComponentId: string,
        smarteditComponentType: string,
        cssClass?: string
    ): JQuery {
        return this.yjQuery(
            this.buildComponentQuery(smarteditComponentId, smarteditComponentType, cssClass)
        );
    }

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
    getOriginalComponentWithinSlot(
        smarteditComponentId: string,
        smarteditComponentType: string,
        slotId: string
    ): JQuery {
        return this.getComponentUnderSlot(
            smarteditComponentId,
            smarteditComponentType,
            slotId,
            COMPONENT_CLASS
        );
    }

    /**
     * Retrieves the yjQuery wrapper around a smartEdit component of the original storefront layer identified by its smartEdit id, smartEdit type
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @param smarteditComponentId the component id as per the smartEdit contract with the storefront
     * @param smarteditComponentType the component type as per the smartEdit contract with the storefront
     *
     * @returns a yjQuery object wrapping the searched component
     */
    getOriginalComponent(smarteditComponentId: string, smarteditComponentType: string): JQuery {
        return this.getComponent(smarteditComponentId, smarteditComponentType, COMPONENT_CLASS);
    }

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
    getOverlayComponentWithinSlot(
        smarteditComponentId: string,
        smarteditComponentType: string,
        slotId: string
    ): JQuery {
        return this.getComponentUnderSlot(
            smarteditComponentId,
            smarteditComponentType,
            slotId,
            OVERLAY_COMPONENT_CLASS
        );
    }

    /**
     * Retrieves the yjQuery wrapper around the smartEdit component of the overlay layer corresponding to the storefront layer component passed as argument
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @param originalComponent the DOM element in the storefront layer
     *
     * @returns a yjQuery object wrapping the searched component
     */
    getOverlayComponent(originalComponent: JQuery): JQuery {
        const slotId: string = this.getParentSlotForComponent(originalComponent.parent());
        if (slotId) {
            return this.getComponentUnderSlot(
                originalComponent.attr(ID_ATTRIBUTE),
                originalComponent.attr(TYPE_ATTRIBUTE),
                slotId,
                OVERLAY_COMPONENT_CLASS
            );
        } else {
            return this.getComponent(
                originalComponent.attr(ID_ATTRIBUTE),
                originalComponent.attr(TYPE_ATTRIBUTE),
                OVERLAY_COMPONENT_CLASS
            );
        }
    }

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
    getComponentInOverlay(smarteditComponentId: string, smarteditComponentType: string): JQuery {
        return this.getComponent(
            smarteditComponentId,
            smarteditComponentType,
            OVERLAY_COMPONENT_CLASS
        );
    }

    /**
     * Retrieves the the slot ID for a given element
     *
     * @param component the yjQuery component for which to search the parent
     *
     * @returns the slot ID for that particular component
     */
    getParentSlotForComponent(component: HTMLElement | JQuery): string {
        const parent: JQuery = this.yjQuery(component).closest(
            `[${TYPE_ATTRIBUTE}=${CONTENT_SLOT_TYPE}]`
        );
        return parent.attr(ID_ATTRIBUTE);
    }

    /**
     * Retrieves the position of a component within a slot based on visible components in the given slotId.
     *
     * @param slotId the slot id as per the smartEdit contract with the storefront
     * @param componentId the component id as per the smartEdit contract with the storefront
     *
     * @returns the position of the component within a slot
     */
    getComponentPositionInSlot(slotId: string, componentId: string): number {
        const components: JQuery[] = this.getOriginalComponentsWithinSlot(slotId);

        return lodash.findIndex(
            components,
            (component: JQuery) => this.getId(component) === componentId
        );
    }

    /**
     * Retrieves the yjQuery wrapper around a list of smartEdit components contained in the slot identified by the given slotId.
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @param slotId the ID of the slot within which the component resides
     *
     * @returns The list of searched components yjQuery objects
     */
    getOriginalComponentsWithinSlot(slotId: string): JQuery[] {
        return this.yjQuery(this.buildComponentsInSlotQuery(slotId)) as unknown as JQuery[];
    }

    /**
     * Gets the id that is relevant to be able to perform slot related operations for this components
     * It typically is CONTAINER_ID_ATTRIBUTE when applicable and defaults to ID_ATTRIBUTE
     *
     * @param component the yjQuery component for which to get the id
     *
     * @returns the slot operations related id
     */
    getSlotOperationRelatedId(component: HTMLElement | JQuery): string {
        component = this.yjQuery(component);
        const containerId: string = component.attr(CONTAINER_ID_ATTRIBUTE);
        return containerId && component.attr(CONTAINER_TYPE_ATTRIBUTE)
            ? containerId
            : component.attr(ID_ATTRIBUTE);
    }

    /**
     * Gets the id that is relevant to be able to perform slot related operations for this components
     * It typically is {@link seConstantsModule.CONTAINER_ID_ATTRIBUTE} when applicable and defaults to {@link seConstantsModule.ID_ATTRIBUTE}
     *
     * @param component the yjQuery component for which to get the Uuid
     *
     * @returns the slot operations related Uuid
     */
    getSlotOperationRelatedUuid(component: HTMLElement | JQuery): string {
        const containerId: string = this.yjQuery(component).attr(CONTAINER_ID_ATTRIBUTE);
        return containerId && this.yjQuery(component).attr(CONTAINER_TYPE_ATTRIBUTE)
            ? containerId
            : this.yjQuery(component).attr(UUID_ATTRIBUTE);
    }

    /**
     * Retrieves the direct smartEdit component parent of a given component.
     * The parent is fetched in the same layer (original storefront or smartEdit overlay) as the child
     * This method can only be invoked from the smartEdit application and not the smartEdit container.
     *
     * @param component the yjQuery component for which to search a parent
     *
     * @returns a yjQuery object wrapping the smae-layer parent component
     */
    getParent(component: HTMLElement | JQuery): JQuery {
        component = this.yjQuery(component);
        let parentClassToLookFor: string = null;
        if (component.hasClass(COMPONENT_CLASS)) {
            parentClassToLookFor = COMPONENT_CLASS;
        } else if (component.hasClass(OVERLAY_COMPONENT_CLASS)) {
            parentClassToLookFor = OVERLAY_COMPONENT_CLASS;
        }
        if (stringUtils.isBlank(parentClassToLookFor)) {
            throw new Error('componentHandlerService.getparent.error.component.from.unknown.layer');
        }
        return component.closest(
            `.${parentClassToLookFor}[${ID_ATTRIBUTE}]` +
                `[${ID_ATTRIBUTE}!='${component.attr(ID_ATTRIBUTE)}']`
        );
    }

    /**
     * Returns the closest parent (or self) being a smartEdit component
     *
     * @param component the DOM/yjQuery element for which to search a parent
     *
     * @returns The closest closest parent (or self) being a smartEdit component
     */
    getClosestSmartEditComponent(component: HTMLElement | JQuery): JQuery {
        return this.yjQuery(component).closest('.' + COMPONENT_CLASS);
    }

    /**
     * Determines whether a DOM/yjQuery element is a smartEdit component
     *
     * @param component the DOM/yjQuery element for which to check if it's a SmartEdit component
     *
     * @returns true if DOM/yjQuery element is a smartEdit component
     */
    isSmartEditComponent(component: HTMLElement | JQuery): boolean {
        return this.yjQuery(component).hasClass(COMPONENT_CLASS);
    }

    /**
     * Sets the smartEdit component id of a given component
     *
     * @param component the yjQuery component for which to set the id
     * @param id the id to be set
     *
     * @returns component the yjQuery component
     */
    setId(component: HTMLElement | JQuery, id: string): JQuery {
        return this.yjQuery(component).attr(ID_ATTRIBUTE, id);
    }

    /**
     * Gets the smartEdit component id of a given component
     *
     * @param component the yjQuery component for which to get the id
     *
     * @returns the component id
     */
    getId(component: HTMLElement | JQuery): string {
        return this.yjQuery(component).attr(ID_ATTRIBUTE);
    }

    /**
     * Gets the smartEdit component id of a given component
     *
     * @param component the yjQuery component for which to get the id
     *
     * @returns the component id
     */
    getUuid(component: HTMLElement | JQuery): string {
        return this.yjQuery(component).attr(UUID_ATTRIBUTE);
    }

    /**
     * Gets the smartEdit component id of a given component
     *
     * @param component the yjQuery component for which to get the id
     *
     * @returns the component id
     */
    getCatalogVersionUuid(component: HTMLElement | JQuery): string {
        return this.yjQuery(component).attr(CATALOG_VERSION_UUID_ATTRIBUTE);
    }

    /**
     * Sets the smartEdit component type of a given component
     *
     * @param component the yjQuery component for which to set the type
     * @param type the type to be set
     *
     * @returns component the yjQuery component
     */
    setType(component: HTMLElement | JQuery, type: string): JQuery {
        return this.yjQuery(component).attr(TYPE_ATTRIBUTE, type);
    }

    /**
     * Gets the smartEdit component type of a given component
     *
     * @param component the yjQuery component for which to get the type
     *
     * @returns the component type
     */
    getType(component: HTMLElement | JQuery): string {
        return this.yjQuery(component).attr(TYPE_ATTRIBUTE);
    }

    /**
     * Gets the type that is relevant to be able to perform slot related operations for this components
     * It typically is CONTAINER_TYPE_ATTRIBUTE when applicable and defaults to TYPE_ATTRIBUTE
     *
     * @param component the yjQuery component for which to get the type
     *
     * @returns the slot operations related type
     */
    getSlotOperationRelatedType(component: HTMLElement | JQuery): string {
        const containerType: string = this.yjQuery(component).attr(CONTAINER_TYPE_ATTRIBUTE);
        return containerType && this.yjQuery(component).attr(CONTAINER_ID_ATTRIBUTE)
            ? containerType
            : this.yjQuery(component).attr(TYPE_ATTRIBUTE);
    }

    /**
     * Retrieves the DOM selector matching all smartEdit components that are not of type ContentSlot
     *
     * @returns components selector
     */
    getAllComponentsSelector(): string {
        return `.${COMPONENT_CLASS}[${TYPE_ATTRIBUTE}!='ContentSlot']`;
    }

    /**
     * Retrieves the DOM selector matching all smartEdit components that are of type ContentSlot
     *
     * @returns the slots selector
     */
    getAllSlotsSelector(): string {
        return `.${COMPONENT_CLASS}[${TYPE_ATTRIBUTE}='ContentSlot']`;
    }

    /**
     * Retrieves the the slot Uuid for a given element
     *
     * @param the DOM element which represents the component
     *
     * @returns the slot Uuid for that particular component
     */
    getParentSlotUuidForComponent(component: HTMLElement | JQuery): string {
        return this.yjQuery(component)
            .closest(`[${TYPE_ATTRIBUTE}=${CONTENT_SLOT_TYPE}]`)
            .attr(UUID_ATTRIBUTE);
    }

    /**
     * @param pattern Pattern of class names to search for
     *
     * @returns Class attributes from the body element of the storefront
     */
    getBodyClassAttributeByRegEx(pattern: RegExp): string {
        try {
            const bodyClass: string = this.yjQuery('body').attr('class');
            return pattern.exec(bodyClass)[1];
        } catch (err) {
            throw new Error(
                JSON.stringify({
                    name: 'InvalidStorefrontPageError',
                    message: 'Error: the page is not a valid storefront page.'
                })
            );
        }
    }

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
    getFirstSmartEditComponentChildren(htmlElement: HTMLElement | JQuery): JQuery[] {
        const node = this.yjQuery(htmlElement);
        const root = node[0];

        if (!root) {
            return [];
        }

        const collection = Array.from(root.getElementsByClassName(COMPONENT_CLASS)).filter(
            (element: HTMLElement) => {
                let current = element.parentElement;
                /**
                 * The filter goes up the tree to see if any of the parents
                 * have the component selector. If it does, it's not a first child.
                 *
                 * If the parent is the htmlElement, the search stops there.
                 */
                while (current !== root) {
                    if (current.classList.contains(COMPONENT_CLASS)) {
                        return false;
                    }
                    current = current.parentElement;
                }

                return true;
            }
        );

        return this.yjQuery(collection) as any;
    }

    /**
     * Get component clone in overlay
     *
     * @param the DOM element which represents the component
     *
     * @returns The component clone in overlay
     */
    getComponentCloneInOverlay(component: JQuery): JQuery {
        const elementUuid: string = component.attr(ELEMENT_UUID_ATTRIBUTE);
        return this.yjQuery(
            `.${OVERLAY_COMPONENT_CLASS}[${ELEMENT_UUID_ATTRIBUTE}='${elementUuid}']`
        );
    }

    /**
     * Get all the slot uids from the DOM
     *
     * @returns An array of slot ids in the DOM
     */
    getAllSlotUids(): string[] {
        const slots = this.yjQuery(this.getAllSlotsSelector());
        return Array.prototype.slice.call(
            slots.map((_index, slot) => this.getId(slot))
        ) as string[];
    }

    private buildComponentQuery(
        smarteditComponentId: string,
        smarteditComponentType: string,
        cssClass?: string
    ): string {
        let query = '';
        query += cssClass ? '.' + cssClass : '';
        query += `[${ID_ATTRIBUTE}='${smarteditComponentId}']`;
        query += `[${TYPE_ATTRIBUTE}='${smarteditComponentType}']`;
        return query;
    }

    private buildComponentsInSlotQuery(slotId: string): string {
        let query = '';
        query += '.' + COMPONENT_CLASS;
        query += `[${ID_ATTRIBUTE}='${slotId}']`;
        query += `[${TYPE_ATTRIBUTE}='${CONTENT_SLOT_TYPE}']`;
        query += ' > ';
        query += `[${ID_ATTRIBUTE}]`;
        return query;
    }
}
