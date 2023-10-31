/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { ISyncStatusItem } from 'cmscommons';
import { PageSynchronizationPanelModalComponent } from 'cmssmarteditcontainer/components/synchronize';
import { PageSynchronizationPanelModalData as ModalData } from 'cmssmarteditcontainer/components/synchronize/types';
import { of } from 'rxjs';
import { ModalManagerService } from 'smarteditcommons';

describe('PageSynchronizationPanelModalComponent', () => {
    const mockSyncBtnId = 'sync';
    const mockModalData = {
        cmsPage: { displayStatus: 'DRAFT' },
        uriContext: {}
    } as ModalData;
    let modalManager: jasmine.SpyObj<ModalManagerService<ModalData>>;

    let component: PageSynchronizationPanelModalComponent;
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);
    beforeEach(() => {
        modalManager = jasmine.createSpyObj<ModalManagerService<ModalData>>(
            'pageSynchronizationPanelModalComponent',
            ['addButtons', 'getModalData', 'disableButton', 'enableButton']
        );

        component = new PageSynchronizationPanelModalComponent(modalManager, cdr);
    });

    describe('initialize', () => {
        beforeEach(() => {
            modalManager.getModalData.and.returnValue(of(mockModalData));
        });

        it('should add buttons', () => {
            component.ngOnInit();

            expect(modalManager.addButtons).toHaveBeenCalledWith([
                jasmine.objectContaining({ id: mockSyncBtnId }),
                jasmine.objectContaining({ id: 'cancel' })
            ] as any);
        });

        it('should get the modal data', () => {
            component.ngOnInit();

            expect(component.data).toBe(mockModalData);
        });
    });

    describe('onSelectedItemsUpdate', () => {
        beforeEach(() => {
            modalManager.getModalData.and.returnValue(of(mockModalData));
            component.ngOnInit();
        });
        it('GIVEN no selected items THEN it should disable Sync Button', () => {
            component.onSelectedItemsUpdate([]);

            expect(modalManager.disableButton).toHaveBeenCalledWith(mockSyncBtnId);
        });

        it('GIVEN page is DRAFT status THEN it should disable Sync Button', () => {
            component.onSelectedItemsUpdate([]);

            expect(modalManager.disableButton).toHaveBeenCalledWith(mockSyncBtnId);
        });

        it('GIVEN selected items and page is in progress status THEN it should disable Sync Button', () => {
            component.data.cmsPage.displayStatus = 'IN_PROGRESS';
            component.onSelectedItemsUpdate([{} as ISyncStatusItem]);

            expect(modalManager.disableButton).toHaveBeenCalledWith(mockSyncBtnId);
        });

        it('GIVEN selected items and page is ready to sync status THEN it should enable Sync Button', () => {
            component.data.cmsPage.displayStatus = 'READY_TO_SYNC';
            component.onSelectedItemsUpdate([{} as ISyncStatusItem]);

            expect(modalManager.enableButton).toHaveBeenCalledWith(mockSyncBtnId);
        });
    });
});
