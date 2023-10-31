import { Injector, OnChanges, SimpleChanges } from '@angular/core';
import { DynamicPagedListColumnKey, DynamicPagedListDropdownItem } from '../dynamicPagedList/interfaces';
export declare class DataTableRendererComponent implements OnChanges {
    private readonly injector;
    column: DynamicPagedListColumnKey;
    item: DynamicPagedListDropdownItem;
    componentInjector: Injector;
    constructor(injector: Injector);
    ngOnChanges(changes: SimpleChanges): void;
    private createInjector;
}
