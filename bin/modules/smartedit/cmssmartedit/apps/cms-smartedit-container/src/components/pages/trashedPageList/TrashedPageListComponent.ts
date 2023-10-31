/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    OnDestroy,
    OnInit,
    ViewEncapsulation,
    ViewRef
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { debounceTime, distinctUntilChanged } from 'rxjs/internal/operators';
import { Subject } from 'rxjs/internal/Subject';
import { Subscription } from 'rxjs/internal/Subscription';
import {
    DynamicPagedListApi,
    DynamicPagedListConfig,
    EVENT_CONTENT_CATALOG_UPDATE,
    IBaseCatalog,
    ICatalogService,
    IDropdownMenuItem,
    IUrlService,
    Pagination,
    SystemEventService,
    TypedMap,
    cmsitemsUri
} from 'smarteditcommons';
import {
    TrashListDropdownItemsWrapperComponent,
    ModifiedTimeWrapperComponent,
    NumberOfRestrictionsWrapperComponent,
    PageStatusWrapperComponent
} from '../pageListComponentWrappers';

@Component({
    selector: 'se-trashed-page-list',
    templateUrl: './TrashedPageListComponent.html',
    styleUrls: ['../pageList.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    encapsulation: ViewEncapsulation.None
})
export class TrashedPageListComponent implements OnInit, OnDestroy {
    // site params
    public siteUID: string;
    public catalogId: string;
    public catalogVersion: string;
    public catalogName: TypedMap<string>;
    public uriContext: any;

    // dynamic paged list params
    public trashedPageListConfig: DynamicPagedListConfig;
    public mask = '';
    public dropdownItems: IDropdownMenuItem[] = [];

    // dynamic paged list API
    public dynamicPagedListApi: DynamicPagedListApi;

    public unsubscribeEventListener: () => void;
    public count = 0;

    private maskSubject$ = new Subject<string>();
    private maskSubscription: Subscription;

    // --------------------------------------------------------------------------------------
    // Constructor
    // --------------------------------------------------------------------------------------
    constructor(
        private catalogService: ICatalogService,
        private route: ActivatedRoute,
        private urlService: IUrlService,
        private systemEventService: SystemEventService,
        private cdr: ChangeDetectorRef
    ) {}

    async ngOnInit(): Promise<void> {
        await this.initialize();
        this.unsubscribeEventListener = this.systemEventService.subscribe(
            EVENT_CONTENT_CATALOG_UPDATE,
            this.onContentCatalogUpdate.bind(this)
        );

        this.maskSubscription = this.maskSubject$
            .pipe(debounceTime(500), distinctUntilChanged())
            .subscribe((newValue) => {
                this.mask = newValue;
                if (!(this.cdr as ViewRef).destroyed) {
                    this.cdr.detectChanges();
                }
            });
    }

    ngOnDestroy(): void {
        this.unsubscribeEventListener();
        this.maskSubscription.unsubscribe();
    }

    public onMaskChange(newValue: string): void {
        this.maskSubject$.next(newValue);
    }

    public onPageItemsUpdate(pagination: Pagination): void {
        this.count = pagination.totalCount;
    }

    public reset(): void {
        this.mask = '';
    }

    public getApi($api: any): void {
        this.dynamicPagedListApi = $api;
    }

    private async initialize(): Promise<void> {
        this.setSiteParams();

        this.setUriContext();

        this.setTrashedListConfigBasis();

        this.setTrashedListColumns();

        return this.setCatalogName();
    }

    private onContentCatalogUpdate(): void {
        if (this.dynamicPagedListApi) {
            this.dynamicPagedListApi.reloadItems();
        }
    }

    private setSiteParams(): void {
        this.siteUID = this.route.snapshot.params.siteId;
        this.catalogId = this.route.snapshot.params.catalogId;
        this.catalogVersion = this.route.snapshot.params.catalogVersion;
    }

    private setUriContext(): void {
        this.uriContext = this.urlService.buildUriContext(
            this.siteUID,
            this.catalogId,
            this.catalogVersion
        );
    }

    private setTrashedListConfigBasis(): void {
        this.trashedPageListConfig = {
            sortBy: 'name',
            reversed: false,
            itemsPerPage: 10,
            displayCount: true,
            uri: cmsitemsUri,
            queryParams: {
                catalogId: this.catalogId,
                catalogVersion: this.catalogVersion,
                typeCode: 'AbstractPage',
                itemSearchParams: 'pageStatus:deleted'
            },
            keys: []
        };
    }

    private async setCatalogName(): Promise<void> {
        const catalogs: IBaseCatalog[] = await this.catalogService.getContentCatalogsForSite(
            this.siteUID
        );
        this.catalogName = catalogs.find(
            (catalog: IBaseCatalog) => catalog.catalogId === this.catalogId
        ).name;
    }

    private setTrashedListColumns(): void {
        this.trashedPageListConfig.keys = [
            {
                property: 'name',
                i18n: 'se.cms.pagelist.headerpagename',
                sortable: true
            },
            {
                property: 'uid',
                i18n: 'se.cms.pagelist.headerpageid',
                sortable: true
            },
            {
                property: 'itemtype',
                i18n: 'se.cms.pagelist.headerpagetype',
                sortable: true
            },
            {
                property: 'label',
                i18n: 'se.cms.pagelist.headerpagelable',
                sortable: false
            },
            {
                property: 'numberOfRestrictions',
                i18n: 'se.cms.pagelist.headerrestrictions',
                sortable: false,
                component: NumberOfRestrictionsWrapperComponent
            },
            {
                property: 'modifiedtime',
                i18n: 'se.cms.trashedpagelist.trashed.date',
                sortable: true,
                component: ModifiedTimeWrapperComponent
            },
            {
                property: 'pageStatus',
                i18n: 'se.cms.pagelist.headerpagestatus',
                sortable: false,
                component: PageStatusWrapperComponent
            },
            {
                property: 'dropdownitems',
                i18n: 'se.cms.pagelist.headerpageaction',
                sortable: false,
                component: TrashListDropdownItemsWrapperComponent
            }
        ];
    }
}
