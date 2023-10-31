import { RestServiceFactory } from 'smarteditcommons';
/**
 * Provides REST API for the CMS variations endpoint.
 *
 * Used on: "Pages" page.
 */
export declare class PagesVariationsRestService {
    private readonly URI;
    private resource;
    constructor(restServiceFactory: RestServiceFactory);
    /**
     * @returns A promise resolving to a list of variation page IDs.
     */
    getVariationsForPrimaryPageId(pageId: string): Promise<string[]>;
}
