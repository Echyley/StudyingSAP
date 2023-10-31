/// <reference types="jquery" />
import { ICMSComponent } from 'cmscommons';
import { CrossFrameEventService, LogService, PromiseUtils, CMSModesService, CmsitemsRestService } from 'smarteditcommons';
/**
 * This service is used to fetch and cache components information.
 * This service keeps track of components added, edited and removed. It also automatically fetches and caches components when they are visible in the viewport (and invalidates them).
 *
 * This service is intended to be used to improve the performance of the application by reducing the number of xhr calls to the cmsitems api.
 * Example:
 * - a component in the overlay that is doing a fetch to the cmsitems api should use this service instead of using cmsitemsRestService.
 *   When a lot of components are rendered in the overlay we want to avoid one xhr call per component, but instead use this service that is listening
 *   to the 'OVERLAY_RERENDERED_EVENT' and fetch components information in batch (POST to cmsitems endpoint with an Array of uuids).
 */
export declare class ComponentInfoService {
    private yjQuery;
    private logService;
    private crossFrameEventService;
    private cmsitemsRestService;
    private promiseUtils;
    private cMSModesService;
    private cachedComponents;
    private promisesQueue;
    constructor(yjQuery: JQueryStatic, logService: LogService, crossFrameEventService: CrossFrameEventService, cmsitemsRestService: CmsitemsRestService, promiseUtils: PromiseUtils, cMSModesService: CMSModesService);
    /**
     * @internal
     * Returns a Promise that will be resolved with the component identified by the given uuid.
     * When called this method works like this:
     * - If the component is in the cache, the promise resolves right away.
     * - If the component is not in the cache, and the forceRetrieval flag is not set, this method won't call the cmsItem backend API right away.
     *   Instead, it waits until the component is cached (e.g., it is added to the overlay).
     * - If the forceRetrieval flag is set, then the method will call the cmsItem backend API right away.
     *
     * @param uuid The uuid of the item to retrieve
     * @param forceRetrieval Boolean flag. It specifies whether to retrieve the cmsItem right away.
     * @returns Promise that will be resolved only if the component was added previously in the overlay and if not will resolve only when the component is added to the overlay.
     *
     */
    getById(uuid: string, forceRetrieval?: boolean): Promise<ICMSComponent>;
    private resolvePromises;
    private rejectPromises;
    private getComponentDataByUUID;
    private getComponentsDataByUUIDs;
    private onComponentsAddedToOverlay;
    private onComponentsRemovedFromOverlay;
    private forceAddComponent;
    private forceRemoveComponent;
    private isComponentCached;
    private clearCache;
    private onOverlayReRendered;
    private onComponentAdded;
    private onComponentRemoved;
}
