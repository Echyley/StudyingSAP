/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NgModule } from '@angular/core';

import { ListItemKeyboardControlDirective } from './list-item-keyboard-control.directive';
import { ListKeyboardControlDirective } from './list-keyboard-control.directive';

@NgModule({
    declarations: [ListKeyboardControlDirective, ListItemKeyboardControlDirective],
    exports: [ListKeyboardControlDirective, ListItemKeyboardControlDirective]
})
export class ListKeyboardControlModule {}
