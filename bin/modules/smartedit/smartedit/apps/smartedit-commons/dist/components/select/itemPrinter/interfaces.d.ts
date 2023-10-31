import { InjectionToken } from '@angular/core';
import { SelectItem } from '../interfaces';
import { SelectComponent } from '../SelectComponent';
export interface ItemComponentData<T extends SelectItem = any> {
    item: T;
    selected: boolean;
    select: SelectComponent<T>;
}
export declare const ITEM_COMPONENT_DATA_TOKEN: InjectionToken<ItemComponentData<any>>;
