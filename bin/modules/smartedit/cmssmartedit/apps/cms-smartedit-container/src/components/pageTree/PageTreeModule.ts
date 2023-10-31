/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { PopupOverlayModule, TranslationModule } from 'smarteditcommons';
import { PageTreeChildComponent } from './PageTreeChildComponent/PageTreeChildComponent';
import { PageTreeComponent } from './PageTreeComponent/PageTreeComponent';
import {
    PageTreeMoreItemsComponent,
    PageTreeMenuItemOverlayComponent,
    PageTreeComponentMenuComponent,
    PageTreeChildComponentMenu
} from './PageTreeComponentMenu';
import { PageTreePanel } from './PageTreePanel/PageTreePanel';
import { PageTreeSlot } from './PageTreeSlot/PageTreeSlot';
import { PageTreeSlotMenuComponent, PageTreeSlotMenuItem } from './PageTreeSlotMenu';
import './slotButtonComponent.scss';
import './componentButtonComponent.scss';

@NgModule({
    imports: [CommonModule, PopupOverlayModule, TranslationModule.forChild()],
    declarations: [
        PageTreePanel,
        PageTreeSlot,
        PageTreeComponent,
        PageTreeMoreItemsComponent,
        PageTreeMenuItemOverlayComponent,
        PageTreeComponentMenuComponent,
        PageTreeSlotMenuComponent,
        PageTreeSlotMenuItem,
        PageTreeChildComponentMenu,
        PageTreeChildComponent
    ],
    entryComponents: [
        PageTreePanel,
        PageTreeSlot,
        PageTreeComponent,
        PageTreeMoreItemsComponent,
        PageTreeMenuItemOverlayComponent,
        PageTreeComponentMenuComponent,
        PageTreeSlotMenuComponent,
        PageTreeSlotMenuItem,
        PageTreeChildComponentMenu,
        PageTreeChildComponent
    ]
})
export class PageTreeModule {}
