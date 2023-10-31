/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';
import { WIZARD_API } from 'smarteditcommons';
import { WizardStepApi } from '../../../pageWizard';

@Component({
    selector: 'se-page-type-step',
    template: `
        <se-select-page-type
            [pageTypeCode]="wizardApi.getPageTypeCode()"
            (onTypeSelected)="wizardApi.typeSelected($event)">
        </se-select-page-type>
    `
})
export class PageTypeStepComponent {
    constructor(@Inject(WIZARD_API) public wizardApi: WizardStepApi) {}
}
