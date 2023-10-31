/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ItemSelectorPanelComponent } from 'cmssmarteditcontainer/components/genericEditor/catalog';
import { ProductCatalog } from 'cmssmarteditcontainer/components/genericEditor/catalog/services';
import { PopulatorItem } from 'cmssmarteditcontainer/components/genericEditor/dropdownPopulators/types';

describe('ItemSelectorPanelComponent', () => {
    const apparelProductCatalog = {
        id: 'apparelProductCatalog',
        name: {
            en: 'Apparel Product Catalog'
        },
        versions: [
            {
                id: 'Online',
                label: 'Online'
            },
            {
                id: 'Staged',
                label: 'Staged'
            }
        ]
    };
    const electronicsProductCatalog = {
        id: 'electronicsProductCatalog',
        name: {
            en: 'Electronics Product Catalog'
        },
        versions: [
            {
                id: 'Online',
                label: 'Online'
            },
            {
                id: 'Staged',
                label: 'Staged'
            }
        ]
    };

    let stubGetCatalogs: (values: ProductCatalog[]) => jasmine.Spy;

    let component: ItemSelectorPanelComponent;
    beforeEach(() => {
        component = new ItemSelectorPanelComponent();

        stubGetCatalogs = (values: ProductCatalog[]) => {
            component.getCatalogs = () => Promise.resolve(values);
            return spyOn(component, 'getCatalogs').and.callThrough();
        };
    });

    describe('initialize', () => {
        it('sets catalogs properly', async () => {
            stubGetCatalogs([apparelProductCatalog, electronicsProductCatalog]);

            await component.ngOnInit();

            expect(component.catalogs.length).toBe(2);
        });

        it('GIVEN there is one catalog THEN that catalog id is assigned to catalogInfo', async () => {
            stubGetCatalogs([apparelProductCatalog]);

            await component.ngOnInit();

            expect(component.catalogs.length).toBe(1);
            expect(component.catalogInfo.catalogId).toBe(apparelProductCatalog.id);
        });
    });

    it('GIVEN catalogId and catalogVersion are set THEN it returns true', () => {
        component.catalogInfo = {
            catalogId: 'apparel-ukContentCatalog',
            catalogVersion: 'apparel-ukContentCatalog/Staged'
        };
        expect(component.isItemSelectorEnabled()).toBe(true);
    });

    it('catalogSelectorFetchStrategy fetchAll returns catalogs properly', async () => {
        stubGetCatalogs([apparelProductCatalog]);
        await component.ngOnInit();

        const catalogs = await component.catalogSelectorFetchStrategy.fetchAll();
        expect(catalogs).toEqual([apparelProductCatalog]);
    });

    describe('catalogVersionSelectorFetchStrategy', () => {
        it('GIVEN catalogInfo has no catalogId THEN it returns empty array', async () => {
            stubGetCatalogs([apparelProductCatalog, electronicsProductCatalog]);

            await component.ngOnInit();

            const selectItems = await component.catalogVersionSelectorFetchStrategy.fetchAll();
            expect(selectItems).toEqual([]);
        });

        it('GIVEN catalogInfo has catalogId THEN it returns array of versions for that catalogId', async () => {
            stubGetCatalogs([apparelProductCatalog]);

            await component.ngOnInit();

            const selectItems = await component.catalogVersionSelectorFetchStrategy.fetchAll();
            expect(selectItems).not.toBe(apparelProductCatalog.versions);
            expect(selectItems).toEqual(apparelProductCatalog.versions);
        });
    });

    describe('itemsSelectorFetchStrategy', () => {
        beforeEach(() => {
            component.itemsFetchStrategy = {} as any;
        });

        it('calls fetchEntity properly', async () => {
            stubGetCatalogs([apparelProductCatalog]);
            const uid = 'uid';
            component.itemsFetchStrategy.fetchEntity = () => Promise.resolve(null);
            const fetchEntitySpy = spyOn(component.itemsFetchStrategy, 'fetchEntity');

            await component.ngOnInit();

            await component.itemsSelectorFetchStrategy.fetchEntity(uid);

            expect(fetchEntitySpy).toHaveBeenCalledWith(uid);
        });

        it('GIVEN selected items THEN fetchPage returns only non selected items', async () => {
            component.internalItemsSelected = ['uid3'];

            const itemsToReturn = [
                {
                    uid: 'uid1'
                },
                {
                    uid: 'uid2'
                }
            ] as PopulatorItem[];
            component.itemsFetchStrategy.fetchPage = () =>
                Promise.resolve({
                    results: [
                        ...itemsToReturn,
                        {
                            uid: 'uid3'
                        }
                    ] as PopulatorItem[],
                    pagination: {
                        count: 3,
                        page: null,
                        totalCount: null,
                        totalPages: null
                    }
                });
            stubGetCatalogs([apparelProductCatalog]);

            await component.ngOnInit();

            const page = await component.itemsSelectorFetchStrategy.fetchPage();
            expect(page.results).toEqual(itemsToReturn);
        });
    });
});
