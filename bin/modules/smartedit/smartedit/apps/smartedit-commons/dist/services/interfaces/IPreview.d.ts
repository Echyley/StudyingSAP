import { Payload } from '@smart/utils';
/**
 * Interface for data representing a catalog version
 */
export interface IPreviewCatalogVersionData extends Payload {
    /**
     * the catalog id
     */
    catalog: string;
    catalogVersion: string;
}
/**
 * Interface for data sent/received to/from the preview API.
 *
 * Since the preview api is extensible, you can send more fields by adding a new interface that extends this one.
 * All additional members of the Object passed to the preview API will be included in the request.
 */
export interface IPreviewData extends Payload {
    catalogVersions: IPreviewCatalogVersionData[];
    /**
     * the isocode of the language to preview
     */
    language: string;
    resourcePath: string;
    /**
     * the uid of the page to preview
     */
    pageId?: string;
    /**
     * the uid of the site corresponding to the page to preview
     */
    siteId?: string;
    /**
     * the time in utc format
     */
    time?: string;
    /**
     * Identifier for the preview
     */
    ticketId?: string;
}
