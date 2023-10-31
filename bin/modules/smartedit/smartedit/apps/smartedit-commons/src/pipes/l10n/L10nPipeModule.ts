/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NgModule } from '@angular/core';
import { L10nService } from '../../services';
import { moduleUtils } from '../../utils';

import { L10nPipe } from './L10nPipe';

@NgModule({
    declarations: [L10nPipe],
    exports: [L10nPipe],
    providers: [
        L10nPipe,
        moduleUtils.initialize(
            (l10nService: L10nService) => l10nService.resolveLanguage(),
            [L10nService]
        )
    ]
})
export class L10nPipeModule {}
