import { IUriContext, RestServiceFactory } from 'smarteditcommons';
/**
 * Provides REST API for the CMS fallbacks endpoint.
 *
 * Used to fetch Primary Pages IDs for Variation Pages.
 */
export declare class PagesFallbacksRestService {
    private restServiceFactory;
    private readonly fallbacksForPageIdResource;
    constructor(restServiceFactory: RestServiceFactory);
    getFallbacksForPageId(pageId: string): Promise<string[]>;
    getFallbacksForPageIdAndContext(pageId: string, uriContext: IUriContext): Promise<string[]>;
    private getUri;
}
