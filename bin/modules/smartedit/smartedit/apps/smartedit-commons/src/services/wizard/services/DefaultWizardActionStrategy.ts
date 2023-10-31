/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { IWizardActionStrategy, WizardAction, WizardConfig, WizardStep } from './types';
import { WizardActions } from './WizardActions';
import { WizardService } from './WizardService';

/* @internal */
@Injectable()
export class DefaultWizardActionStrategy implements IWizardActionStrategy {
    constructor(private readonly wizardActions: WizardActions) {}

    applyStrategy(wizardService: WizardService, conf: WizardConfig): void {
        const nextAction = this.applyOverrides(
            wizardService,
            this.wizardActions.next(),
            conf.nextLabel,
            conf.onNext,
            conf.isFormValid
        );
        const doneAction = this.applyOverrides(
            wizardService,
            this.wizardActions.done(),
            conf.doneLabel,
            conf.onDone,
            conf.isFormValid
        );

        const backConf = conf.backLabel
            ? {
                  i18n: conf.backLabel
              }
            : null;
        const backAction = this.wizardActions.back(backConf);

        conf.steps.forEach((step: WizardStep, index: number) => {
            step.actions = [];
            if (index > 0) {
                step.actions.push(backAction);
            }
            if (index === conf.steps.length - 1) {
                step.actions.push(doneAction);
            } else {
                step.actions.push(nextAction);
            }
        });

        conf.cancelAction = this.applyOverrides(
            wizardService,
            this.wizardActions.cancel(),
            conf.cancelLabel,
            conf.onCancel,
            null
        );
        conf.templateOverride = 'modalWizardNavBarTemplate.html';
    }

    private applyOverrides(
        wizardService: WizardService,
        action: WizardAction,
        label: string,
        executeCondition: (stepId: string) => boolean | Promise<any>,
        enableCondition: (stepId: string) => boolean
    ): WizardAction {
        if (label) {
            action.i18n = label;
        }
        if (executeCondition) {
            action.executeIfCondition = function (): boolean | Promise<any> {
                return executeCondition(wizardService.getCurrentStepId());
            };
        }
        if (enableCondition) {
            action.enableIfCondition = function (): boolean {
                return enableCondition(wizardService.getCurrentStepId());
            };
        }

        return action;
    }
}
