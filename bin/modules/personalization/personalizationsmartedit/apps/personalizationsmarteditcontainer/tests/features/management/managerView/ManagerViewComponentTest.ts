import { TranslateService } from '@ngx-translate/core';
import { ManagerViewComponent } from 'personalizationsmarteditcontainer/management';

describe('ManagerViewController', () => {
    let translateService: jasmine.SpyObj<TranslateService>;
    let managerViewUtilsService: jasmine.SpyObj<any>;
    let messageHandler: jasmine.SpyObj<any>;
    let contextService: jasmine.SpyObj<any>;
    let utils: jasmine.SpyObj<any>;
    let manageCustomizationViewManager: jasmine.SpyObj<any>;
    let systemEventService: jasmine.SpyObj<any>;

    let managerViewComponent: ManagerViewComponent;

    // === SETUP ===
    beforeEach(() => {
        managerViewUtilsService = jasmine.createSpyObj('managerViewUtilsService', [
            'setCustomizationRank',
            'setVariationRank',
            'getCustomizations'
        ]);
        messageHandler = jasmine.createSpyObj('messageHandler', ['sendError']);
        contextService = jasmine.createSpyObj('contextService', ['getSeData']);
        contextService.getSeData.and.callFake(() => {
            return {
                seExperienceData: {
                    languageDescriptor: {
                        isocode: 'AA'
                    },
                    catalogDescriptor: {
                        name: {
                            AA: 'aaa'
                        },
                        catalogVersion: 'catalogVersion'
                    }
                }
            };
        });
        utils = jasmine.createSpyObj('utils', ['getVisibleItems', 'getStatusesMapping']);
        utils.getStatusesMapping.and.callFake(() => {
            return [];
        });
        utils.getVisibleItems.and.callFake(() => {
            return ['a', 'b'];
        });
        manageCustomizationViewManager = jasmine.createSpyObj('manageCustomizationViewManager', [
            'openCreateCustomizationModal'
        ]);
        systemEventService = jasmine.createSpyObj('systemEventService', ['registerEventHandler']);

        managerViewComponent = new ManagerViewComponent(
            translateService,
            managerViewUtilsService,
            messageHandler,
            contextService,
            utils,
            manageCustomizationViewManager,
            systemEventService
        );
    });

    it('Public API', () => {
        expect(managerViewComponent.searchInputKeypress).toBeDefined();
        expect(managerViewComponent.addMoreItems).toBeDefined();
        expect(managerViewComponent.openNewModal).toBeDefined();
        expect(managerViewComponent.isSearchGridHeaderHidden).toBeDefined();
        expect(managerViewComponent.scrollZoneReturnToTop).toBeDefined();
        expect(managerViewComponent.isReturnToTopButtonVisible).toBeDefined();
        expect(managerViewComponent.refreshGrid).toBeDefined();
        expect(managerViewComponent.catalogName).toBeDefined();
        expect(managerViewComponent.moreCustomizationsRequestProcessing).toBeDefined();
        expect(managerViewComponent.pagination).toBeDefined();
        expect(managerViewComponent.customizations).toBeDefined();
        expect(managerViewComponent.customizationSearch.name).toBeDefined();
    });
});
