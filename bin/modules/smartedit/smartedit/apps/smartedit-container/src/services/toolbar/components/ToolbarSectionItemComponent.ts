/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Input } from '@angular/core';

import { ToolbarItemInternal } from 'smarteditcommons';

/** @internal  */
@Component({
    selector: 'se-toolbar-section-item',
    host: {
        class: 'se-toolbar-section-item'
    },
    template: `
        <se-toolbar-action-outlet [item]="item"></se-toolbar-action-outlet>
        <se-toolbar-item-context
            *ngIf="item.contextComponent"
            class="se-template-toolbar__context-template"
            [attr.data-item-key]="item.key"
            [itemKey]="item.key"
            [isOpen]="item.isOpen"
            [contextComponent]="item.contextComponent"></se-toolbar-item-context>
    `
})
export class ToolbarSectionItemComponent {
    @Input() item: ToolbarItemInternal;
}
