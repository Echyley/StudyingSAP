import { InjectionToken } from '@angular/core';
import { TypedMap } from '@smart/utils';
import { StringUtils } from '../../../utils';
import { WizardStep, IWizardActionStrategy, WizardConfig, WizardAction } from './types';
export declare const WIZARD_MANAGER: InjectionToken<WizardService>;
export declare const WIZARD_API: InjectionToken<unknown>;
/**
 * The Wizard Manager is a wizard management service that can be injected into your wizard controller.
 */
export declare class WizardService {
    private readonly defaultWizardActionStrategy;
    private readonly stringUtils;
    onLoadStep: (index: number, nextStep: WizardStep) => void;
    onClose: (result: any) => void;
    onCancel: () => void;
    onStepsUpdated: (steps: WizardStep[]) => void;
    properties: TypedMap<any>;
    private _actionStrategy;
    private _currentIndex;
    private _conf;
    private _steps;
    private _getResult;
    constructor(defaultWizardActionStrategy: IWizardActionStrategy, stringUtils: StringUtils);
    initialize(conf: WizardConfig): void;
    executeAction(action: WizardAction): Promise<void>;
    /**
     * Navigates the wizard to the given step.
     * @param index The 0-based index from the steps array returned by the wizard controllers getWizardConfig() function
     */
    goToStepWithIndex(index: number): void;
    /**
     * Navigates the wizard to the given step.
     * @param id The ID of a step returned by the wizard controllers getWizardConfig() function. Note that if
     * no id was provided for a given step, then one is automatically generated.
     */
    goToStepWithId(id: string): void;
    /**
     * Adds an additional step to the wizard at runtime
     * @param index (OPTIONAL) A 0-based index position in the steps array. Default is 0.
     */
    addStep(newStep: WizardStep, index: number): void;
    /**
     * Remove a step form the wizard at runtime. If you are removing the currently displayed step, the
     * wizard will return to the first step. Removing all the steps will result in an error.
     */
    removeStepById(id: string): void;
    /**
     * Remove a step form the wizard at runtime. If you are removing the currently displayed step, the
     * wizard will return to the first step. Removing all the steps will result in an error.
     * @param index The 0-based index of the step you wish to remove.
     */
    removeStepByIndex(index: number): void;
    /**
     * Close the wizard. This will return a resolved promise to the creator of the wizard, and if any
     * resultFn was provided in the {@link ModalWizardConfig} the returned
     * value of this function will be passed as the result.
     */
    close(): void;
    /**
     * Cancel the wizard. This will return a rejected promise to the creator of the wizard.
     */
    cancel(): void;
    getSteps(): WizardStep[];
    getStepIndexFromId(id: string): number;
    /**
     * @returns True if the ID exists in one of the steps
     */
    containsStep(stepId: string): boolean;
    getCurrentStepId(): string;
    getCurrentStepIndex(): number;
    getCurrentStep(): WizardStep;
    /**
     * @returns The number of steps in the wizard. This should always be equal to the size of the array.
     * returned by [getSteps]{@link WizardManager#getSteps}.
     */
    getStepsCount(): number;
    getStepWithId(id: string): WizardStep;
    getStepWithIndex(index: number): WizardStep;
    private validateConfig;
    private validateStepUids;
}
