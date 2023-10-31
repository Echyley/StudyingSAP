import { IUriContext } from './IUriContext';
export declare abstract class IUrlService {
    /**
     * Opens a given URL in a new browser pop up without authentication.
     */
    openUrlInPopup(url: string): void;
    /**
     * Navigates to the given path in the same browser tab.
     */
    path(path: string): void;
    /**
     * Returns a uri context array populated with the given siteId, catalogId and catalogVersion information
     */
    buildUriContext(siteId: string, catalogId: string, catalogVersion: string): IUriContext;
    /**
     * Returns a page uri context array populated with the given siteId, catalogId and catalogVersion information
     */
    buildPageUriContext(siteId: string, catalogId: string, catalogVersion: string): IUriContext;
}
