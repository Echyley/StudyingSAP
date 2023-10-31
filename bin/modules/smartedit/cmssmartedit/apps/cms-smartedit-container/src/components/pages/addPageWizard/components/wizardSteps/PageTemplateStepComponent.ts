/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';
import { WIZARD_API } from 'smarteditcommons';
import { WizardStepApi } from '../../../pageWizard';

@Component({
    selector: 'se-page-template-step',
    template: `
        <se-select-page-template
            [uriContext]="wizardApi.uriContext"
            [pageTypeCode]="wizardApi.getPageTypeCode()"
            (onTemplateSelected)="wizardApi.templateSelected($event)">
        </se-select-page-template>
    `
})
export class PageTemplateStepComponent {
    constructor(@Inject(WIZARD_API) public wizardApi: WizardStepApi) {}
}
