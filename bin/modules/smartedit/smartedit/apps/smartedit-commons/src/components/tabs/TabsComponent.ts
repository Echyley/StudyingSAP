/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { ISelectItem } from '@smart/utils';
import { isEqual } from 'lodash';
import { BehaviorSubject, Observable } from 'rxjs';
import { distinctUntilChanged, map } from 'rxjs/operators';
import { IUserTrackingService } from '../../services/interfaces';
import { USER_TRACKING_FUNCTIONALITY } from '../../utils';
import { TabsSelectAdapter } from './TabsSelectAdapter';
import { Tab } from './types';

/**
 * The component responsible for displaying and organizing [\<se-tab\>]{@link TabComponent} components within a [\<se-tabs\>]{@link TabsComponent} component.
 * A specified number of tabs will display a tab header.
 * If there are more tabs than the maximum number defined, the remaining tabs will be grouped in a dropdown menu with the header "More".
 * When a user clicks on a tab header or an item from the dropdown menu,
 * the content of the tabs changes to the body of the selected tab.
 *
 * Note: The body of each tab is wrapped within a {@link TabComponent}.
 */
@Component({
    selector: 'se-tabs',
    templateUrl: './TabsComponent.html'
})
export class TabsComponent<T> implements OnChanges {
    /**
     * Custom data to be passed to each tab. Neither the [\<se-tabs\>]{@link TabsComponent} component or the [\<se-tab\>]{@link TabComponent} component can modify this value.
     * The tabs contents determine how to parse and use this object.
     */
    @Input() model: T;

    /**
     * A list that contains the configuration for each of the tabs to be displayed in the \<se-tabs\>.
     */
    @Input() tabsList: Tab[] = [];

    /**
     * The number of tabs for which tab headers will be displayed.
     * The remaining tab headers will be grouped within the 'MORE' dropdown menu.
     */
    @Input() numTabsDisplayed: number;

    @Output() onTabSelected: EventEmitter<string> = new EventEmitter<string>();

    public selectedTab: Tab;
    public selectItems: ISelectItem<Tab>[];
    public dropdownTabs: ISelectItem<Tab>[];

    private tabChangedStream: BehaviorSubject<void> = new BehaviorSubject(null);

    constructor(private readonly userTrackingService: IUserTrackingService) {}

    public get isInitialized(): boolean {
        return !!this.tabsList && this.tabsList.length > 1 && !!this.selectedTab;
    }

    isActiveInMoreTab(): boolean {
        return (
            this.tabsList.findIndex((tab) => tab.id === this.selectedTab.id) >=
            this.numTabsDisplayed - 1
        );
    }

    ngOnChanges(changes: SimpleChanges): void {
        const hasTabsInitialized: boolean =
            (changes.tabsList &&
                changes.tabsList.currentValue &&
                !changes.tabsList.previousValue) ||
            (changes.tabsList && changes.tabsList.firstChange && !!changes.tabsList.currentValue);

        const hasTabsChanged: boolean =
            changes.tabsList && !!changes.tabsList.previousValue && !!changes.tabsList.currentValue;

        // ensure that selected is the same as active
        if (this.tabsList) {
            const active = this.tabsList.find((tab: Tab) => tab.active) || this.tabsList[0];
            if (!this.selectedTab || (this.selectedTab && this.selectedTab.id !== active.id)) {
                this.selectedTab = active;
                this.selectedTab.active = true;
            }
        }

        if (hasTabsInitialized) {
            this.tabsList = this.tabsList.map((tab) => ({
                ...tab,
                active: false,
                hasErrors: false
            }));
            this.tabChangedStream.next();
        }

        if (hasTabsChanged) {
            this.tabChangedStream.next();
        }

        this.getDropdownTabs().subscribe((tabs) => {
            this.dropdownTabs = tabs;
        });
    }

    selectTab(tabToSelect: Tab): void {
        if (tabToSelect && tabToSelect.id !== this.selectedTab.id) {
            this.userTrackingService.trackingUserAction(
                USER_TRACKING_FUNCTIONALITY.ADD_COMPONENT,
                tabToSelect.title
            );

            if (!this.selectedTab.active) {
                // If a tab is made active 'manually' from outside this controller, there can be a mismatch.
                // This method finds the tab that is actually selected.
                this.findSelectedTab();
            }
            this.selectedTab.active = false;
            this.selectedTab = tabToSelect;
            this.selectedTab.active = true;

            this.onTabSelected.emit(this.selectedTab.id);
        }
    }

    dropDownHasErrors(): boolean {
        const tabsInDropDown = this.tabsList.slice(this.numTabsDisplayed - 1);

        return tabsInDropDown.some((tab: Tab) => tab.hasErrors);
    }

    findSelectedTab(): void {
        const selectedTab = this.tabsList.find((tab: Tab) => tab.active);

        if (selectedTab) {
            this.selectedTab = selectedTab;
        }
    }

    getDropdownTabs(): Observable<ISelectItem<Tab>[]> {
        return this.tabChangedStream.pipe(
            map(() =>
                (this.tabsList || [])
                    .slice(this.numTabsDisplayed - 1)
                    .map(TabsSelectAdapter.transform)
            ),
            distinctUntilChanged((a: ISelectItem<Tab>[], b: ISelectItem<Tab>[]) => isEqual(a, b))
        );
    }

    getVisibleTabs(): Observable<Tab[]> {
        return this.tabChangedStream.pipe(
            map(() => (this.tabsList || []).slice(0, this.numTabsDisplayed - 1)),
            distinctUntilChanged((a: Tab[], b: Tab[]) => isEqual(a, b))
        );
    }

    trackTabById(index: number): number {
        return index;
    }
}
