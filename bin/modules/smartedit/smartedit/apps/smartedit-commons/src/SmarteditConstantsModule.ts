/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NgModule } from '@angular/core';

import { EXTENDED_VIEW_PORT_MARGIN, HEART_BEAT_TIMEOUT_THRESHOLD_MS } from './utils';
import {
    EXTENDED_VIEW_PORT_MARGIN_TOKEN,
    HEART_BEAT_TIMEOUT_THRESHOLD_MS_TOKEN
} from './utils/SmarteditConstantsTokens';

@NgModule({
    providers: [
        {
            provide: EXTENDED_VIEW_PORT_MARGIN_TOKEN,
            useValue: EXTENDED_VIEW_PORT_MARGIN
        },
        {
            provide: HEART_BEAT_TIMEOUT_THRESHOLD_MS_TOKEN,
            useValue: HEART_BEAT_TIMEOUT_THRESHOLD_MS
        }
    ]
})
export class SmarteditConstantsModule {}
