import { SystemEventService } from 'smarteditcommons';
/**
 * @ignore
 * @internal
 *
 * Navigates to a site with the given site id.
 */
export declare class CatalogNavigateToSite {
    private systemEvent;
    constructor(systemEvent: SystemEventService);
    navigate(siteId: string): void;
}
