/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
export enum COMPONENT_IN_SLOT_STATUS {
    ALLOWED = 'allowed',
    DISALLOWED = 'disallowed',
    MAYBEALLOWED = 'mayBeAllowed'
}

/**
 * Provide methods that cache and return the restrictions of a slot in a page.
 * This restrictions determine whether a component of a certain type is allowed or forbidden in a particular slot.
 */
export abstract class ISlotRestrictionsService {
    /**
     * This methods retrieves the list of component types droppable in at least one of the slots of the current page.
     *
     * @returns Promise containing an array with the component types droppable on the current page.
     *
     * **Deprecated since 2005**
     * @deprecated
     */
    public getAllComponentTypesSupportedOnPage(): Promise<any[] | void> {
        'proxyFunction';
        return null;
    }

    /**
     * This methods retrieves the list of restrictions applied to the slot identified by the provided ID.
     *
     * @returns Promise containing an array with the restrictions applied to the slot.
     */
    public getSlotRestrictions(slotId: string): Promise<string[] | void> {
        'proxyFunction';
        return null;
    }

    public isSlotEditable(slotId: string): Promise<boolean> {
        'proxyFunction';
        return null;
    }

    public isComponentAllowedInSlot(slot: any, dragInfo: any): Promise<COMPONENT_IN_SLOT_STATUS> {
        'proxyFunction';
        return null;
    }

    public determineComponentStatusInSlot(
        slotId: string,
        dragInfo: any
    ): Promise<COMPONENT_IN_SLOT_STATUS> {
        'proxyFunction';
        return null;
    }
}
