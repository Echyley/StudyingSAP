/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';

import { CollapsibleContainerComponent } from './CollapsibleContainerComponent';
import { CollapsibleContainerContentComponent } from './CollapsibleContainerContentComponent';
import { CollapsibleContainerHeaderComponent } from './CollapsibleContainerHeaderComponent';

@NgModule({
    imports: [TranslateModule.forChild(), CommonModule],
    declarations: [
        CollapsibleContainerComponent,
        CollapsibleContainerContentComponent,
        CollapsibleContainerHeaderComponent
    ],
    entryComponents: [
        CollapsibleContainerComponent,
        CollapsibleContainerContentComponent,
        CollapsibleContainerHeaderComponent
    ],
    exports: [
        CollapsibleContainerComponent,
        CollapsibleContainerContentComponent,
        CollapsibleContainerHeaderComponent
    ]
})
export class CollapsibleContainerModule {}
