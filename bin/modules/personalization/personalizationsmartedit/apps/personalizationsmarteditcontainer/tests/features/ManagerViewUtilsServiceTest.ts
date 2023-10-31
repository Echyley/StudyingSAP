import { TranslateService } from '@ngx-translate/core';
import 'jasmine';
import { ManagerViewUtilsService } from 'personalizationsmarteditcontainer/management/managerView/ManagerViewUtilsService';

describe('PersonalizationsmarteditManagerViewUtilsService', () => {
    let personalizationsmarteditRestService: jasmine.SpyObj<any>;
    let personalizationsmarteditMessageHandler: jasmine.SpyObj<any>;
    let personalizationsmarteditCommerceCustomizationService: jasmine.SpyObj<any>;
    let PERSONALIZATION_MODEL_STATUS_CODES: jasmine.SpyObj<any>;
    let waitDialogService: jasmine.SpyObj<any>;
    let confirmationModalService: jasmine.SpyObj<any>;
    let translateService: TranslateService;

    let personalizationsmarteditManagerViewUtilsService: ManagerViewUtilsService;

    // === SETUP ===
    beforeEach(() => {
        personalizationsmarteditRestService = jasmine.createSpyObj(
            'personalizationsmarteditRestService',
            ['getCustomization']
        );
        personalizationsmarteditMessageHandler = jasmine.createSpyObj(
            'personalizationsmarteditMessageHandler',
            ['sendError']
        );
        personalizationsmarteditCommerceCustomizationService = jasmine.createSpyObj(
            'personalizationsmarteditCommerceCustomizationService',
            ['isCommerceCustomizationEnabled']
        );
        PERSONALIZATION_MODEL_STATUS_CODES = jasmine.createSpyObj(
            'PERSONALIZATION_MODEL_STATUS_CODES',
            ['mock']
        );
        waitDialogService = jasmine.createSpyObj('waitDialogService', ['showWaitModal']);
        confirmationModalService = jasmine.createSpyObj('confirmationModalService', ['confirm']);
        translateService = jasmine.createSpyObj('translate', ['instant']);

        personalizationsmarteditManagerViewUtilsService = new ManagerViewUtilsService(
            personalizationsmarteditRestService,
            personalizationsmarteditMessageHandler,
            personalizationsmarteditCommerceCustomizationService,
            waitDialogService,
            confirmationModalService,
            translateService
        );
    });

    it('Public API', () => {
        expect(
            personalizationsmarteditManagerViewUtilsService.deleteCustomizationAction
        ).toBeDefined();
        expect(personalizationsmarteditManagerViewUtilsService.deleteVariationAction).toBeDefined();
        expect(personalizationsmarteditManagerViewUtilsService.toggleVariationActive).toBeDefined();
        expect(
            personalizationsmarteditManagerViewUtilsService.customizationClickAction
        ).toBeDefined();
        expect(personalizationsmarteditManagerViewUtilsService.getCustomizations).toBeDefined();
        expect(
            personalizationsmarteditManagerViewUtilsService.updateCustomizationRank
        ).toBeDefined();
        expect(personalizationsmarteditManagerViewUtilsService.updateVariationRank).toBeDefined();
        expect(personalizationsmarteditManagerViewUtilsService.setCustomizationRank).toBeDefined();
        expect(personalizationsmarteditManagerViewUtilsService.setVariationRank).toBeDefined();
        //TODO: private getActionsForVariation, use any to fix temporarily
        expect(
            (personalizationsmarteditManagerViewUtilsService as any).getActionsForVariation
        ).toBeDefined();
    });
});
