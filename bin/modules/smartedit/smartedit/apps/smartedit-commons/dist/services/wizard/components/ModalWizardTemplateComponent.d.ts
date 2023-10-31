import { ComponentFactoryResolver, Injector, Type } from '@angular/core';
import { ModalManagerService } from '../../modal';
import { WizardAction, WizardStep } from '../services';
import { DefaultWizardActionStrategy } from '../services/DefaultWizardActionStrategy';
import { WizardActions } from '../services/WizardActions';
import { WizardService } from '../services/WizardService';
export declare class ModalWizardTemplateComponent {
    private readonly modalManager;
    private readonly wizardActions;
    private readonly defaultWizardActionStrategy;
    private readonly componentFactoryResolver;
    private readonly injector;
    executeAction: (action: WizardAction) => void;
    _wizardContext: {
        _steps: WizardStep[];
        templateUrl?: string;
        component?: Type<any>;
        navActions?: WizardAction[];
        templateOverride?: string;
    };
    wizardService: WizardService;
    wizardInjector: Injector;
    private readonly getWizardConfig;
    constructor(modalManager: ModalManagerService, wizardActions: WizardActions, defaultWizardActionStrategy: DefaultWizardActionStrategy, componentFactoryResolver: ComponentFactoryResolver, injector: Injector);
    ngOnInit(): void;
    private setupNavBar;
    private setupModal;
    private convertActionToButtonConf;
    private createWizardApiInjector;
}
