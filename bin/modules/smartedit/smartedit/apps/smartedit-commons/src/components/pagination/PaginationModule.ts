/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NgModule } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';

import { FundamentalsModule } from '../../FundamentalsModule';
import { PaginationComponent } from './PaginationComponent';

@NgModule({
    imports: [FundamentalsModule, TranslateModule.forChild()],
    declarations: [PaginationComponent],
    exports: [PaginationComponent]
})
export class PaginationModule {}
