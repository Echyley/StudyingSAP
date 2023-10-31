/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { ICatalogService, ModalWizard, ICMSPage } from 'smarteditcommons';
import { PageFacade } from '../../../facades';
import { ClonePageWizardComponent } from './ClonePageWizardComponent';

@Injectable()
export class ClonePageWizardService {
    constructor(
        private modalWizard: ModalWizard,
        private catalogService: ICatalogService,
        private pageFacade: PageFacade
    ) {}

    /**
     * When called, this method opens a modal window containing a wizard to clone an existing page.
     *
     * @param pageData An object containing the pageData when the clone page wizard is opened from the page list.
     * @returns A promise that will resolve when the modal wizard is closed or reject if it's canceled.
     *
     */

    public async openClonePageWizard(pageData?: ICMSPage): Promise<any> {
        const uriContext = pageData
            ? await this.catalogService.retrieveUriContext()
            : await this.pageFacade.retrievePageUriContext();

        return this.modalWizard.open({
            component: ClonePageWizardComponent,
            properties: {
                uriContext,
                basePageUUID: pageData ? pageData.uuid : undefined
            }
        });
    }
}
