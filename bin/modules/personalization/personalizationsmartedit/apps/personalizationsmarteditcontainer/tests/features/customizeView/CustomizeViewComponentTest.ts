import {
    CustomizationVariation,
    PersonalizationsmarteditMessageHandler,
    PERSONALIZATION_MODEL_STATUS_CODES
} from 'personalizationcommons';
import { CustomizeViewComponent } from 'personalizationsmarteditcontainer/customizeView/CustomizeViewComponent';
import { promiseUtils, ToolbarItemInternal } from 'smarteditcommons';

describe('PersonalizationsmarteditCustomizeViewComponent', () => {
    // ======= Injected mocks =======
    let translate: jasmine.SpyObj<any>;
    let personalizationsmarteditContextService: jasmine.SpyObj<any>;
    let personalizationsmarteditUtils: jasmine.SpyObj<any>;
    let personalizationsmarteditRestService: jasmine.SpyObj<any>;
    let customizationDataFactory: jasmine.SpyObj<any>;
    let personalizationsmarteditMessageHandler: PersonalizationsmarteditMessageHandler;
    let personalizationsmarteditCustomizeViewComponent: CustomizeViewComponent;

    const mockToolbarItem = {
        isOpen: false
    } as ToolbarItemInternal;

    const mockTrigger = {
        type: '',
        catalog: '',
        catalogVersion: '',
        code: ''
    };

    const mockCustomizationActionList = [
        {
            type: '',
            catalog: '',
            catalogVersion: '',
            code: '',
            rank: 0,
            componentCatalog: '',
            componentId: '',
            containerId: ''
        }
    ];

    const mockVariation = ({
        code: 'testVariation',
        active: false,
        catalog: '',
        catalogVersion: '',
        enabled: true,
        name: '',
        rank: 0,
        status: PERSONALIZATION_MODEL_STATUS_CODES.ENABLED,
        triggers: mockTrigger,
        actions: mockCustomizationActionList,
        catalogVersionNameL10N: ''
    } as unknown) as CustomizationVariation;

    const mockCustomization = {
        code: 'testCustomization',
        name: '',
        enabledStartDate: '',
        enabledEndDate: '',
        status: PERSONALIZATION_MODEL_STATUS_CODES.ENABLED,
        statusBoolean: false,
        catalog: '',
        catalogVersion: '',
        rank: 0,
        variations: [mockVariation]
    };

    // === SETUP ===
    beforeEach(() => {
        translate = jasmine.createSpyObj('$translate', ['instant']);
        personalizationsmarteditRestService = jasmine.createSpyObj(
            'personalizationsmarteditRestService',
            ['getCustomizations']
        );
        personalizationsmarteditUtils = jasmine.createSpyObj('personalizationsmarteditUtils', [
            'getStatusesMapping'
        ]);
        customizationDataFactory = jasmine.createSpyObj('customizationDataFactory', [
            'updateData',
            'resetData',
            'items'
        ]);
        personalizationsmarteditContextService = jasmine.createSpyObj(
            'personalizationsmarteditContextService',
            ['getCustomizeFiltersState', 'setCustomizeFiltersState', 'refreshExperienceData']
        );
        personalizationsmarteditMessageHandler = jasmine.createSpyObj(
            'personalizationsmarteditMessageHandler',
            ['sendError']
        );
        personalizationsmarteditCustomizeViewComponent = new CustomizeViewComponent(
            mockToolbarItem,
            translate,
            customizationDataFactory,
            personalizationsmarteditContextService,
            personalizationsmarteditMessageHandler,
            personalizationsmarteditUtils
        );

        customizationDataFactory.updateData.and.callFake(() => {
            const retCustomizations = [mockCustomization, mockCustomization];
            personalizationsmarteditCustomizeViewComponent.customizationsList = retCustomizations;
            return Promise.resolve({
                customizations: retCustomizations,
                pagination: {
                    count: 5,
                    page: 0,
                    totalCount: 5,
                    totalPages: 1
                }
            });
        });

        personalizationsmarteditRestService.getCustomizations.and.callFake(() => {
            const deferred = promiseUtils.defer();
            const retCustomizations = [mockCustomization, mockCustomization];

            deferred.resolve({
                customizations: retCustomizations,
                pagination: {
                    count: 5,
                    page: 0,
                    totalCount: 5,
                    totalPages: 1
                }
            });
            return deferred.promise;
        });

        personalizationsmarteditUtils.getStatusesMapping.and.callFake(() => {
            return [
                {
                    modelStatuses: {},
                    code: 'all'
                }
            ];
        });

        personalizationsmarteditContextService.getCustomizeFiltersState.and.callFake(() => {
            return {
                catalogFilter: 'catalogMock',
                pageFilter: 'pageMock',
                statusFilter: 'statusMock',
                nameFilter: 'nameMock'
            };
        });
    });

    describe('Component API', () => {
        it('should have proper api when initialized without parameters', () => {
            expect(
                personalizationsmarteditCustomizeViewComponent.catalogFilterChange
            ).toBeDefined();
            expect(personalizationsmarteditCustomizeViewComponent.pageFilterChange).toBeDefined();
            expect(personalizationsmarteditCustomizeViewComponent.statusFilterChange).toBeDefined();
            expect(personalizationsmarteditCustomizeViewComponent.nameInputKeypress).toBeDefined();
            expect(
                personalizationsmarteditCustomizeViewComponent.addMoreCustomizationItems
            ).toBeDefined();
            expect(personalizationsmarteditCustomizeViewComponent.ngOnInit).toBeDefined();
            expect(personalizationsmarteditCustomizeViewComponent.ngOnDestroy).toBeDefined();
        });
    });

    describe('customizationsList', () => {
        it('should be instantiation and empty', () => {
            personalizationsmarteditCustomizeViewComponent.ngOnInit();
            expect(personalizationsmarteditCustomizeViewComponent.customizationsList).toBeDefined();
            expect(personalizationsmarteditCustomizeViewComponent.customizationsList.length).toBe(
                0
            );
        });
    });

    describe('addMoreCustomizationItems', () => {
        it('after called array customizationsOnPage should contain objects return by REST service', () => {
            // when
            personalizationsmarteditCustomizeViewComponent.ngOnInit();
            personalizationsmarteditCustomizeViewComponent.statusFilter = 'all';
            personalizationsmarteditCustomizeViewComponent.addMoreCustomizationItems();

            // then
            expect(personalizationsmarteditCustomizeViewComponent.customizationsList).toBeDefined();
            expect(personalizationsmarteditCustomizeViewComponent.customizationsList.length).toBe(
                2
            );
            expect(personalizationsmarteditCustomizeViewComponent.customizationsList).toContain(
                mockCustomization
            );
        });
    });

    describe('onInit', () => {
        it('should call proper service on init', () => {
            personalizationsmarteditCustomizeViewComponent.ngOnInit();

            expect(personalizationsmarteditCustomizeViewComponent.catalogFilter).toBe(
                'catalogMock'
            );
            expect(personalizationsmarteditCustomizeViewComponent.pageFilter).toBe('pageMock');
            expect(personalizationsmarteditCustomizeViewComponent.statusFilter).toBe('statusMock');
            expect(personalizationsmarteditCustomizeViewComponent.nameFilter).toBe('nameMock');
        });
    });

    describe('onDestroy', () => {
        it('should call proper service on destroy', () => {
            const mockFilter = {
                catalogFilter: 'catalog',
                pageFilter: 'page',
                statusFilter: 'status',
                nameFilter: 'name'
            };
            personalizationsmarteditCustomizeViewComponent.catalogFilter = mockFilter.catalogFilter;
            personalizationsmarteditCustomizeViewComponent.pageFilter = mockFilter.pageFilter;
            personalizationsmarteditCustomizeViewComponent.statusFilter = mockFilter.statusFilter;
            personalizationsmarteditCustomizeViewComponent.nameFilter = mockFilter.nameFilter;

            personalizationsmarteditCustomizeViewComponent.ngOnDestroy();

            expect(
                personalizationsmarteditContextService.setCustomizeFiltersState
            ).toHaveBeenCalledWith(mockFilter);
        });
    });

    describe('onCheck', () => {
        it('should only refresh list when customize view open', () => {
            spyOn<any>(personalizationsmarteditCustomizeViewComponent, 'refreshList');
            mockToolbarItem.isOpen = true;
            personalizationsmarteditCustomizeViewComponent.ngDoCheck();
            expect(
                // eslint-disable-next-line @typescript-eslint/dot-notation
                personalizationsmarteditCustomizeViewComponent['refreshList']
            ).toHaveBeenCalledTimes(1);

            personalizationsmarteditCustomizeViewComponent.ngDoCheck();
            expect(
                // eslint-disable-next-line @typescript-eslint/dot-notation
                personalizationsmarteditCustomizeViewComponent['refreshList']
            ).toHaveBeenCalledTimes(1);

            mockToolbarItem.isOpen = false;
            personalizationsmarteditCustomizeViewComponent.ngDoCheck();
            expect(
                // eslint-disable-next-line @typescript-eslint/dot-notation
                personalizationsmarteditCustomizeViewComponent['refreshList']
            ).toHaveBeenCalledTimes(1);
        });
    });
});
