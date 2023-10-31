/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, forwardRef, Inject, OnInit } from '@angular/core';
import { DROPDOWN_MENU_ITEM_DATA, IDropdownMenuItemData } from 'smarteditcommons';
import { TargetGroupTabComponent } from '../TargetGroupTabComponent';
import { TargetGroupDropdownItemSelectedItem } from './types';

@Component({
    selector: 'perso-set-variation-rank-down-item',
    template: `
        <a
            (click)="moveDown($event)"
            [ngClass]="{ 'perso-dropdown-menu__item--disabled disabled': isLast }"
            class="se-dropdown-item fd-list__link"
            translate="personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.movedown"
        ></a>
    `,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MoveVariationDownItemComponent implements OnInit {
    public isLast: boolean;

    private selectedItem: TargetGroupDropdownItemSelectedItem;

    constructor(
        @Inject(DROPDOWN_MENU_ITEM_DATA) dropdownMenuItemData: IDropdownMenuItemData,
        @Inject(forwardRef(() => TargetGroupTabComponent))
        private targetGroupTabComponent: TargetGroupTabComponent
    ) {
        ({ selectedItem: this.selectedItem } = dropdownMenuItemData);

        this.isLast = false;
    }

    ngOnInit(): void {
        this.isLast =
            this.selectedItem.variation.rank ===
            this.targetGroupTabComponent.visibleVariations.length - 1;
    }

    public moveDown(event: PointerEvent): void {
        if (this.isLast) {
            event.stopPropagation(); // do not close the dropdown when disabled button is clicked
            return;
        }

        this.targetGroupTabComponent.setVariationRank(
            this.selectedItem.variation,
            1,
            event,
            this.isLast
        );
    }
}
