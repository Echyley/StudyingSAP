import { PersonalizationsmarteditUtils } from 'personalizationcommons';
import { StatusFilterDropdownComponent } from 'personalizationsmarteditcontainer/commonComponents/StatusFilterDropdownComponent';

describe('StatusFilterDropdownComponent', () => {
    let personalizationsmarteditUtils: PersonalizationsmarteditUtils;
    let statusFilterDropdownComponent: StatusFilterDropdownComponent;
    let translateService: jasmine.SpyObj<any>;
    let l10nPipe: jasmine.SpyObj<any>;

    beforeEach(() => {
        translateService = jasmine.createSpyObj('translateService', ['instant']);
        l10nPipe = jasmine.createSpyObj('l10nPipe', ['debug']);
        const catalogService = jasmine.createSpyObj('catalogService', ['debug']);

        personalizationsmarteditUtils = new PersonalizationsmarteditUtils(
            translateService,
            l10nPipe,
            catalogService
        );
        statusFilterDropdownComponent = new StatusFilterDropdownComponent(
            personalizationsmarteditUtils
        );
    });

    describe('Component API', () => {
        it('should have proper api before initialized', () => {
            expect(statusFilterDropdownComponent.items).not.toBeDefined();
            expect(statusFilterDropdownComponent.selectedId).not.toBeDefined();
            expect(statusFilterDropdownComponent.onSelectCallback).toBeDefined();
            expect(statusFilterDropdownComponent.ngOnInit).toBeDefined();
            expect(statusFilterDropdownComponent.onChange).toBeDefined();
            expect(statusFilterDropdownComponent.fetchStrategy).toBeDefined();
        });

        it('should have proper api after initialized', () => {
            statusFilterDropdownComponent.ngOnInit();
            expect(statusFilterDropdownComponent.items.length).toBe(3);
            expect(statusFilterDropdownComponent.selectedId).toBe(
                statusFilterDropdownComponent.items[0].id
            );
            expect(statusFilterDropdownComponent.ngOnInit).toBeDefined();
            expect(statusFilterDropdownComponent.onChange).toBeDefined();
            expect(statusFilterDropdownComponent.fetchStrategy).toBeDefined();
        });

        it('should call onSelectCallback properly', () => {
            spyOn(statusFilterDropdownComponent.onSelectCallback, 'emit');
            statusFilterDropdownComponent.ngOnInit();
            statusFilterDropdownComponent.onChange();
            expect(statusFilterDropdownComponent.onSelectCallback.emit).toHaveBeenCalledWith(
                statusFilterDropdownComponent.items[0].id
            );
        });
    });
});
