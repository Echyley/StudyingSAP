import { EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { ISelectItem } from '@smart/utils';
import { Observable } from 'rxjs';
import { IUserTrackingService } from '../../services/interfaces';
import { Tab } from './types';
export declare class TabsComponent<T> implements OnChanges {
    private readonly userTrackingService;
    model: T;
    tabsList: Tab[];
    numTabsDisplayed: number;
    onTabSelected: EventEmitter<string>;
    selectedTab: Tab;
    selectItems: ISelectItem<Tab>[];
    dropdownTabs: ISelectItem<Tab>[];
    private tabChangedStream;
    constructor(userTrackingService: IUserTrackingService);
    get isInitialized(): boolean;
    isActiveInMoreTab(): boolean;
    ngOnChanges(changes: SimpleChanges): void;
    selectTab(tabToSelect: Tab): void;
    dropDownHasErrors(): boolean;
    findSelectedTab(): void;
    getDropdownTabs(): Observable<ISelectItem<Tab>[]>;
    getVisibleTabs(): Observable<Tab[]>;
    trackTabById(index: number): number;
}
