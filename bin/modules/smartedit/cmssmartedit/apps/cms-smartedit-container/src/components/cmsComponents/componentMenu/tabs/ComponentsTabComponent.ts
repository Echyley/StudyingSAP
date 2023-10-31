/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, OnInit, Type, ViewEncapsulation, Inject } from '@angular/core';
import { ComponentService, LoadPagedComponentsRequestPayload } from 'cmscommons';
import {
    ISharedDataService,
    FetchStrategy,
    Page,
    TAB_DATA,
    TabData,
    CMSItem,
    ComponentMenuService,
    CatalogVersion,
    IUserTrackingService,
    USER_TRACKING_FUNCTIONALITY
} from 'smarteditcommons';
import { ENABLE_CLONE_ON_DROP } from '../../../../services';
import { CatalogVersionItemComponent } from '../components';
import { ComponentMenuTabModel } from '../types';

@Component({
    selector: 'se-components-tab',
    templateUrl: './ComponentsTabComponent.html',
    styleUrls: ['./ComponentsTabComponent.scss'],
    encapsulation: ViewEncapsulation.None
})
export class ComponentsTabComponent implements OnInit {
    public catalogVersions: CatalogVersion[];
    public catalogVersionsFetchStrategy: FetchStrategy<CatalogVersion>;
    public cloneOnDrop: boolean;
    public componentsContext: { items: CMSItem[] };
    public forceRecompile: boolean;
    public itemComponent: Type<CatalogVersionItemComponent>;
    public searchTerm: string;
    public selectedCatalogVersionId: string;
    public selectedCatalogVersion: CatalogVersion;

    constructor(
        @Inject(TAB_DATA) private tabData: TabData<ComponentMenuTabModel>,
        private componentMenuService: ComponentMenuService,
        private componentService: ComponentService,
        private sharedDataService: ISharedDataService,
        private userTrackingService: IUserTrackingService
    ) {
        this.catalogVersions = [];
        this.catalogVersionsFetchStrategy = {
            fetchAll: (): Promise<CatalogVersion[]> => this.fetchCatalogVersions()
        };
        this.componentsContext = { items: [] };
        this.forceRecompile = true;
        this.itemComponent = CatalogVersionItemComponent;
        this.selectedCatalogVersionId = null;
        this.selectedCatalogVersion = null;
        this.searchTerm = '';
    }

    async ngOnInit(): Promise<void> {
        this.cloneOnDrop = !!(await this.sharedDataService.get(ENABLE_CLONE_ON_DROP));
        await this.fetchCatalogVersions();

        if (!this.hasMultipleContentCatalogs()) {
            this.onCatalogVersionChange();
        }
    }

    public isActive(): boolean {
        return this.tabData.tab.active;
    }

    public hasMultipleContentCatalogs(): boolean {
        return this.tabData.model.componentsTab.hasMultipleContentCatalogs;
    }

    public trackById(item: CatalogVersion): string {
        return item.id;
    }

    public async onCatalogVersionChange(): Promise<void> {
        if (this.selectedCatalogVersionId) {
            this.selectedCatalogVersion = this.catalogVersions.find(
                (catalogVersion) => catalogVersion.id === this.selectedCatalogVersionId
            );

            await this.componentMenuService.persistCatalogVersion(this.selectedCatalogVersionId);
            // This forces se-infinite-scrolling to reload items when catalog changes
            this.forceRecompile = false;
            setTimeout(() => {
                this.forceRecompile = true;
            });
        }
    }

    public onSearchTermChanged(searchTerm: string): void {
        this.searchTerm = searchTerm;
    }

    public loadComponentItems = async (
        mask: string,
        pageSize: number,
        currentPage: number
    ): Promise<Page<CMSItem>> => {
        if (!this.selectedCatalogVersion) {
            return {
                results: []
            } as Page<CMSItem>;
        }

        const payload = {
            catalogId: this.selectedCatalogVersion.catalogId,
            catalogVersion: this.selectedCatalogVersion.catalogVersionId,
            mask,
            pageSize,
            page: currentPage
        } as LoadPagedComponentsRequestPayload;

        const loadedPage = await this.componentService.loadPagedComponentItemsByCatalogVersion(
            payload
        );
        return {
            ...loadedPage,
            results: loadedPage.response
        } as Page<CMSItem>;
    };

    public onComponentCloneOnDropChange(): void {
        this.userTrackingService.trackingUserAction(
            USER_TRACKING_FUNCTIONALITY.ADD_COMPONENT,
            'Clone on Drop'
        );
        this.sharedDataService.set(ENABLE_CLONE_ON_DROP, this.cloneOnDrop);
    }

    private async fetchCatalogVersions(): Promise<CatalogVersion[]> {
        const catalogVersions = await this.componentMenuService.getValidContentCatalogVersions();
        this.catalogVersions = catalogVersions;

        const selectedCatalogVersion = await this.componentMenuService.getInitialCatalogVersion(
            this.catalogVersions
        );
        this.selectedCatalogVersion = selectedCatalogVersion;
        this.selectedCatalogVersionId = this.selectedCatalogVersion
            ? this.selectedCatalogVersion.id
            : undefined;

        return this.catalogVersions;
    }
}
