/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { CatalogAwareSelectorComponent } from 'cmssmarteditcontainer/components/genericEditor/catalog';
import { CatalogFetchStrategy } from 'cmssmarteditcontainer/components/genericEditor/catalog/services';
import { EditableListNodeItemDTO, LogService } from 'smarteditcommons';

describe('CatalogAwareSelectorComponent', () => {
    let logService: jasmine.SpyObj<LogService>;
    let fetchStrategy: jasmine.SpyObj<CatalogFetchStrategy<EditableListNodeItemDTO>>;
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);

    const listItem1 = { id: '1' } as EditableListNodeItemDTO;
    const listItem2 = { id: '2' } as EditableListNodeItemDTO;
    const listItem3 = { id: '3' } as EditableListNodeItemDTO;
    const itemList = new Map([
        ['1', listItem1],
        ['2', listItem2],
        ['3', listItem3]
    ]);

    let component: CatalogAwareSelectorComponent;
    beforeEach(() => {
        logService = jasmine.createSpyObj<LogService>('logService', ['warn']);

        fetchStrategy = jasmine.createSpyObj<CatalogFetchStrategy<EditableListNodeItemDTO>>(
            'fetchStrategy',
            ['fetchEntity']
        );

        component = new CatalogAwareSelectorComponent(logService, cdr);
    });

    beforeEach(() => {
        fetchStrategy.fetchEntity.and.callFake((id) => Promise.resolve(itemList.get(id)));
        component.itemsFetchStrategy = fetchStrategy;
    });

    it('GIVEN more selectedItemsIds than items in itemList THEN items are fetched AND itemList is populated', async () => {
        const selectedItemsIds = ['1', '2', '3'];
        component.itemList = [listItem1];

        await component.onItemSelectorPanelSaveChanges(selectedItemsIds);

        expect(fetchStrategy.fetchEntity).toHaveBeenCalledTimes(2);
        expect(component.itemList).toEqual([listItem1, listItem2, listItem3]);
    });

    it('GIVEN less selectedItemsIds than items in itemList THEN items that does not match selectedItemsIds are removed', async () => {
        const selectedItemsIds = ['1'];

        component.itemList = [listItem1, listItem2, listItem3];

        await component.onItemSelectorPanelSaveChanges(selectedItemsIds);

        expect(fetchStrategy.fetchEntity).not.toHaveBeenCalled();
        expect(component.itemList).toEqual([listItem1]);
    });

    it('GIVEN no selectedItemsIds AND itemList is populated THEN itemList is cleared', async () => {
        const selectedItemsIds = [];

        component.itemList = [listItem1, listItem2, listItem3];

        await component.onItemSelectorPanelSaveChanges(selectedItemsIds);

        expect(component.itemList.length).toBe(0);
    });

    it('GIVEN itemList has been updated THEN it should update their position', async () => {
        const selectedItemsIds = ['1', '2', '3'];
        component.itemList = [listItem1];
        component.refreshItemListWidget = jasmine.createSpy('refreshItemListWidget');

        await component.onItemSelectorPanelSaveChanges(selectedItemsIds);

        expect(component.refreshItemListWidget).toHaveBeenCalled();
    });
});
