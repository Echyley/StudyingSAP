/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ClonePageWizardComponent,
    ClonePageWizardService
} from 'cmssmarteditcontainer/components/pages/clonePageWizard';
import { PageFacade } from 'cmssmarteditcontainer/facades';
import { ICatalogService, ModalWizard, ICMSPage } from 'smarteditcommons';

describe('ClonePageWizardService', () => {
    let clonePageWizardService: ClonePageWizardService;
    let modalWizard: jasmine.SpyObj<ModalWizard>;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let pageFacade: jasmine.SpyObj<PageFacade>;

    const uriContext = {
        a: 'b'
    };

    beforeEach(() => {
        modalWizard = jasmine.createSpyObj('modalWizard', ['open']);
        catalogService = jasmine.createSpyObj('catalogService', ['retrieveUriContext']);
        pageFacade = jasmine.createSpyObj('pageFacade', ['retrievePageUriContext']);

        catalogService.retrieveUriContext.and.returnValue(Promise.resolve(uriContext));
        pageFacade.retrievePageUriContext.and.returnValue(Promise.resolve(uriContext));

        clonePageWizardService = new ClonePageWizardService(
            modalWizard,
            catalogService,
            pageFacade
        );
    });

    describe('clonePageWizardService', () => {
        it('GIVEN pageData is sent to the method THEN will delegate to the modal wizard a valid basePageUid', async () => {
            await clonePageWizardService.openClonePageWizard({
                uuid: 'some uuid'
            } as ICMSPage);

            expect(modalWizard.open).toHaveBeenCalledWith({
                component: ClonePageWizardComponent,
                properties: {
                    uriContext,
                    basePageUUID: 'some uuid'
                }
            });
        });

        it('GIVEN no pageData sent to the method THEN will delegate to the modal wizard with a undefined basePageUid', async () => {
            await clonePageWizardService.openClonePageWizard();

            expect(modalWizard.open).toHaveBeenCalledWith({
                component: ClonePageWizardComponent,
                properties: {
                    uriContext,
                    basePageUUID: undefined
                }
            });
        });
    });
});
