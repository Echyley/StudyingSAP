import { Injector, SimpleChanges, Type } from '@angular/core';
import { SelectItem } from '../interfaces';
import { SelectComponent } from '../SelectComponent';
import { ItemComponentData } from './interfaces';
export declare class ItemPrinterComponent<T extends SelectItem> {
    private injector;
    item: T;
    component: Type<any>;
    selectComponentCtx: SelectComponent<T>;
    isSelected: boolean;
    componentInjector: Injector;
    itemComponentData: ItemComponentData;
    constructor(injector: Injector);
    ngOnChanges(changes: SimpleChanges): void;
    private createItemComponentData;
    private createItemComponentInjector;
}
