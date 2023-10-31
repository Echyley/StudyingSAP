/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
    selector: 'se-page-display-status-wrapper',
    template: `
        <div class="se-page-status-toolbar-container">
            <se-page-display-status></se-page-display-status>
        </div>
    `,
    styles: [
        `
            .se-page-status-toolbar-container {
                padding-left: 5px;
                padding-right: 20px;
                height: 100%;
                display: flex;
                align-items: center;
                border-left: 1px solid var(--sapGroup_ContentBorderColor);
            }
        `
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PageDisplayStatusWrapperComponent {}
