/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TooltipModule, TranslationModule } from 'smarteditcommons';
import { SegmentExpressionAsHtmlComponent } from './SegmentExpressionAsHtmlComponent';

@NgModule({
    imports: [CommonModule, TooltipModule, TranslationModule.forChild()],
    declarations: [SegmentExpressionAsHtmlComponent],
    exports: [SegmentExpressionAsHtmlComponent]
})
export class SegmentExpressionAsHtmlModule {}
