/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Input,
    OnChanges,
    ViewRef
} from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { ISyncStatus } from 'cmscommons';
import { take } from 'rxjs/operators';
import {
    ICatalogService,
    IExperience,
    IExperiencePageContext,
    ISharedDataService,
    L10nPipe,
    EXPERIENCE_STORAGE_KEY,
    CmsitemsRestService
} from 'smarteditcommons';
import { PageSyncConditions } from '../../types';

@Component({
    selector: 'se-page-synchronization-header',
    templateUrl: './PageSynchronizationHeaderComponent.html',
    styleUrls: ['./PageSynchronizationHeaderComponent.scss'],
    providers: [L10nPipe],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PageSynchronizationHeaderComponent implements OnChanges {
    @Input() syncStatus: ISyncStatus;
    @Input() pageSyncConditions: PageSyncConditions;

    public ready: boolean;
    public headerText: string;

    constructor(
        private sharedDataService: ISharedDataService,
        private catalogService: ICatalogService,
        private cmsitemsRestService: CmsitemsRestService,
        private translateService: TranslateService,
        private l10nPipe: L10nPipe,
        private cdr: ChangeDetectorRef
    ) {
        this.ready = false;
    }

    async ngOnChanges(): Promise<void> {
        if (!this.syncStatus) {
            return;
        }
        this.ready = false;

        await this.updateHeaderText();

        this.ready = true;

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    public getSubHeaderText(): string {
        if (this.isDefaultSubHeader()) {
            // text about synchronization of non-shared content slots and page information
            return 'se.cms.synchronization.page.header';
        } else if (this.isSyncOldHomeHeader()) {
            // text about an attempt to synchronize the old Homepage
            return 'se.cms.synchronization.page.header.old.homepage';
        }
        return '';
    }

    public isNewPage(): boolean {
        return this.pageSyncConditions.pageHasNoDepOrNoSyncStatus;
    }

    /**
     * Returns true for the following scenario.
     *
     * The new Homepage has been created which has not been synchronized yet.
     * The user attempts to synchronize the old Homepage.
     */
    public isSyncOldHomeHeader(): boolean {
        return (
            this.pageSyncConditions.pageHasSyncStatus && !this.pageSyncConditions.canSyncHomepage
        );
    }

    public isDefaultSubHeader(): boolean {
        return !this.isSyncOldHomeHeader() && !this.isNewPage();
    }

    /** Updates header text on sync status change */
    private async updateHeaderText(): Promise<void> {
        const experience = (await this.sharedDataService.get(
            EXPERIENCE_STORAGE_KEY
        )) as IExperience;
        const { catalogId, catalogName } = await this.getCurrentCatalogIdFromExperience(experience);

        const catalogVersion = await this.catalogService.getActiveContentCatalogVersionByCatalogId(
            catalogId
        );

        const itemNames = await (this.pageSyncConditions.pageHasUnavailableDependencies
            ? this.fetchUnavailableDependencies()
            : null);

        this.headerText = this.pageSyncConditions.pageHasUnavailableDependencies
            ? this.translateService.instant(
                  'se.cms.synchronization.page.unavailable.items.description',
                  {
                      itemNames,
                      catalogName,
                      catalogVersion
                  }
              )
            : this.translateService.instant('se.cms.synchronization.page.new.description', {
                  catalogName,
                  catalogVersion
              });
    }

    private userIsInsidePage(pageContext: IExperiencePageContext): boolean {
        return !!pageContext;
    }

    private async getCurrentCatalogIdFromExperience({
        pageContext,
        catalogDescriptor
    }: IExperience): Promise<{ catalogId: string; catalogName: string }> {
        const catalogId = this.userIsInsidePage(pageContext)
            ? pageContext.catalogId
            : catalogDescriptor.catalogId;
        const catalogName = this.userIsInsidePage(pageContext)
            ? pageContext.catalogName
            : catalogDescriptor.name;
        const catalogNameTranslated = await this.l10nPipe
            .transform(catalogName)
            .pipe(take(1))
            .toPromise();

        return {
            catalogId,
            catalogName: catalogNameTranslated
        };
    }

    private fetchUnavailableDependencies(): Promise<string> {
        const itemIds = this.syncStatus.unavailableDependencies.map(
            ({ itemId }: ISyncStatus) => itemId
        );
        return this.cmsitemsRestService
            .getByIds(itemIds)
            .then(({ response }) => response.map(({ name }) => name).join(', '));
    }
}
