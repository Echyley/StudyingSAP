import { Injector, OnInit } from '@angular/core';
import { ClientPagedListColumnKey, ClientPagedListItem } from './interfaces';
export declare class ClientPagedListCellComponent implements OnInit {
    private injector;
    item: ClientPagedListItem;
    key: ClientPagedListColumnKey;
    componentInjector: Injector;
    constructor(injector: Injector);
    ngOnInit(): void;
    private createComponentInjector;
}
