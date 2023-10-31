/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FundamentalNgxCoreModule } from '@fundamental-ngx/core';
import { ListModule } from '@fundamental-ngx/core/list';
import { ListKeyboardControlModule } from '../../directives';
import { TranslationModule } from '../../services/translations';
import { SelectComponent } from './select.component';

@NgModule({
    imports: [
        FundamentalNgxCoreModule,
        CommonModule,
        ListKeyboardControlModule,
        TranslationModule.forChild(),
        ListModule
    ],
    declarations: [SelectComponent],
    entryComponents: [SelectComponent],
    exports: [SelectComponent]
})
export class SelectModule {}
