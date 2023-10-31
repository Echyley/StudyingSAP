/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NgModule } from '@angular/core';
import { LinkModule } from '@fundamental-ngx/core/link';
import { TranslationModule } from 'smarteditcommons';
import { ComponentVisibilityAlertComponent } from './ComponentVisibilityAlertComponent';

@NgModule({
    imports: [TranslationModule.forChild(), LinkModule],
    declarations: [ComponentVisibilityAlertComponent],
    entryComponents: [ComponentVisibilityAlertComponent]
})
export class ComponentVisibilityAlertModule {}
