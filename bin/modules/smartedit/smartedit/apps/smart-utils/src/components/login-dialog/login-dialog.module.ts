/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MessageStripModule, ButtonModule, FormModule } from '@fundamental-ngx/core';
import { TranslationModule } from '../../services/translations';
import { LanguageDropdownModule } from '../language-dropdown';
import { LoginDialogComponent } from './login-dialog.component';

@NgModule({
    imports: [
        CommonModule,
        LanguageDropdownModule,
        FormsModule,
        ReactiveFormsModule,
        MessageStripModule,
        TranslationModule.forChild(),
        ButtonModule,
        FormModule
    ],
    declarations: [LoginDialogComponent],
    entryComponents: [LoginDialogComponent],
    exports: [LanguageDropdownModule]
})
export class LoginDialogModule {}
