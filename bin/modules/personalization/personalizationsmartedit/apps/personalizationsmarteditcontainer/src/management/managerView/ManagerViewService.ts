/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { ManagerViewComponent } from 'personalizationsmarteditcontainer/management/managerView';
import { IModalService } from 'smarteditcommons';

@Injectable()
export class ManagerViewService {
    constructor(private modalService: IModalService) {}

    public openManagerAction(): void {
        this.modalService.open({
            templateConfig: {
                title: 'personalization.modal.manager.title',
                isDismissButtonVisible: true
            },
            config: {
                height: '100%',
                width: '100%',
                dialogPanelClass: 'perso-library',
                focusTrapped: false // If set true, it'll have conflicts with fd-popover's focus.
            },
            component: ManagerViewComponent
        });
    }
}
