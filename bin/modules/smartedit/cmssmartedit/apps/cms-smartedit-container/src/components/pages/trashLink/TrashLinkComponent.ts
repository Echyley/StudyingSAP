/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    Component,
    OnInit,
    OnDestroy,
    ChangeDetectionStrategy,
    ChangeDetectorRef
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TRASHED_PAGE_LIST_PATH } from 'cmscommons';
import { ManagePageService } from 'cmssmarteditcontainer/services/pages/ManagePageService';
import {
    ICatalogService,
    IUriContext,
    IUrlService,
    SystemEventService,
    SmarteditRoutingService,
    EVENT_CONTENT_CATALOG_UPDATE,
    TypedMap
} from 'smarteditcommons';

@Component({
    selector: 'se-trash-link',
    templateUrl: './TrashLinkComponent.html',
    styleUrls: ['../pageList.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TrashLinkComponent implements OnInit, OnDestroy {
    public trashedPagesTranslationData: TypedMap<number>;
    public isNonActiveCatalog: boolean;

    private siteId: string;
    private catalogId: string;
    private catalogVersion: string;
    private uriContext: IUriContext;
    private unsubscribeContentCatalogUpdateEvent: () => void;

    constructor(
        private route: ActivatedRoute,
        private routingsService: SmarteditRoutingService,
        private managePageService: ManagePageService,
        private urlService: IUrlService,
        private catalogService: ICatalogService,
        private readonly systemEventService: SystemEventService,
        private readonly cdr: ChangeDetectorRef
    ) {
        this.isNonActiveCatalog = false;
    }

    async ngOnInit(): Promise<void> {
        ({
            siteId: this.siteId,
            catalogId: this.catalogId,
            catalogVersion: this.catalogVersion
        } = this.route.snapshot.params);
        this.uriContext = this.urlService.buildUriContext(
            this.siteId,
            this.catalogId,
            this.catalogVersion
        );
        const isNonActiveCatalog = await this.catalogService.isContentCatalogVersionNonActive();
        this.isNonActiveCatalog = isNonActiveCatalog;
        if (isNonActiveCatalog) {
            this.updateTrashedPagesCount();
        }

        this.unsubscribeContentCatalogUpdateEvent = this.systemEventService.subscribe(
            EVENT_CONTENT_CATALOG_UPDATE,
            () => this.updateTrashedPagesCount()
        );
    }

    ngOnDestroy(): void {
        this.unsubscribeContentCatalogUpdateEvent();
    }

    /**
     * Retrieves the total count of all soft-deleted pages.
     */
    public async updateTrashedPagesCount(): Promise<void> {
        const trashedPagesCount = await this.managePageService.getSoftDeletedPagesCount(
            this.uriContext
        );
        this.trashedPagesTranslationData = {
            totalCount: trashedPagesCount
        };
        this.cdr.detectChanges();
    }

    public goToTrash(): void {
        this.routingsService.go(
            TRASHED_PAGE_LIST_PATH.replace(':siteId', this.siteId)
                .replace(':catalogId', this.catalogId)
                .replace(':catalogVersion', this.catalogVersion)
        );
    }
}
