import { PageRestrictionsRestService } from '../../dao';
/**
 * Service that concerns business logic tasks related to CMS restrictions for CMS pages.
 */
export declare class PageRestrictionsService {
    private pageRestrictionsRestService;
    constructor(pageRestrictionsRestService: PageRestrictionsRestService);
    /**
     * @returns The number of restrictions applied to the page for the given page UID.
     */
    getPageRestrictionsCountForPageUID(pageUID: string): Promise<number>;
}
