/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { IModalService } from 'smarteditcommons';
import { ManageCustomizationViewComponent } from './ManageCustomizationViewComponent';
import { ManageCustomizationViewModalData } from './types';

@Injectable()
export class ManageCustomizationViewManager {
    private readonly className = 'sliderPanelParentModal';
    private readonly modalTitle = 'personalization.modal.customizationvariationmanagement.title';

    constructor(private modalService: IModalService) {}

    public openCreateCustomizationModal(): void {
        this.modalService.open({
            component: ManageCustomizationViewComponent,
            config: {
                dialogPanelClass: `lg ${this.className}`,
                width: '650px',
                backdropClickCloseable: false,
                focusTrapped: false // If set true, it'll have conflicts with fd-popover's focus.
            },
            templateConfig: {
                title: this.modalTitle,
                isDismissButtonVisible: true
            }
        });
    }

    public openEditCustomizationModal(
        customizationCode: ManageCustomizationViewModalData['customizationCode'],
        variationCode: ManageCustomizationViewModalData['variationCode']
    ): void {
        this.modalService.open({
            component: ManageCustomizationViewComponent,
            data: {
                customizationCode,
                variationCode
            },
            config: {
                dialogPanelClass: `lg ${this.className}`,
                width: '650px',
                backdropClickCloseable: false,
                focusTrapped: false // If set true, it'll have conflicts with fd-popover's focus.
            },
            templateConfig: {
                title: this.modalTitle,
                isDismissButtonVisible: true
            }
        });
    }
}
