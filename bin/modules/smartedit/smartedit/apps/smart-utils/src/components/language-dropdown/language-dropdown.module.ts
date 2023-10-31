/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { SelectModule } from '../select';
import { LanguageDropdownComponent } from './language-dropdown.component';

@NgModule({
    imports: [CommonModule, SelectModule],
    declarations: [LanguageDropdownComponent],
    entryComponents: [LanguageDropdownComponent],
    exports: [LanguageDropdownComponent]
})
export class LanguageDropdownModule {}
