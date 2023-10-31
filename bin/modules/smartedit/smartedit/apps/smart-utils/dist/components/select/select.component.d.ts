import { EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { ISelectItem } from '../../interfaces/i-select-item';
import { BaseValueAccessor } from '../../utils/base-value-accessor';
export declare class SelectComponent<T> extends BaseValueAccessor<ISelectItem<T>> implements OnChanges {
    items: ISelectItem<T>[];
    initialValue: ISelectItem<T>;
    placeholder: string;
    isKeyboardControlEnabled: boolean;
    hasCustomTrigger: boolean;
    onItemSelected: EventEmitter<ISelectItem<T>>;
    isOpen: boolean;
    selectItem(id: number): void;
    ngOnChanges(changes: SimpleChanges): void;
    private setInitialValue;
    private setValue;
    private setValueById;
}
