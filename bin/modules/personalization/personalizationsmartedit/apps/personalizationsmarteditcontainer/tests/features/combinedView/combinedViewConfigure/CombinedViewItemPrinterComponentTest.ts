import {
    CombinedViewSelectItem,
    CustomizationVariation,
    PERSONALIZATION_CATALOG_FILTER,
    PersonalizationsmarteditUtils
} from 'personalizationcommons';
import {
    CombinedViewConfigureService,
    CombinedViewItemPrinterComponent
} from 'personalizationsmarteditcontainer/combinedView/combinedViewConfigure';
import { PersonalizationsmarteditContextService } from 'personalizationsmarteditcontainer/service';
import { from } from 'rxjs';
import { ItemComponentData } from 'smarteditcommons';

describe('CombinedViewItemPrinterComponent', () => {
    let itemComponentData: ItemComponentData;
    let contextService: jasmine.SpyObj<PersonalizationsmarteditContextService>;
    let personalizationsmarteditUtils: jasmine.SpyObj<PersonalizationsmarteditUtils>;
    let combinedViewConfigureService: jasmine.SpyObj<CombinedViewConfigureService>;
    let combinedViewItemPrinterComponent: CombinedViewItemPrinterComponent;

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
    const currentCatalog = 'catalog1';

    beforeEach(() => {
        itemComponentData = {
            item: selectedItems[0],
            selected: false,
            select: jasmine.createSpy() as any
        };
        contextService = jasmine.createSpyObj('contextService', ['getSeData']);
        personalizationsmarteditUtils = jasmine.createSpyObj('personalizationsmarteditUtils', [
            'getAndSetCatalogVersionNameL10N',
            'isItemFromCurrentCatalog'
        ]);
        personalizationsmarteditUtils.getAndSetCatalogVersionNameL10N.and.callFake(
            (variation: CustomizationVariation): Promise<void> => {
                variation.catalogVersionNameL10N = variation.catalogVersion + 'L10N';
                return Promise.resolve();
            }
        );
        personalizationsmarteditUtils.isItemFromCurrentCatalog.and.callFake(
            (itemVariation: CustomizationVariation) => itemVariation.catalog === currentCatalog
        );

        combinedViewConfigureService = jasmine.createSpyObj('combinedViewConfigureService', [
            'getCatalogFilter$',
            'isItemSelected'
        ]);
        combinedViewConfigureService.getCatalogFilter$.and.returnValue(
            from(PERSONALIZATION_CATALOG_FILTER.CURRENT)
        );
        combinedViewConfigureService.isItemSelected.and.callFake(
            (item: CombinedViewSelectItem): boolean => {
                return (
                    selectedItems.find(
                        (currentItem: CombinedViewSelectItem) =>
                            currentItem.customization.code === item.customization.code &&
                            currentItem.variation.code === item.variation.code
                    ) !== undefined
                );
            }
        );

        combinedViewItemPrinterComponent = new CombinedViewItemPrinterComponent(
            itemComponentData,
            contextService,
            personalizationsmarteditUtils,
            combinedViewConfigureService
        );
    });

    it('WHEN initialize combinedViewItemPrinterComponent THEN should has proper functions', () => {
        expect(combinedViewItemPrinterComponent.ngOnInit).toBeDefined();
        expect(combinedViewItemPrinterComponent.ngOnDestroy).toBeDefined();
        expect(combinedViewItemPrinterComponent.isItemFromCurrentCatalog).toBeDefined();
        expect(combinedViewItemPrinterComponent.isItemSelected).toBeDefined();
    });

    it('WHEN call isItemFromCurrentCatalog THEN should call proper functions', () => {
        combinedViewItemPrinterComponent.isItemFromCurrentCatalog(selectedItems[0].variation);
        expect(contextService.getSeData).toHaveBeenCalled();
        expect(personalizationsmarteditUtils.isItemFromCurrentCatalog).toHaveBeenCalled();
    });

    it('WHEN call isItemSelected THEN should call proper functions', () => {
        combinedViewItemPrinterComponent.isItemSelected(selectedItems[0]);
        expect(combinedViewConfigureService.isItemSelected).toHaveBeenCalled();
    });

    afterEach(() => {
        combinedViewItemPrinterComponent.ngOnDestroy();
    });
});
