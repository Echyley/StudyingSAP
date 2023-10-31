import { EventEmitter, OnInit, Type } from '@angular/core';
import { PersonalizationsmarteditContextService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuter';
import { ComponentMenuService, L10nPipe } from 'smarteditcommons';
export declare class CatalogVersionFilterDropdownComponent implements OnInit {
    protected componentMenuService: ComponentMenuService;
    protected personalizationsmarteditContextService: PersonalizationsmarteditContextService;
    private l10nPipe;
    initialValue: string;
    onSelectCallback: EventEmitter<string>;
    selectedId: string;
    items: any[];
    itemComponent: Type<any>;
    fetchStrategy: {
        fetchAll: any;
    };
    constructor(componentMenuService: ComponentMenuService, personalizationsmarteditContextService: PersonalizationsmarteditContextService, l10nPipe: L10nPipe);
    ngOnInit(): Promise<void>;
    onChange(_changes: any): void;
}
