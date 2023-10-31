import { ICMSPageTypeRestriction, PageTypesRestrictionTypesRestService } from '../../dao';
export declare class PageTypesRestrictionTypesService {
    private pageTypesRestrictionTypesRestService;
    private cache;
    constructor(pageTypesRestrictionTypesRestService: PageTypesRestrictionTypesRestService);
    getRestrictionTypeCodesForPageType(pageType: string): Promise<string[]>;
    getPageTypesRestrictionTypes(): Promise<ICMSPageTypeRestriction[]>;
}
