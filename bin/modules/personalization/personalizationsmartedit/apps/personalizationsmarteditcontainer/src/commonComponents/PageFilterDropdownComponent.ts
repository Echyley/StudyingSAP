/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER } from 'personalizationcommons';
@Component({
    selector: 'page-filter-dropdown',
    templateUrl: './SharedFilterDropdownComponent.html'
})
export class PageFilterDropdownComponent implements OnInit {
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
                id: PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER.ONLY_THIS_PAGE,
                label: 'personalization.filter.page.onlythispage'
            },
            {
                id: PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER.ALL,
                label: 'personalization.filter.page.all'
            }
        ];
        this.selectedId = this.initialValue || this.items[0].id;
    }

    public onChange(): void {
        this.onSelectCallback.emit(this.selectedId);
    }
}
