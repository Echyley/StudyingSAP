/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NgModule } from '@angular/core';
import { IAlertService, AlertModule } from 'smarteditcommons';
import { AlertFactory } from './AlertFactory';
import { AlertService } from './AlertServiceOuter';
@NgModule({
    imports: [AlertModule],
    providers: [
        AlertFactory,
        {
            provide: IAlertService,
            useClass: AlertService
        }
    ]
})
export class AlertServiceModule {}
