/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Injector, Input } from '@angular/core';
import { WizardStep } from '../services';
import { WizardService } from '../services/WizardService';

@Component({
    selector: 'se-modal-wizard-step-outlet',
    template: `
        <ng-container *ngIf="steps && steps.length > 0">
            <ng-container *ngFor="let step of steps">
                <div
                    [ngClass]="{
                        'se-modal-wizard__content--visible': isActive(step)
                    }"
                    class="se-modal-wizard__content">
                    <div *ngIf="step.component">
                        <ng-container
                            *ngComponentOutlet="
                                step.component;
                                injector: wizardApiInjector
                            "></ng-container>
                    </div>
                </div>
            </ng-container>
        </ng-container>
    `
})
export class ModalWizardStepOutletComponent {
    @Input() steps: WizardStep[];
    @Input() wizardService: WizardService;

    @Input() wizardApiInjector: Injector;

    public isActive(step: WizardStep): boolean {
        return this.wizardService.getCurrentStepId() === step.id;
    }
}
