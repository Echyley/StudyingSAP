import { ChangeDetectorRef, OnChanges } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { ISyncStatus } from 'cmscommons';
import { ICatalogService, ISharedDataService, L10nPipe, CmsitemsRestService } from 'smarteditcommons';
import { PageSyncConditions } from '../../types';
export declare class PageSynchronizationHeaderComponent implements OnChanges {
    private sharedDataService;
    private catalogService;
    private cmsitemsRestService;
    private translateService;
    private l10nPipe;
    private cdr;
    syncStatus: ISyncStatus;
    pageSyncConditions: PageSyncConditions;
    ready: boolean;
    headerText: string;
    constructor(sharedDataService: ISharedDataService, catalogService: ICatalogService, cmsitemsRestService: CmsitemsRestService, translateService: TranslateService, l10nPipe: L10nPipe, cdr: ChangeDetectorRef);
    ngOnChanges(): Promise<void>;
    getSubHeaderText(): string;
    isNewPage(): boolean;
    isSyncOldHomeHeader(): boolean;
    isDefaultSubHeader(): boolean;
    private updateHeaderText;
    private userIsInsidePage;
    private getCurrentCatalogIdFromExperience;
    private fetchUnavailableDependencies;
}
