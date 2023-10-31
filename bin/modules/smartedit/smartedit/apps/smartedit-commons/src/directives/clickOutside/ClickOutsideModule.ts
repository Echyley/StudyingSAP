/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { ClickOutsideDirective } from './ClickOutsideDirective';

@NgModule({
    imports: [CommonModule],
    declarations: [ClickOutsideDirective],
    exports: [ClickOutsideDirective]
})
export class ClickOutsideModule {}
