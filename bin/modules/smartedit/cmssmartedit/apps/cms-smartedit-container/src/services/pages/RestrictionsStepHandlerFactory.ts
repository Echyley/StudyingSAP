/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* eslint-disable max-classes-per-file */
import { Injectable } from '@angular/core';
import { IRestrictionsStepHandler } from 'cmssmarteditcontainer/interfaces';
import { WizardService, WizardStep } from 'smarteditcommons';
import { RestrictionsEditorFunctionBindings } from './types';

export class RestrictionsStepHandler implements IRestrictionsStepHandler {
    private stepDetails: WizardStep;

    constructor(
        private wizardManager: WizardService,
        private restrictionsEditorFunctionBindings: RestrictionsEditorFunctionBindings,
        stepProperties: WizardStep
    ) {
        this.stepDetails = stepProperties;
    }

    public hideStep(): void {
        if (this.isStepOnWizard()) {
            this.wizardManager.removeStepById(this.getStepId());
        }
    }

    public showStep(): void {
        if (!this.isStepOnWizard()) {
            this.wizardManager.addStep(this.stepDetails, this.wizardManager.getStepsCount());
        }
    }

    public isStepValid(): boolean {
        return (
            this.restrictionsEditorFunctionBindings.isDirty &&
            this.restrictionsEditorFunctionBindings.isDirty()
        );
    }

    public getStepId(): string {
        return this.stepDetails.id;
    }

    public goToStep(): void {
        this.wizardManager.goToStepWithId(this.getStepId());
    }

    private isStepOnWizard(): boolean {
        return this.wizardManager.containsStep(this.getStepId());
    }
}

@Injectable()
export class RestrictionsStepHandlerFactory {
    public createRestrictionsStepHandler(
        wizardManager: WizardService,
        restrictionsEditorFunctionBindings: RestrictionsEditorFunctionBindings,
        stepProperties: WizardStep
    ): RestrictionsStepHandler {
        return new RestrictionsStepHandler(
            wizardManager,
            restrictionsEditorFunctionBindings,
            stepProperties
        );
    }
}
