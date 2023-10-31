/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { SlicePipe } from '@angular/common';
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Input,
    OnChanges,
    SimpleChanges,
    ViewRef
} from '@angular/core';
import { TypedMap } from '@smart/utils';

import { FilterByFieldPipe, StartFromPipe } from '../../pipes';
import { objectUtils } from '../../utils';
import { IDropdownMenuItem } from '../dropdown/dropdownMenu';
import { ClientPagedList, ClientPagedListColumnKey, ClientPagedListItem } from './interfaces';

/**
 * Component responsible for displaying a client-side paginated list of items as a text or custom components.
 * It allows the user to search and sort the list.
 *
 * ### Example
 *      <se-client-paged-list
 *          [items]="pages"
 *          [keys]="[{
 *                  property:'title',
 *                  i18n:'pagelist.headerpagetitle'
 *              },{
 *                  property:'uid',
 *                  i18n:'pagelist.headerpageid'
 *              },{
 *                  property:'typeCode',
 *                  i18n:'pagelist.headerpagetype'
 *              },{
 *                  property:'template',
 *                  i18n:'pagelist.headerpagetemplate'
 *          }]"
 *          [sortyBy]="'title'"
 *          [reversed]="true"
 *          [itemsPerPage]="10"
 *          [query]="query.value"
 *          [displayCount]="true"
 *      >
 *      </se-client-paged-list>
 */
@Component({
    selector: 'se-client-paged-list',
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [FilterByFieldPipe, StartFromPipe, SlicePipe],
    templateUrl: './ClientPagedListComponent.html'
})
export class ClientPagedListComponent implements ClientPagedList, OnChanges {
    @Input() items: ClientPagedListItem[];
    /**
     * (OPTIONAL) An array of object keys that will determine which fields the "FilterByFieldPipe"
     * will use to filter through the items.
     */
    @Input() itemFilterKeys: string[];
    /**
     * An array of object(s) with a property and an i18n key.
     * The properties must match one at least one of the descriptors' keys and will be used as the columns of the table. The related i18n keys are used for the column headers' title.
     */
    @Input() keys: ClientPagedListColumnKey[];
    @Input() itemsPerPage: number;
    /** (OPTIONAL) */
    @Input() sortBy: string;
    @Input() reversed = false;
    /** (OPTIONAL) The ngModel query object used to filter the list. */
    @Input() query: string;
    /** If set to true the size of the filtered collection will be displayed. */
    @Input() displayCount = false;
    /** (OPTIONAL) */
    @Input() dropdownItems: IDropdownMenuItem[];

    public totalItems = 0;
    /**
     * Pagination page number
     */
    public currentPage = 1;
    public columnWidth: number;
    public columnToggleReversed: boolean;
    public headersSortingState: TypedMap<boolean> = {};
    public visibleSortingHeader: string;

    public filteredItems: ClientPagedListItem[];

    constructor(
        private cdr: ChangeDetectorRef,
        private filterByFieldPipe: FilterByFieldPipe,
        private startFromPipe: StartFromPipe,
        private slicePipe: SlicePipe
    ) {}

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.items || changes.query || changes.itemFilterKeys || changes.itemsPerPage) {
            if (changes.query) {
                this.currentPage = 1;
            }
            this.filteredItems = this.filterItems();
            this.totalItems =
                changes.query && changes.query.currentValue
                    ? this.filteredItems.length
                    : this.items.length;
        }

        if (changes.keys) {
            this.columnWidth = 100 / (this.keys.length || 1);
        }

        if (changes.reversed) {
            this.columnToggleReversed = this.reversed;
        }

        if (changes.sortBy) {
            this.headersSortingState = {
                ...this.headersSortingState,
                [this.sortBy]: this.columnToggleReversed
            };
            this.visibleSortingHeader = this.sortBy;

            this.items = objectUtils.sortBy(this.items, this.sortBy, this.columnToggleReversed);
            this.filteredItems = this.filterItems();
        }
    }

    public keysTrackBy(_index: number, key: ClientPagedListColumnKey): string {
        return key.property;
    }

    public onOrderByColumn(columnKeyProp: string): void {
        this.columnToggleReversed = !this.columnToggleReversed;
        this.headersSortingState[columnKeyProp] = this.columnToggleReversed;
        this.visibleSortingHeader = columnKeyProp;
        this.items = objectUtils.sortBy(this.items, columnKeyProp, this.columnToggleReversed);
        this.filteredItems = this.filterItems();
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    public onCurrentPageChange(page: number): void {
        this.currentPage = page;
        this.filteredItems = this.filterItems();
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    private filterItems(): ClientPagedListItem[] {
        const filterKeys = this.itemFilterKeys || [];
        const filteredItems = this.filterByFieldPipe.transform<ClientPagedListItem>(
            this.items,
            this.query,
            filterKeys
        );

        const startFromIndex = (this.currentPage - 1) * this.itemsPerPage;
        const startFromItems = this.startFromPipe.transform<ClientPagedListItem>(
            filteredItems,
            startFromIndex
        );

        const slicedItems = this.slicePipe.transform<ClientPagedListItem>(
            startFromItems,
            0,
            this.itemsPerPage
        );
        return slicedItems;
    }
}
