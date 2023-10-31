import { ICMSComponent, IPageContentSlotsComponentsRestService } from 'cmscommons';
import { IPageInfoService, IRestServiceFactory, TypedMap, CmsitemsRestService } from 'smarteditcommons';
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
export declare class PageContentSlotsComponentsRestService extends IPageContentSlotsComponentsRestService {
    private pageInfoService;
    private cmsitemsRestService;
    private pagesContentSlotsComponentsRestService;
    constructor(restServiceFactory: IRestServiceFactory, pageInfoService: IPageInfoService, cmsitemsRestService: CmsitemsRestService);
    clearCache(): void;
    getComponentsForSlot(slotId: string): Promise<ICMSComponent[]>;
    /**
     * Returns a list of Page Content Slots Components associated to a page.
     *
     * @param pageUid The uid of the page to retrieve the content slots to components map.
     */
    getSlotsToComponentsMapForPageUid(pageUid: string): Promise<TypedMap<ICMSComponent[]>>;
    private getCachedSlotsToComponentsMapForPageUid;
    private createSlotUuidToComponentMap;
    private mapPageContentSlotComponentListToComponentUuids;
    private createUuidToComponentMap;
}
