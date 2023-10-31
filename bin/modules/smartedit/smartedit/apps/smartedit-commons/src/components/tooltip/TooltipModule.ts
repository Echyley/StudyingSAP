/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FundamentalsModule } from '../../FundamentalsModule';
import { SeTranslationModule } from '../../modules';
import { TooltipComponent } from './TooltipComponent';

@NgModule({
    imports: [CommonModule, FundamentalsModule, SeTranslationModule.forChild()],
    declarations: [TooltipComponent],
    entryComponents: [TooltipComponent],
    exports: [TooltipComponent]
})
export class TooltipModule {}
