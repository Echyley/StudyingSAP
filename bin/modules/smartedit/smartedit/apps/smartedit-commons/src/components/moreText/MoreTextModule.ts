/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MoreTextComponent } from './MoreTextComponent';

@NgModule({
    imports: [CommonModule],
    declarations: [MoreTextComponent],
    entryComponents: [MoreTextComponent],
    exports: [MoreTextComponent]
})
export class MoreTextModule {}
