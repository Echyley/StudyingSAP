/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* forbiddenNameSpaces useValue:false */
import { Component, EventEmitter, Injector, Input, OnInit, Output } from '@angular/core';
import { DROPDOWN_MENU_ITEM_DATA } from './DropdownMenuItemDefaultComponent';
import { IDropdownMenuItem } from './IDropdownMenuItem';

/** @internal */
@Component({
    selector: 'se-dropdown-menu-item',
    templateUrl: './DropdownMenuItemComponent.html'
})
export class DropdownMenuItemComponent implements OnInit {
    @Input() dropdownItem: IDropdownMenuItem;
    @Input() selectedItem: any;
    @Output() selectedItemChange = new EventEmitter<any>();

    public dropdownItemInjector: Injector;

    constructor(private injector: Injector) {}

    ngOnInit(): void {
        this.createDropdownItemInjector();
    }

    onClick(): void {
        this.selectedItemChange.emit(this.dropdownItem);
    }

    private createDropdownItemInjector(): void {
        const { selectedItem } = this;
        this.dropdownItemInjector = Injector.create({
            parent: this.injector,
            providers: [
                {
                    provide: DROPDOWN_MENU_ITEM_DATA,
                    useValue: {
                        dropdownItem: this.dropdownItem,
                        selectedItem
                    }
                }
            ]
        });
    }
}
