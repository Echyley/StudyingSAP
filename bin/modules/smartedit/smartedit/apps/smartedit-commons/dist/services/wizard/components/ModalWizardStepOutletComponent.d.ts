import { Injector } from '@angular/core';
import { WizardStep } from '../services';
import { WizardService } from '../services/WizardService';
export declare class ModalWizardStepOutletComponent {
    steps: WizardStep[];
    wizardService: WizardService;
    wizardApiInjector: Injector;
    isActive(step: WizardStep): boolean;
}
