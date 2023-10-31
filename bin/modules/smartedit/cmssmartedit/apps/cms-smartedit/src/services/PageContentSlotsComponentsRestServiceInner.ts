/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    slotEvictionTag,
    ICMSComponent,
    IPageContentSlotsComponentsRestService,
    PAGE_CONTEXT_CATALOG,
    PAGE_CONTEXT_CATALOG_VERSION
} from 'cmscommons';
import { cloneDeep } from 'lodash';
import {
    pageChangeEvictionTag,
    rarelyChangingContent,
    Cached,
    GatewayProxied,
    InvalidateCache,
    IPageInfoService,
    IRestService,
    IRestServiceFactory,
    TypedMap,
    PAGE_CONTEXT_SITE_ID,
    CmsitemsRestService,
    cmsitemsEvictionTag
} from 'smarteditcommons';

export interface PageContentSlotComponent {
    pageId: string;
    slotId: string;
    componentId: string;
    componentUuid: string;
    position: number;
}

export interface PageContentSlotComponentResponse {
    pageContentSlotComponentList: PageContentSlotComponent[];
}

@GatewayProxied('clearCache', 'getSlotsToComponentsMapForPageUid', 'getComponentsForSlot')
@Injectable()
export class PageContentSlotsComponentsRestService extends IPageContentSlotsComponentsRestService {
    private pagesContentSlotsComponentsRestService: IRestService<PageContentSlotComponentResponse>;

    constructor(
        restServiceFactory: IRestServiceFactory,
        private pageInfoService: IPageInfoService,
        private cmsitemsRestService: CmsitemsRestService
    ) {
        super();

        const contentSlotContainerResourceURI = `/cmswebservices/v1/sites/${PAGE_CONTEXT_SITE_ID}/catalogs/${PAGE_CONTEXT_CATALOG}/versions/${PAGE_CONTEXT_CATALOG_VERSION}/pagescontentslotscomponents?pageId=:pageId`;
        this.pagesContentSlotsComponentsRestService =
            restServiceFactory.get<PageContentSlotComponentResponse>(
                contentSlotContainerResourceURI
            );
    }

    @InvalidateCache(slotEvictionTag)
    public clearCache(): void {
        return;
    }

    public async getComponentsForSlot(slotId: string): Promise<ICMSComponent[]> {
        const pageUID = await this.pageInfoService.getPageUID();
        const slotsToComponentsMap = await this.getSlotsToComponentsMapForPageUid(pageUID);

        return slotsToComponentsMap[slotId] || [];
    }

    /**
     * Returns a list of Page Content Slots Components associated to a page.
     *
     * @param pageUid The uid of the page to retrieve the content slots to components map.
     */
    public async getSlotsToComponentsMapForPageUid(
        pageUid: string
    ): Promise<TypedMap<ICMSComponent[]>> {
        const response = await this.getCachedSlotsToComponentsMapForPageUid(pageUid);

        return cloneDeep(response);
    }

    @Cached({
        actions: [rarelyChangingContent],
        tags: [cmsitemsEvictionTag, pageChangeEvictionTag, slotEvictionTag]
    })
    private async getCachedSlotsToComponentsMapForPageUid(
        pageUid: string
    ): Promise<TypedMap<ICMSComponent[]>> {
        const { pageContentSlotComponentList } =
            await this.pagesContentSlotsComponentsRestService.get({
                pageId: pageUid
            });
        const componentUuids = this.mapPageContentSlotComponentListToComponentUuids(
            pageContentSlotComponentList
        );

        const { response: components } = await this.cmsitemsRestService.getByIds<ICMSComponent>(
            componentUuids,
            'DEFAULT'
        );
        const uuidToComponentMap = this.createUuidToComponentMap(components);

        // load all components as SlotUuid-Component[] map
        const allSlotsToComponentsMap = this.createSlotUuidToComponentMap(
            pageContentSlotComponentList,
            uuidToComponentMap
        );
        return allSlotsToComponentsMap;
    }

    private createSlotUuidToComponentMap(
        componentList: PageContentSlotComponent[],
        uuidToComponentMap: TypedMap<ICMSComponent>
    ): TypedMap<ICMSComponent[]> {
        return componentList.reduce(
            (map: TypedMap<ICMSComponent[]>, component: PageContentSlotComponent) => {
                map[component.slotId] = map[component.slotId] || [];
                if (uuidToComponentMap[component.componentUuid]) {
                    map[component.slotId].push(uuidToComponentMap[component.componentUuid]);
                }
                return map;
            },
            {}
        );
    }

    private mapPageContentSlotComponentListToComponentUuids(
        componentList: PageContentSlotComponent[]
    ): string[] {
        let componentUuids = componentList.map(
            (pageContentSlotComponent: PageContentSlotComponent) =>
                pageContentSlotComponent.componentUuid
        );
        componentUuids = Array.from(new Set(componentUuids)); // remove duplicates
        return componentUuids;
    }

    private createUuidToComponentMap(components: ICMSComponent[]): TypedMap<ICMSComponent> {
        return (components || []).reduce(
            (map: TypedMap<ICMSComponent>, component: ICMSComponent) => {
                map[component.uuid] = component;
                return map;
            },
            {}
        );
    }
}
