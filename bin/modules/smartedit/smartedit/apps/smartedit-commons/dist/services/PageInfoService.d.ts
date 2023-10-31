import { IPageInfoService } from './interfaces';
export declare class PageInfoService extends IPageInfoService {
    static PATTERN_SMARTEDIT_CATALOG_VERSION_UUID: RegExp;
    getPageUID(): Promise<string>;
    getPageUUID(): Promise<string>;
    getCatalogVersionUUIDFromPage(): Promise<string>;
}
