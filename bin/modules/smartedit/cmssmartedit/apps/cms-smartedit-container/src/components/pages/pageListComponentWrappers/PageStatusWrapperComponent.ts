/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component } from '@angular/core';
import { BaseWrapperComponent } from './BaseWrapperComponent';

@Component({
    selector: 'se-page-status-wrapper',
    template: `<div>
        <se-page-display-status
            [cmsPage]="item"
            [showLastSyncTime]="false"></se-page-display-status>
    </div>`
})
export class PageStatusWrapperComponent extends BaseWrapperComponent {}
