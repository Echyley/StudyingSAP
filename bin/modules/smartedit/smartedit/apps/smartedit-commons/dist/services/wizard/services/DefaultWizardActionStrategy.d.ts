import { IWizardActionStrategy, WizardConfig } from './types';
import { WizardActions } from './WizardActions';
import { WizardService } from './WizardService';
export declare class DefaultWizardActionStrategy implements IWizardActionStrategy {
    private readonly wizardActions;
    constructor(wizardActions: WizardActions);
    applyStrategy(wizardService: WizardService, conf: WizardConfig): void;
    private applyOverrides;
}
