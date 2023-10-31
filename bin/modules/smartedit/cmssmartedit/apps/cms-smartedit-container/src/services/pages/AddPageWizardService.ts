/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { ICatalogService, ModalWizard } from 'smarteditcommons';
import { AddPageWizardComponent } from '../../components/pages/addPageWizard/components/AddPageWizardComponent';

@Injectable()
export class AddPageWizardService {
    constructor(private modalWizard: ModalWizard, private catalogService: ICatalogService) {}

    public async openAddPageWizard(): Promise<any> {
        const uriContext = await this.catalogService.retrieveUriContext();

        return this.modalWizard.open({
            component: AddPageWizardComponent,
            properties: {
                uriContext
            }
        });
    }
}
