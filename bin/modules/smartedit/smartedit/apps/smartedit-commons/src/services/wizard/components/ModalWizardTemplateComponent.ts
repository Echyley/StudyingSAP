/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* forbiddenNameSpaces useValue:false */

import {
    Component,
    ComponentFactory,
    ComponentFactoryResolver,
    Injector,
    Type
} from '@angular/core';
import * as lo from 'lodash';
import { Observable, of } from 'rxjs';

import { stringUtils } from '../../../utils/StringUtils';
import {
    ModalManagerService,
    ModalButtonAction,
    ModalButtonStyle,
    ModalButtonOptions
} from '../../modal';
import { WizardAction, WizardConfig, WizardStep } from '../services';
import { DefaultWizardActionStrategy } from '../services/DefaultWizardActionStrategy';
import { WizardActions } from '../services/WizardActions';
import { WizardService, WIZARD_API, WIZARD_MANAGER } from '../services/WizardService';

@Component({
    selector: 'se-modal-wizard-template',
    template: `
        <div id="yModalWizard">
            <div class="se-modal-wizard-template">
                <se-modal-wizard-nav-bar
                    [navActions]="_wizardContext?.navActions"
                    (executeAction)="executeAction($event)"></se-modal-wizard-nav-bar>
                <se-modal-wizard-step-outlet
                    [steps]="_wizardContext?._steps"
                    [wizardService]="wizardService"
                    [wizardApiInjector]="wizardInjector"></se-modal-wizard-step-outlet>
            </div>
        </div>
    `
})
export class ModalWizardTemplateComponent {
    public executeAction: (action: WizardAction) => void;
    public _wizardContext: {
        _steps: WizardStep[];
        templateUrl?: string;
        component?: Type<any>;
        navActions?: WizardAction[];
        templateOverride?: string;
    };
    public wizardService: WizardService;
    public wizardInjector: Injector;

    private readonly getWizardConfig: () => WizardConfig;

    constructor(
        private readonly modalManager: ModalManagerService,
        private readonly wizardActions: WizardActions,
        private readonly defaultWizardActionStrategy: DefaultWizardActionStrategy,
        private readonly componentFactoryResolver: ComponentFactoryResolver,
        private readonly injector: Injector
    ) {}

    ngOnInit(): void {
        this.modalManager.getModalData().subscribe((config: WizardAction) => {
            this.wizardService = new WizardService(this.defaultWizardActionStrategy, stringUtils);
            this.wizardService.properties = config.properties;

            if (config.component) {
                this.createWizardApiInjector(config);
            }

            if (typeof this.getWizardConfig !== 'function') {
                throw new Error(
                    'The provided controller must provide a getWizardConfig() function.'
                );
            }

            const modalConfig = this.getWizardConfig();

            this._wizardContext = {
                _steps: modalConfig.steps
            };

            this.executeAction = (action: WizardAction): void => {
                this.wizardService.executeAction(action);
            };

            this.wizardService.onLoadStep = (stepIndex: number, step: WizardStep): void => {
                this.modalManager.setTitle(step.title);

                if (step.component) {
                    this._wizardContext.component = step.component;
                }

                this.modalManager.removeAllButtons();
                const buttonsConfig = (step.actions || []).map((action) =>
                    this.convertActionToButtonConf(action)
                );
                this.modalManager.addButtons(buttonsConfig);
            };
            this.wizardService.onClose = (result: unknown): void => {
                this.modalManager.close(result);
            };

            this.wizardService.onCancel = (): void => {
                this.modalManager.close();
            };

            this.wizardService.onStepsUpdated = (steps: WizardStep[]): void => {
                this.setupNavBar(steps);
                this._wizardContext._steps = steps;
            };

            this.wizardService.initialize(modalConfig);
            this.setupModal(modalConfig);
        });
    }

    private setupNavBar(steps: WizardStep[]): void {
        this._wizardContext.navActions = steps.map((step, index: number) => {
            const action = this.wizardActions.navBarAction({
                id: 'NAV-' + step.id,
                stepIndex: index,
                wizardService: this.wizardService,
                destinationIndex: index,
                i18n: step.name,
                isCurrentStep: () => action.stepIndex === this.wizardService.getCurrentStepIndex()
            });
            return action;
        });
    }

    private setupModal(setupConfig: WizardConfig): void {
        this._wizardContext.templateOverride = setupConfig.templateOverride;

        if (setupConfig.cancelAction) {
            this.modalManager.setDismissCallback(() =>
                this.wizardService.executeAction(setupConfig.cancelAction)
            );
        }

        this.setupNavBar(setupConfig.steps);
    }

    private convertActionToButtonConf(action: WizardAction): ModalButtonOptions {
        return {
            id: action.id,
            style: action.isMainAction ? ModalButtonStyle.Primary : ModalButtonStyle.Default,
            label: action.i18n,
            action: ModalButtonAction.None,
            disabledFn: (): boolean => !action.enableIfCondition(),
            callback: (): Observable<any> => {
                this.wizardService.executeAction(action);

                return of(null);
            }
        };
    }

    private createWizardApiInjector(config: WizardAction): void {
        const injector: Injector = Injector.create({
            providers: [{ provide: WIZARD_MANAGER, useValue: this.wizardService }],
            parent: this.injector
        });
        const factory: ComponentFactory<any> =
            this.componentFactoryResolver.resolveComponentFactory(config.component);
        const createdComponent = factory.create(injector).instance;

        lo.assign(this, createdComponent);

        this.wizardInjector = Injector.create({
            providers: [{ provide: WIZARD_API, useValue: createdComponent }],
            parent: this.injector
        });
    }
}
