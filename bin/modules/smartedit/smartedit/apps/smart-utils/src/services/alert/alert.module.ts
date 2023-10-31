/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { NgModule } from '@angular/core';
import { AlertModule as FundamentalAlertModule } from '@fundamental-ngx/core';
import { TranslationModule } from '../translations';
import { AlertFactory } from './alert-factory';
import { ALERT_CONFIG_DEFAULTS, ALERT_CONFIG_DEFAULTS_TOKEN } from './alert-token';
import { AlertService } from './alert.service';

@NgModule({
    imports: [FundamentalAlertModule, TranslationModule],
    providers: [
        {
            provide: ALERT_CONFIG_DEFAULTS_TOKEN,
            useValue: ALERT_CONFIG_DEFAULTS
        },
        AlertService,
        AlertFactory
    ]
})
export class AlertModule {}
