/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { PopoverModule } from '@fundamental-ngx/core';
import { PopupOverlayComponent } from './PopupOverlayComponent';

@NgModule({
    imports: [CommonModule, PopoverModule],
    declarations: [PopupOverlayComponent],
    entryComponents: [PopupOverlayComponent],
    exports: [PopupOverlayComponent]
})
export class PopupOverlayModule {}
