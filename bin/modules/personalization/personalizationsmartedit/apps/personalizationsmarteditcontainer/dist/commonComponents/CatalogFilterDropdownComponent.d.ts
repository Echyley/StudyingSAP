import { EventEmitter, OnInit } from '@angular/core';
export declare class CatalogFilterDropdownComponent implements OnInit {
    initialValue: string;
    onSelectCallback: EventEmitter<string>;
    selectedId: any;
    items: any[];
    fetchStrategy: {
        fetchAll: () => Promise<any[]>;
    };
    constructor();
    ngOnInit(): void;
    onChange(): void;
}
