import { ElementRef, OnInit, OnDestroy } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { PersonalizationsmarteditMessageHandler, PersonalizationsmarteditUtils, PaginationHelper, CustomizationTreeItem } from 'personalizationcommons';
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
export declare class ManagerViewComponent implements OnInit, OnDestroy {
    private translateService;
    private managerViewUtilsService;
    private messageHandler;
    private contextService;
    private utils;
    private manageCustomizationViewManager;
    private systemEventService;
    managerScrollZone: ElementRef;
    catalogName: string;
    moreCustomizationsRequestProcessing: boolean;
    pagination: PaginationHelper;
    customizations: CustomizationTreeItem[];
    customizationSearch: CustomizationSearch;
    showResetButton: boolean;
    scrollZoneObject: ScrollZoneObject;
    private customizationsModifiedUnsubscribe;
    constructor(translateService: TranslateService, managerViewUtilsService: ManagerViewUtilsService, messageHandler: PersonalizationsmarteditMessageHandler, contextService: PersonalizationsmarteditContextService, utils: PersonalizationsmarteditUtils, manageCustomizationViewManager: ManageCustomizationViewManager, systemEventService: SystemEventService);
    ngOnDestroy(): void;
    ngOnInit(): void;
    ngAfterViewInit(): void;
    searchInputKeypress(): void;
    resetSearch(event?: Event): void;
    statusFilterChange(itemId: string): void;
    addMoreItems(): void;
    openNewModal(): void;
    isSearchGridHeaderHidden(): boolean;
    scrollZoneReturnToTop(): void;
    isReturnToTopButtonVisible(): boolean;
    refreshGrid(): void;
    getPage: () => void;
    private getCustomizations;
    private getCustomizationsFilterObject;
    private getStatus;
}
export {};
