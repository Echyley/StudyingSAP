/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FundamentalNgxCoreModule } from '@fundamental-ngx/core';
import { TranslationModule } from 'smarteditcommons';
import { ModalFullScreenButtonComponent } from './ModalFullScreenButtonComponent';
import { ModalFullScreenButtonService } from './ModalFullScreenButtonService';
@NgModule({
    imports: [FundamentalNgxCoreModule, CommonModule, TranslationModule.forChild()],
    providers: [ModalFullScreenButtonService],
    declarations: [ModalFullScreenButtonComponent],
    exports: [ModalFullScreenButtonComponent]
})
export class ModalFullScreenButtonModule {}
