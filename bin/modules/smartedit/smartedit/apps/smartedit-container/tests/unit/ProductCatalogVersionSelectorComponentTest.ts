/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import {
    GenericEditorWidgetData,
    IBaseCatalog,
    ICatalogService,
    SelectItem,
    SystemEventService,
    TypedMap
} from 'smarteditcommons';
import { ProductCatalogVersionsSelectorComponent } from 'smarteditcontainer/components/productCatalogVersion/productCatalogVersionsSelector';

describe('ProductCatalogVersionSelectorComponent', () => {
    let component: ProductCatalogVersionsSelectorComponent;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let geData: GenericEditorWidgetData<TypedMap<any>>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;

    const VERSION1 = 'version1';
    const VERSION2 = 'version2';
    const CATALOG_VERSION_UUID1 = 'catalog1Version/Online';
    const CATALOG_VERSION_UUID2 = 'catalog2Version/Online';

    const DUMMY_CATALOG_SINGLE_RESULT: IBaseCatalog[] = [
        {
            catalogId: 'catalog1',
            name: {
                en: 'catalog1_s_en'
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

    const PARSED_VERSION_ID_LABEL: SelectItem[] = [
        {
            id: CATALOG_VERSION_UUID1,
            label: VERSION1
        },
        {
            id: CATALOG_VERSION_UUID2,
            label: VERSION2
        }
    ];

    beforeEach(() => {
        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'getProductCatalogsForSite',
            'returnActiveCatalogVersionUIDs'
        ]);

        systemEventService = jasmine.createSpyObj('systemEventService', ['subscribe']);
        systemEventService.subscribe.and.returnValue(jasmine.any(Function) as any);

        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);

        geData = {
            id: '',
            qualifier: 'productCatalogVersions',
            model: {
                catalogDescriptor: {
                    siteId: 'electronics'
                },
                previewCatalog: 'electronics_electyronics-catalog_online',
                productCatalogVersions: ['catalog1Version/Online']
            },
            field: null,
            isFieldDisabled: null
        };

        component = new ProductCatalogVersionsSelectorComponent(
            geData,
            catalogService,
            systemEventService,
            cdr
        );
    });

    it('GIVEN only one product catalog is available for site WHEN component is initialized THEN it will set Single Selector', async () => {
        catalogService.getProductCatalogsForSite.and.returnValue(
            Promise.resolve(DUMMY_CATALOG_SINGLE_RESULT)
        );

        await component.ngOnInit();

        expect(component.isSingleVersionSelector).toBe(true);
        expect(component.isMultiVersionSelector).toBe(false);
    });

    it('GIVEN Single Selector defaults WHEN component is initialized THEN it will set fetchAll strategy', async () => {
        catalogService.getProductCatalogsForSite.and.returnValue(
            Promise.resolve(DUMMY_CATALOG_SINGLE_RESULT)
        );

        await component.ngOnInit();

        const productCatalogVersionsFetchAll = component.fetchStrategy.fetchAll;

        expect(await productCatalogVersionsFetchAll()).toEqual(PARSED_VERSION_ID_LABEL);
    });

    it('GIVEN product catalogs WHEN fetch strategy method is called THEN it will resolve with mapped product catalogs versions to Select Items', async () => {
        catalogService.getProductCatalogsForSite.and.returnValue(
            Promise.resolve(DUMMY_CATALOG_SINGLE_RESULT)
        );

        await component.ngOnInit();

        const versionsActual = await component.fetchStrategy.fetchAll();
        expect(versionsActual).toEqual([
            {
                id: CATALOG_VERSION_UUID1,
                label: VERSION1
            },
            {
                id: CATALOG_VERSION_UUID2,
                label: VERSION2
            }
        ]);
    });

    it('GIVEN more than one product catalog is available for site WHEN component is initialized THEN it will set Multiple Selector', async () => {
        catalogService.getProductCatalogsForSite.and.returnValue(
            Promise.resolve(DUMMY_MULTIPLE_CATALOGS_RESULT)
        );

        await component.ngOnInit();

        expect(component.isMultiVersionSelector).toBe(true);
    });

    it('WHEN component is destroyed THEN it will unsubscribe from Site Change Event', async () => {
        catalogService.getProductCatalogsForSite.and.returnValue(
            Promise.resolve(DUMMY_CATALOG_SINGLE_RESULT)
        );
        await component.ngOnInit();

        const unRegSiteChangeEventSpy = spyOn<any>(component, '$unRegSiteChangeEvent');
        component.ngOnDestroy();
        expect(unRegSiteChangeEventSpy).toHaveBeenCalledTimes(1);
    });
});
