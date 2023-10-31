/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    COMPONENT_CREATED_EVENT,
    COMPONENT_REMOVED_EVENT,
    COMPONENT_UPDATED_EVENT,
    ICMSComponent,
    IPageContentSlotsComponentsRestService,
    ISlotVisibilityService
} from 'cmscommons';
import {
    CrossFrameEventService,
    IPageInfoService,
    LogService,
    TypedMap,
    IComponentHandlerService,
    GatewayProxied
} from 'smarteditcommons';

/**
 * The slot visibility service provides methods to manage all backend calls and loads an internal
 * structure that provides the necessary data to the slot visibility button and slot visibility component.
 */
@GatewayProxied('getHiddenComponents')
@Injectable()
export class SlotVisibilityService extends ISlotVisibilityService {
    constructor(
        private crossFrameEventService: CrossFrameEventService,
        private componentHandlerService: IComponentHandlerService,
        private logService: LogService,
        private pageInfoService: IPageInfoService,
        private pageContentSlotsComponentsRestService: IPageContentSlotsComponentsRestService
    ) {
        super();
        this.crossFrameEventService.subscribe(COMPONENT_CREATED_EVENT, () =>
            this.clearComponentsCache()
        );
        this.crossFrameEventService.subscribe(COMPONENT_UPDATED_EVENT, () =>
            this.clearComponentsCache()
        );
        this.crossFrameEventService.subscribe(COMPONENT_REMOVED_EVENT, () =>
            this.clearComponentsCache()
        );
    }

    /**
     * Returns the list of hidden components for a given slotId
     *
     * @param slotId the slot id
     *
     * @returns A promise that resolves to a list of hidden components for the slotId
     */
    public async getHiddenComponents(slotId: string): Promise<ICMSComponent[]> {
        const slots = await this.getSlotToComponentsMap();
        const filteredSlots = this.filterVisibleComponents(slots);

        return filteredSlots[slotId] || [];
    }

    /**
     * Reloads and cache's the pagesContentSlotsComponents for the current page in context.
     * this method can be called when ever a component is added or modified to the slot so that the pagesContentSlotsComponents is re-evaluated.
     *
     * @returns A promise that resolves to the contentSlot - Components [] map for the page in context.
     */
    public async reloadSlotsInfo(): Promise<TypedMap<ICMSComponent[]>> {
        try {
            const pageUid = await this.pageInfoService.getPageUID();
            this.pageContentSlotsComponentsRestService.clearCache();

            return this.pageContentSlotsComponentsRestService.getSlotsToComponentsMapForPageUid(
                pageUid
            );
        } catch (exception) {
            this.logService.error(
                'SlotVisibilityService::reloadSlotsInfo - failed call to pageInfoService.getPageUID'
            );

            throw exception;
        }
    }

    /**
     * Function that filters the given SlotsToComponentsMap to return only those components that are hidden in the storefront.
     * @param allSlotsToComponentsMap object containing slotId - components list.
     *
     * @return allSlotsToComponentsMap object containing slotId - components list.
     */
    private filterVisibleComponents(
        allSlotsToComponentsMap: TypedMap<ICMSComponent[]>
    ): TypedMap<ICMSComponent[]> {
        Object.keys(allSlotsToComponentsMap).forEach((slotId) => {
            const jQueryComponents = this.componentHandlerService.getOriginalComponentsWithinSlot(
                slotId
            ) as any as JQuery;
            const componentsOnDOM = jQueryComponents
                .get()
                .map((component) => this.componentHandlerService.getId(component));

            const hiddenComponents = allSlotsToComponentsMap[slotId].filter(
                (component) => !componentsOnDOM.includes(component.uid)
            );

            allSlotsToComponentsMap[slotId] = hiddenComponents;
        });

        return allSlotsToComponentsMap;
    }

    /**
     * Function to load slot to component map for the current page in context
     */
    private async getSlotToComponentsMap(): Promise<TypedMap<ICMSComponent[]>> {
        try {
            const pageUid = await this.pageInfoService.getPageUID();

            return this.pageContentSlotsComponentsRestService.getSlotsToComponentsMapForPageUid(
                pageUid
            );
        } catch (exception) {
            this.logService.error(
                'SlotVisibilityService::getSlotToComponentsMap - failed call to pageInfoService.getPageUID'
            );

            throw exception;
        }
    }

    private clearComponentsCache(): void {
        this.pageContentSlotsComponentsRestService.clearCache();
    }
}
