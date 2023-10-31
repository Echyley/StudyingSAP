/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    EventEmitter,
    Input,
    OnInit,
    Output,
    ViewEncapsulation,
    ViewRef
} from '@angular/core';

import {
    FetchPageStrategy,
    IPerspectiveService,
    LogService,
    Page,
    CMSModesService,
    IPageVersion,
    PageVersioningService
} from 'smarteditcommons';

/**
 * Represents a panel where versions are displayed and selected.
 * The users can also query for specific versions.
 */
@Component({
    selector: 'se-versions-panel',
    templateUrl: './VersionsPanelComponent.html',
    styleUrls: ['./VersionsPanelComponent.scss'],
    encapsulation: ViewEncapsulation.None,
    host: {
        '[class.se-versions-panel]': 'true'
    },
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VersionsPanelComponent implements OnInit {
    @Input() pageUuid: string;
    /** Ensure that panel gets closed. */
    @Output() switchMode: EventEmitter<boolean> = new EventEmitter();

    public searchTerm = '';
    public versionsFound: number;
    public versionItems: IPageVersion[];
    public showManageButton: boolean;
    public showManageLink: boolean;

    private totalPageVersions = 0;
    private isLoading = true;
    private VERSIONING_MODE_KEY = CMSModesService.VERSIONING_PERSPECTIVE_KEY;

    constructor(
        private pageVersioningService: PageVersioningService,
        private perspectiveService: IPerspectiveService,
        private logService: LogService,
        private cdr: ChangeDetectorRef
    ) {}

    ngOnInit(): void {
        this.showManageLink = this.canShowManageLink();
    }

    public onVersionItemsLoaded(versionItems: IPageVersion[]): void {
        this.versionItems = versionItems;
    }

    public onSearchTermChanged(newSearchTerm: string): void {
        this.searchTerm = newSearchTerm;
    }

    public switchToVersioningMode(): void {
        this.perspectiveService.switchTo(this.VERSIONING_MODE_KEY);

        this.switchMode.emit();
    }

    public fetchPageOfVersions: FetchPageStrategy<IPageVersion> = (
        search: string,
        pageSize: number,
        currentPage: number
    ) => this.loadVersions(search, pageSize, currentPage);

    public pageHasVersions(): boolean {
        return this.totalPageVersions > 0;
    }

    public pageHasVersionsOrIsLoading(): boolean {
        return this.pageHasVersions() || this.isLoading;
    }

    private canShowManageButton(): boolean {
        return this.showManageLink && this.pageHasVersions();
    }

    private canShowManageLink(): boolean {
        const activePerspective = this.perspectiveService.getActivePerspective();
        return activePerspective && activePerspective.key !== this.VERSIONING_MODE_KEY;
    }

    private async loadVersions(
        mask: string,
        pageSize: number,
        currentPage: number
    ): Promise<Page<IPageVersion>> {
        try {
            const page = await this.pageVersioningService.findPageVersions({
                pageUuid: this.pageUuid,
                currentPage,
                mask,
                pageSize
            });
            this.versionsFound = page.pagination.totalCount;

            if (this.isLoading) {
                // only for initial load, when component is created
                this.isLoading = false;
                this.totalPageVersions = this.versionsFound;
                this.showManageButton = this.canShowManageButton();
            }
            if (!(this.cdr as ViewRef).destroyed) {
                this.cdr.detectChanges();
            }

            return page;
        } catch {
            this.logService.error(`Cannot find page versions for page ${this.pageUuid}.`);
            return null;
        }
    }
}
