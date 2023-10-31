import { PersonalizationsmarteditUtils } from 'personalizationcommons';
import { ShowActionListComponent } from 'personalizationsmartedit/contextMenu/ShowActionList/ShowActionListComponent';
import { PersonalizationsmarteditComponentHandlerService } from 'personalizationsmartedit/service/PersonalizationsmarteditComponentHandlerService';
import { PersonalizationsmarteditContextService } from 'personalizationsmartedit/service/PersonalizationsmarteditContextServiceInner';
import { PersonalizationsmarteditContextualMenuService } from 'personalizationsmartedit/service/PersonalizationsmarteditContextualMenuService';
import { ContextualMenuItemData } from 'smarteditcommons';

describe('ShowActionListComponent', () => {
    let showActionListComponent: ShowActionListComponent;
    let persoContextService: jasmine.SpyObj<PersonalizationsmarteditContextService>;
    let persoComponentHandlerService: jasmine.SpyObj<PersonalizationsmarteditComponentHandlerService>;
    let persoUtils: jasmine.SpyObj<PersonalizationsmarteditUtils>;
    let contextualMenuItem: ContextualMenuItemData;
    let yjQuery: any;
    let persoContextualMenuService: jasmine.SpyObj<PersonalizationsmarteditContextualMenuService>;

    const CONTAINER_SOURCE_ID = '1234';
    const CONTAINER_PARENT_SOURCE_ID = '1234';
    const mockSelectedItems = [
        {
            containerId: '1234',
            visible: false,
            variation: {
                actions: ['1', '2', '3']
            }
        },
        {
            containerId: '5678',
            visible: false,
            variation: {
                actions: ['4', '5', '6']
            }
        }
    ];

    beforeEach(() => {
        yjQuery = jasmine.createSpy('yjQuery');
        persoUtils = jasmine.createSpyObj('personalizationsmarteditUtils', [
            'getClassForElement',
            'getLetterForElement',
            'getAndSetCatalogVersionNameL10N'
        ]);
        persoContextService = jasmine.createSpyObj('personalizationsmarteditContextService', [
            'getCombinedView'
        ]);
        persoComponentHandlerService = jasmine.createSpyObj(
            'personalizationsmarteditComponentHandlerService',
            ['getParentContainerIdForComponent', 'getContainerSourceIdForContainerId']
        );

        persoContextualMenuService = jasmine.createSpyObj(
            'personalizationsmarteditContextualMenuService',
            ['isContextualMenuShowActionListEnabled']
        );
    });

    beforeEach(() => {
        (persoContextService.getCombinedView as any).and.callFake(() => {
            return {
                selectedItems: mockSelectedItems
            };
        });

        (persoContextualMenuService.isContextualMenuShowActionListEnabled as any).and.returnValue(
            () => {
                return true;
            }
        );

        persoComponentHandlerService.getParentContainerIdForComponent.and.returnValue(
            CONTAINER_PARENT_SOURCE_ID
        );

        persoComponentHandlerService.getContainerSourceIdForContainerId.and.returnValue(
            CONTAINER_SOURCE_ID
        );

        (persoUtils.getClassForElement as any).and.returnValue(() => {
            return 'classForElement';
        });
        persoUtils.getLetterForElement.and.returnValue('letterForElement');

        (persoUtils.getAndSetCatalogVersionNameL10N as any).and.returnValue(() => {
            Promise.resolve('1234');
        });

        showActionListComponent = new ShowActionListComponent(
            contextualMenuItem,
            persoContextService,
            persoUtils,
            persoComponentHandlerService,
            persoContextualMenuService,
            yjQuery
        );
    });

    describe('Component API', () => {
        it('should have proper api when initialized without parameters', () => {
            expect(showActionListComponent.getClassForElement).toBeDefined();
            expect(showActionListComponent.getLetterForElement).toBeDefined();
            expect(showActionListComponent.initItem).toBeDefined();
            expect(showActionListComponent.isCustomizationFromCurrentCatalog).toBeDefined();
            expect(showActionListComponent.ngOnInit).toBeDefined();
        });

        it('should have proper api when initialized with parameters', () => {
            // given
            const bindings = {
                containerId: '1234'
            };
            (showActionListComponent as any).component = bindings;

            // when
            showActionListComponent.ngOnInit();

            // then
            expect(showActionListComponent.selectedItems.length).toBe(2);
            expect(showActionListComponent.getClassForElement).toBeDefined();
            expect(showActionListComponent.getLetterForElement).toBeDefined();
            expect(showActionListComponent.ngOnInit).toBeDefined();
            expect((showActionListComponent as any).component.containerId).toEqual('1234');
        });
    });
});
