/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NgModule } from '@angular/core';

import { CustomComponentOutletDirective } from './CustomComponentOutletDirective';

/** @ignore */
@NgModule({
    declarations: [CustomComponentOutletDirective],
    exports: [CustomComponentOutletDirective]
})
export class CustomComponentOutletDirectiveModule {}
