import { PageFilterDropdownComponent } from 'personalizationsmarteditcontainer/commonComponents/PageFilterDropdownComponent';

describe('PageFilterDropdownComponent', () => {
    let pageFilterDropdownComponent: PageFilterDropdownComponent;

    beforeEach(() => {
        pageFilterDropdownComponent = new PageFilterDropdownComponent();
    });

    describe('Component API', () => {
        it('should have proper api before initialized', () => {
            expect(pageFilterDropdownComponent.items).not.toBeDefined();
            expect(pageFilterDropdownComponent.selectedId).not.toBeDefined();
            expect(pageFilterDropdownComponent.onSelectCallback).toBeDefined();
            expect(pageFilterDropdownComponent.ngOnInit).toBeDefined();
            expect(pageFilterDropdownComponent.onChange).toBeDefined();
            expect(pageFilterDropdownComponent.fetchStrategy).toBeDefined();
        });

        it('should have proper api after initialized', () => {
            pageFilterDropdownComponent.ngOnInit();
            expect(pageFilterDropdownComponent.items.length).toBe(2);
            expect(pageFilterDropdownComponent.selectedId).toBe(
                pageFilterDropdownComponent.items[0].id
            );
            expect(pageFilterDropdownComponent.ngOnInit).toBeDefined();
            expect(pageFilterDropdownComponent.onChange).toBeDefined();
            expect(pageFilterDropdownComponent.fetchStrategy).toBeDefined();
        });
    });
});
