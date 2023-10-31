import { ChangeDetectorRef, OnInit } from '@angular/core';
import { ICatalogService, L10nPipe, ContextualMenuItemData } from 'smarteditcommons';
export declare class ExternalComponentButtonComponent implements OnInit {
    private catalogService;
    private l10nPipe;
    private cdr;
    isReady: boolean;
    catalogVersion: string;
    private catalogVersionUuid;
    constructor(item: ContextualMenuItemData, catalogService: ICatalogService, l10nPipe: L10nPipe, cdr: ChangeDetectorRef);
    ngOnInit(): Promise<void>;
}
