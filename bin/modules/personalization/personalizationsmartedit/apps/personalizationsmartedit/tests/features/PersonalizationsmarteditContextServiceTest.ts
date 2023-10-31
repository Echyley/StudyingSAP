import {
    CombinedViewSelectItem,
    CustomizationVariation,
    PersonalizationsmarteditContextUtils
} from 'personalizationcommons';
import { PersonalizationsmarteditContextService } from 'personalizationsmartedit/service/PersonalizationsmarteditContextServiceInner';
import { PersonalizationsmarteditContextServiceReverseProxy } from 'personalizationsmartedit/service/PersonalizationsmarteditContextServiceInnerReverseProxy';
import { IContextualMenuService } from 'smarteditcommons';
import { coreAnnotationsHelper } from 'testhelpers';

describe('personalizationsmarteditContextService', () => {
    // ======= Injected mocks =======
    const yjQuery = (jasmine.createSpy('yjQuery') as any) as JQueryStatic;
    let contextualMenuService: jasmine.SpyObj<IContextualMenuService>;
    let personalizationsmarteditContextUtils: PersonalizationsmarteditContextUtils;

    // Service being tested
    let personalizationsmarteditContextService: PersonalizationsmarteditContextService;
    let personalizationsmarteditContextServiceReverseProxy: PersonalizationsmarteditContextServiceReverseProxy;

    beforeEach(() => {
        coreAnnotationsHelper.init();

        contextualMenuService = jasmine.createSpyObj('contextualMenuService', ['refreshMenuItems']);
        personalizationsmarteditContextUtils = new PersonalizationsmarteditContextUtils();

        personalizationsmarteditContextServiceReverseProxy = new PersonalizationsmarteditContextServiceReverseProxy();

        personalizationsmarteditContextService = new PersonalizationsmarteditContextService(
            yjQuery,
            contextualMenuService,
            personalizationsmarteditContextServiceReverseProxy,
            personalizationsmarteditContextUtils
        );
    });

    describe('seData', () => {
        it('should be defined and initialized', () => {
            expect(personalizationsmarteditContextService.getSeData()).toBeDefined();
            expect(personalizationsmarteditContextService.getSeData().pageId).toBe(null);
            expect(personalizationsmarteditContextService.getSeData().seExperienceData).toBe(null);
            expect(personalizationsmarteditContextService.getSeData().seConfigurationData).toBe(
                null
            );
        });

        it('should properly set value', () => {
            // given
            const seData = personalizationsmarteditContextService.getSeData();
            seData.pageId = 'mockMainPage';
            seData.seExperienceData = {
                mock: 'mockValue'
            } as any;
            // when
            personalizationsmarteditContextService.setSeData(seData);
            // then
            expect(personalizationsmarteditContextService.getSeData()).toBe(seData);
        });
    });

    describe('customize', () => {
        it('should be defined and initialized', () => {
            expect(personalizationsmarteditContextService.getCustomize()).toBeDefined();
            expect(personalizationsmarteditContextService.getCustomize().enabled).toBe(false);
            expect(
                personalizationsmarteditContextService.getCustomize().selectedCustomization
            ).toBe(null);
            expect(personalizationsmarteditContextService.getCustomize().selectedVariations).toBe(
                null
            );
            expect(personalizationsmarteditContextService.getCustomize().selectedComponents).toBe(
                null
            );
        });

        it('should properly set value', () => {
            // given
            const customize = personalizationsmarteditContextService.getCustomize();
            customize.selectedCustomization = {
                code: 'mockCustomization'
            };
            customize.selectedVariations = [
                {
                    code: 'mockVar1'
                },
                {
                    code: 'mockVar2'
                }
            ] as CustomizationVariation[];
            customize.enabled = true;
            // when
            personalizationsmarteditContextService.setCustomize(customize);
            // then
            expect(personalizationsmarteditContextService.getCustomize()).toBe(customize);
        });
    });

    describe('combinedView', () => {
        it('should be defined and initialized', () => {
            const customize = personalizationsmarteditContextService.getCustomize();
            expect(personalizationsmarteditContextService.getCombinedView()).toBeDefined();
            expect(personalizationsmarteditContextService.getCombinedView().enabled).toBe(false);
            expect(personalizationsmarteditContextService.getCombinedView().selectedItems).toBe(
                null
            );
            expect(personalizationsmarteditContextService.getCombinedView().customize).toEqual(
                customize
            );
            expect(
                personalizationsmarteditContextService.getCombinedView().customize
            ).toBeDefined();
            expect(
                personalizationsmarteditContextService.getCombinedView().customize
                    .selectedCustomization
            ).toBe(null);
            expect(
                personalizationsmarteditContextService.getCombinedView().customize
                    .selectedVariations
            ).toBe(null);
            expect(
                personalizationsmarteditContextService.getCombinedView().customize
                    .selectedComponents
            ).toBe(null);
        });

        it('should properly set value', () => {
            // given
            const combinedView = personalizationsmarteditContextService.getCombinedView();
            combinedView.enabled = true;
            combinedView.selectedItems = [{}, {}] as CombinedViewSelectItem[];
            // when
            personalizationsmarteditContextService.setCombinedView(combinedView);
            // then
            expect(personalizationsmarteditContextService.getCombinedView()).toBe(combinedView);
        });
    });

    describe('personalization', () => {
        it('should be defined and initialized', () => {
            expect(personalizationsmarteditContextService.getPersonalization()).toBeDefined();
            expect(personalizationsmarteditContextService.getPersonalization().enabled).toBe(false);
        });

        it('should properly set value', () => {
            // given
            const personalization = personalizationsmarteditContextService.getPersonalization();
            personalization.enabled = true;
            // when
            personalizationsmarteditContextService.setPersonalization(personalization);
            // then
            expect(personalizationsmarteditContextService.getPersonalization()).toBe(
                personalization
            );
        });
    });
});
