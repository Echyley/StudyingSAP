/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { PersonalizationsmarteditUtils } from 'personalizationcommons';
@Component({
    selector: 'status-filter-dropdown',
    templateUrl: './SharedFilterDropdownComponent.html'
})
export class StatusFilterDropdownComponent implements OnInit {
    @Input() public initialValue: string;
    @Output() public onSelectCallback: EventEmitter<string>;
    public selectedId: string;
    public items: any[];
    public fetchStrategy = {
        fetchAll: (): Promise<any[]> => Promise.resolve(this.items)
    };

    constructor(protected personalizationsmarteditUtils: PersonalizationsmarteditUtils) {
        this.onChange = this.onChange.bind(this);
        this.onSelectCallback = new EventEmitter();
    }

    ngOnInit(): void {
        this.items = this.personalizationsmarteditUtils.getStatusesMapping().map((elem: any) => ({
            id: elem.code,
            label: elem.text,
            modelStatuses: elem.modelStatuses
        }));
        this.selectedId = this.initialValue || this.items[0].id;
    }

    public onChange(): void {
        this.onSelectCallback.emit(this.selectedId);
    }
}
