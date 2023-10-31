import {
    PersonalizationsmarteditContextUtils,
    Customize,
    CustomizationVariation,
    CombinedViewSelectItem
} from 'personalizationcommons';
import { PersonalizationsmarteditContextService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuter';
import { PersonalizationsmarteditContextServiceProxy } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuterProxy';
import { IExperience } from 'smarteditcommons';
import { coreAnnotationsHelper } from 'testhelpers';
describe('personalizationsmarteditContextService', () => {
    // ======= Injected mocks =======
    let sharedDataService: jasmine.SpyObj<any>;
    let loadConfigManagerService: jasmine.SpyObj<any>;

    // Service being tested
    let personalizationsmarteditContextService: PersonalizationsmarteditContextService;

    let personalizationsmarteditContextUtils: PersonalizationsmarteditContextUtils;
    let personalizationsmarteditContextServiceProxy: PersonalizationsmarteditContextServiceProxy;

    const mockConfig = {
        test: 'test'
    };

    beforeEach(() => {
        coreAnnotationsHelper.init();

        sharedDataService = jasmine.createSpyObj('sharedDataService', ['get']);
        loadConfigManagerService = jasmine.createSpyObj('loadConfigManagerService', [
            'loadAsObject'
        ]);
        personalizationsmarteditContextUtils = new PersonalizationsmarteditContextUtils();

        sharedDataService.get.and.callFake(function () {
            return Promise.resolve(mockConfig);
        });

        loadConfigManagerService.loadAsObject.and.callFake(function () {
            return Promise.resolve(mockConfig);
        });

        personalizationsmarteditContextServiceProxy = new PersonalizationsmarteditContextServiceProxy();

        personalizationsmarteditContextService = new PersonalizationsmarteditContextService(
            sharedDataService,
            loadConfigManagerService,
            personalizationsmarteditContextServiceProxy,
            personalizationsmarteditContextUtils
        );
        personalizationsmarteditContextServiceProxy = personalizationsmarteditContextService.getContexServiceProxy();

        // Create spy objects
        spyOn(personalizationsmarteditContextService, 'refreshExperienceData').and.callThrough();
        spyOn(personalizationsmarteditContextService, 'refreshConfigurationData').and.callThrough();
        spyOn(personalizationsmarteditContextServiceProxy, 'setSeData').and.callThrough();
        spyOn(personalizationsmarteditContextServiceProxy, 'setCustomize').and.callThrough();
        spyOn(personalizationsmarteditContextServiceProxy, 'setCombinedView').and.callThrough();
        spyOn(personalizationsmarteditContextServiceProxy, 'setPersonalization').and.callThrough();
    });

    describe('applySynchronization', () => {
        it('after call all objects in contex service are set properly', () => {
            // After object creation properties should have default values
            expect(personalizationsmarteditContextService.getSeData().pageId).toBe(null);
            expect(personalizationsmarteditContextService.getSeData().seExperienceData).toBe(null);
            expect(personalizationsmarteditContextService.getSeData().seConfigurationData).toBe(
                null
            );
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
            expect(personalizationsmarteditContextService.getCombinedView().enabled).toBe(false);
            expect(personalizationsmarteditContextService.getCombinedView().selectedItems).toBe(
                null
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
            expect(personalizationsmarteditContextService.getPersonalization().enabled).toBe(false);

            // Set some mock properties
            const customize: Customize = {
                enabled: false,
                selectedVariations: [
                    { code: 'mockVariation', catalog: null, catalogVersion: null }
                ],
                selectedComponents: [''],
                selectedCustomization: {
                    code: 'mockCustomization'
                }
            };
            const seData = {
                enabled: false,
                seExperienceData: {} as IExperience,
                seConfigurationData: [''],
                pageId: 'mainpage'
            };
            const combinedView = {
                enabled: true,
                selectedItems: [{ customization: null, id: null, variation: null }],
                customize: new Customize()
            };
            const personalization = {
                enabled: true
            };

            personalizationsmarteditContextService.setCustomize(customize);
            personalizationsmarteditContextService.setSeData(seData);
            personalizationsmarteditContextService.setCombinedView(combinedView);
            personalizationsmarteditContextService.setPersonalization(personalization);

            // Call method and run digest cycle
            personalizationsmarteditContextService.applySynchronization();

            // Test if methods have been called properly
            expect(personalizationsmarteditContextService.refreshExperienceData).toHaveBeenCalled();
            expect(
                personalizationsmarteditContextService.refreshConfigurationData
            ).toHaveBeenCalled();

            expect(personalizationsmarteditContextServiceProxy.setCustomize).toHaveBeenCalledWith(
                customize
            );
            expect(personalizationsmarteditContextServiceProxy.setSeData).toHaveBeenCalledWith(
                seData
            );
            expect(
                personalizationsmarteditContextServiceProxy.setCombinedView
            ).toHaveBeenCalledWith(combinedView);
            expect(
                personalizationsmarteditContextServiceProxy.setPersonalization
            ).toHaveBeenCalledWith(personalization);

            // Test if properties are set properly
            expect(personalizationsmarteditContextService.getCustomize()).toBe(customize);
            expect(personalizationsmarteditContextService.getSeData()).toBe(seData);
            expect(personalizationsmarteditContextService.getCombinedView()).toBe(combinedView);
            expect(personalizationsmarteditContextService.getPersonalization()).toBe(
                personalization
            );
        });
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
                mock: 'mockValue',
                catalogDescriptor: undefined,
                siteDescriptor: undefined,
                productCatalogVersions: undefined,
                time: undefined
            };
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
