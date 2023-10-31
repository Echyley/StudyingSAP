/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Inject,
    OnDestroy,
    OnInit,
    ViewRef
} from '@angular/core';
import { ManagePageService } from 'cmssmarteditcontainer/services/pages/ManagePageService';
import {
    CrossFrameEventService,
    EVENTS,
    EVENT_CONTENT_CATALOG_UPDATE,
    IUriContext,
    Nullable,
    SystemEventService,
    ToolbarItemInternal,
    TOOLBAR_ITEM,
    IPageService,
    ICMSPage,
    IUserTrackingService,
    USER_TRACKING_FUNCTIONALITY
} from 'smarteditcommons';
import { PageFacade } from '../../../facades';

@Component({
    selector: 'se-delete-page-toolbar-item',
    templateUrl: './DeletePageToolbarItemComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DeletePageToolbarItemComponent implements OnInit, OnDestroy {
    public isReady: boolean;
    public isDeletePageEnabled: boolean;
    public tooltipMessage: Nullable<string>;
    public uriContext: IUriContext;
    public pageInfo: ICMSPage;

    private unRegPageChange: () => void;

    constructor(
        private pageService: IPageService,
        private pageFacade: PageFacade,
        private managePageService: ManagePageService,
        private systemEventService: SystemEventService,
        private crossFrameEventService: CrossFrameEventService,
        private cdr: ChangeDetectorRef,
        private userTrackingService: IUserTrackingService,
        @Inject(TOOLBAR_ITEM) public toolbarItem: ToolbarItemInternal
    ) {}

    ngOnInit(): Promise<void> {
        this.unRegPageChange = this.crossFrameEventService.subscribe(EVENTS.PAGE_CHANGE, () =>
            this.updateToolbar()
        );
        return this.updateToolbar();
    }

    ngOnDestroy(): void {
        this.unRegPageChange();
    }

    get isDeletePageDisabled(): boolean {
        return !this.isDeletePageEnabled;
    }

    public async deletePage(): Promise<void> {
        this.userTrackingService.trackingUserAction(
            USER_TRACKING_FUNCTIONALITY.TOOL_BAR,
            'Delete Page'
        );
        const pageInfo = await this.pageService.getCurrentPageInfo();
        await this.managePageService.softDeletePage(pageInfo, this.uriContext);

        this.systemEventService.publishAsync(EVENT_CONTENT_CATALOG_UPDATE);
    }

    private async updateToolbar(): Promise<void> {
        this.markAsNotReady();
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }

        const uriContext = await this.pageFacade.retrievePageUriContext();
        if (!uriContext) {
            return;
        }
        this.uriContext = uriContext;

        this.pageInfo = await this.pageService.getCurrentPageInfo();
        this.isDeletePageEnabled = await this.managePageService.isPageTrashable(
            this.pageInfo,
            this.uriContext
        );

        this.tooltipMessage = await this.resolveTooltipMessage(
            this.isDeletePageEnabled,
            this.pageInfo,
            this.uriContext
        );

        this.markAsReady();
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    private markAsNotReady(): void {
        this.isReady = false;
        this.isDeletePageEnabled = false;
        this.tooltipMessage = null;
    }

    private markAsReady(): void {
        this.isReady = true;
    }

    private async resolveTooltipMessage(
        isDeleteEnabled: boolean,
        pageInfo: ICMSPage,
        uriContext: IUriContext
    ): Promise<Nullable<string>> {
        return await (isDeleteEnabled
            ? null
            : this.managePageService.getDisabledTrashTooltipMessage(pageInfo, uriContext));
    }
}
