/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Inject, Injectable } from '@angular/core';
import { uniq, first } from 'lodash';
import { COMPONENT_CONTAINER_TYPE, CONTAINER_SOURCE_ID_ATTR } from 'personalizationcommons';
import {
    CONTAINER_ID_ATTRIBUTE,
    CONTAINER_TYPE_ATTRIBUTE,
    CONTENT_SLOT_TYPE,
    TYPE_ATTRIBUTE,
    YJQUERY_TOKEN,
    IPageInfoService,
    IRestService,
    IRestServiceFactory,
    EVENTS,
    CrossFrameEventService,
    IComponentHandlerService
} from 'smarteditcommons';
import { PAGES_CONTENT_SLOT_RESOURCE_URI } from '../constants';
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

@Injectable()
export class PersonalizationsmarteditComponentHandlerService {
    private pageContentSlots: IPageContentSlot[];
    private readonly resource: IRestService<IPageContentSlotsResponse>;
    constructor(
        restServiceFactory: IRestServiceFactory,
        private crossFrameEventService: CrossFrameEventService,
        @Inject(YJQUERY_TOKEN) private yjQuery: JQueryStatic,
        private componentHandlerService: IComponentHandlerService,
        private pageInfoService: IPageInfoService
    ) {
        this.resource = restServiceFactory.get(PAGES_CONTENT_SLOT_RESOURCE_URI);
        this.crossFrameEventService.subscribe(EVENTS.PAGE_CHANGE, () =>
            this.reloadPageContentSlots()
        );
    }

    public getParentContainerForComponent(component: JQuery<HTMLElement> | JQuery): JQuery {
        const parent: JQuery = component.closest(
            `[${CONTAINER_TYPE_ATTRIBUTE}=${COMPONENT_CONTAINER_TYPE}]`
        );
        return parent;
    }

    public getParentContainerIdForComponent(component: JQuery<HTMLElement> | JQuery): string {
        const parent = component.closest(
            `[${CONTAINER_TYPE_ATTRIBUTE}=${COMPONENT_CONTAINER_TYPE}]`
        );
        return parent.attr(CONTAINER_ID_ATTRIBUTE);
    }

    public async getPageContentSlots(): Promise<IPageContentSlot[]> {
        if (!this.pageContentSlots) {
            await this.reloadPageContentSlots();
        }
        return this.pageContentSlots;
    }

    public getParentContainerSourceIdForComponent(component: JQuery<HTMLElement> | JQuery): string {
        const parent = component.closest(
            `[${CONTAINER_TYPE_ATTRIBUTE}=${COMPONENT_CONTAINER_TYPE}]`
        );
        return parent.attr(CONTAINER_SOURCE_ID_ATTR);
    }

    public getParentSlotForComponent(component: JQuery<HTMLElement> | JQuery): JQuery {
        const parent: JQuery = component.closest(`[${TYPE_ATTRIBUTE}=${CONTENT_SLOT_TYPE}]`);
        return parent;
    }

    public getParentSlotIdForComponent(component: HTMLElement | JQuery): string {
        return this.componentHandlerService.getParentSlotForComponent(component);
    }

    public getOriginalComponent(componentId: string, componentType: string): JQuery {
        return this.componentHandlerService.getOriginalComponent(componentId, componentType);
    }

    public isExternalComponent(componentId: string, componentType: string): Promise<boolean> {
        return this.componentHandlerService.isExternalComponent(componentId, componentType);
    }

    public getCatalogVersionUuid(component: HTMLElement | JQuery): string {
        return this.componentHandlerService.getCatalogVersionUuid(component);
    }

    public getAllSlotsSelector(): string {
        return this.componentHandlerService.getAllSlotsSelector();
    }

    // Since implementation class for JQueryStatic does not support constructor with multiple dynamic parameter well
    // Here we split into different sub methods to avoid build warning
    public getFromJQuerySelector(selector: JQuery): JQuery {
        return this.yjQuery(selector);
    }

    public getFromSelector(selector: string | HTMLElement): JQuery {
        if (typeof selector === 'string') {
            return this.yjQuery(selector);
        } else {
            return this.getFromHTMLSelector(selector);
        }
    }

    public getFromHTMLSelector(selector: HTMLElement): JQuery {
        return this.yjQuery(selector);
    }

    public getContainerSourceIdForContainerId(containerId: string): string {
        let containerSelector: string = this.getAllSlotsSelector();
        containerSelector += ' [' + CONTAINER_ID_ATTRIBUTE + '="' + containerId + '"]'; // space at beginning is important
        const container: any = this.getFromSelector(containerSelector);
        return container[0] ? container[0].getAttribute(CONTAINER_SOURCE_ID_ATTR) : '';
    }

    public async isSlotShared(slotId: string): Promise<boolean> {
        await this.getPageContentSlots();

        const matchedSlotData = first(
            this.pageContentSlots.filter((pageContentSlot) => pageContentSlot.slotId === slotId)
        );
        return matchedSlotData && matchedSlotData.slotShared;
    }

    private async reloadPageContentSlots(): Promise<void> {
        const pageId = await this.pageInfoService.getPageUID();
        const pageContent = await this.resource.get({ pageId });
        this.pageContentSlots = uniq(pageContent.pageContentSlotList || []);
    }
}
