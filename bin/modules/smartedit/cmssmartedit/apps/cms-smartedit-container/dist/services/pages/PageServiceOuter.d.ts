import { PagesFallbacksRestService } from 'cmssmarteditcontainer/dao/PagesFallbacksRestService';
import { PagesRestService } from 'cmssmarteditcontainer/dao/PagesRestService';
import { PagesVariationsRestService } from 'cmssmarteditcontainer/dao/PagesVariationsRestService';
import { IExperienceService, IPageInfoService, IUriContext, IUrlService, Nullable, CmsitemsRestService, IPage, IPageService, ICMSPage, CmsApprovalStatus } from 'smarteditcommons';
export declare class PageService extends IPageService {
    private pagesRestService;
    private pagesFallbacksRestService;
    private pagesVariationsRestService;
    private pageInfoService;
    private cmsitemsRestService;
    private experienceService;
    private urlService;
    constructor(pagesRestService: PagesRestService, pagesFallbacksRestService: PagesFallbacksRestService, pagesVariationsRestService: PagesVariationsRestService, pageInfoService: IPageInfoService, cmsitemsRestService: CmsitemsRestService, experienceService: IExperienceService, urlService: IUrlService);
    getPageByUuid(pageUuid: string): Promise<ICMSPage>;
    getCurrentPageInfo(): Promise<ICMSPage>;
    getPageById(pageUid: string): Promise<ICMSPage>;
    getCurrentPageInfoByVersion(versionId: string | null): Promise<ICMSPage>;
    primaryPageForPageTypeExists(pageTypeCode: string, uriParams?: IUriContext): Promise<boolean>;
    getPaginatedPrimaryPagesForPageType(pageTypeCode: string, uriParams?: IUriContext, fetchPageParams?: {
        search: string;
        pageSize: number;
        currentPage: number;
    }): Promise<IPage<ICMSPage>>;
    isPagePrimary(pageUid: string): Promise<boolean>;
    isPagePrimaryWithContext(pageUid: string, uriContext: IUriContext): Promise<boolean>;
    getPrimaryPage(variationPageId: string): Promise<Nullable<ICMSPage>>;
    getVariationPages(primaryPageId: string): Promise<ICMSPage[]>;
    updatePageById(pageUid: string, payload: ICMSPage): Promise<ICMSPage>;
    forcePageApprovalStatus(newPageStatus: CmsApprovalStatus): Promise<ICMSPage>;
    isPageApproved(pageParam: string | ICMSPage): Promise<boolean>;
    buildUriContextForCurrentPage(siteId: Nullable<string>, catalogId: Nullable<string>, catalogVersion: Nullable<string>): Promise<IUriContext>;
}
