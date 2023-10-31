/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { DisplayConditionsFacade } from 'cmssmarteditcontainer/facades/DisplayConditionsFacade';
import { DisplayConditionsEditorModel } from 'cmssmarteditcontainer/services/pageDisplayConditions';

describe('DisplayConditionsEditorModel', () => {
    const MOCK_PRIMARY_PAGE_INFO = {
        pageName: 'Some Page Name',
        pageType: 'somePageType',
        isPrimary: true
    };

    const MOCK_VARIATION_PAGE_INFO = {
        pageName: 'Some Page Name',
        pageType: 'somePageType',
        isPrimary: false
    };

    const MOCK_VARIATIONS = [
        {
            pageName: 'Some Variation Page Name',
            creationDate: new Date().toString(),
            restrictions: 1
        },
        {
            pageName: 'Some Other Variation Page Name',
            creationDate: new Date().toString(),
            restrictions: 2
        }
    ];

    const MOCK_PRIMARY_PAGE = {
        uid: 'somePrimaryPage',
        uuid: 'somePrimaryPage',
        name: 'Some Primary Page',
        label: 'some-primary-page'
    };

    let service: DisplayConditionsEditorModel;
    let serviceAny: any;
    let displayConditionsFacade: jasmine.SpyObj<DisplayConditionsFacade>;

    beforeEach(() => {
        displayConditionsFacade = jasmine.createSpyObj<DisplayConditionsFacade>(
            'displayConditionsFacade',
            [
                'getVariationsForPageUid',
                'getPrimaryPageForVariationPage',
                'getPageInfoForPageUid',
                'updatePage'
            ]
        );

        service = new DisplayConditionsEditorModel(displayConditionsFacade);
        serviceAny = service;
    });

    describe('initModel', () => {
        beforeEach(() => {
            serviceAny.initModelForPrimary = jasmine.createSpy('initModelForPrimary');
            serviceAny.initModelForVariation = jasmine.createSpy('initModelForVariation');
        });

        it('should put the page name, page, type, and is primary values on the model scope', () => {
            displayConditionsFacade.getPageInfoForPageUid.and.returnValue(
                Promise.resolve(MOCK_PRIMARY_PAGE_INFO)
            );

            service.initModel('somePageUid').then(() => {
                expect(service.pageName).toBe('Some Page Name');
                expect(service.pageType).toBe('somePageType');
                expect(service.isPrimary).toBe(true);
            });
        });

        it('should delegate to initModelForPrimary if the page is primary', () => {
            displayConditionsFacade.getPageInfoForPageUid.and.returnValue(
                Promise.resolve(MOCK_PRIMARY_PAGE_INFO)
            );
            service.initModel('somePageUid').then(() => {
                expect(serviceAny.initModelForPrimary).toHaveBeenCalledWith('somePageUid');
            });
        });

        it('should delegate to initModelForVariation if the page is variation', () => {
            displayConditionsFacade.getPageInfoForPageUid.and.returnValue(
                Promise.resolve(MOCK_VARIATION_PAGE_INFO)
            );
            service.initModel('somePageUid').then(() => {
                expect(serviceAny.initModelForVariation).toHaveBeenCalledWith('somePageUid');
            });
        });
    });

    describe('initModelForPrimary', () => {
        beforeEach(() => {
            displayConditionsFacade.getVariationsForPageUid.and.returnValue(
                Promise.resolve(MOCK_VARIATIONS)
            );
        });

        it('should put the variations on the model scope', () => {
            serviceAny.initModelForPrimary('somePageUid').then(() => {
                expect(service.variations).toEqual(MOCK_VARIATIONS);
            });
        });
    });

    describe('initModelForVariation', () => {
        beforeEach(() => {
            displayConditionsFacade.getPrimaryPageForVariationPage.and.returnValue(
                Promise.resolve(MOCK_PRIMARY_PAGE)
            );
        });

        it('should put the associated primary page on the model scope', () => {
            serviceAny.initModelForVariation('somePageUid').then(() => {
                expect(service.associatedPrimaryPage).toEqual(MOCK_PRIMARY_PAGE);
                expect(service.originalPrimaryPage).toEqual(MOCK_PRIMARY_PAGE);
            });
        });
    });
});
