import { EventEmitter, OnInit } from '@angular/core';
import { PersonalizationsmarteditUtils } from 'personalizationcommons';
export declare class StatusFilterDropdownComponent implements OnInit {
    protected personalizationsmarteditUtils: PersonalizationsmarteditUtils;
    initialValue: string;
    onSelectCallback: EventEmitter<string>;
    selectedId: string;
    items: any[];
    fetchStrategy: {
        fetchAll: () => Promise<any[]>;
    };
    constructor(personalizationsmarteditUtils: PersonalizationsmarteditUtils);
    ngOnInit(): void;
    onChange(): void;
}
