/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { EVENT_PAGE_STATUS_UPDATED_IN_ACTIVE_CV } from 'cmscommons';
import { ManagePageService } from 'cmssmarteditcontainer/services/pages/ManagePageService';
import {
    CrossFrameEventService,
    DROPDOWN_MENU_ITEM_DATA,
    ICatalogService,
    IDropdownMenuItemData,
    MultiNamePermissionContext,
    CmsitemsRestService,
    ICMSPage,
    CMSPageStatus,
    IUserTrackingService,
    USER_TRACKING_FUNCTIONALITY
} from 'smarteditcommons';

@Component({
    selector: 'se-permanently-delete-page-item',
    templateUrl: 'PermanentlyDeletePageItemComponent.html',
    styleUrls: ['../PageItems.scss']
})
export class PermanentlyDeletePageItemComponent implements OnInit, OnDestroy {
    public pageInfo: ICMSPage;
    public permanentlyDeletePagePermission: MultiNamePermissionContext[];

    private unregPageSyncStatusUpdate: () => void;
    private isDeletable = false;

    constructor(
        private managePageService: ManagePageService,
        private cmsitemsRestService: CmsitemsRestService,
        private catalogService: ICatalogService,
        private crossFrameEventService: CrossFrameEventService,
        private userTrackingService: IUserTrackingService,
        @Inject(DROPDOWN_MENU_ITEM_DATA) private dropdownMenuData: IDropdownMenuItemData
    ) {}

    async ngOnInit(): Promise<void> {
        this.pageInfo = this.dropdownMenuData.selectedItem;
        this.permanentlyDeletePagePermission = [
            {
                names: ['se.permanently.delete.page.type'],
                context: {
                    typeCode: this.pageInfo.typeCode
                }
            }
        ];

        this.unregPageSyncStatusUpdate = this.crossFrameEventService.subscribe(
            EVENT_PAGE_STATUS_UPDATED_IN_ACTIVE_CV,
            this.fetchPageDeletableConditions.bind(this)
        );

        await this.fetchPageDeletableConditions();
    }

    ngOnDestroy(): void {
        this.unregPageSyncStatusUpdate();
    }

    public permanentlyDelete(): void {
        this.userTrackingService.trackingUserAction(
            USER_TRACKING_FUNCTIONALITY.PAGE_MANAGEMENT,
            'Permanently Delete'
        );
        if (this.isDeletable) {
            this.managePageService.hardDeletePage(this.pageInfo);
        }
    }

    public isDeleteButtonDisabled(): boolean {
        return !this.isDeletable;
    }

    private async fetchPageDeletableConditions(): Promise<void> {
        const uriContext = await this.catalogService.retrieveUriContext();
        const activeVersion = await this.catalogService.getContentCatalogActiveVersion(uriContext);
        const cmsItemResult = await this.cmsitemsRestService.get({
            pageSize: 1,
            currentPage: 0,
            typeCode: 'AbstractPage',
            fields: 'BASIC',
            itemSearchParams: `uid:${this.pageInfo.uid}`,
            catalogId: uriContext.CURRENT_CONTEXT_CATALOG,
            catalogVersion: activeVersion
        });

        this.isDeletable =
            cmsItemResult.pagination.totalCount === 0 ||
            cmsItemResult.response[0].pageStatus === CMSPageStatus.DELETED;
    }
}
