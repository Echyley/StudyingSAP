/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { of } from 'rxjs';
import { ModalManagerService, IBaseCatalog, SystemEventService } from 'smarteditcommons';

import { MULTI_PRODUCT_CATALOGS_UPDATED } from 'smarteditcontainer/components/productCatalogVersion';
import { MultiProductCatalogVersionConfigurationComponent } from 'smarteditcontainer/components/productCatalogVersion/multiProductCatalogVersionConfiguration';

describe('MultiProductCatalogVersionConfigurationComponent', () => {
    let component: MultiProductCatalogVersionConfigurationComponent;
    let modalManager: jasmine.SpyObj<ModalManagerService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;

    const VERSION1 = 'Online';
    const VERSION2 = 'Staged';
    const CATALOG_VERSION_UUID1 = 'catalog1Version/Online';
    const CATALOG_VERSION_UUID2 = 'catalog2Version/Staged';

    const PRODUCT_CATALOGS: IBaseCatalog[] = [
        {
            catalogId: 'catalog1',
            name: {
                en: 'catalog1_en'
            },
            versions: [
                {
                    version: VERSION1,
                    active: true,
                    uuid: CATALOG_VERSION_UUID1,
                    pageDisplayConditions: null
                },
                {
                    version: VERSION2,
                    active: false,
                    uuid: CATALOG_VERSION_UUID2,
                    pageDisplayConditions: null
                }
            ]
        },
        {
            catalogId: 'catalog2',
            name: {
                en: 'catalog2_en'
            },
            versions: [
                {
                    version: VERSION1,
                    active: true,
                    uuid: CATALOG_VERSION_UUID1,
                    pageDisplayConditions: null
                },
                {
                    version: VERSION2,
                    active: false,
                    uuid: CATALOG_VERSION_UUID2,
                    pageDisplayConditions: null
                }
            ]
        }
    ];

    const SELECTED_CATALOG_VERSIONS = [CATALOG_VERSION_UUID1, CATALOG_VERSION_UUID1];
    const UPDATED_CATALOG_VERSIONS = [CATALOG_VERSION_UUID2, CATALOG_VERSION_UUID2];

    beforeEach(() => {
        modalManager = jasmine.createSpyObj<ModalManagerService>('modalManager', [
            'getModalData',
            'setDismissCallback',
            'addButtons',
            'enableButton',
            'disableButton',
            'close'
        ]);
        modalManager.getModalData.and.returnValue(
            of({
                productCatalogs: PRODUCT_CATALOGS,
                selectedCatalogVersions: SELECTED_CATALOG_VERSIONS
            })
        );

        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'publishAsync'
        ]);

        component = new MultiProductCatalogVersionConfigurationComponent(
            modalManager,
            systemEventService
        );
        component.ngOnInit();
    });

    describe('WHEN initialized THEN', () => {
        it('it will set selectedCatalogVersions', () => {
            expect(component.selectedCatalogVersions).toBe(SELECTED_CATALOG_VERSIONS);
        });

        it('it will set productCatalogs', () => {
            expect(component.productCatalogs.length).toBe(2);
        });

        it('it will set dismissCallback', () => {
            expect(modalManager.setDismissCallback).toHaveBeenCalled();
        });
    });

    it('WHEN option is selected THEN it will enable save button', () => {
        const updateModelCallback = component.updateModel();
        component.productCatalogs.forEach((productCatalog, i) => {
            productCatalog.selectedItem = UPDATED_CATALOG_VERSIONS[i];
        });

        updateModelCallback();
        expect(modalManager.enableButton).toHaveBeenCalled();
    });

    it('WHEN options is selected AND pristine options have been selected THEN it disable save button', () => {
        const updateModelCallback = component.updateModel();

        updateModelCallback();
        expect(modalManager.disableButton).toHaveBeenCalled();
    });

    it('WHEN the modal is dismissed THEN it will be closed', async () => {
        const dismissCallback = modalManager.setDismissCallback.calls.argsFor(0)[0];
        await dismissCallback();

        expect(modalManager.close).toHaveBeenCalled();
    });

    it('WHEN save button is pressed THEN it will close the modal', () => {
        const cancelButtonCallback = modalManager.addButtons.calls.argsFor(0)[0][0].callback;
        cancelButtonCallback().subscribe(() => {
            expect(modalManager.close).toHaveBeenCalled();
        });
    });

    it('WHEN cancel button is pressed THEN it will publish an event and close the modal', () => {
        const mockUpdatedCatalogVersions = ['catalog1Version/Online', 'catalog2Version/Staged'];
        component.updatedCatalogVersions = mockUpdatedCatalogVersions;
        const saveButtonCallback = modalManager.addButtons.calls.argsFor(0)[0][0].callback;

        saveButtonCallback().subscribe(() => {
            expect(systemEventService.publishAsync).toHaveBeenCalledWith(
                MULTI_PRODUCT_CATALOGS_UPDATED,
                mockUpdatedCatalogVersions
            );
            expect(modalManager.close).toHaveBeenCalled();
        });
    });
});
