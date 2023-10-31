/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { PERSONALIZATION_CATALOG_FILTER } from 'personalizationcommons';
@Component({
    selector: 'catalog-filter-dropdown',
    templateUrl: './SharedFilterDropdownComponent.html'
})
export class CatalogFilterDropdownComponent implements OnInit {
    @Input() public initialValue: string;
    @Output() public onSelectCallback: EventEmitter<string>;

    public selectedId: any;
    public items: any[];
    public fetchStrategy = {
        fetchAll: (): Promise<any[]> => Promise.resolve(this.items)
    };

    constructor() {
        this.onChange = this.onChange.bind(this);
        this.onSelectCallback = new EventEmitter();
    }

    ngOnInit(): void {
        this.items = [
            {
                id: PERSONALIZATION_CATALOG_FILTER.ALL,
                label: 'personalization.filter.catalog.all'
            },
            {
                id: PERSONALIZATION_CATALOG_FILTER.CURRENT,
                label: 'personalization.filter.catalog.current'
            },
            {
                id: PERSONALIZATION_CATALOG_FILTER.PARENTS,
                label: 'personalization.filter.catalog.parents'
            }
        ];
        this.selectedId = this.initialValue || this.items[1].id;
    }

    public onChange(): void {
        this.onSelectCallback.emit(this.selectedId);
    }
}
