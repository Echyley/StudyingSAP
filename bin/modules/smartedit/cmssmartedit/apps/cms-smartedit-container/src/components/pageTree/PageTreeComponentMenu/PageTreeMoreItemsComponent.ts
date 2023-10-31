/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component } from '@angular/core';
import { PopupOverlayConfig } from 'smarteditcommons';
import { PageTreeMenuItemOverlayComponent } from './PageTreeMenuItemOverlayComponent';
import { ParentMenu } from './ParentMenu';

@Component({
    selector: 'se-page-tree-more-items',
    styleUrls: ['./PageTreeComponentMenuComponent.scss'],
    template: `
        <div class="se-page-tree-component-more">
            <ul
                id="{{ parent.component.uid }}-{{ parent.component.typeCode }}-more-menu"
                class="fd-menu__list se-page-tree-component-more-list">
                <li
                    *ngFor="let item of parent.getItems()"
                    [attr.data-smartedit-id]="parent.component.uid"
                    [attr.data-smartedit-type]="parent.component.typeCode"
                    class="se-page-tree-component-more-menu fd-menu__item">
                    <se-popup-overlay
                        [popupOverlay]="itemTemplateOverlayWrapper"
                        [popupOverlayTrigger]="parent.canShowTemplate(item)"
                        [popupOverlayData]="{ item: item }"
                        (popupOverlayOnHide)="parent.onHideItemPopup(true)">
                        <span
                            class="se-page-tree-component-more-menu--span"
                            (click)="parent.triggerMenuItemAction(item, $event)">
                            {{ item.i18nKey | translate }}
                        </span>
                    </se-popup-overlay>
                </li>
            </ul>
        </div>
    `
})
export class PageTreeMoreItemsComponent {
    public itemTemplateOverlayWrapper: PopupOverlayConfig = {
        component: PageTreeMenuItemOverlayComponent
    };
    constructor(public parent: ParentMenu) {}
}
