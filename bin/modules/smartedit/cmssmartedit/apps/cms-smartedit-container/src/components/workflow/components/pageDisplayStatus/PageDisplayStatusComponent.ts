/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Input,
    OnDestroy,
    OnInit,
    ViewRef
} from '@angular/core';
import {
    DEFAULT_SYNCHRONIZATION_POLLING as SYNCHRONIZATION_POLLING,
    ISyncStatus
} from 'cmscommons';
import {
    CrossFrameEventService,
    EVENTS,
    Nullable,
    SystemEventService,
    IPageService,
    ICMSPage
} from 'smarteditcommons';
import { WORKFLOW_REFRESH_EVENT } from '../../constants';

@Component({
    selector: 'se-page-display-status',
    templateUrl: './PageDisplayStatusComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PageDisplayStatusComponent implements OnInit, OnDestroy {
    /**
     * Page for which the status is updated.
     *
     * If not provided, the current page will be fetched.
     */
    @Input() cmsPage?: ICMSPage;
    /**
     * Whether to show Last Synchronization Time within Tooltip.
     */
    @Input() showLastSyncTime = true;

    public page: ICMSPage;
    public lastSynchedDate: number;

    private unRegPageSyncEvent: () => void;
    private unRegPageUpdatedEvent: () => void;

    constructor(
        private pageService: IPageService,
        private crossFrameEventService: CrossFrameEventService,
        private systemEventService: SystemEventService,
        private cdr: ChangeDetectorRef
    ) {}

    async ngOnInit(): Promise<void> {
        this.unRegPageSyncEvent = this.crossFrameEventService.subscribe(
            SYNCHRONIZATION_POLLING.FETCH_STATUS,
            (_eventId: string, eventData?: any) => this.updateLastSynchedDate(eventData)
        );

        this.unRegPageUpdatedEvent = this.crossFrameEventService.subscribe(
            EVENTS.PAGE_UPDATED,
            (_eventId: string, eventData?: { uuid: string }) =>
                this.updatePageInfo(eventData?.uuid || null)
        );

        const pageUuid = this.cmsPage?.uuid || null;
        await this.initPageInfo(pageUuid);
    }

    ngOnDestroy(): void {
        this.unRegPageSyncEvent();
        this.unRegPageUpdatedEvent();
    }

    public hasBeenSynchedBefore(): boolean {
        return this.lastSynchedDate !== undefined;
    }

    private async initPageInfo(pageUuid: string): Promise<void> {
        this.page = await (pageUuid ? this.cmsPage : this.pageService.getCurrentPageInfo());
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    private updateLastSynchedDate(syncStatus?: ISyncStatus): void {
        if (
            this.canUpdateLastSynchedDate(syncStatus.itemId) &&
            this.lastSynchedDate !== syncStatus.lastSyncStatus
        ) {
            this.lastSynchedDate = syncStatus.lastSyncStatus;
            if (!(this.cdr as ViewRef).destroyed) {
                this.cdr.detectChanges();
            }
        }
    }

    private async updatePageInfo(updatedPageUuid: Nullable<string>): Promise<void> {
        if (!this.canUpdatePageInfo(updatedPageUuid)) {
            return;
        }

        const displayStatusPrevious = this.page?.displayStatus;
        this.page = await this.pageService.getCurrentPageInfo();
        // used to notify PageWorkflowMenuComponent to display workflow start button when the page has changed from any other status to DRAFT
        if (!!displayStatusPrevious && this.page.displayStatus === 'DRAFT') {
            this.systemEventService.publish(WORKFLOW_REFRESH_EVENT);
        }

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    private canUpdateLastSynchedDate(syncStatusItemId: string): boolean {
        return this.page && this.page.uuid === syncStatusItemId;
    }

    private canUpdatePageInfo(cmsPageUuidToUpdate: Nullable<string>): boolean {
        return !cmsPageUuidToUpdate || (this.page && this.page.uuid === cmsPageUuidToUpdate);
    }
}
