/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, ElementRef, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

import {
    PersonalizationsmarteditMessageHandler,
    PersonalizationsmarteditUtils,
    PaginationHelper,
    CustomizationTreeItem,
    CustomizationsFilter,
    CustomizationStatus
} from 'personalizationcommons';
import { SystemEventService } from 'smarteditcommons';
import { PersonalizationsmarteditContextService } from '../../service';
import { ManageCustomizationViewManager } from '../manageCustomizationView/ManageCustomizationViewManager';
import { ManagerViewUtilsService } from './ManagerViewUtilsService';

interface ScrollZoneObject {
    scrollZoneElement: HTMLElement | undefined;
    filteredCustomizationsCount: number;
}

interface CustomizationSearch {
    name: string;
    status: string;
}
@Component({
    selector: 'customizations-list',
    templateUrl: './ManagerViewComponent.html',
    styleUrls: ['./ManagerViewComponent.scss']
})
export class ManagerViewComponent implements OnInit, OnDestroy {
    @ViewChild('managerScrollZone', { static: false }) managerScrollZone: ElementRef;
    public catalogName: string;
    public moreCustomizationsRequestProcessing: boolean;
    public pagination: PaginationHelper;
    public customizations: CustomizationTreeItem[];
    public customizationSearch: CustomizationSearch;
    public showResetButton: boolean;
    public scrollZoneObject: ScrollZoneObject;

    private customizationsModifiedUnsubscribe: () => void;

    constructor(
        private translateService: TranslateService,
        private managerViewUtilsService: ManagerViewUtilsService,
        private messageHandler: PersonalizationsmarteditMessageHandler,
        private contextService: PersonalizationsmarteditContextService,
        private utils: PersonalizationsmarteditUtils,
        private manageCustomizationViewManager: ManageCustomizationViewManager,
        private systemEventService: SystemEventService
    ) {
        const seExperienceData = this.contextService.getSeData().seExperienceData;
        const currentLanguageIsocode = seExperienceData.languageDescriptor.isocode;
        this.catalogName = `${seExperienceData.catalogDescriptor.name[currentLanguageIsocode]} - ${seExperienceData.catalogDescriptor.catalogVersion}`;

        this.customizations = [];
        this.showResetButton = false;

        this.customizationSearch = {
            name: '',
            status: ''
        };

        this.moreCustomizationsRequestProcessing = false;
        this.pagination = new PaginationHelper();
        this.pagination.reset();

        this.scrollZoneObject = {
            scrollZoneElement: undefined,
            filteredCustomizationsCount: 0
        };
    }

    ngOnDestroy(): void {
        if (this.customizationsModifiedUnsubscribe) {
            this.customizationsModifiedUnsubscribe();
        }
    }

    ngOnInit(): void {
        this.customizationsModifiedUnsubscribe = this.systemEventService.subscribe(
            'CUSTOMIZATIONS_MODIFIED',
            () => {
                this.refreshGrid();
            }
        );
    }

    ngAfterViewInit(): void {
        this.scrollZoneObject.scrollZoneElement = this.managerScrollZone.nativeElement;
    }

    public searchInputKeypress(): void {
        if (this.customizationSearch.name.length > 0) {
            this.showResetButton = true;
        }
        if (
            this.customizationSearch.name.length > 2 ||
            this.customizationSearch.name.length === 0
        ) {
            this.refreshGrid();
        }
    }

    public resetSearch(event?: Event): void {
        if (event) {
            event.stopPropagation();
        }

        this.customizationSearch.name = '';
        this.showResetButton = false;
        this.refreshGrid();
    }

    public statusFilterChange(itemId: string): void {
        this.customizationSearch.status = itemId;
        this.refreshGrid();
    }

    public addMoreItems(): void {
        if (
            this.pagination.getPage() < this.pagination.getTotalPages() - 1 &&
            !this.moreCustomizationsRequestProcessing
        ) {
            this.moreCustomizationsRequestProcessing = true;
            this.getCustomizations(this.getCustomizationsFilterObject());
        }
    }

    public openNewModal(): void {
        this.manageCustomizationViewManager.openCreateCustomizationModal();
    }

    public isSearchGridHeaderHidden(): boolean {
        if (!this.scrollZoneObject.scrollZoneElement) {
            return false;
        }
        return this.scrollZoneObject.scrollZoneElement.scrollTop >= 120;
    }

    public scrollZoneReturnToTop(): void {
        setTimeout(() => {
            this.scrollZoneObject.scrollZoneElement.scrollTop = 0;
        }, 500);
    }

    public isReturnToTopButtonVisible(): boolean {
        if (!this.scrollZoneObject.scrollZoneElement) {
            return false;
        } else {
            return this.scrollZoneObject.scrollZoneElement.scrollTop > 50;
        }
    }

    public refreshGrid(): void {
        this.pagination.reset();
        this.customizations = [];
        this.addMoreItems();
    }

    // use arrow function in purpose as it will be involked by other components
    public getPage = (): void => this.addMoreItems();

    private async getCustomizations(filter: CustomizationsFilter): Promise<void> {
        const customizationResponse = await this.managerViewUtilsService.getCustomizations(filter);
        try {
            this.utils.uniqueArray(this.customizations, customizationResponse.customizations || []);
            this.scrollZoneObject.filteredCustomizationsCount =
                customizationResponse.pagination.totalCount;
            this.pagination = new PaginationHelper(customizationResponse.pagination);
            this.moreCustomizationsRequestProcessing = false;
        } catch (err) {
            this.messageHandler.sendError(
                this.translateService.instant('personalization.error.gettingcustomizations')
            );
            this.moreCustomizationsRequestProcessing = false;
        }
    }

    private getCustomizationsFilterObject(): CustomizationsFilter {
        return {
            active: 'all',
            name: this.customizationSearch.name,
            currentSize: this.pagination.getCount(),
            currentPage: this.pagination.getPage() + 1,
            statuses: this.getStatus().modelStatuses
        };
    }

    private getStatus(): CustomizationStatus {
        if (this.customizationSearch.status === '') {
            return this.utils.getStatusesMapping()[0]; // all elements
        }
        return this.utils
            .getStatusesMapping()
            .filter((elem: any) => elem.code === this.customizationSearch.status)[0];
    }
}
