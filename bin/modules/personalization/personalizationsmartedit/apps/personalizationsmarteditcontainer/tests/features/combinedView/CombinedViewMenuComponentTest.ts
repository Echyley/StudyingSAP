import { TranslateService } from '@ngx-translate/core';
import {
    CombinedViewCommonsService,
    CombinedViewMenuComponent
} from 'personalizationsmarteditcontainer/combinedView';
import { promiseUtils } from 'smarteditcommons';

describe('Test Personalizationsmartedit Combined View Module', () => {
    let translateService: jasmine.SpyObj<TranslateService>;
    let contextService: jasmine.SpyObj<any>;
    let restService: jasmine.SpyObj<any>;
    let previewService: jasmine.SpyObj<any>;
    let personalizationsmarteditUtils: jasmine.SpyObj<any>;
    let messageHandler: jasmine.SpyObj<any>;
    let contextUtils: jasmine.SpyObj<any>;
    let crossFrameEventService: jasmine.SpyObj<any>;
    let permissionService: jasmine.SpyObj<any>;
    let toolbarItem: jasmine.SpyObj<any>;

    const mockCombinedView = {
        enabled: false,
        selectedItems: []
    };
    const mockExperienceData = {
        catalogDescriptor: {
            catalogId: 'myId'
        }
    };
    const mockVariation = {
        code: 'testVariation'
    };
    const mockCustomization = {
        code: 'testCustomization',
        variations: [mockVariation]
    };

    let combinedViewCommonsService: jasmine.SpyObj<CombinedViewCommonsService>;
    let combinedViewMenuComponent: CombinedViewMenuComponent;

    // === SETUP ===
    beforeEach(() => {
        translateService = jasmine.createSpyObj('$translate', ['instant']);

        contextService = jasmine.createSpyObj('personalizationsmarteditContextService', [
            'getCombinedView',
            'getSeExperienceData',
            'setCombinedView'
        ]);
        restService = jasmine.createSpyObj('personalizationsmarteditRestService', [
            'getCustomizations'
        ]);
        messageHandler = jasmine.createSpyObj('personalizationsmarteditMessageHandler', [
            'sendError'
        ]);
        crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', ['publish']);
        permissionService = jasmine.createSpyObj('permissionService', ['isPermitted']);

        contextService.getCombinedView.and.callFake(() => {
            return mockCombinedView;
        });
        contextService.getSeExperienceData.and.callFake(() => {
            return mockExperienceData;
        });
        restService.getCustomizations.and.callFake(() => {
            const deferred = promiseUtils.defer();
            deferred.resolve({
                customizations: [mockCustomization, mockCustomization],
                pagination: {
                    count: 5,
                    page: 0,
                    totalCount: 5,
                    totalPages: 1
                }
            });
            return deferred.promise;
        });
        previewService = jasmine.createSpyObj('personalizationsmarteditPreviewService', [
            'updatePreviewTicketWithVariations'
        ]);

        contextUtils = jasmine.createSpyObj('personalizationsmarteditContextUtils', [
            'clearCombinedViewCustomizeContext'
        ]);
        personalizationsmarteditUtils = jasmine.createSpyObj('personalizationsmarteditUtils', [
            'isItemFromCurrentCatalog'
        ]);

        combinedViewCommonsService = jasmine.createSpyObj('combinedViewCommonsService', [
            'updatePreview'
        ]);
        combinedViewMenuComponent = new CombinedViewMenuComponent(
            translateService,
            contextService,
            messageHandler,
            restService,
            contextUtils,
            personalizationsmarteditUtils,
            previewService,
            combinedViewCommonsService,
            crossFrameEventService,
            permissionService,
            toolbarItem
        );
    });

    it('GIVEN that combinedViewMenuComponent properties are instantiated properly', () => {
        combinedViewMenuComponent.ngOnInit();

        expect(combinedViewMenuComponent.combinedView).toBeDefined();
        expect(combinedViewMenuComponent.selectedItems).toBeDefined();
        expect(combinedViewMenuComponent.getClassForElement).toBeDefined();
        expect(combinedViewMenuComponent.combinedView).toEqual(mockCombinedView as any);
    });
});
