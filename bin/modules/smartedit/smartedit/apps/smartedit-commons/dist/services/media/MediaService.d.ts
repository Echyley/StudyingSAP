import { RestServiceFactory, Page } from '@smart/utils';
import { IMediaService, Media } from '../interfaces/IMediaService';
/** Service to deal with media related CRUD operations. */
export declare class MediaService extends IMediaService {
    private restServiceFactory;
    constructor(restServiceFactory: RestServiceFactory);
    /**
     * Fetches paged search results by making a REST call to the appropriate item endpoint.
     *
     * @param mask for filtering the search.
     * @param pageSize number of items in the page.
     * @param currentPage current page number.
     */
    getPage(mask: string, pageSize: number, currentPage: number, mimeType: string): Promise<Page<Media>>;
    /**
     * This method fetches a Media by its UUID.
     * @param uuid uuid of a media (contains catalog information).
     */
    getMedia(uuid: string): Promise<Media>;
    /**
     * Returns comma separated params that will be attached to payload.
     *
     * E.g. "catalogId:CURRENT_CONTEXT_CATALOG,catalogVersion:CURRENT_CONTEXT_CATALOG_VERSION"
     */
    private contextParamsToCommaSeparated;
    private mediaDTOtoMedia;
}
