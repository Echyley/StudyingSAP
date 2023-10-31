/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* eslint-disable max-classes-per-file */
/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NgModule } from '@angular/core';
import { OptionsDropdownPopulator } from './options';
import { UriDropdownPopulator } from './uri';

/**
 * so here we return constructor function with pre-set dependencies.
 */

@NgModule({
    providers: [OptionsDropdownPopulator, UriDropdownPopulator]
})
export class DropdownPopulatorModule {}
