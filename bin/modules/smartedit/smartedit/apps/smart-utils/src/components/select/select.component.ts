/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */

import {
    forwardRef,
    Component,
    EventEmitter,
    Input,
    OnChanges,
    Output,
    SimpleChanges,
    ViewEncapsulation
} from '@angular/core';
import { NG_VALUE_ACCESSOR } from '@angular/forms';

import { ISelectItem } from '../../interfaces/i-select-item';
import { BaseValueAccessor } from '../../utils/base-value-accessor';

/**
 * @ngdoc component
 * @name  @smartutils.components:SelectComponent
 *
 * @description
 * Dropdown component allowing to select item from dropdown
 *
 * @param {ISelectItem<T>[]} items Items to displayed in dropdown
 * @param {ISelectItem<T>} initialValue Value dropdown should be initialized with
 * @param {string} placeholder String to be displayed when value is not selected
 * @param {Boolean} isKeyboardControlEnabled Flag enabling dropdown items selection with arrow keys
 * @param {Boolean} hasCustomTrigger If set true the trigger opening the dropdown will be set by user
 */

@Component({
    selector: 'su-select',
    encapsulation: ViewEncapsulation.None,
    providers: [
        { provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => SelectComponent), multi: true }
    ],
    styleUrls: ['./select.component.scss'],
    template: `
        <fd-popover [(isOpen)]="isOpen" fillControlMode="equal" class="su-select">
            <fd-popover-control class="su-select__popover-control">
                <button
                    fd-button
                    aria-expanded="true"
                    class="fd-dropdown__control fd-button fd-select__control"
                    type="button"
                    *ngIf="!hasCustomTrigger; else customTrigger"
                    label="{{ (value && value.label) || placeholder | translate }}">
                    <span class="fd-button fd-button--transparent se-select-arrow">
                        <i class="sap-icon--slim-arrow-down"></i>
                    </span>
                </button>
                <ng-template #customTrigger>
                    <ng-content select="[su-select-custom-trigger]"></ng-content>
                </ng-template>
            </fd-popover-control>
            <fd-popover-body>
                <div class="su-select__menu">
                    <ul
                        fd-list
                        suListKeyboardControl
                        [suListKeyboardControlEnabled]="isKeyboardControlEnabled && isOpen"
                        (suListKeyboardControlEnterKeydown)="selectItem($event)"
                        class="fd-list--dropdown">
                        <li
                            fd-list-item
                            suListItemKeyboardControl
                            [ngClass]="item.listItemClassName"
                            *ngFor="let item of items"
                            (click)="selectItem(item.id)"
                            [attr.tabindex]="-1"
                            [attr.data-select-id]="item.id"
                            role="option">
                            <a fd-list-link>
                                <span fd-list-title> {{ item.label | translate }} </span>
                            </a>
                        </li>
                    </ul>
                </div>
            </fd-popover-body>
        </fd-popover>
    `
})
export class SelectComponent<T> extends BaseValueAccessor<ISelectItem<T>> implements OnChanges {
    @Input() items: ISelectItem<T>[] = [];
    @Input() initialValue: ISelectItem<T> = null as unknown as ISelectItem<T>;
    @Input() placeholder = '';
    @Input() isKeyboardControlEnabled = true;
    @Input() hasCustomTrigger = false;
    @Output() onItemSelected: EventEmitter<ISelectItem<T>> = new EventEmitter();

    public isOpen = false;

    selectItem(id: number): void {
        const item: ISelectItem<T> = this.items.find((selected) => selected.id === id);

        this.setValue(item);
        this.isOpen = false;
        this.onItemSelected.emit(item);
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.initialValue && changes.initialValue.currentValue && !this.value) {
            this.setInitialValue(changes.initialValue.currentValue);
        }
    }

    private setInitialValue(value: ISelectItem<T> | number): void {
        if (typeof value === 'number') {
            this.setValueById(value);
        } else {
            this.setValue(value);
        }
    }

    private setValue(item: ISelectItem<T>): void {
        this.writeValue(item);
        this.onChange(item);
    }

    private setValueById(id: number): void {
        this.setValue(this.items.find((item) => item.id === id));
    }
}
