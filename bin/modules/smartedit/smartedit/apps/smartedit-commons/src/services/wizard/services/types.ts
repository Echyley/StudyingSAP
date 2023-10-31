/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Type } from '@angular/core';
import { TypedMap } from '@smart/utils';
import { WizardService } from './WizardService';

export interface IWizardActionStrategy {
    applyStrategy(wizardService: WizardService, conf: WizardConfig): void;
}

export interface WizardAction {
    id?: string;
    i18n?: string;
    /**
     * Component to be used as a wizard controller.
     */
    component?: Type<any>;
    /**
     * **Deprecated since 2005, use [component]{@link WizardAction#component}.**
     *
     * An angular controller which will be the underlying controller
     * for all of the wizard. This controller MUST implement the function <strong>getWizardConfig()</strong> which
     * returns a {@link WizardConfig}.<br />
     * If you need to do any manual wizard manipulation, 'wizardManager' can be injected into your controller.
     * See {@link WizardService}.
     */
    controller?: string | (new (...args: any[]) => any);
    /**
     * **Deprecated since 2005, use [component]{@link WizardAction.component}.**
     *
     * An alternate controller name that can be used in your wizard step
     */
    controllerAs?: string;
    isMainAction?: boolean;
    destinationIndex?: number;
    stepIndex?: number;
    wizardService?: WizardService;
    /**
     * A map of properties to initialize the {@link WizardService} with. They are accessible under wizardManager.properties.
     * templates. By default the controller name is wizardController.
     */
    properties?: TypedMap<any>;
    isCurrentStep?(): boolean;
    enableIfCondition?(): boolean;
    executeIfCondition?(): boolean | Promise<any>;
    execute?(wizardService: WizardService): void;
}

/**
 * A plain JSON object, representing the configuration options for a single step in a wizard.
 */
export interface WizardStep {
    /**
     * An optional unique ID for this step in the wizard. If no ID is provided, one is automatically generated.<br />
     * You may choose to provide an ID, making it easier to reference this step explicitly via the wizard service, or
     * be able to identify for which step a callback is being triggered.
     */
    id: string;
    /** Step component */
    component: Type<any>;
    /**
     * An i18n key representing a meaning (short) name for this step.
     * This name will be displayed in the wizard navigation menu.
     */
    name: string;
    /**
     * An i18n key, representing the title that will be displayed at the top of the wizard for this step.
     */
    title: string;
    actions?: WizardAction[];
}

/**
 * A plain JSON object, representing the configuration options for a modal wizard
 */
export interface WizardConfig {
    /**
     * An ordered array of Wizard Steps.
     */
    steps: WizardStep[];
    actionStrategy?: IWizardActionStrategy;
    /**
     * An optional callback function that has no parameters. This callback is triggered after the done
     * action is fired, and the wizard is about to be closed. If this function is defined and returns a value, this
     * value will be returned in the resolved promise returned by the [open]{@link ModalWizard#open}.
     * This is an easy way to pass a result from the wizard to the caller.
     */
    resultFn?: () => void;
    /**
     * An optional callback function that receives a single parameter, the current step ID. This callback
     * is used to enable/disable the next action and the done action.
     * The callback should return a boolean to enabled the action. Null, or if this callback is not defined defaults to
     * true (enabled)
     */
    isFormValid?: (stepId: string) => boolean;
    /**
     * An optional callback function that receives a single parameter, the current step ID.
     * This callback is triggered after the next action is fired. You have the opportunity to halt the Next action by
     * returning promise and rejecting it, otherwise the wizard will continue and load the next step.
     */
    onNext?: (stepId: string) => boolean | Promise<any>;
    /**
     * An optional callback function that receives a single parameter, the current step ID.
     * This callback is triggered after the cancel action is fired. You have the opportunity to halt the cancel action
     * (thereby stopping the wizard from being closed), by returning a promise and rejecting it, otherwise the wizard will
     * continue the cancel action.
     */
    onCancel?: (stepId: string) => boolean | Promise<any>;
    /**
     * An optional callback function that has no parameters. This callback is triggered after the done
     * action is fired. You have the opportunity to halt the done action (thereby stopping the wizard from being closed),
     * by returning a promise and rejecting it, otherwise the wizard will continue and close the wizard.
     */
    onDone?: (stepId: string) => boolean | Promise<any>;
    /**
     * An optional i18n key to override the default label for the Done button
     */
    doneLabel?: string;
    /**
     * An optional i18n key to override the default label for the Next button
     */
    nextLabel?: string;
    /**
     * An optional i18n key to override the default label for the Back button
     */
    backLabel?: string;
    /**
     * An optional i18n key to override the default label for the Cancel button
     */
    cancelLabel?: string;
    templateOverride?: string;
    cancelAction?: WizardAction;
}
