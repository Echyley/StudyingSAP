import { CustomizationDataFactory } from 'personalizationsmarteditcontainer/dataFactory/CustomizationDataFactory';
import { promiseUtils } from 'smarteditcommons';

describe('CustomizationDataFactory', () => {
    let personalizationsmarteditRestService: jasmine.SpyObj<any>;
    let personalizationSmartEditUtils: jasmine.SpyObj<any>;
    let customizationDataFactory: CustomizationDataFactory;

    beforeEach(() => {
        personalizationsmarteditRestService = jasmine.createSpyObj(
            'personalizationsmarteditRestService',
            ['getCustomizations']
        );
        personalizationsmarteditRestService.getCustomizations.and.returnValue(
            promiseUtils.defer().promise
        );
        personalizationSmartEditUtils = jasmine.createSpyObj('personalizationSmartEditUtils', [
            'uniqueArray'
        ]);

        customizationDataFactory = new CustomizationDataFactory(
            personalizationsmarteditRestService,
            personalizationSmartEditUtils
        );

        spyOn(customizationDataFactory, 'getCustomizations').and.callThrough();
    });

    describe('getCustomizations', () => {
        it('should be defined', () => {
            expect(customizationDataFactory.getCustomizations).toBeDefined();
        });

        it('should pass proper object to personalizationsmarteditRestService', () => {
            // when
            customizationDataFactory.updateData(
                {},
                () => 'mockSuccessCallbackFunction',
                () => 'mockErrorCallbackFunction'
            );
            customizationDataFactory.getCustomizations({});
            // then
            expect(personalizationsmarteditRestService.getCustomizations).toHaveBeenCalledWith({});
        });
    });

    describe('updateData', () => {
        it('should be defined', () => {
            expect(customizationDataFactory.updateData).toBeDefined();
        });

        // when
        it('should en up with calling getCustomizations', () => {
            customizationDataFactory.updateData(
                {},
                () => 'mockSuccessCallbackFunction',
                () => 'mockErrorCallbackFunction'
            );
            // then
            expect(customizationDataFactory.getCustomizations).toHaveBeenCalled();
        });
    });

    describe('refreshData', () => {
        it('should be defined', () => {
            expect(customizationDataFactory.refreshData).toBeDefined();
        });
    });

    describe('resetData', () => {
        it('should be defined', () => {
            expect(customizationDataFactory.resetData).toBeDefined();
        });
    });
});
