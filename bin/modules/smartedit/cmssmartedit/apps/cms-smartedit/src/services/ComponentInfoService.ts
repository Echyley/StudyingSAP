/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Inject, Injectable } from '@angular/core';
import {
    COMPONENT_CREATED_EVENT,
    COMPONENT_REMOVED_EVENT,
    COMPONENT_UPDATED_EVENT,
    ICMSComponent
} from 'cmscommons';
import {
    CrossFrameEventService,
    Deferred,
    EVENTS,
    LogService,
    OVERLAY_RERENDERED_EVENT,
    PromiseUtils,
    TypedMap,
    UUID_ATTRIBUTE,
    YJQUERY_TOKEN,
    CMSModesService,
    CmsitemsRestService
} from 'smarteditcommons';

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
@Injectable()
export class ComponentInfoService {
    private cachedComponents: TypedMap<ICMSComponent> = {};
    private promisesQueue: TypedMap<Deferred<ICMSComponent>> = {};

    constructor(
        @Inject(YJQUERY_TOKEN) private yjQuery: JQueryStatic,
        private logService: LogService,
        private crossFrameEventService: CrossFrameEventService,
        private cmsitemsRestService: CmsitemsRestService,
        private promiseUtils: PromiseUtils,
        private cMSModesService: CMSModesService
    ) {
        this.crossFrameEventService.subscribe(OVERLAY_RERENDERED_EVENT, (eventId, data) => {
            this.onOverlayReRendered(data);
        });
        this.crossFrameEventService.subscribe(COMPONENT_CREATED_EVENT, (eventId, data) => {
            this.onComponentAdded(data);
        });
        this.crossFrameEventService.subscribe(COMPONENT_UPDATED_EVENT, (eventId, data) => {
            this.onComponentAdded(data);
        });
        this.crossFrameEventService.subscribe(COMPONENT_REMOVED_EVENT, (eventId, data) => {
            this.onComponentRemoved(data);
        });

        // clear cache
        this.crossFrameEventService.subscribe(EVENTS.PAGE_CHANGE, () => this.clearCache());
        this.crossFrameEventService.subscribe(EVENTS.USER_HAS_CHANGED, () => this.clearCache());
    }

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
    public async getById(uuid: string, forceRetrieval?: boolean): Promise<ICMSComponent> {
        const uuidSelector = `[${UUID_ATTRIBUTE}='${uuid}']`;
        if (
            !forceRetrieval &&
            !this.cachedComponents[uuid] &&
            !document.querySelectorAll(uuidSelector).length
        ) {
            // For hidden components that are not present in the DOM
            forceRetrieval = true;
        }
        if (this.isComponentCached(uuid)) {
            return this.cachedComponents[uuid];
        } else if (forceRetrieval) {
            return this.getComponentDataByUUID(uuid);
        } else {
            const deferred = this.promisesQueue[uuid] || this.promiseUtils.defer<ICMSComponent>();

            if (!this.promisesQueue[uuid]) {
                this.promisesQueue[uuid] = deferred;
            }

            return deferred.promise;
        }
    }

    private resolvePromises(data: { response: ICMSComponent[] } | ICMSComponent): void {
        ((data.response ? data.response : [data]) as ICMSComponent[]).forEach((component) => {
            this.cachedComponents[component.uuid] = component;
            if (this.promisesQueue[component.uuid]) {
                this.promisesQueue[component.uuid].resolve(component);
                delete this.promisesQueue[component.uuid];
            }
        });
    }

    private rejectPromises(uuids: string[], error: any): void {
        this.logService.error('componentInfoService:: getById error:', error.message);
        uuids.forEach((uuid) => {
            if (this.promisesQueue[uuid]) {
                this.promisesQueue[uuid].reject(error);
                delete this.promisesQueue[uuid];
            }
        });
    }

    private async getComponentDataByUUID(uuid: string): Promise<ICMSComponent> {
        try {
            const response = await this.cmsitemsRestService.getById<ICMSComponent>(uuid);
            this.resolvePromises(response as any);
            return this.cachedComponents[uuid];
        } catch (error) {
            this.rejectPromises([uuid], error);
            throw error;
        }
    }

    private async getComponentsDataByUUIDs(uuids: string[]): Promise<void> {
        try {
            const components = await this.cmsitemsRestService.getByIds<ICMSComponent>(
                uuids,
                'DEFAULT'
            );
            this.resolvePromises(components);
        } catch (e) {
            this.rejectPromises(uuids, e);
        }
    }

    private onComponentsAddedToOverlay(addedComponentsDomElements: HTMLElement[]): void {
        const uuids = addedComponentsDomElements
            .map((component) => this.yjQuery(component).attr(UUID_ATTRIBUTE))
            .filter((uuid) => !Object.keys(this.cachedComponents).includes(uuid));

        if (uuids.length) {
            this.getComponentsDataByUUIDs(uuids);
        }
    }

    // delete from the cache the components that were removed from the DOM
    // note: components that are still in the DOM were only removed from the overlay
    private onComponentsRemovedFromOverlay(removedComponentsDomElements: HTMLElement[]): void {
        removedComponentsDomElements
            .filter((component) => {
                const uuidSelector = `[${UUID_ATTRIBUTE}='${this.yjQuery(component).attr(
                    UUID_ATTRIBUTE
                )}']`;
                return !this.yjQuery(uuidSelector).length;
            })
            .filter((component) =>
                Object.keys(this.cachedComponents).includes(
                    this.yjQuery(component).attr(UUID_ATTRIBUTE)
                )
            )
            .map((component) => this.yjQuery(component).attr(UUID_ATTRIBUTE))
            .forEach((uuid) => {
                delete this.cachedComponents[uuid];
            });
    }

    private forceAddComponent(cmsComponentToAdd: ICMSComponent): void {
        this.resolvePromises({
            response: [cmsComponentToAdd]
        });
    }

    private forceRemoveComponent(componentToRemove: ICMSComponent): void {
        delete this.cachedComponents[componentToRemove.uuid];
    }

    private isComponentCached(componentUuid: string): boolean {
        return !!this.cachedComponents[componentUuid];
    }

    private clearCache(): void {
        this.cachedComponents = {};
        this.promisesQueue = {};
    }

    //  CMSX-10058: In versioning perspective, edit page is not allowed, do not need to update the component cache
    private async onOverlayReRendered(data): Promise<void> {
        const isVersioningPerspectiveActive =
            await this.cMSModesService.isVersioningPerspectiveActive();
        if (data && !isVersioningPerspectiveActive) {
            if (data.addedComponents && data.addedComponents.length) {
                this.onComponentsAddedToOverlay(data.addedComponents);
            }
            if (data.removedComponents && data.removedComponents.length) {
                this.onComponentsRemovedFromOverlay(data.removedComponents);
            }
        }
    }

    // Components added & removed from storefront page.
    private onComponentAdded(data): void {
        this.forceAddComponent(data);
    }

    private onComponentRemoved(data): void {
        this.forceRemoveComponent(data);
    }
}
