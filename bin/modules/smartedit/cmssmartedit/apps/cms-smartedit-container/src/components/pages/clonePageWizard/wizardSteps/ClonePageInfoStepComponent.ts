/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';
import { WIZARD_API } from 'smarteditcommons';
import { WizardStepApi } from '../../pageWizard';

@Component({
    selector: 'se-clone-page-info-step',
    template: `
        <se-component-clone-info-form
            *ngIf="wizardApi.isPageInfoActive()"
            [structure]="wizardApi.getPageInfoStructure()"
            [content]="wizardApi.getPageInfo()"
            [(submit)]="wizardApi.callbacks.savePageInfo"
            [(reset)]="wizardApi.callbacks.resetPageInfo"
            [(isDirty)]="wizardApi.callbacks.isDirtyPageInfo"
            [(isValid)]="wizardApi.callbacks.isValidPageInfo"
            [pageTemplate]="wizardApi.getPageTemplate()"
            [pageTypeCode]="wizardApi.getPageTypeCode()"
            [uriContext]="wizardApi.uriContext"
            [targetCatalogVersion]="wizardApi.getTargetCatalogVersion()">
        </se-component-clone-info-form>
    `
})
export class ClonePageInfoStepComponent {
    constructor(@Inject(WIZARD_API) public wizardApi: WizardStepApi) {}
}
