import { IPageInfoService, IRestServiceFactory, CmsitemsRestService, IPage, CMSItem } from 'smarteditcommons';
import { IPageContentSlotsComponentsRestService } from '../dao';
import { CMSItemStructure } from '../dtos';
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
export declare class ComponentService {
    private restServiceFactory;
    private cmsitemsRestService;
    private pageInfoService;
    private pageContentSlotsComponentsRestService;
    private pageComponentTypesRestService;
    private restServiceForAddExistingComponent;
    private readonly pageComponentTypesRestServiceURI;
    constructor(restServiceFactory: IRestServiceFactory, cmsitemsRestService: CmsitemsRestService, pageInfoService: IPageInfoService, pageContentSlotsComponentsRestService: IPageContentSlotsComponentsRestService);
    /**
     * Fetches all component types that are applicable to the current page.
     *
     * @returns A promise resolving to a page of component types applicable to the current page.
     */
    getSupportedComponentTypesForCurrentPage(payload: LoadPagedComponentTypesRequestPayload): Promise<IPage<CMSItemStructure>>;
    /**
     * Given a component info and the component payload, a new componentItem is created and added to a slot
     */
    createNewComponent(componentInfo: ComponentInfo, componentPayload: CMSItem): Promise<CMSItem>;
    /**
     * Given a component payload related to an existing component, it will be updated with the new supplied values.
     */
    updateComponent(componentPayload: CMSItem): Promise<CMSItem>;
    /**
     * Add an existing component item to a slot.
     *
     * @param pageId used to identify the page containing the slot in the current template.
     * @param componentId used to identify the existing component which will be added to the slot.
     * @param slotId used to identify the slot in the current template.
     * @param position used to identify the position in the slot in the current template.
     */
    addExistingComponent(pageId: string, componentId: string, slotId: string, position: number): Promise<void>;
    /**
     * Load a component identified by its id.
     */
    loadComponentItem(id: string): Promise<CMSItem>;
    /**
     * All existing component items for the provided content catalog are retrieved in the form of pages
     * used for pagination especially when the result set is very large.
     *
     * E.g. Add Components -> Saved Components.
     *
     * @returns A promise resolving to a page of component items retrieved from the provided catalog version.
     */
    loadPagedComponentItemsByCatalogVersion(payload: LoadPagedComponentsRequestPayload): Promise<IPage<CMSItem>>;
    /**
     * Returns slot IDs for the given componentUuid.
     *
     * E.g. Edit Component on Storefront and click Save button.
     */
    getSlotsForComponent(componentUuid: string): Promise<string[]>;
    private getContentSlotsForComponents;
}
export {};
