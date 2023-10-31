/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';
import { WIZARD_API } from 'smarteditcommons';
import { WizardStepApi } from '../../pageWizard';

@Component({
    selector: 'se-clone-page-options-step',
    template: `
        <se-event-message
            class="existing-homepage__ymessage"
            [showEvent]="'CMS_EVENT_SHOW_REPLACE_PARENT_HOMEPAGE_INFO'"
            [hideEvent]="'CMS_EVENT_HIDE_REPLACE_PARENT_HOMEPAGE_INFO'">
        </se-event-message>

        <se-select-target-catalog-version
            *ngIf="wizardApi.isBasePageInfoAvailable()"
            [pageTypeCode]="wizardApi.getPageTypeCode()"
            [pageLabel]="wizardApi.getPageLabel()"
            [uriContext]="wizardApi.uriContext"
            (onTargetCatalogVersionSelected)="wizardApi.onTargetCatalogVersionSelected($event)">
        </se-select-target-catalog-version>
        <se-new-page-display-condition
            [pageTypeCode]="wizardApi.getPageTypeCode()"
            [uriContext]="wizardApi.uriContext"
            [resultFn]="wizardApi.variationResult"
            [initialConditionSelectedKey]="'page.displaycondition.variation'"
            [pageUuid]="wizardApi.getBasePageUuid()"
            [targetCatalogVersion]="wizardApi.getTargetCatalogVersion()">
        </se-new-page-display-condition>

        <se-component-clone-option-form
            (onSelectionChange)="
                wizardApi.triggerUpdateCloneOptionResult($event)
            "></se-component-clone-option-form>
    `
})
export class ClonePageOptionsStepComponent {
    constructor(@Inject(WIZARD_API) public wizardApi: WizardStepApi) {}
}
