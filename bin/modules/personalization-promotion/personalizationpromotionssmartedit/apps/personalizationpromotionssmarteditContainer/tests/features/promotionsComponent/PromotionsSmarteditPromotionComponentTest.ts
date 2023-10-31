import { PersonlizationPromotionExtensionInjector } from 'personalizationcommons';
import { PromotionsSmarteditPromotionComponent } from 'personalizationpromotionssmarteditcontainer/promotionsComponent/PromotionsSmarteditPromotionComponent';

describe('PromotionsSmarteditPromotionComponent', () => {
    // ======= Injected mocks =======
    let translateService: jasmine.SpyObj<any>;
    let personalizationpromotionssmarteditRestService: jasmine.SpyObj<any>;
    let personalizationsmarteditMessageHandler: jasmine.SpyObj<any>;
    let experienceService: jasmine.SpyObj<any>;

    // Service being tested
    let promotionsComponent: PromotionsSmarteditPromotionComponent;
    let injectedData: PersonlizationPromotionExtensionInjector;

    // === SETUP ===
    beforeEach(() => {
        translateService = jasmine.createSpyObj('$translate', ['instant']);
        personalizationpromotionssmarteditRestService = jasmine.createSpyObj(
            'personalizationpromotionssmarteditRestService',
            ['getPromotions']
        );
        personalizationsmarteditMessageHandler = jasmine.createSpyObj(
            'personalizationsmarteditMessageHandler',
            ['sendError']
        );

        injectedData = {
            getSelectedTypeCode: jasmine.createSpy('getSelectedTypeCode'),
            addAction: jasmine.createSpy('addAction'),
            isActionInSelectDisabled: jasmine.createSpy('isActionInSelectDisabled')
        };

        experienceService = jasmine.createSpyObj('experienceService', ['getCurrentExperience']);

        promotionsComponent = new PromotionsSmarteditPromotionComponent(
            injectedData,
            translateService,
            experienceService,
            personalizationpromotionssmarteditRestService,
            personalizationsmarteditMessageHandler
        );
    });

    it('Public API', () => {
        expect(promotionsComponent.getCatalogs).toBeDefined();
        expect(promotionsComponent.getPromotions).toBeDefined();
        expect(promotionsComponent.getAvailablePromotions).toBeDefined();
        expect(promotionsComponent.buildAction).toBeDefined();
        expect(promotionsComponent.comparer).toBeDefined();
    });
});
