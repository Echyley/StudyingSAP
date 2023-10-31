/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';
import { WIZARD_API } from 'smarteditcommons';
import { WizardStepApi } from '../../pageWizard';

@Component({
    selector: 'se-clone-page-options-step',
    template: `
        <span *ngIf="wizardApi.isRestrictionsActive()" class="se-clone-page-restriction-step">
            <div *ngIf="wizardApi.getPageRestrictions().length > 1">
                <se-page-restrictions-info-message></se-page-restrictions-info-message>
            </div>

            <se-restrictions-editor
                [editable]="true"
                [(resetFn)]="wizardApi.restrictionsEditorFunctionBindings.reset"
                [(cancelFn)]="wizardApi.restrictionsEditorFunctionBindings.cancel"
                [(isDirtyFn)]="wizardApi.restrictionsEditorFunctionBindings.isDirty"
                (onRestrictionsChange)="wizardApi.restrictionsResult($event)"
                [getRestrictionTypes]="wizardApi.getRestrictionTypes"
                [getSupportedRestrictionTypes]="wizardApi.getSupportedRestrictionTypes"
                [item]="wizardApi.getBasePageInfo()"
                [restrictionUuids]="wizardApi.getPageRestrictions()">
            </se-restrictions-editor>
        </span>
    `
})
export class ClonePageRestrictionsStepComponent {
    constructor(@Inject(WIZARD_API) public wizardApi: WizardStepApi) {}
}
