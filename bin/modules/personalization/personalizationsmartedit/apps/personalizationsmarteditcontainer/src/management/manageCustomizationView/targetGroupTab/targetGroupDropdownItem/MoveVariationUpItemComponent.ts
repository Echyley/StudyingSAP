/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, forwardRef, Inject, OnInit } from '@angular/core';
import { DROPDOWN_MENU_ITEM_DATA, IDropdownMenuItemData } from 'smarteditcommons';
import { TargetGroupTabComponent } from '../TargetGroupTabComponent';
import { TargetGroupDropdownItemSelectedItem } from './types';

@Component({
    selector: 'perso-set-variation-rank-up-item',
    template: `
        <a
            (click)="moveUp($event)"
            [ngClass]="{ 'perso-dropdown-menu__item--disabled disabled': isFirst }"
            class="se-dropdown-item fd-list__link"
            translate="personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.moveup"
        ></a>
    `,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MoveVariationUpItemComponent implements OnInit {
    public isFirst: boolean;

    private selectedItem: TargetGroupDropdownItemSelectedItem;

    constructor(
        @Inject(DROPDOWN_MENU_ITEM_DATA) dropdownMenuItemData: IDropdownMenuItemData,
        @Inject(forwardRef(() => TargetGroupTabComponent))
        private targetGroupTabComponent: TargetGroupTabComponent
    ) {
        this.isFirst = false;

        ({ selectedItem: this.selectedItem } = dropdownMenuItemData);
    }

    ngOnInit(): void {
        this.isFirst = this.selectedItem.variation.rank === 0;
    }

    public moveUp(event: PointerEvent): void {
        if (this.isFirst) {
            event.stopPropagation(); // do not close the dropdown when disabled button is clicked
            return;
        }

        this.targetGroupTabComponent.setVariationRank(
            this.selectedItem.variation,
            -1,
            event,
            this.isFirst
        );
    }
}
