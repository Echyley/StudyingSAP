import { IRestrictionsStepHandler } from 'cmssmarteditcontainer/interfaces';
import { WizardService, WizardStep } from 'smarteditcommons';
import { RestrictionsEditorFunctionBindings } from './types';
export declare class RestrictionsStepHandler implements IRestrictionsStepHandler {
    private wizardManager;
    private restrictionsEditorFunctionBindings;
    private stepDetails;
    constructor(wizardManager: WizardService, restrictionsEditorFunctionBindings: RestrictionsEditorFunctionBindings, stepProperties: WizardStep);
    hideStep(): void;
    showStep(): void;
    isStepValid(): boolean;
    getStepId(): string;
    goToStep(): void;
    private isStepOnWizard;
}
export declare class RestrictionsStepHandlerFactory {
    createRestrictionsStepHandler(wizardManager: WizardService, restrictionsEditorFunctionBindings: RestrictionsEditorFunctionBindings, stepProperties: WizardStep): RestrictionsStepHandler;
}
