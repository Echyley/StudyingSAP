/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, OnInit, OnDestroy, Inject, ViewEncapsulation } from '@angular/core';
import { CMSItemStructure } from 'cmscommons';
import {
    ICatalogService,
    IUriContext,
    MultiNamePermissionContext,
    SystemEventService,
    TOOLBAR_ITEM,
    ToolbarItemInternal,
    EVENT_CONTENT_CATALOG_UPDATE,
    IPageService,
    ICMSPage,
    IUserTrackingService,
    USER_TRACKING_FUNCTIONALITY
} from 'smarteditcommons';
import { PageInfoForViewing, PageInfoMenuService } from '../services';

/**
 * This component is used to create a Page Info Menu to be displayed in the toolbar.
 */
@Component({
    selector: 'se-page-info-menu',
    templateUrl: './PageInfoMenuComponent.html',
    styleUrls: ['./PageInfoMenuComponent.scss'],
    encapsulation: ViewEncapsulation.None
})
export class PageInfoMenuComponent implements OnInit, OnDestroy {
    public pageInfo: PageInfoForViewing;
    public pageStructure: CMSItemStructure;
    public isReady: boolean;
    public cmsPage: ICMSPage = null;
    public uriContext: IUriContext = null;
    public editPagePermission: MultiNamePermissionContext[];

    private unRegContentCatalogUpdate: () => void;

    constructor(
        @Inject(TOOLBAR_ITEM) public toolbarItem: ToolbarItemInternal,
        private pageInfoMenuService: PageInfoMenuService,
        private pageService: IPageService,
        private catalogService: ICatalogService,
        private systemEventService: SystemEventService,
        private userTrackingService: IUserTrackingService
    ) {
        this.isReady = false;
    }

    async ngOnInit(): Promise<void> {
        this.unRegContentCatalogUpdate = this.systemEventService.subscribe(
            EVENT_CONTENT_CATALOG_UPDATE,
            () => this.setPageData()
        );
        await this.setPageData();
    }

    ngOnDestroy(): void {
        this.unRegContentCatalogUpdate();
    }

    public onEditPageClick(): void {
        this.pageInfoMenuService.openPageEditor(this.pageInfo.content);
        this.closeMenu();
    }

    public async setPageData(): Promise<void> {
        const [page, uriContext] = await Promise.all([
            this.pageService.getCurrentPageInfo(),
            this.catalogService.retrieveUriContext()
        ]);
        this.cmsPage = page;
        this.uriContext = uriContext;

        this.editPagePermission = [
            {
                names: ['se.edit.page.type'],
                context: {
                    typeCode: page.typeCode
                }
            },
            {
                names: ['se.edit.page.link']
            }
        ];
    }

    public async onDropdownToggle(open: boolean): Promise<void> {
        if (!open) {
            return;
        }
        this.userTrackingService.trackingUserAction(USER_TRACKING_FUNCTIONALITY.TOOL_BAR, 'Info');

        const pageInfo =
            (await this.pageInfoMenuService.getCurrentPageInfo()) as PageInfoForViewing;
        this.pageInfo = pageInfo;

        const pageStructure = (await this.pageInfoMenuService.getPageStructureForViewing(
            pageInfo.typeCode,
            pageInfo.defaultPage
        )) as CMSItemStructure;
        this.pageStructure = pageStructure;
        this.isReady = true;
    }

    private closeMenu(): void {
        this.toolbarItem.isOpen = false;
    }
}
