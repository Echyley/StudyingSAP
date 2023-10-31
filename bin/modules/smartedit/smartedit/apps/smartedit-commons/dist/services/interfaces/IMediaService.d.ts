import { Page } from '@smart/utils';
export interface Media {
    id: string;
    code: string;
    description: string;
    altText: string;
    url: string;
    downloadUrl: string;
    mime: string;
    [index: string]: string;
}
export interface MediaDTO {
    altText: string;
    catalogId: string;
    catalogVersion: string;
    code: string;
    description: string;
    downloadUrl: string;
    mime: string;
    url: string;
    uuid: string;
    [index: string]: string;
}
export interface ContextParams {
    catalogId: string;
    catalogVersion: string;
    code?: string;
}
export interface MediaPage extends Page<Media> {
    media: MediaDTO[];
}
/**
 * The IMediaService provides abstract class to retrieve media content.
 */
export declare abstract class IMediaService {
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
}
