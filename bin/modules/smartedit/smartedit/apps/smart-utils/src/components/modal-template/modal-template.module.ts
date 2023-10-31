/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ButtonModule } from '@fundamental-ngx/core';
import { DialogModule } from '@fundamental-ngx/core/dialog';

import { TranslationModule } from '../../services/translations';
import { ModalTemplateComponent } from './modal-template.component';

@NgModule({
    imports: [CommonModule, DialogModule, ButtonModule, TranslationModule.forChild()],
    declarations: [ModalTemplateComponent],
    entryComponents: [ModalTemplateComponent],
    exports: [ModalTemplateComponent]
})
export class FundamentalModalTemplateModule {}
