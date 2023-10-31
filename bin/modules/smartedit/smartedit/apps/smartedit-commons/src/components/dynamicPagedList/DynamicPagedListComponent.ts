/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import './dynamicPageList.scss';
import {
    ChangeDetectorRef,
    Component,
    EventEmitter,
    Input,
    OnChanges,
    OnInit,
    Output,
    SimpleChanges,
    ViewRef
} from '@angular/core';
import { LogService, Page, Pageable, RestServiceFactory } from '@smart/utils';
import { cloneDeep } from 'lodash';

import { SortDirections } from '../../utils';
import {
    DynamicPagedListApi,
    DynamicPagedListColumnKey,
    DynamicPagedListConfig,
    DynamicPagedListDropdownItem,
    SortStatus
} from './interfaces';

/**
 * Component responsible for displaying a server-side paginated list of items with custom components.
 * It allows the user to search and sort the list.
 */
@Component({
    selector: 'se-dynamic-paged-list',
    templateUrl: './DynamicPagedListComponent.html'
})
export class DynamicPagedListComponent implements OnInit, OnChanges {
    @Input() config: DynamicPagedListConfig;

    /** The string value used to filter the result. */
    @Input() mask: string;

    /** Event emitted when the component is initialized. Passes back the Paged List API object */
    @Output() getApi: EventEmitter<DynamicPagedListApi> = new EventEmitter();

    /** Event emitted when items are loaded. Passes back the Page object. */
    @Output() onItemsUpdate: EventEmitter<Page<any>> = new EventEmitter<Page<any>>();

    public internalSortBy: string;
    public columnSortMode: string;
    public currentPage: number;
    public ready: boolean;
    public totalItems: number;
    public items: DynamicPagedListDropdownItem[];
    public columns: DynamicPagedListColumnKey[];
    public sortStatus: SortStatus;

    private oldMask: string;

    private readonly api: DynamicPagedListApi = {
        reloadItems: () => this.loadItems()
    };

    constructor(
        private readonly logService: LogService,
        private readonly restServiceFactory: RestServiceFactory,
        private readonly cdr: ChangeDetectorRef
    ) {}

    ngOnInit(): void {
        this._validateInput();

        this.ready = false;

        this.totalItems = 0;
        this.currentPage = 1;

        this.columnSortMode = this.config.reversed
            ? SortDirections.Descending
            : SortDirections.Ascending;
        this.internalSortBy = cloneDeep(this.config.sortBy);
        this.oldMask = this.mask;

        this.columns = [];
        this.sortStatus = {
            internalSortBy: this.internalSortBy,
            reversed: this.config.reversed,
            currentPage: this.currentPage
        };
        this._buildColumnData();
        this.loadItems();

        this.getApi.emit(this.api);
    }

    ngOnChanges(changes: SimpleChanges): void {
        const maskChange = changes.mask;
        if (!!maskChange && !maskChange.firstChange && this.oldMask !== this.mask) {
            this.oldMask = this.mask;

            // set page to 1 and reload data
            this.currentPage = 1;
            this.loadItems();
        }
    }

    public orderByColumn(event: {
        $columnKey: DynamicPagedListColumnKey;
        $columnSortMode: string;
    }): void {
        this.internalSortBy = event.$columnKey.property;
        this.columnSortMode = event.$columnSortMode;

        this.config.reversed = this.columnSortMode === SortDirections.Descending;
        this.sortStatus.internalSortBy = this.internalSortBy;
        this.sortStatus.reversed = this.config.reversed;

        if (event.$columnKey.sortable) {
            this.currentPage = 1;
            this.sortStatus.currentPage = 1;
            this.loadItems();
        }
    }

    public loadItems(): Promise<Page<any>> {
        this.ready = false;

        const params: Pageable = {
            ...(this.config.queryParams || {}),
            currentPage: this.currentPage - 1,
            mask: this.mask,
            pageSize: this.config.itemsPerPage,
            sort: `${this.internalSortBy}:${this.columnSortMode}`
        };

        return this.restServiceFactory
            .get(this.config.uri)
            .page(params)
            .then((result: Page<any>) => {
                this.items = result.results;

                if (this.items.length === 0) {
                    this.logService.warn('PagedList: No items returned to display');
                }

                this.totalItems = result.pagination.totalCount;
                this.currentPage = parseInt(String(result.pagination.page), 10) + 1;

                this.ready = true;
                // Trigger cdr to hide the spinner immediately (For some reason, Change Detection is not triggered)
                if (!(this.cdr as ViewRef).destroyed) {
                    this.cdr.detectChanges();
                }

                this.onItemsUpdate.emit(result);

                return result;
            });
    }

    public onCurrentPageChange(newCurrentPage: number): void {
        if (newCurrentPage === this.currentPage) {
            return;
        }
        this.currentPage = newCurrentPage;
        this.loadItems();
    }

    private _validateInput(): void {
        if (!this.config) {
            throw new Error('config object is required');
        }

        if (!(this.config.keys instanceof Array)) {
            throw new Error('keys must be an array');
        }

        if (this.config.keys.length < 1) {
            throw new Error('dynamic paged list requires at least one key');
        }

        if (!this.config.uri) {
            throw new Error('dynamic paged list requires a uri to fetch the list of items');
        }
    }

    private _buildColumnData(): void {
        this.columns = this.config.keys.map((key) => ({
            ...key
        }));
    }
}
