/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { AddPageWizardService } from 'cmssmarteditcontainer/services/pages/AddPageWizardService';
import { ICatalogService, ModalWizard } from 'smarteditcommons';

describe('AddPageWizardService', () => {
    let service: AddPageWizardService;
    let modalWizard: jasmine.SpyObj<ModalWizard>;
    let catalogService: jasmine.SpyObj<ICatalogService>;

    beforeEach(() => {
        modalWizard = jasmine.createSpyObj<ModalWizard>('modalWizard', ['open']);
        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'retrieveUriContext'
        ]);

        catalogService.retrieveUriContext.and.returnValue(
            Promise.resolve({ context: 'uriContext' })
        );

        service = new AddPageWizardService(modalWizard, catalogService);
    });

    it('should retrieve uri context and open modal', async () => {
        await service.openAddPageWizard();

        expect(catalogService.retrieveUriContext).toHaveBeenCalled();
        expect(modalWizard.open).toHaveBeenCalledWith({
            component: jasmine.any(Function) as any,
            properties: {
                uriContext: { context: 'uriContext' }
            }
        });
    });
});
