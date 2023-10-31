/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { ICatalogService, ICMSPage } from 'smarteditcommons';
import { ActionableAlertService } from './ActionableAlertService';
import { ClonePageAlertComponent } from './ClonePageAlertComponent';

@Injectable()
export class ClonePageAlertService {
    constructor(
        private actionableAlertService: ActionableAlertService,
        private catalogService: ICatalogService
    ) {}

    /**
     * Displays an alert containing an hyperlink allowing for the user
     * to navigate to the newly cloned page.
     */
    public async displayClonePageAlert(clonedPageInfo: ICMSPage): Promise<void> {
        const catalogVersion = await this.catalogService.getCatalogVersionByUuid(
            clonedPageInfo.catalogVersion
        );
        return this.actionableAlertService.displayActionableAlert({
            component: ClonePageAlertComponent,
            mousePersist: true,
            data: {
                catalogVersion,
                clonedPageInfo
            },
            minWidth: '',
            duration: 1000,
            dismissible: true,
            width: '300px'
        });
    }
}
