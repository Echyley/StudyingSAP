/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Inject, Injectable } from '@angular/core';
import {
    CONTENT_SLOTS_TYPE_RESTRICTION_RESOURCE_URI,
    IPageContentSlotsComponentsRestService,
    TypePermissionsRestService,
    CmsDragAndDropDragInfo
} from 'cmscommons';
import { flatten, uniq, chunk, includes } from 'lodash';
import {
    GatewayProxied,
    IPageInfoService,
    LogService,
    YJQUERY_TOKEN,
    stringUtils,
    CONTENT_SLOT_TYPE,
    TypedMap,
    IRestServiceFactory,
    IRestService,
    CrossFrameEventService,
    EVENTS,
    CMSModesService,
    ISlotRestrictionsService,
    COMPONENT_IN_SLOT_STATUS,
    IComponentHandlerService,
    SlotSharedService
} from 'smarteditcommons';
import { CmsDragAndDropCachedSlot } from './dragAndDrop';

export interface ContentSlot {
    /**
     * The uid of content slot.
     *
     * E.g. 'NavigationBarSlot'
     */
    contentSlotUid: string;
    /**
     * Components types that can be placed within the slot.
     *
     * E.g.
     * ['NavigationBarComponent', 'SimpleBannerComponent', 'BannerComponent', 'CMSParagraphComponent']
     */
    validComponentTypes: string[];
}

@GatewayProxied(
    'getAllComponentTypesSupportedOnPage',
    'getSlotRestrictions',
    'determineComponentStatusInSlot',
    'isSlotEditable'
)
@Injectable()
export class SlotRestrictionsService extends ISlotRestrictionsService {
    private currentPageId: string;
    private slotRestrictions: TypedMap<string[]>;
    private slotsRestrictionsRestService: IRestService<ContentSlot[]>;
    private readonly CONTENT_SLOTS_TYPE_RESTRICTION_FETCH_LIMIT: number;

    constructor(
        private componentHandlerService: IComponentHandlerService,
        private logService: LogService,
        private pageContentSlotsComponentsRestService: IPageContentSlotsComponentsRestService,
        private pageInfoService: IPageInfoService,
        private restServiceFactory: IRestServiceFactory,
        private slotSharedService: SlotSharedService,
        private typePermissionsRestService: TypePermissionsRestService,
        @Inject(YJQUERY_TOKEN) private yjQuery: JQueryStatic,
        crossFrameEventService: CrossFrameEventService,
        private cMSModesService: CMSModesService
    ) {
        super();

        this.currentPageId = null;
        this.slotRestrictions = {};
        this.CONTENT_SLOTS_TYPE_RESTRICTION_FETCH_LIMIT = 100;

        crossFrameEventService.subscribe(EVENTS.PAGE_CHANGE, async () => {
            //  CMSX-10058: In versioning perspective, edit page is not allowed, do not need to update the slots restrictions cache
            const isVersioningPerspectiveActive =
                await this.cMSModesService.isVersioningPerspectiveActive();
            if (!isVersioningPerspectiveActive) {
                this.emptyCache();
                this.cacheSlotsRestrictions();
            }
        });
    }

    /**
     * @deprecated since 2005
     */
    public async getAllComponentTypesSupportedOnPage(): Promise<string[] | void> {
        const slots = this.yjQuery(this.componentHandlerService.getAllSlotsSelector());
        const slotIds: string[] = slots
            .get()
            .map((slot) => this.componentHandlerService.getId(slot));
        const slotRestrictionPromises = slotIds.map((slotId) => this.getSlotRestrictions(slotId));

        try {
            const arrayOfSlotRestrictions = await Promise.all(slotRestrictionPromises);
            return flatten<any>(arrayOfSlotRestrictions);
        } catch (error) {
            this.logService.info(error);
            return Promise.reject();
        }
    }

    public async getSlotRestrictions(slotId: string): Promise<string[] | void> {
        const pageId = await this.getPageUID(this.currentPageId);
        const isExternalSlot = await this.isExternalSlot(slotId);
        this.currentPageId = pageId;

        const restrictionId = this.getEntryId(this.currentPageId, slotId);
        if (this.slotRestrictions[restrictionId]) {
            return this.slotRestrictions[restrictionId];
        } else if (isExternalSlot) {
            this.slotRestrictions[restrictionId] = [];
        }
        return [];
    }

    /**
     * This methods determines whether a component of the provided type is allowed in the slot.
     *
     * @param slot The slot for which to verify if it allows a component of the provided type.
     * @returns Promise containing COMPONENT_IN_SLOT_STATUS (ALLOWED, DISALLOWED, MAYBEALLOWED) string that determines whether a component of the provided type is allowed in the slot.
     *
     * TODO: The name is misleading. To not introduce breaking change in 2105, consider changing the name in the next release (after 2105).
     * Candidates: "getComponentStatusInSlot", "determineComponentStatusInSlot"
     */
    public async isComponentAllowedInSlot(
        slot: CmsDragAndDropCachedSlot,
        dragInfo: CmsDragAndDropDragInfo
    ): Promise<COMPONENT_IN_SLOT_STATUS> {
        return this.determineComponentStatusInSlot(slot.id, dragInfo);
    }

    public async determineComponentStatusInSlot(
        slotId: string,
        dragInfo: CmsDragAndDropDragInfo
    ): Promise<COMPONENT_IN_SLOT_STATUS> {
        const currentSlotRestrictions = await this.getSlotRestrictions(slotId);
        const componentsForSlot =
            await this.pageContentSlotsComponentsRestService.getComponentsForSlot(slotId);
        const isComponentIdAllowed =
            slotId === dragInfo.slotId ||
            !componentsForSlot.some((component) => component.uid === dragInfo.componentId);

        if (isComponentIdAllowed) {
            if (currentSlotRestrictions) {
                return includes(currentSlotRestrictions, dragInfo.componentType)
                    ? COMPONENT_IN_SLOT_STATUS.ALLOWED
                    : COMPONENT_IN_SLOT_STATUS.DISALLOWED;
            }
            return COMPONENT_IN_SLOT_STATUS.MAYBEALLOWED;
        }
        return COMPONENT_IN_SLOT_STATUS.DISALLOWED;
    }

    public async isSlotEditable(slotId: string): Promise<boolean> {
        // This method can get called with slotId as "undefined", which means that the slot has not been rendered yet.
        if (!slotId) {
            return Promise.resolve(false);
        }

        const slotPermissions = await this.typePermissionsRestService.hasUpdatePermissionForTypes([
            CONTENT_SLOT_TYPE
        ]);

        const isShared = await this.slotSharedService.isSlotShared(slotId);
        let result = slotPermissions[CONTENT_SLOT_TYPE];
        if (isShared) {
            const isExternalSlot = await this.isExternalSlot(slotId);
            result = result && !isExternalSlot && !this.slotSharedService.areSharedSlotsDisabled();
        }

        return result;
    }

    private emptyCache(): void {
        this.slotRestrictions = {};
        this.currentPageId = null;
    }

    private async cacheSlotsRestrictions(): Promise<void> {
        const originalSlotIds = this.componentHandlerService.getAllSlotUids() || [];

        const nonExternalOriginalSlotIds = [];
        await Promise.all(
            originalSlotIds.map(async (slotId) => {
                const isExternalSlot = await this.isExternalSlot(slotId);
                if (!isExternalSlot) {
                    nonExternalOriginalSlotIds.push(slotId);
                }
            })
        );

        const uniqueSlotIds = uniq(nonExternalOriginalSlotIds);
        const chunks = chunk(uniqueSlotIds, this.CONTENT_SLOTS_TYPE_RESTRICTION_FETCH_LIMIT);
        return this.recursiveFetchSlotsRestrictions(chunks, 0);
    }

    // Recursively fetch slots restrictions by the number of chunks of slotIds split by fetch limit
    private async recursiveFetchSlotsRestrictions(
        slotIdsByChunks: string[][],
        chunkIndex: number
    ): Promise<void> {
        if (chunkIndex === slotIdsByChunks.length) {
            return;
        }

        await this.fetchSlotsRestrictions(slotIdsByChunks[chunkIndex]);
        this.recursiveFetchSlotsRestrictions(slotIdsByChunks, chunkIndex + 1);
    }

    // Fetch slot restriction and cache them in-memory
    private async fetchSlotsRestrictions(slotIds: string[]): Promise<void> {
        const pageId = await this.getPageUID(this.currentPageId);
        this.currentPageId = pageId;

        this.slotsRestrictionsRestService =
            this.slotsRestrictionsRestService ||
            this.restServiceFactory.get(
                CONTENT_SLOTS_TYPE_RESTRICTION_RESOURCE_URI,
                this.currentPageId
            );

        try {
            const contentSlotsResponse = await this.slotsRestrictionsRestService.save({
                slotIds,
                pageUid: this.currentPageId
            });
            const contentSlots = contentSlotsResponse || [];

            contentSlots.forEach((slot) => {
                const restrictionId = this.getEntryId(this.currentPageId, slot.contentSlotUid);
                this.slotRestrictions[restrictionId] = slot.validComponentTypes;
            });
        } catch (error) {
            this.logService.info(error);
            throw new Error(error);
        }
    }

    private getPageUID(pageUID: string): Promise<string> {
        return !stringUtils.isBlank(pageUID)
            ? Promise.resolve(pageUID)
            : this.pageInfoService.getPageUID();
    }

    private getEntryId(pageId: string, slotId: string): string {
        return `${pageId}_${slotId}`;
    }

    private isExternalSlot(slotId: string): Promise<boolean> {
        return this.componentHandlerService.isExternalComponent(slotId, CONTENT_SLOT_TYPE);
    }
}
