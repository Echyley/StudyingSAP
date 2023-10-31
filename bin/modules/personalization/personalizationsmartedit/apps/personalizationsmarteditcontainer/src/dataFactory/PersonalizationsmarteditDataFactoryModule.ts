/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SePersonalizationsmarteditServicesModule } from '../service';
import { CustomizationDataFactory } from './CustomizationDataFactory';

@NgModule({
    imports: [CommonModule, SePersonalizationsmarteditServicesModule],
    providers: [CustomizationDataFactory]
})
export class PersonalizationsmarteditDataFactoryModule {}
