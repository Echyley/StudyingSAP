/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Input,
    OnChanges,
    SimpleChanges,
    ViewRef
} from '@angular/core';
import { Nullable, WorkflowService } from 'smarteditcommons';

@Component({
    selector: 'se-page-status',
    templateUrl: './PageStatusComponent.html',
    styleUrls: ['./PageStatusComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PageStatusComponent implements OnChanges {
    @Input() uuid: Nullable<string>;
    @Input() displayStatus: Nullable<string>;

    /**
     * I18n label that translates to Draft, In Progress, Ready To Sync etc.
     */
    public statusLabel: string;
    /**
     * Represents the status, e.g. In Progress status circle, padlock
     */
    public statusIconCssClass: string;

    private LOCALIZATION_PREFIX = 'se.cms.page.displaystatus.';
    private isPageLocked: boolean;

    constructor(private workflowService: WorkflowService, private cdr: ChangeDetectorRef) {}

    async ngOnChanges(changes: SimpleChanges): Promise<void> {
        const uuidChange = changes.uuid;
        const displayStatusChange = changes.displayStatus;

        let detectChanges = false;
        if (uuidChange && uuidChange.currentValue) {
            this.isPageLocked = await this.isPageLockedToCurrentUser(this.uuid);
            detectChanges = true;
        }

        if (displayStatusChange && displayStatusChange.currentValue) {
            const displayStatusLC = this.displayStatus.toLowerCase();

            this.statusLabel = `${this.LOCALIZATION_PREFIX}${displayStatusLC}`;

            this.statusIconCssClass = this.getStatusIconCssClass(
                this.isPageLocked,
                displayStatusLC
            );
        }

        if (detectChanges) {
            if (!(this.cdr as ViewRef).destroyed) {
                this.cdr.detectChanges();
            }
        }
    }

    private async isPageLockedToCurrentUser(uuid: string): Promise<boolean> {
        const workflow = await this.workflowService.getActiveWorkflowForPageUuid(uuid);
        return workflow && !workflow.isAvailableForCurrentPrincipal;
    }

    private getStatusIconCssClass(isPageLocked: boolean, displayStatus: string): string {
        return isPageLocked
            ? 'sap-icon--locked se-page-status__icon--locked'
            : `se-page-status__icon--${displayStatus}`;
    }
}
