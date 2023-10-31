import { TranslateService } from '@ngx-translate/core';
import * as lodash from 'lodash';
import {
    CombinedViewSelectItem,
    PERSONALIZATION_CATALOG_FILTER,
    PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER,
    PersonalizationsmarteditMessageHandler,
    PersonalizationsmarteditUtils
} from 'personalizationcommons';
import {
    CombinedViewConfigureComponent,
    CombinedViewConfigureService
} from 'personalizationsmarteditcontainer/combinedView/combinedViewConfigure';
import {
    PersonalizationsmarteditContextService,
    PersonalizationsmarteditRestService
} from 'personalizationsmarteditcontainer/service';
import { ComponentMenuService, ModalManagerService } from 'smarteditcommons';

describe('Test CombinedViewConfigureComponent', () => {
    let translateService: jasmine.SpyObj<TranslateService>;
    let contextService: jasmine.SpyObj<PersonalizationsmarteditContextService>;
    let messageHandler: jasmine.SpyObj<PersonalizationsmarteditMessageHandler>;
    let personalizationsmarteditUtils: jasmine.SpyObj<PersonalizationsmarteditUtils>;
    let componentMenuService: jasmine.SpyObj<ComponentMenuService>;
    let restService: jasmine.SpyObj<PersonalizationsmarteditRestService>;
    let modalManager: jasmine.SpyObj<ModalManagerService>;
    let combinedViewConfigureService: jasmine.SpyObj<CombinedViewConfigureService>;
    let combinedViewConfigureComponent: CombinedViewConfigureComponent;

    const selectedItems: CombinedViewSelectItem[] = [
        {
            id: 'customization1-variation1',
            customization: {
                code: 'customization1',
                catalog: 'catalog1',
                catalogVersion: 'catalogVersion1',
                name: 'name1',
                rank: 0
            },
            variation: {
                code: 'variation1',
                catalog: 'catalog1',
                catalogVersion: 'catalogVersion1'
            }
        },
        {
            id: 'customization2-variation2',
            customization: {
                code: 'customization2',
                catalog: 'catalog2',
                catalogVersion: 'catalogVersion2',
                name: 'name2',
                rank: 0
            },
            variation: {
                code: 'variation2',
                catalog: 'catalog2',
                catalogVersion: 'catalogVersion2'
            }
        }
    ];
    const catalogsVersions = [
        {
            catalogId: 'catalog1',
            catalogName: { en: 'Apparel UK Content Catalog' },
            catalogVersionId: 'catalogVersion1',
            id: 'catalog1/catalogVersion1',
            isCurrentCatalog: false
        },
        {
            catalogId: 'catalog2',
            catalogName: { en: 'Apparel UK Content Catalog' },
            catalogVersionId: 'catalogVersion2',
            id: 'catalog2/catalogVersion2',
            isCurrentCatalog: true
        }
    ];
    const combinedView = {
        enabled: false,
        selectedItems: []
    };

    beforeEach(() => {
        translateService = jasmine.createSpyObj('translateService', ['instant']);
        contextService = jasmine.createSpyObj('contextService', [
            'getCombinedView',
            'setCombinedView',
            'getSeData'
        ]);
        (contextService.getCombinedView as any).and.returnValue(combinedView);
        messageHandler = jasmine.createSpyObj('messageHandler', ['sendError']);
        personalizationsmarteditUtils = jasmine.createSpyObj('personalizationsmarteditUtils', [
            'getClassForElement',
            'getLetterForElement',
            'isItemFromCurrentCatalog',
            'isItemVisible',
            'getStatusesMapping'
        ]);
        componentMenuService = jasmine.createSpyObj('componentMenuService', [
            'getValidContentCatalogVersions'
        ]);
        (componentMenuService.getValidContentCatalogVersions as any).and.returnValue(catalogsVersions);
        restService = jasmine.createSpyObj('restService', [
            'getCustomizations',
            'getCustomization'
        ]);
        modalManager = jasmine.createSpyObj('modalManager', ['addButtons']);
        combinedViewConfigureService = jasmine.createSpyObj('combinedViewConfigureService', [
            'setSelectedItems',
            'setCatalogFilter'
        ]);
        combinedViewConfigureComponent = new CombinedViewConfigureComponent(
            translateService,
            contextService,
            messageHandler,
            personalizationsmarteditUtils,
            componentMenuService,
            restService,
            modalManager,
            combinedViewConfigureService
        );
        combinedViewConfigureComponent.ngOnInit();
        combinedViewConfigureComponent.resetSelectedItems = (): void => {};
        spyOn(combinedViewConfigureComponent, 'resetSelectedItems').and.callThrough();
    });

    it('WHEN initialize combinedViewConfigureComponent THEN should has proper functions', () => {
        expect(combinedViewConfigureComponent.ngOnInit).toBeDefined();
        expect(combinedViewConfigureComponent.selectElement).toBeDefined();
        expect(combinedViewConfigureComponent.removeSelectedItem).toBeDefined();
        expect(combinedViewConfigureComponent.getClassForElement).toBeDefined();
        expect(combinedViewConfigureComponent.getLetterForElement).toBeDefined();
        expect(combinedViewConfigureComponent.isItemInSelectDisabled).toBeDefined();
        expect(combinedViewConfigureComponent.pageFilterChange).toBeDefined();
        expect(combinedViewConfigureComponent.catalogFilterChange).toBeDefined();
        expect(combinedViewConfigureComponent.isItemFromCurrentCatalog).toBeDefined();
    });

    it('WHEN call ngOnInit THEN should call proper functions', () => {
        combinedViewConfigureComponent.ngOnInit();
        expect(contextService.getCombinedView).toHaveBeenCalled();
        expect(combinedViewConfigureService.setSelectedItems).toHaveBeenCalledWith(
            combinedView.selectedItems
        );
        expect(modalManager.addButtons).toHaveBeenCalled();
    });

    it(
        'WHEN select two items by the sequence of catalogVersion' +
            ' THEN should call proper functions with corresponding parameters' +
            ' AND the sequence of selectedCombinedViewItems should be reversed',
        async () => {
            await combinedViewConfigureComponent.selectElement(selectedItems[0]);
            expect(combinedViewConfigureService.setSelectedItems).toHaveBeenCalledWith([
                selectedItems[0]
            ]);
            await combinedViewConfigureComponent.selectElement(selectedItems[1]);
            expect(combinedViewConfigureService.setSelectedItems).toHaveBeenCalledWith([
                selectedItems[1],
                selectedItems[0]
            ]);
            expect(combinedViewConfigureComponent.resetSelectedItems).toHaveBeenCalledTimes(2);
        }
    );

    it('WHEN call removeSelectedItem THEN call proper functions', async () => {
        await combinedViewConfigureComponent.selectElement(selectedItems[0]);
        await combinedViewConfigureComponent.selectElement(selectedItems[1]);
        combinedViewConfigureComponent.removeSelectedItem(selectedItems[1]);
        expect(combinedViewConfigureService.setSelectedItems).toHaveBeenCalledWith([
            selectedItems[0]
        ]);
        expect(combinedViewConfigureComponent.resetSelectedItems).toHaveBeenCalledTimes(3);
    });

    it('WHEN call getClassForElement THEN call proper function', () => {
        combinedViewConfigureComponent.getClassForElement(0);
        expect(personalizationsmarteditUtils.getClassForElement).toHaveBeenCalledWith(0);
    });

    it('WHEN call getLetterForElement THEN call proper function', () => {
        combinedViewConfigureComponent.getLetterForElement(0);
        expect(personalizationsmarteditUtils.getLetterForElement).toHaveBeenCalledWith(0);
    });

    it(
        'WHEN call isItemInSelectDisabled with selected customization and unselected variation ' +
            'THEN it should return true',
        async () => {
            await combinedViewConfigureComponent.selectElement(selectedItems[0]);
            await combinedViewConfigureComponent.selectElement(selectedItems[1]);
            const item = lodash.cloneDeep(selectedItems[0]);
            item.variation.code = 'variation3';
            expect(combinedViewConfigureComponent.isItemInSelectDisabled(item)).toBe(true);
        }
    );

    it('WHEN call pageFilterChange THEN should set correct page filter', () => {
        combinedViewConfigureComponent.pageFilterChange(
            PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER.ONLY_THIS_PAGE
        );
        expect(combinedViewConfigureComponent.customizationPageFilter).toBe(
            PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER.ONLY_THIS_PAGE
        );
    });

    it('WHEN call catalogFilterChange THEN should set correct catalog filter', () => {
        combinedViewConfigureComponent.catalogFilterChange(PERSONALIZATION_CATALOG_FILTER.ALL);
        expect(combinedViewConfigureComponent.catalogFilter).toBe(
            PERSONALIZATION_CATALOG_FILTER.ALL
        );
        expect(combinedViewConfigureService.setCatalogFilter).toHaveBeenCalledWith(
            PERSONALIZATION_CATALOG_FILTER.ALL
        );
    });

    it('WHEN call isItemFromCurrentCatalog THEN should call proper functions', () => {
        combinedViewConfigureComponent.isItemFromCurrentCatalog(selectedItems[0].variation);
        expect(contextService.getSeData).toHaveBeenCalled();
        expect(personalizationsmarteditUtils.isItemFromCurrentCatalog).toHaveBeenCalled();
    });
});
