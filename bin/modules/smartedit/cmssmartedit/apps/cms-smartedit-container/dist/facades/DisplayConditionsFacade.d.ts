import { IUriContext, Page as SePage, SelectItem, LogService, IPageService, ICMSPage } from 'smarteditcommons';
import { PageRestrictionsService } from '../services/pageRestrictions/PageRestrictionsService';
export interface IDisplayConditionsPageVariation {
    pageName: string;
    creationDate: string | Date;
    restrictions: number;
}
export interface IDisplayConditionsPageInfo {
    pageName: string;
    pageType: string;
    isPrimary: boolean;
}
export interface IDisplayConditionsPrimaryPage {
    uid: string;
    uuid: string;
    name: string;
    label: string;
}
export declare class DisplayConditionsFacade {
    private pageService;
    private pageRestrictionsService;
    private logService;
    constructor(pageService: IPageService, pageRestrictionsService: PageRestrictionsService, logService: LogService);
    getPageInfoForPageUid(pageId: string): Promise<IDisplayConditionsPageInfo>;
    getVariationsForPageUid(primaryPageId: string): Promise<IDisplayConditionsPageVariation[]>;
    updatePage(pageId: string, pageData: ICMSPage): Promise<ICMSPage>;
    isPagePrimary(pageId: string): Promise<boolean>;
    getPrimaryPageForVariationPage(variationPageId: string): Promise<Partial<IDisplayConditionsPrimaryPage>>;
    getPrimaryPagesForPageType(pageTypeCode: string, uriParams?: IUriContext, fetchPageParams?: {
        search: string;
        pageSize: number;
        currentPage: number;
    }): Promise<SePage<SelectItem>>;
}
