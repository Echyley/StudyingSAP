/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    pageChangeEvictionTag,
    rarelyChangingContent,
    Cached,
    IPageInfoService,
    IRestService,
    TypedMap,
    IRestServiceFactory,
    CmsitemsRestService,
    IPage,
    CMSItem
} from 'smarteditcommons';
import {
    IPageContentSlotsComponentsRestService,
    PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI
} from '../dao';
import { CMSItemStructure, ICMSComponent } from '../dtos';

/**
 * The payload that contains the information of the page of components to load
 */
export interface LoadPagedComponentsRequestPayload {
    /**
     * The current page number.
     */
    page: number;
    /**
     * The number of elements that a page can contain.
     */
    pageSize: number;
    /**
     * The search string to filter the results.
     */
    mask: string;
    typeCode: string;
    /**
     * The id of the catalog for which to retrieve the component items.
     */
    catalogId: string;
    /**
     * The id of the catalog version for which to retrieve the component items.
     */
    catalogVersion: string;
}

/**
 * Describes the information of the component types to load.
 */
export interface LoadPagedComponentTypesRequestPayload {
    catalogId: string;
    catalogVersion: string;
    pageId: string;
    pageSize: number;
    currentPage: number;
    mask: string;
}

/**
 * The basic information of the ComponentType to be created and added to the slot.
 */
interface ComponentInfo {
    name: string;
    /**
     * Identifies the slot in the current template.
     */
    slotId: string;
    /**
     * Identifies the current page template.
     */
    pageId: string;
    /**
     * Identifies the position in the slot in the current template.
     */
    position: number;
    typeCode: string;
    itemtype: string;
    catalogVersion: string;
    targetSlotId?: string;
    componentType?: string;
    catalogVersionUuid?: string;
}

/**
 * Service which manages component types and items.
 */
@Injectable()
export class ComponentService {
    private pageComponentTypesRestService: IRestService<IPage<CMSItemStructure>>;
    private restServiceForAddExistingComponent: IRestService<void>;
    private readonly pageComponentTypesRestServiceURI =
        '/cmssmarteditwebservices/v1/catalogs/:catalogId/versions/:catalogVersion/pages/:pageId/types';

    constructor(
        private restServiceFactory: IRestServiceFactory,
        private cmsitemsRestService: CmsitemsRestService,
        private pageInfoService: IPageInfoService,
        private pageContentSlotsComponentsRestService: IPageContentSlotsComponentsRestService
    ) {
        this.pageComponentTypesRestService = this.restServiceFactory.get(
            this.pageComponentTypesRestServiceURI
        );

        this.restServiceForAddExistingComponent = this.restServiceFactory.get(
            PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI
        );
    }

    /**
     * Fetches all component types that are applicable to the current page.
     *
     * @returns A promise resolving to a page of component types applicable to the current page.
     */
    @Cached({ actions: [rarelyChangingContent], tags: [pageChangeEvictionTag] })
    public getSupportedComponentTypesForCurrentPage(
        payload: LoadPagedComponentTypesRequestPayload
    ): Promise<IPage<CMSItemStructure>> {
        return this.pageComponentTypesRestService.get(payload as any);
    }

    /**
     * Given a component info and the component payload, a new componentItem is created and added to a slot
     */
    public createNewComponent(
        componentInfo: ComponentInfo,
        componentPayload: CMSItem
    ): Promise<CMSItem> {
        const payload: CMSItem = {
            name: componentInfo.name,
            slotId: componentInfo.targetSlotId,
            pageId: componentInfo.pageId,
            position: componentInfo.position,
            typeCode: componentInfo.componentType,
            itemtype: componentInfo.componentType,
            catalogVersion: componentInfo.catalogVersionUuid,
            uid: '',
            uuid: ''
        };

        // TODO: consider refactor. Remove the if statement, rely on TypeScript.
        if (typeof componentPayload === 'object') {
            for (const property in componentPayload) {
                if (componentPayload.hasOwnProperty(property)) {
                    payload[property] = componentPayload[property];
                }
            }
        } else if (componentPayload) {
            throw new Error(
                `ComponentService.createNewComponent() - Illegal componentPayload - [${componentPayload}]`
            );
        }

        return this.cmsitemsRestService.create(payload);
    }

    /**
     * Given a component payload related to an existing component, it will be updated with the new supplied values.
     */
    public updateComponent(componentPayload: CMSItem): Promise<CMSItem> {
        return this.cmsitemsRestService.update(componentPayload);
    }

    /**
     * Add an existing component item to a slot.
     *
     * @param pageId used to identify the page containing the slot in the current template.
     * @param componentId used to identify the existing component which will be added to the slot.
     * @param slotId used to identify the slot in the current template.
     * @param position used to identify the position in the slot in the current template.
     */
    public addExistingComponent(
        pageId: string,
        componentId: string,
        slotId: string,
        position: number
    ): Promise<void> {
        return this.restServiceForAddExistingComponent.save({
            pageId,
            slotId,
            componentId,
            position
        });
    }

    /**
     * Load a component identified by its id.
     */
    public loadComponentItem(id: string): Promise<CMSItem> {
        return this.cmsitemsRestService.getById(id);
    }

    /**
     * All existing component items for the provided content catalog are retrieved in the form of pages
     * used for pagination especially when the result set is very large.
     *
     * E.g. Add Components -> Saved Components.
     *
     * @returns A promise resolving to a page of component items retrieved from the provided catalog version.
     */
    public loadPagedComponentItemsByCatalogVersion(
        payload: LoadPagedComponentsRequestPayload
    ): Promise<IPage<CMSItem>> {
        const requestParams = {
            pageSize: payload.pageSize,
            currentPage: payload.page,
            mask: payload.mask,
            sort: 'name',
            typeCode: 'AbstractCMSComponent',
            catalogId: payload.catalogId,
            catalogVersion: payload.catalogVersion,
            itemSearchParams: ''
        };

        return this.cmsitemsRestService.get(requestParams);
    }

    /**
     * Returns slot IDs for the given componentUuid.
     *
     * E.g. Edit Component on Storefront and click Save button.
     */
    public async getSlotsForComponent(componentUuid: string): Promise<string[]> {
        const allSlotsToComponents = await this.getContentSlotsForComponents();

        return Object.entries(allSlotsToComponents)
            .filter(([, components]) =>
                components.find((component: ICMSComponent) => component.uuid === componentUuid)
            )
            .map(([slotId]) => slotId);
    }

    private async getContentSlotsForComponents(): Promise<TypedMap<ICMSComponent[]>> {
        const pageId = await this.pageInfoService.getPageUID();
        return await this.pageContentSlotsComponentsRestService.getSlotsToComponentsMapForPageUid(
            pageId
        );
    }
}
