import { CMSRestriction } from 'cmscommons';
import { RestServiceFactory } from 'smarteditcommons';
export interface ICMSPageRestriction {
    pageRestrictionList: CMSRestriction[];
}
export declare class PageRestrictionsRestService {
    private restServiceFactory;
    private readonly contextualPageRestrictionsRestService;
    private readonly pageRestrictionsRestService;
    constructor(restServiceFactory: RestServiceFactory);
    getPagesRestrictionsForPageId(pageId: string): Promise<ICMSPageRestriction>;
    getPagesRestrictionsForCatalogVersion(siteUID: string, catalogId: string, catalogVersion: string): Promise<ICMSPageRestriction>;
}
