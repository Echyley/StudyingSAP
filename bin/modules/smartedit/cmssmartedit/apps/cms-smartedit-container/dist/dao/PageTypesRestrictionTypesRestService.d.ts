import { RestServiceFactory } from 'smarteditcommons';
export interface ICMSPageTypeRestriction {
    pageType: string;
    restrictionType: string;
}
export interface ICMSPageTypeRestrictions {
    pageTypeRestrictionTypeList: ICMSPageTypeRestriction[];
}
export declare class PageTypesRestrictionTypesRestService {
    private restServiceFactory;
    private readonly restService;
    constructor(restServiceFactory: RestServiceFactory);
    getPageTypesRestrictionTypes(): Promise<ICMSPageTypeRestrictions>;
}
