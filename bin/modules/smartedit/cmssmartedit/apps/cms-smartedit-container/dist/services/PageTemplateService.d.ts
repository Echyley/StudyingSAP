import { IRestServiceFactory, IUriContext, CMSPageTypes, PageTemplateType } from 'smarteditcommons';
export interface PageTemplateResponse {
    templates: PageTemplateType[];
}
export declare class PageTemplateService {
    private pageTemplateRestService;
    constructor(restServiceFactory: IRestServiceFactory);
    getPageTemplatesForType(uriContext: IUriContext, pageType: CMSPageTypes): Promise<PageTemplateResponse>;
}
