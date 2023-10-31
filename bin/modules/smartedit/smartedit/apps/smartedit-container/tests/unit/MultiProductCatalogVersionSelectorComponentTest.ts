/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { EventEmitter } from '@angular/core';
import { of } from 'rxjs';

import {
    IBaseCatalog,
    ModalOpenConfig,
    IModalService,
    L10nPipe,
    LocalizedMap,
    SystemEventService
} from 'smarteditcommons';

import { MultiProductCatalogVersionSelectorData } from 'smarteditcontainer/components/productCatalogVersion';
import { MultiProductCatalogVersionConfigurationComponent } from 'smarteditcontainer/components/productCatalogVersion/multiProductCatalogVersionConfiguration';
import { MultiProductCatalogVersionSelectorComponent } from 'smarteditcontainer/components/productCatalogVersion/multiProductCatalogVersionSelector';

describe('MultiProductCatalogVersionSelectorComponent', () => {
    let component: MultiProductCatalogVersionSelectorComponent;
    let modalService: jasmine.SpyObj<IModalService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let l10nPipe: jasmine.SpyObj<L10nPipe>;

    const VERSION1 = 'Online';
    const VERSION2 = 'Staged';
    const CATALOG_VERSION_UUID1 = 'catalog1Version/Online';
    const CATALOG_VERSION_UUID2 = 'catalog2Version/Staged';

    const DUMMY_MULTIPLE_CATALOGS_RESULT: IBaseCatalog[] = [
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

    beforeEach(() => {
        modalService = jasmine.createSpyObj<IModalService>('modalService', ['open']);

        systemEventService = jasmine.createSpyObj('systemEventService', ['subscribe']);
        systemEventService.subscribe.and.returnValue(jasmine.any(Function) as any);

        l10nPipe = jasmine.createSpyObj<L10nPipe>('l10n', ['transform']);
        l10nPipe.transform.and.callFake((catalogName: LocalizedMap) => of(catalogName.en));

        component = new MultiProductCatalogVersionSelectorComponent(
            l10nPipe,
            modalService,
            systemEventService
        );
    });

    it('WHEN component is destroyed THEN it will unsubscribe from system event', () => {
        component.ngOnInit();
        expect((component as any).$unRegEventForMultiProducts).toBeDefined();
        const unRegEventForMultiProducts = spyOn<any>(component, '$unRegEventForMultiProducts');

        component.ngOnDestroy();
        expect(unRegEventForMultiProducts).toHaveBeenCalledTimes(1);
    });

    it('WHEN modal has been closed THEN it will notify parent component with selected catalog versions', () => {
        const mockProductCatalogVersionsUpdated = [
            'updatedCatalog1Version/Online',
            'updatedCatalog2Version/Online'
        ];
        component.ngOnInit();

        const multiProductsCatalogUpdatedCallback = systemEventService.subscribe.calls.argsFor(
            0
        )[1];
        const selectedProductCatalogVersionsChangeSpy = spyOn<EventEmitter<string[]>>(
            component.selectedProductCatalogVersionsChange,
            'emit'
        );

        multiProductsCatalogUpdatedCallback('eventId', mockProductCatalogVersionsUpdated);
        expect(selectedProductCatalogVersionsChangeSpy).toHaveBeenCalledWith(
            mockProductCatalogVersionsUpdated
        );
    });

    it('WHEN any of @Inputs have changed THEN it will set selected options text', () => {
        component.productCatalogs = [...DUMMY_MULTIPLE_CATALOGS_RESULT];
        component.selectedProductCatalogVersions = [CATALOG_VERSION_UUID1, CATALOG_VERSION_UUID2];
        component.ngOnChanges();

        component.multiProductCatalogVersionsSelectedOptions$.subscribe((selectedOptions) => {
            expect(selectedOptions).toBe('catalog1_en (Online), catalog2_en (Online)');
        });
    });

    it('getCatalogNameCatalogVersionLabel GIVEN catalog id WHEN called THEN subscription will receive the translated label', () => {
        component.productCatalogs = [...DUMMY_MULTIPLE_CATALOGS_RESULT];
        component.selectedProductCatalogVersions = [CATALOG_VERSION_UUID1, CATALOG_VERSION_UUID2];
        component.ngOnChanges();

        const label$ = component.getCatalogNameCatalogVersionLabel('catalog1');

        label$.subscribe((v) => expect(v).toBe('catalog1_en (Online)'));
    });

    it('WHEN input is clicked THEN it will open a Multi Product Catalog modal', () => {
        component.onClickSelector();

        const confArgs: ModalOpenConfig<MultiProductCatalogVersionSelectorData> = modalService.open.calls.argsFor(
            0
        )[0] as any;
        expect(confArgs.component).toBe(MultiProductCatalogVersionConfigurationComponent);
    });
});
