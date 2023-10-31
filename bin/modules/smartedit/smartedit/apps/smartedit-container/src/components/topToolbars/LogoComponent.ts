/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component } from '@angular/core';

@Component({
    selector: 'se-logo',
    template: `
        <div class="se-app-logo">
            <img
                src="static-resources/images/SAP_scrn_R.png"
                class="se-logo-image"
                width="48"
                height="24"
                title="{{ 'se.logo.title' | translate }}" />
            <div class="se-logo-text">{{ 'se.application.name' | translate }}</div>
        </div>
    `
})
export class LogoComponent {}
