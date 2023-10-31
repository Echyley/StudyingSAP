import { WizardAction } from './types';
export declare class WizardActions {
    customAction(configuration: WizardAction): WizardAction;
    done(configuration?: WizardAction): WizardAction;
    next(configuration?: WizardAction): WizardAction;
    navBarAction(configuration: WizardAction): WizardAction;
    back(configuration: WizardAction): WizardAction;
    cancel(): WizardAction;
    private createNewAction;
}
