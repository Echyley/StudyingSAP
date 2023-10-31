/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ICMSComponent } from 'cmscommons';
import { TypedMap } from 'smarteditcommons';

export abstract class ISlotVisibilityService {
    /**
     * Returns the list of hidden components for a given slotId
     */
    public getHiddenComponents(slotId: string): Promise<ICMSComponent[]> {
        'proxyFunction';
        return null;
    }

    /**
     * Reloads and cache's the pagesContentSlotsComponents for the current page in context.
     * this method can be called when ever a component is added or modified to the slot
     * so that the pagesContentSlotsComponents is re-evaluated.
     *
     * @returns A promise that resolves to the contentSlot - Components [] map for the page in context.
     */
    public reloadSlotsInfo(): Promise<TypedMap<ICMSComponent[]>> {
        'proxyFunction';
        return null;
    }
}
