/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, EventEmitter, InjectionToken, Input, OnInit, Output } from '@angular/core';
import { TypedMap } from '@smart/utils';

import { SortDirections } from '../../utils';
import {
    DynamicPagedListColumnKey,
    DynamicPagedListDropdownItem,
    SortStatus
} from '../dynamicPagedList/interfaces';

export interface DataTableConfig {
    sortBy: string;
    reversed: boolean;
    injectedContext: any;
}

export interface DataTableComponentData<T = DynamicPagedListDropdownItem> {
    item: T;
    column: DynamicPagedListColumnKey;
}

/**
 * An injection token used to retrieve information about data table column and item from within rendered component.
 */
export const DATA_TABLE_COMPONENT_DATA = new InjectionToken('DATA_TABLE_COMPONENT_DATA');

/**
 * Component used to print the provided data in the form of table and also enable sorting by column.
 */
@Component({
    selector: 'se-data-table',
    styleUrls: ['./DataTableComponent.scss'],
    templateUrl: './DataTableComponent.html'
})
export class DataTableComponent implements OnInit {
    @Input() public columns: DynamicPagedListColumnKey[];
    @Input() public config: DataTableConfig;
    @Input() public items: DynamicPagedListDropdownItem[];
    @Input() public sortStatus: SortStatus;

    /**
     * Event emitted, each time sorting has been performed.
     * Passes back the column key and the sort direction.
     *
     * The invoker can bind this to a custom function to act and fetch results based on new data.
     */
    @Output() public onSortColumn: EventEmitter<{
        $columnKey: DynamicPagedListColumnKey;
        $columnSortMode: SortDirections;
    }> = new EventEmitter();

    public internalSortBy: string;
    public currentPage: number;
    public columnWidth: number;
    public columnToggleReversed: boolean;
    public columnSortMode: SortDirections;
    public headersSortingState: TypedMap<boolean> = {};
    public visibleSortingHeader: string;

    ngOnInit(): void {
        this._configure();
    }

    public sortColumn(columnKey: DynamicPagedListColumnKey): void {
        if (columnKey.sortable) {
            this.columnToggleReversed = !this.columnToggleReversed;
            this.headersSortingState[columnKey.property] = this.columnToggleReversed;
            this.visibleSortingHeader = columnKey.property;

            this.currentPage = 1;
            this.internalSortBy = columnKey.property;
            this.columnSortMode = this.columnToggleReversed
                ? SortDirections.Descending
                : SortDirections.Ascending;
            this.onSortColumn.emit({
                $columnKey: columnKey,
                $columnSortMode: this.columnSortMode
            });
        }
    }

    private _configure(): void {
        const numberOfWidth = 100;
        this.columnWidth = numberOfWidth / this.columns.length;
        this.columnToggleReversed = this.sortStatus.reversed;

        this.columnSortMode = this.sortStatus.reversed
            ? SortDirections.Descending
            : SortDirections.Ascending;

        this.headersSortingState[this.sortStatus.internalSortBy] = this.config.reversed;
        this.visibleSortingHeader = this.sortStatus.internalSortBy;
    }
}
