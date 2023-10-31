/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TranslationModule } from '../../services';
import { MessageModule } from '../message';
import { ConfirmDialogComponent } from './ConfirmDialogComponent';

@NgModule({
    imports: [CommonModule, TranslationModule.forChild(), MessageModule],
    declarations: [ConfirmDialogComponent],
    entryComponents: [ConfirmDialogComponent]
})
export class ConfirmDialogModule {}
