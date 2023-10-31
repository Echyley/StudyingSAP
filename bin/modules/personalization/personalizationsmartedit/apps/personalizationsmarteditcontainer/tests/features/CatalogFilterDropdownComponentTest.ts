import { CatalogFilterDropdownComponent } from 'personalizationsmarteditcontainer/commonComponents/CatalogFilterDropdownComponent';

describe('CatalogFilterDropdownComponent', () => {
    let catalogFilterDropdownComponent: CatalogFilterDropdownComponent;

    beforeEach(() => {
        catalogFilterDropdownComponent = new CatalogFilterDropdownComponent();
    });

    describe('Component API', () => {
        it('should have proper api before initialized', () => {
            expect(catalogFilterDropdownComponent.items).not.toBeDefined();
            expect(catalogFilterDropdownComponent.selectedId).not.toBeDefined();
            expect(catalogFilterDropdownComponent.onSelectCallback).toBeDefined();
            expect(catalogFilterDropdownComponent.ngOnInit).toBeDefined();
            expect(catalogFilterDropdownComponent.onChange).toBeDefined();
            expect(catalogFilterDropdownComponent.fetchStrategy).toBeDefined();
        });

        it('should have proper api after initialized', () => {
            catalogFilterDropdownComponent.ngOnInit();
            expect(catalogFilterDropdownComponent.items.length).toBe(3);
            expect(catalogFilterDropdownComponent.selectedId).toBe(
                catalogFilterDropdownComponent.items[1].id
            );
            expect(catalogFilterDropdownComponent.ngOnInit).toBeDefined();
            expect(catalogFilterDropdownComponent.onChange).toBeDefined();
            expect(catalogFilterDropdownComponent.fetchStrategy).toBeDefined();
        });
    });
});
