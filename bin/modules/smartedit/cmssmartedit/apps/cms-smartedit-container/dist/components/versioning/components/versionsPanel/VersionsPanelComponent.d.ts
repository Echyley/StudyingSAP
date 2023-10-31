import { ChangeDetectorRef, EventEmitter, OnInit } from '@angular/core';
import { FetchPageStrategy, IPerspectiveService, LogService, IPageVersion, PageVersioningService } from 'smarteditcommons';
export declare class VersionsPanelComponent implements OnInit {
    private pageVersioningService;
    private perspectiveService;
    private logService;
    private cdr;
    pageUuid: string;
    switchMode: EventEmitter<boolean>;
    searchTerm: string;
    versionsFound: number;
    versionItems: IPageVersion[];
    showManageButton: boolean;
    showManageLink: boolean;
    private totalPageVersions;
    private isLoading;
    private VERSIONING_MODE_KEY;
    constructor(pageVersioningService: PageVersioningService, perspectiveService: IPerspectiveService, logService: LogService, cdr: ChangeDetectorRef);
    ngOnInit(): void;
    onVersionItemsLoaded(versionItems: IPageVersion[]): void;
    onSearchTermChanged(newSearchTerm: string): void;
    switchToVersioningMode(): void;
    fetchPageOfVersions: FetchPageStrategy<IPageVersion>;
    pageHasVersions(): boolean;
    pageHasVersionsOrIsLoading(): boolean;
    private canShowManageButton;
    private canShowManageLink;
    private loadVersions;
}
