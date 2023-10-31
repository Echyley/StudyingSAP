/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* eslint-disable max-classes-per-file */
import { CATALOG_DETAILS_COLUMNS } from 'smarteditcommons';
import { HomePageLinkComponent } from 'smarteditcontainer/services/widgets/catalogDetails/components/HomePageLinkComponent';
import { CatalogDetailsService } from 'smarteditcontainer/services/widgets/catalogDetails/services/CatalogDetailsService';

class ComponentA {}
class ComponentB {}
class ComponentC {}
class ComponentD {}
class ComponentE {}
class ComponentF {}

describe('CatalogDetailsService', () => {
    // Service Under Test
    let catalogDetailsService: CatalogDetailsService;
    const defaultItem = {
        component: HomePageLinkComponent
    };

    // Set-up Service Under Test
    beforeEach(() => {
        catalogDetailsService = new CatalogDetailsService();
    });

    it('Should have an empty lists in the begining(left and right sides) ', () => {
        expect(catalogDetailsService.getItems().left).toEqual([defaultItem]);
        expect(catalogDetailsService.getItems().right).toEqual([]);
    });

    it('Should add items to the list at the left side', () => {
        const theItems = [
            { component: ComponentA },
            { component: ComponentB },
            { component: ComponentC }
        ];
        catalogDetailsService.addItems(theItems, CATALOG_DETAILS_COLUMNS.LEFT);
        expect(catalogDetailsService.getItems().left).toEqual([...theItems, defaultItem]);
    });

    it('Should add items to the list at the right side', () => {
        const theItems = [
            { component: ComponentA },
            { component: ComponentB },
            { component: ComponentC }
        ];
        catalogDetailsService.addItems(theItems, CATALOG_DETAILS_COLUMNS.RIGHT);
        expect(catalogDetailsService.getItems().right).toEqual(theItems);
    });

    it('Should add items to the list by sequences at the left(default) side', () => {
        const items0 = [{ component: ComponentA }, { component: ComponentB }];
        const items1 = [{ component: ComponentC }, { component: ComponentD }];
        const items2 = [{ component: ComponentE }, { component: ComponentF }];

        catalogDetailsService.addItems(items0, CATALOG_DETAILS_COLUMNS.LEFT);
        catalogDetailsService.addItems(items1, CATALOG_DETAILS_COLUMNS.LEFT);
        catalogDetailsService.addItems(items2, CATALOG_DETAILS_COLUMNS.LEFT);

        expect(catalogDetailsService.getItems().left).toEqual([
            ...items0,
            ...items1,
            ...items2,
            defaultItem
        ]);
    });
});
