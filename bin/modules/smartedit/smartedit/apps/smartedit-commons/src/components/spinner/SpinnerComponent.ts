/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
    selector: 'se-spinner',
    changeDetection: ChangeDetectionStrategy.OnPush,
    template: `
        <div
            *ngIf="isSpinning"
            class="se-spinner fd-busy-indicator-extended panel-body"
            [ngClass]="{ 'se-spinner--fluid': isFluid }">
            <div class="spinner">
                <fd-busy-indicator [loading]="true" size="m"></fd-busy-indicator>
            </div>
        </div>
    `
})
export class SpinnerComponent {
    @Input() isSpinning: boolean;
    @Input() isFluid = true;
}
