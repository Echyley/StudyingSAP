/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    OnInit,
    ViewChild,
    ViewRef
} from '@angular/core';
import { ISyncStatusItem } from 'cmscommons';
import { from, Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { ModalButtonAction, ModalButtonStyle, ModalManagerService } from 'smarteditcommons';
import { PageSynchronizationPanelModalData } from '../../types';
import { PageSynchronizationPanelComponent } from '../pageSynchronizationPanel/PageSynchronizationPanelComponent';

@Component({
    selector: 'se-page-synchronization-panel-modal',
    template: `
        <se-page-synchronization-panel
            *ngIf="isReady"
            (selectedItemsUpdate)="onSelectedItemsUpdate($event)"
            [uriContext]="data.uriContext"
            [cmsPage]="data.cmsPage"
            [showFooter]="false"></se-page-synchronization-panel>
    `,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PageSynchronizationPanelModalComponent implements OnInit {
    @ViewChild(PageSynchronizationPanelComponent, { static: false })
    pageSynchronizationPanelComponent: PageSynchronizationPanelComponent;

    public data: PageSynchronizationPanelModalData;
    public isReady: boolean;

    private readonly syncBtnId = 'sync';

    constructor(
        private readonly modalManager: ModalManagerService<PageSynchronizationPanelModalData>,
        private readonly cdr: ChangeDetectorRef
    ) {}

    ngOnInit(): void {
        this.modalManager.addButtons([
            {
                id: this.syncBtnId,
                label: 'se.cms.actionitem.page.sync',
                style: ModalButtonStyle.Primary,
                action: ModalButtonAction.Close,
                disabled: true,
                callback: (): Observable<void> =>
                    from(this.pageSynchronizationPanelComponent.syncItems())
            },
            {
                id: 'cancel',
                label: 'se.cms.component.confirmation.modal.cancel',
                style: ModalButtonStyle.Default,
                action: ModalButtonAction.Dismiss
            }
        ]);

        this.modalManager
            .getModalData()
            .pipe(take(1))
            .subscribe((data) => {
                this.data = data;

                this.isReady = true;
                if (!(this.cdr as ViewRef).destroyed) {
                    this.cdr.detectChanges();
                }
            });
    }

    public onSelectedItemsUpdate(selectedItems: ISyncStatusItem[]): void {
        if (
            selectedItems.length === 0 ||
            this.data.cmsPage.displayStatus !== 'READY_TO_SYNC' // draft or in process page can't be synced from page list
        ) {
            this.modalManager.disableButton(this.syncBtnId);
            return;
        }
        this.modalManager.enableButton(this.syncBtnId);
    }
}
