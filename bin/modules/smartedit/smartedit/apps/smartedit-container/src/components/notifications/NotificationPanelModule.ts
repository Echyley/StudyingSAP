/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { CustomComponentOutletDirectiveModule, SharedComponentsModule } from 'smarteditcommons';
import { SmarteditServicesModule } from '../../services/SmarteditServicesModule';

import { NotificationComponent } from './NotificationComponent';
import { NotificationPanelComponent } from './NotificationPanelComponent';

@NgModule({
    imports: [
        SmarteditServicesModule,
        SharedComponentsModule,
        CommonModule,
        CustomComponentOutletDirectiveModule
    ],
    declarations: [NotificationPanelComponent, NotificationComponent],
    exports: [NotificationPanelComponent]
})
export class NotificationPanelModule {}
