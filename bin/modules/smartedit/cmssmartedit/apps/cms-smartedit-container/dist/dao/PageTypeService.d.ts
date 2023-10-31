import { RestServiceFactory, TypedMap, CMSPageTypes } from 'smarteditcommons';
export interface PageType {
    code: CMSPageTypes;
    description: TypedMap<string>;
    name: TypedMap<string>;
    type: string;
}
/**
 * A service used to retrive all supported page types configured on the platform, and caches them for the duration of the session.
 */
export declare class PageTypeService {
    private pageTypeRestService;
    private pageTypesResponse;
    constructor(restServiceFactory: RestServiceFactory);
    /**
     * Returns a list of page type descriptor objects.
     */
    getPageTypes(): Promise<PageType[]>;
}
