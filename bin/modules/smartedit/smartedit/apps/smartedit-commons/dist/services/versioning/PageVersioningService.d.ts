import { Page } from '@smart/utils';
import { IRestServiceFactory, IPageVersion, PageVersionSearchPayload } from '../interfaces';
/**
 * Used to manage versions in a page.
 */
export declare class PageVersioningService {
    private readonly restServiceFactory;
    private readonly pageVersionRESTService;
    private readonly pageVersionsRollbackRESTService;
    private readonly pageVersionsServiceResourceURI;
    private readonly pageVersionsRollbackServiceResourceURI;
    constructor(restServiceFactory: IRestServiceFactory);
    /**
     * Retrieves the list of versions found for the page identified by the provided id. This method is paged.
     *
     * @param payload The payload containing search query params, including the pageable information.
     * @returns A promise that resolves to a paged list of versions.
     */
    findPageVersions(payload: PageVersionSearchPayload): Promise<Page<IPageVersion>>;
    /**
     * Retrieves the page version information for the provided versionId.
     */
    getPageVersionForId(pageUuid: string, versionId: string): Promise<IPageVersion>;
    /**
     * Retrieves the resource URI to manage page versions.
     */
    getResourceURI(): string;
    deletePageVersion(pageUuid: string, versionId: string): Promise<void>;
    /**
     * Rollbacks the page to the provided version. This process will automatically create a version of the current page.
     */
    rollbackPageVersion(pageUuid: string, versionId: string): Promise<void>;
}
