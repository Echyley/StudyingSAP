/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { PreventVerticalOverflowComponent } from './PreventVerticalOverflowComponent';

@NgModule({
    imports: [CommonModule],
    declarations: [PreventVerticalOverflowComponent],
    entryComponents: [PreventVerticalOverflowComponent],
    exports: [PreventVerticalOverflowComponent]
})
export class PreventVerticalOverflowModule {}
