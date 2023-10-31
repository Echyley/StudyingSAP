/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { IModalService } from '@smart/utils';
import { ModalWizardTemplateComponent } from '../components/ModalWizardTemplateComponent';

import '../components/modalWizardNavBar.scss';
import { WizardAction } from './types';

/**
 * Used to create wizards that are embedded into the {@link ModalService}.
 */
@Injectable()
export class ModalWizard {
    constructor(private readonly modalService: IModalService) {}

    /**
     * Open provides a simple way to create modal wizards, with much of the boilerplate taken care of for you
     * such as look, feel and wizard navigation.
     *
     * @returns Promise that will either be resolved (wizard finished) or
     * rejected (wizard cancelled).
     */
    open(config: WizardAction): Promise<any> {
        this.validateConfig(config);

        return new Promise((resolve, reject) => {
            const ref = this.modalService.open<WizardAction>({
                component: ModalWizardTemplateComponent,
                templateConfig: { isDismissButtonVisible: true },
                data: config,
                config: {
                    focusTrapped: false,
                    backdropClickCloseable: false,
                    dialogPanelClass: 'se-wizard-dialog'
                }
            });

            ref.afterClosed.subscribe(resolve, reject);
        });
    }

    private validateConfig(config: WizardAction): void {
        if (!config.controller && !config.component) {
            throw new Error(
                'WizardService - initialization exception. No controller nor component provided'
            );
        }

        if (config.controller && config.component) {
            throw new Error(
                'WizardService - initialization exception. Provide either controller or component'
            );
        }
    }
}
