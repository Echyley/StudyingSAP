/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { WizardAction } from '../services/types';

@Component({
    selector: 'se-modal-wizard-nav-bar',
    template: `
        <div class="se-modal-wizard__steps-container" *ngIf="navActions">
            <fd-wizard [appendToWizard]="false" [displaySummaryStep]="true">
                <fd-wizard-navigation>
                    <ul fd-wizard-progress-bar size="md">
                        <li
                            [attr.id]="action.id"
                            fd-wizard-step
                            *ngFor="let action of navActions; let i = index"
                            [status]="
                                action.isCurrentStep()
                                    ? 'current'
                                    : action.enableIfCondition()
                                    ? 'completed'
                                    : 'upcoming'
                            "
                            [ngClass]="{ 'se-none-cursor': !action.enableIfCondition() }"
                            [label]="action.i18n | translate"
                            (click)="onClickAction(action)">
                            <fd-wizard-step-indicator>{{ i + 1 }}</fd-wizard-step-indicator>
                            <fd-wizard-content size="md"> </fd-wizard-content>
                        </li>
                    </ul>
                </fd-wizard-navigation>
            </fd-wizard>
        </div>
    `
})
export class ModalWizardNavBarComponent {
    @Input() navActions: WizardAction[];
    @Output() executeAction: EventEmitter<WizardAction> = new EventEmitter();

    public onClickAction(action: WizardAction): void {
        this.executeAction.emit(action);
    }
}
