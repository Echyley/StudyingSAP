import { ChangeDetectorRef, OnInit } from '@angular/core';
import { ISyncStatusItem } from 'cmscommons';
import { ModalManagerService } from 'smarteditcommons';
import { PageSynchronizationPanelModalData } from '../../types';
import { PageSynchronizationPanelComponent } from '../pageSynchronizationPanel/PageSynchronizationPanelComponent';
export declare class PageSynchronizationPanelModalComponent implements OnInit {
    private readonly modalManager;
    private readonly cdr;
    pageSynchronizationPanelComponent: PageSynchronizationPanelComponent;
    data: PageSynchronizationPanelModalData;
    isReady: boolean;
    private readonly syncBtnId;
    constructor(modalManager: ModalManagerService<PageSynchronizationPanelModalData>, cdr: ChangeDetectorRef);
    ngOnInit(): void;
    onSelectedItemsUpdate(selectedItems: ISyncStatusItem[]): void;
}
