import {
    CustomizationVariation,
    PersonalizationsmarteditCommerceCustomizationService,
    PersonalizationsmarteditContextUtils,
    PersonalizationsmarteditUtils,
    PERSONALIZATION_MODEL_STATUS_CODES
} from 'personalizationcommons';
import { CustomizationsListComponent } from 'personalizationsmarteditcontainer/customizeView/customizationsList/CustomizationsListComponent';
import { PersonalizationsmarteditContextService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuter';
import { PersonalizationsmarteditContextServiceProxy } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuterProxy';
import { promiseUtils, SystemEventService } from 'smarteditcommons';
import { coreAnnotationsHelper } from 'testhelpers';

describe('CustomizationsListComponent', () => {
    coreAnnotationsHelper.init();

    let translate: jasmine.SpyObj<any>;
    let translateService: jasmine.SpyObj<any>;
    let l10nPipe: jasmine.SpyObj<any>;
    let catalogService: jasmine.SpyObj<any>;
    let personalizationsmarteditRestService: jasmine.SpyObj<any>;
    let personalizationsmarteditDateUtils: jasmine.SpyObj<any>;
    let personalizationsmarteditPreviewService: jasmine.SpyObj<any>;
    let personalizationsmarteditManager: jasmine.SpyObj<any>;
    let personalizationsmarteditMessageHandler: jasmine.SpyObj<any>;
    let sharedDataService: jasmine.SpyObj<any>;
    let loadConfigManagerService: jasmine.SpyObj<any>;
    let personalizationsmarteditCustomizeViewServiceProxy: jasmine.SpyObj<any>;
    let crossFrameEventService: jasmine.SpyObj<any>;
    let toolbarItem: jasmine.SpyObj<any>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;

    const personalizationsmarteditContextUtils = new PersonalizationsmarteditContextUtils();
    const personalizationsmarteditCommerceCustomizationService = new PersonalizationsmarteditCommerceCustomizationService();
    const personalizationsmarteditContextServiceProxy = new PersonalizationsmarteditContextServiceProxy();

    let personalizationsmarteditUtils: PersonalizationsmarteditUtils;
    let personalizationsmarteditContextService: PersonalizationsmarteditContextService;
    let customizationsListComponent: CustomizationsListComponent;

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
    const mockCustomization1 = {
        code: 'testCustomization1',
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
    const mockCustomization2 = {
        code: 'testCustomization2',
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
    const mockCustomizationCollapsed = {
        code: 'testCustomizationCollapsed',
        name: '',
        enabledStartDate: '',
        enabledEndDate: '',
        status: PERSONALIZATION_MODEL_STATUS_CODES.ENABLED,
        statusBoolean: false,
        catalog: '',
        catalogVersion: '',
        rank: 0,
        variations: [mockVariation],
        collapsed: true
    };

    const mockComponentList = ['component1', 'component2'];
    const mockVariationList = [mockVariation];

    beforeEach(() => {
        translate = jasmine.createSpyObj('translate', ['instant']);
        translateService = jasmine.createSpyObj('translateService', ['instant']);
        l10nPipe = jasmine.createSpyObj('l10nPipe', ['debug']);
        catalogService = jasmine.createSpyObj('catalogService', ['debug']);
        personalizationsmarteditRestService = jasmine.createSpyObj(
            'personalizationsmarteditRestService',
            [
                'getCustomizations',
                'getComponentsIdsForVariation',
                'getVariationsForCustomization',
                'getCxCmsActionsOnPageForCustomization'
            ]
        );
        personalizationsmarteditDateUtils = jasmine.createSpyObj(
            'personalizationsmarteditDateUtils',
            ['formatDate', 'isDateValidOrEmpty', 'isDateRangeValid']
        );
        personalizationsmarteditPreviewService = jasmine.createSpyObj(
            'personalizationsmarteditPreviewService',
            ['updatePreviewTicketWithVariations']
        );
        personalizationsmarteditManager = jasmine.createSpyObj('personalizationsmarteditManager', [
            'openCreateCustomizationModal'
        ]);
        personalizationsmarteditMessageHandler = jasmine.createSpyObj(
            'personalizationsmarteditMessageHandler',
            ['sendError']
        );
        sharedDataService = jasmine.createSpyObj('sharedDataService', ['get']);
        loadConfigManagerService = jasmine.createSpyObj('loadConfigManagerService', [
            'loadAsObject'
        ]);
        personalizationsmarteditCustomizeViewServiceProxy = jasmine.createSpyObj(
            'personalizationsmarteditCustomizeViewServiceProxy',
            ['getSourceContainersInfo']
        );
        crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', ['publish']);
        systemEventService = jasmine.createSpyObj('systemEventService', [
            'subscribe',
            'publishAsync'
        ]);
        personalizationsmarteditUtils = new PersonalizationsmarteditUtils(
            translateService,
            l10nPipe,
            catalogService
        );
        personalizationsmarteditContextService = new PersonalizationsmarteditContextService(
            sharedDataService,
            loadConfigManagerService,
            personalizationsmarteditContextServiceProxy,
            personalizationsmarteditContextUtils
        );

        customizationsListComponent = new CustomizationsListComponent(
            translate,
            personalizationsmarteditContextService,
            personalizationsmarteditRestService,
            personalizationsmarteditCommerceCustomizationService,
            personalizationsmarteditMessageHandler,
            personalizationsmarteditUtils,
            personalizationsmarteditDateUtils,
            personalizationsmarteditContextUtils,
            personalizationsmarteditPreviewService,
            personalizationsmarteditManager,
            personalizationsmarteditCustomizeViewServiceProxy,
            systemEventService,
            crossFrameEventService,
            toolbarItem
        );

        personalizationsmarteditRestService.getComponentsIdsForVariation.and.callFake(() => {
            const deferred = promiseUtils.defer();
            deferred.resolve({
                components: mockComponentList
            });
            return deferred.promise;
        });

        personalizationsmarteditRestService.getVariationsForCustomization.and.callFake(() => {
            const deferred = promiseUtils.defer();
            deferred.resolve({
                variations: mockVariationList
            });
            return deferred.promise;
        });

        personalizationsmarteditRestService.getCxCmsActionsOnPageForCustomization.and.callFake(
            () => {
                return promiseUtils.defer().promise;
            }
        );

        personalizationsmarteditPreviewService.updatePreviewTicketWithVariations.and.callFake(
            () => {
                return promiseUtils.defer().promise;
            }
        );

        personalizationsmarteditCustomizeViewServiceProxy.getSourceContainersInfo.and.callFake(
            () => {
                return promiseUtils.defer().promise;
            }
        );
    });

    describe('Component API', () => {
        it('should have proper api when initialized without parameters', () => {
            expect(customizationsListComponent.updateCustomizationList).toBeDefined();
            expect(customizationsListComponent.editCustomizationAction).toBeDefined();
            expect(customizationsListComponent.customizationClick).toBeDefined();
            expect(customizationsListComponent.customizationRowClick).toBeDefined();
            expect(customizationsListComponent.getSelectedVariationClass).toBeDefined();
            expect(customizationsListComponent.variationClick).toBeDefined();
            expect(customizationsListComponent.hasCommerceActions).toBeDefined();
            expect(customizationsListComponent.clearAllSubMenu).toBeDefined();
            expect(customizationsListComponent.getActivityStateForCustomization).toBeDefined();
            expect(customizationsListComponent.getActivityStateForVariation).toBeDefined();
            expect(customizationsListComponent.getEnablementTextForCustomization).toBeDefined();
            expect(customizationsListComponent.getEnablementTextForVariation).toBeDefined();
            expect(customizationsListComponent.isEnabled).toBeDefined();
            expect(customizationsListComponent.getDatesForCustomization).toBeDefined();
            expect(customizationsListComponent.customizationSubMenuAction).toBeDefined();
        });

        it('should have proper api when initialized with parameters', () => {
            customizationsListComponent.customizationsList = [
                mockCustomization1,
                mockCustomization2
            ];
            expect(customizationsListComponent.updateCustomizationList).toBeDefined();
            expect(customizationsListComponent.editCustomizationAction).toBeDefined();
            expect(customizationsListComponent.customizationRowClick).toBeDefined();
            expect(customizationsListComponent.customizationClick).toBeDefined();
            expect(customizationsListComponent.getSelectedVariationClass).toBeDefined();
            expect(customizationsListComponent.variationClick).toBeDefined();
            expect(customizationsListComponent.hasCommerceActions).toBeDefined();
            expect(customizationsListComponent.clearAllSubMenu).toBeDefined();
            expect(customizationsListComponent.getActivityStateForCustomization).toBeDefined();
            expect(customizationsListComponent.getActivityStateForVariation).toBeDefined();
            expect(customizationsListComponent.getEnablementTextForCustomization).toBeDefined();
            expect(customizationsListComponent.getEnablementTextForVariation).toBeDefined();
            expect(customizationsListComponent.isEnabled).toBeDefined();
            expect(customizationsListComponent.getDatesForCustomization).toBeDefined();
            expect(customizationsListComponent.customizationSubMenuAction).toBeDefined();
            expect(customizationsListComponent.customizationsList.length).toBe(2);
        });
    });

    describe('customizationClick', () => {
        it('after called all objects in contex service are set properly', () => {
            expect(
                personalizationsmarteditContextService.getCustomize().selectedCustomization
            ).toBe(null);
            expect(personalizationsmarteditContextService.getCustomize().selectedVariations).toBe(
                null
            );
            expect(personalizationsmarteditContextService.getCustomize().selectedComponents).toBe(
                null
            );
            // when
            customizationsListComponent.customizationClick(mockCustomization1);
            // then
            expect(
                personalizationsmarteditContextService.getCustomize().selectedCustomization
            ).toBe(mockCustomization1);
            expect(
                personalizationsmarteditContextService.getCustomize().selectedVariations[0].code
            ).toBe(mockCustomization1.variations[0].code);
        });
    });

    describe('variationClick', () => {
        it('after called all objects in contex service are set properly', () => {
            expect(
                personalizationsmarteditContextService.getCustomize().selectedCustomization
            ).toBe(null);
            expect(personalizationsmarteditContextService.getCustomize().selectedVariations).toBe(
                null
            );
            expect(personalizationsmarteditContextService.getCustomize().selectedComponents).toBe(
                null
            );
            // when
            customizationsListComponent.variationClick(mockCustomization1, mockVariation);
            // then
            expect(
                personalizationsmarteditContextService.getCustomize().selectedCustomization
            ).toBe(mockCustomization1);
            expect(personalizationsmarteditContextService.getCustomize().selectedVariations).toBe(
                mockVariation
            );
        });
    });

    describe('customizationRowClick', () => {
        it('after called all objects in contex service are set properly', () => {
            customizationsListComponent.customizationsList = [mockCustomizationCollapsed];
            // given
            expect(
                personalizationsmarteditContextService.getCustomize().selectedCustomization
            ).toBe(null);
            expect(personalizationsmarteditContextService.getCustomize().selectedVariations).toBe(
                null
            );
            expect(personalizationsmarteditContextService.getCustomize().selectedComponents).toBe(
                null
            );
            // when
            customizationsListComponent.customizationRowClick(mockCustomizationCollapsed, true);
            // then
            expect(
                personalizationsmarteditContextService.getCustomize().selectedCustomization
            ).toBe(mockCustomizationCollapsed);
            expect(
                personalizationsmarteditContextService.getCustomize().selectedVariations[0].code
            ).toBe(mockCustomizationCollapsed.variations[0].code);
            expect(
                personalizationsmarteditContextService.getCustomize().selectedVariations[0]
                    .numberOfAffectedComponents
            ).toBe(undefined);
        });
    });
});
