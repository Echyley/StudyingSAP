import { CombinedViewSelectItem } from 'personalizationcommons';
import { CombinedViewConfigureService } from 'personalizationsmarteditcontainer/combinedView/combinedViewConfigure';
import { take } from 'rxjs/operators';

describe('CombinedViewConfigureService', () => {
    let combinedViewConfigureService: CombinedViewConfigureService;
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
    const catalogFilter = 'defaultFilter';

    beforeEach(() => {
        combinedViewConfigureService = new CombinedViewConfigureService();
    });

    afterEach(() => {
        combinedViewConfigureService.ngOnDestroy();
    });

    it('WHEN CombinedViewConfigureService initialize THEN proper functions should be defined', () => {
        expect(combinedViewConfigureService.ngOnDestroy).toBeDefined();
        expect(combinedViewConfigureService.setCatalogFilter).toBeDefined();
        expect(combinedViewConfigureService.getCatalogFilter$).toBeDefined();
        expect(combinedViewConfigureService.setSelectedItems).toBeDefined();
        expect(combinedViewConfigureService.isItemSelected).toBeDefined();
    });

    it('WHEN call getCatalogFilter$ THEN it should return proper value', () => {
        combinedViewConfigureService.setCatalogFilter(catalogFilter);
        combinedViewConfigureService
            .getCatalogFilter$()
            .pipe(take(1))
            .subscribe((filter) => {
                expect(filter).toBe(catalogFilter);
            });
    });

    it('GIVEN item in selected items WHEN call isItemSelected THEN it should return true', () => {
        const item = selectedItems[0];
        combinedViewConfigureService.setSelectedItems(selectedItems);
        expect(combinedViewConfigureService.isItemSelected(item)).toBe(true);
    });

    it('GIVEN item not in selected items WHEN call isItemSelected THEN it should return false', () => {
        const item = {
            id: 'customization3-variation3',
            customization: {
                code: 'customization3',
                catalog: 'catalog1',
                catalogVersion: 'catalogVersion1',
                name: 'name1',
                rank: 0
            },
            variation: {
                code: 'variation3',
                catalog: 'catalog1',
                catalogVersion: 'catalogVersion1'
            }
        };
        combinedViewConfigureService.setSelectedItems(selectedItems);
        expect(combinedViewConfigureService.isItemSelected(item)).toBe(false);
    });
});
