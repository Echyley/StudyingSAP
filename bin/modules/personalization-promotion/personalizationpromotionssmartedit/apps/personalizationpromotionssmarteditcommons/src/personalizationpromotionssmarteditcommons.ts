/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { PromotionsSmarteditRestService } from './services/PromotionsSmarteditRestService';
@NgModule({
    imports: [CommonModule, BrowserModule],
    providers: [PromotionsSmarteditRestService]
})
export class PersonalizationPromotionsSmarteditCommonsModule {}
