/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';
import { WIZARD_API } from 'smarteditcommons';
import { WizardStepApi } from '../../../pageWizard';

@Component({
    selector: 'se-page-condition-step',
    template: `
        <se-event-message
            class="existing-homepage__ymessage"
            [showEvent]="'CMS_EVENT_SHOW_REPLACE_PARENT_HOMEPAGE_INFO'"
            [hideEvent]="'CMS_EVENT_HIDE_REPLACE_PARENT_HOMEPAGE_INFO'">
        </se-event-message>

        <se-new-page-display-condition
            [pageTypeCode]="wizardApi.getPageTypeCode()"
            [uriContext]="wizardApi.uriContext"
            [resultFn]="wizardApi.variationResult"></se-new-page-display-condition>
    `
})
export class PageDisplayConditionStepComponent {
    constructor(@Inject(WIZARD_API) public wizardApi: WizardStepApi) {}
}
