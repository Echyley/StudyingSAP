/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, OnInit, OnDestroy, DoCheck, Inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
    PaginationHelper,
    PersonalizationsmarteditMessageHandler,
    PersonalizationsmarteditUtils,
    PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER,
    Customization
} from 'personalizationcommons';
import { CustomizationDataFactory } from 'personalizationsmarteditcontainer/dataFactory';
import { PersonalizationsmarteditContextService } from 'personalizationsmarteditcontainer/service';
import { TOOLBAR_ITEM, ToolbarItemInternal } from 'smarteditcommons';

@Component({
    selector: 'customize-view',
    templateUrl: './CustomizeViewComponent.html'
})
export class CustomizeViewComponent implements OnInit, OnDestroy, DoCheck {
    public pagination: PaginationHelper;
    public moreCustomizationsRequestProcessing: boolean;
    public customizationsList: Customization[];
    public filters: any;
    public catalogFilter: any;
    public pageFilter: any;
    public statusFilter: any;
    public nameFilter: any;
    public showResetButton: boolean;
    private oldIsOpen: boolean;

    constructor(
        @Inject(TOOLBAR_ITEM) private readonly toolbarItem: ToolbarItemInternal,
        private readonly translateService: TranslateService,
        protected customizationDataFactory: CustomizationDataFactory,
        protected personalizationsmarteditContextService: PersonalizationsmarteditContextService,
        protected personalizationsmarteditMessageHandler: PersonalizationsmarteditMessageHandler,
        protected personalizationsmarteditUtils: PersonalizationsmarteditUtils
    ) {}

    ngOnInit(): void {
        this.personalizationsmarteditContextService.refreshExperienceData();
        this.moreCustomizationsRequestProcessing = false;
        this.customizationsList = this.customizationDataFactory.items;
        this.customizationDataFactory.resetData();
        this.pagination = new PaginationHelper({});
        this.pagination.reset();
        this.filters = this.personalizationsmarteditContextService.getCustomizeFiltersState();
        this.catalogFilter = this.filters.catalogFilter;
        this.pageFilter = this.filters.pageFilter;
        this.statusFilter = this.filters.statusFilter;
        this.nameFilter = this.filters.nameFilter;
        this.showResetButton = false;
        this.oldIsOpen = this.toolbarItem.isOpen;
    }

    ngOnDestroy(): void {
        const filters = this.personalizationsmarteditContextService.getCustomizeFiltersState();
        filters.catalogFilter = this.catalogFilter;
        filters.pageFilter = this.pageFilter;
        filters.statusFilter = this.statusFilter;
        filters.nameFilter = this.nameFilter;
        this.personalizationsmarteditContextService.setCustomizeFiltersState(filters);
    }

    ngDoCheck(): void {
        if (this.toolbarItem.isOpen !== this.oldIsOpen) {
            if (this.toolbarItem.isOpen) {
                this.refreshList();
            }
            this.oldIsOpen = this.toolbarItem.isOpen;
        }
    }

    // Properties
    public catalogFilterChange(itemId: string): void {
        this.catalogFilter = itemId;
        this.refreshList();
    }

    public pageFilterChange(itemId: string): void {
        this.pageFilter = itemId;
        this.refreshList();
    }

    public statusFilterChange(itemId: string): void {
        this.statusFilter = itemId;
        this.refreshList();
    }

    public nameInputKeypress(): void {
        if (this.nameFilter.length > 0) {
            this.showResetButton = true;
        }
        if (this.nameFilter.length > 2 || this.nameFilter.length === 0) {
            this.refreshList();
        }
    }

    public resetSearch(event?: Event): void {
        if (event) {
            event.stopPropagation();
        }

        this.nameFilter = '';
        this.showResetButton = false;
        this.refreshList();
    }

    public addMoreCustomizationItems(): void {
        if (
            this.pagination.getPage() < this.pagination.getTotalPages() - 1 &&
            !this.moreCustomizationsRequestProcessing
        ) {
            this.moreCustomizationsRequestProcessing = true;
            this.getCustomizations(this.getCustomizationsFilterObject());
        }
    }

    // use arrow function in purpose as it will be invoked by other components
    public getPage = (): void => this.addMoreCustomizationItems();

    // Private methods
    private errorCallback(): void {
        this.personalizationsmarteditMessageHandler.sendError(
            this.translateService.instant('personalization.error.gettingcustomizations')
        );
        this.moreCustomizationsRequestProcessing = false;
    }

    private successCallback(response: any): void {
        this.pagination = new PaginationHelper(response.pagination);
        this.moreCustomizationsRequestProcessing = false;
    }

    private getStatus(): any {
        if (this.statusFilter === undefined) {
            return this.personalizationsmarteditUtils.getStatusesMapping()[0]; // all elements
        }
        return this.personalizationsmarteditUtils
            .getStatusesMapping()
            .filter((elem: any) => elem.code === this.statusFilter)[0];
    }

    private getCustomizations(categoryFilter: any): void {
        const params = {
            filter: categoryFilter,
            dataArrayName: 'customizations'
        };
        this.customizationDataFactory.updateData(
            params,
            this.successCallback.bind(this),
            this.errorCallback.bind(this)
        );
    }

    private getCustomizationsFilterObject(): any {
        const ret = {
            currentSize: this.pagination.getCount(),
            currentPage: this.pagination.getPage() + 1,
            name: this.nameFilter,
            statuses: this.getStatus().modelStatuses,
            catalogs: this.catalogFilter
        } as any;
        if (this.pageFilter === PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER.ONLY_THIS_PAGE) {
            ret.pageId = this.personalizationsmarteditContextService.getSeData().pageId;
            ret.pageCatalogId = (
                this.personalizationsmarteditContextService.getSeData().seExperienceData
                    .pageContext || {}
            ).catalogId;
        }
        return ret;
    }

    private refreshList(): any {
        if (this.moreCustomizationsRequestProcessing === false) {
            this.moreCustomizationsRequestProcessing = true;
            this.pagination.reset();
            this.customizationDataFactory.resetData();
            this.getCustomizations(this.getCustomizationsFilterObject());
        }
    }
}
