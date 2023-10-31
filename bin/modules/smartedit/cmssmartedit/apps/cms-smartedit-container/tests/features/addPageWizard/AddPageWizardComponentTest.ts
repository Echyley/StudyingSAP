/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    AddPageWizardComponent,
    ADD_PAGE_WIZARD_TITLE
} from 'cmssmarteditcontainer/components/pages/addPageWizard/components/AddPageWizardComponent';
import { PageType } from 'cmssmarteditcontainer/dao/PageTypeService';
import { IRestrictionType } from 'cmssmarteditcontainer/dao/RestrictionTypesRestService';
import { PageFacade } from 'cmssmarteditcontainer/facades';
import { RestrictionTypesService } from 'cmssmarteditcontainer/services/pageRestrictions/RestrictionTypesService';
import { PageBuilderFactory } from 'cmssmarteditcontainer/services/pages/PageBuilderFactory';
import { RestrictionsStepHandlerFactory } from 'cmssmarteditcontainer/services/pages/RestrictionsStepHandlerFactory';
import { RestrictionsService } from 'cmssmarteditcontainer/services/RestrictionsService';
import {
    functionsUtils,
    IConfirmationModalService,
    IExperienceService,
    IUriContext,
    SystemEventService,
    WizardService,
    ICMSPage,
    CMSPageTypes,
    PageTemplateType
} from 'smarteditcommons';
import { RestrictionsDTO } from '../../../dist/components/pages/pageWizard';

describe('AddPageWizardComponent', () => {
    let component: AddPageWizardComponent;
    let wizardManager: jasmine.SpyObj<WizardService>;
    let pageBuilderFactory: jasmine.SpyObj<PageBuilderFactory>;
    let restrictionsStepHandlerFactory: jasmine.SpyObj<RestrictionsStepHandlerFactory>;
    let experienceService: jasmine.SpyObj<IExperienceService>;
    let confirmationModalService: jasmine.SpyObj<IConfirmationModalService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let restrictionTypesService: jasmine.SpyObj<RestrictionTypesService>;
    let restrictionsService: jasmine.SpyObj<RestrictionsService>;
    let pageFacade: jasmine.SpyObj<PageFacade>;

    const mockRestrictionStepId = 'restrictionStepId';
    const mockUriContext = {
        CURRENT_CONTEXT_SITE_ID: 'siteId',
        CURRENT_CONTEXT_CATALOG: 'catalog',
        CURRENT_CONTEXT_CATALOG_VERSION: 'catalogVersion'
    } as IUriContext;
    const mockRestrictionType: IRestrictionType[] = [
        {
            id: 1,
            code: 'r1',
            name: { en: 'r1' }
        },
        {
            id: 2,
            code: 'r2',
            name: { en: 'r2' }
        }
    ];
    const mockSupportedTypes = ['code1', 'code2', 'code3'];
    let mockRestrictionStepHandler;
    let mockPageBuilder;

    beforeEach(() => {
        wizardManager = jasmine.createSpyObj<WizardService>('wizardManager', [
            'getCurrentStepId',
            'goToStepWithId'
        ]);
        pageBuilderFactory = jasmine.createSpyObj<PageBuilderFactory>('pageBuilderFactory', [
            'createPageBuilder'
        ]);
        restrictionsStepHandlerFactory = jasmine.createSpyObj<RestrictionsStepHandlerFactory>(
            'restrictionsStepHandlerFactory',
            ['createRestrictionsStepHandler']
        );
        experienceService = jasmine.createSpyObj<IExperienceService>('experienceService', [
            'loadExperience'
        ]);
        confirmationModalService = jasmine.createSpyObj<IConfirmationModalService>(
            'confirmationModalService',
            ['confirm']
        );
        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'publishAsync'
        ]);
        restrictionTypesService = jasmine.createSpyObj<RestrictionTypesService>(
            'restrictionTypesService',
            ['getRestrictionTypesByPageType']
        );
        restrictionsService = jasmine.createSpyObj<RestrictionsService>('restrictionsService', [
            'getSupportedRestrictionTypeCodes'
        ]);
        pageFacade = jasmine.createSpyObj<PageFacade>('pageFacade', ['createPage']);

        wizardManager.properties = {
            uriContext: mockUriContext
        } as any;
        mockRestrictionStepHandler = {
            getStepId: jasmine.createSpy().and.returnValue(mockRestrictionStepId),
            isStepValid: jasmine.createSpy()
        };
        mockPageBuilder = {
            displayConditionSelected: jasmine.createSpy(),
            pageTypeSelected: jasmine.createSpy(),
            pageTemplateSelected: jasmine.createSpy(),
            getPageTypeCode: jasmine.createSpy(),
            setRestrictions: jasmine.createSpy(),
            getPage: jasmine.createSpy(),
            getPageInfoStructure: jasmine.createSpy(),
            getPageRestrictions: jasmine.createSpy(),
            getTemplateUuid: jasmine.createSpy(),
            setPageUid: jasmine.createSpy()
        };

        restrictionsStepHandlerFactory.createRestrictionsStepHandler.and.returnValue(
            mockRestrictionStepHandler
        );
        pageBuilderFactory.createPageBuilder.and.returnValue(mockPageBuilder);
        restrictionTypesService.getRestrictionTypesByPageType.and.returnValue(
            Promise.resolve(mockRestrictionType)
        );
        restrictionsService.getSupportedRestrictionTypeCodes.and.returnValue(
            Promise.resolve(mockSupportedTypes)
        );

        component = new AddPageWizardComponent(
            wizardManager,
            pageBuilderFactory,
            restrictionsStepHandlerFactory,
            experienceService,
            confirmationModalService,
            systemEventService,
            restrictionTypesService,
            restrictionsService,
            pageFacade
        );
    });

    describe('initialize', () => {
        it('should initialize values when instance is created', () => {
            expect(component.uriContext).toEqual(mockUriContext);
            expect(component.callbacks).toEqual({});
            expect(component.restrictionStepProperties).toEqual({
                id: 'restrictionsStepId',
                name: 'se.cms.restrictions.editor.tab',
                title: ADD_PAGE_WIZARD_TITLE,
                component: jasmine.any(Function)
            });
            expect(component.restrictionsEditorFunctionBindings).toEqual({});
            expect(component.restrictionsStepHandler).toEqual(mockRestrictionStepHandler);
            expect(component.pageBuilder).toEqual(mockPageBuilder);
            expect(component.saveInProgress).toEqual(false);
            expect(component.typeChanged).toEqual(true);
            expect((component as any).ADD_PAGE_WIZARD_STEPS).toEqual({
                PAGE_TYPE: 'pageType',
                PAGE_TEMPLATE: 'pageTemplate',
                PAGE_DISPLAY_CONDITION: 'pageDisplayCondition',
                PAGE_INFO: 'pageInfo',
                PAGE_RESTRICTIONS: mockRestrictionStepId
            });
        });
    });

    describe('getWizardConfig', () => {
        it('should return a wizard config', () => {
            const config = component.getWizardConfig();

            expect(config).toEqual({
                isFormValid: jasmine.any(Function),
                onNext: jasmine.any(Function),
                onDone: jasmine.any(Function),
                onCancel: jasmine.any(Function),
                steps: [
                    {
                        id: 'pageType',
                        name: 'se.cms.addpagewizard.pagetype.tabname',
                        title: ADD_PAGE_WIZARD_TITLE,
                        component: jasmine.any(Function)
                    },
                    {
                        id: 'pageTemplate',
                        name: 'se.cms.addpagewizard.pagetemplate.tabname',
                        title: ADD_PAGE_WIZARD_TITLE,
                        component: jasmine.any(Function)
                    },
                    {
                        id: 'pageDisplayCondition',
                        name: 'se.cms.addpagewizard.pageconditions.tabname',
                        title: ADD_PAGE_WIZARD_TITLE,
                        component: jasmine.any(Function)
                    },
                    {
                        id: 'pageInfo',
                        name: 'se.cms.addpagewizard.pageinfo.tabname',
                        title: ADD_PAGE_WIZARD_TITLE,
                        component: jasmine.any(Function)
                    }
                ]
            });
        });
    });

    describe('getRestrictionTypes', () => {
        it('should call restriction types service and get restriction types by page type', async () => {
            mockPageBuilder.getPageTypeCode.and.returnValue('ContentPage');

            const actual = await component.getRestrictionTypes();

            expect(restrictionTypesService.getRestrictionTypesByPageType).toHaveBeenCalledWith(
                'ContentPage'
            );
            expect(actual).toEqual(mockRestrictionType);
        });
    });

    describe('getRestrictionTypes', () => {
        it('should call restriction types service and get restriction types by page type', async () => {
            const actual = await component.getSupportedRestrictionTypes();

            expect(restrictionsService.getSupportedRestrictionTypeCodes).toHaveBeenCalled();
            expect(actual).toEqual(mockSupportedTypes);
        });
    });

    describe('variationResult', () => {
        it('should set infoChanged flag to true and call displayConditionSelected from pagebuild', () => {
            const page = { uid: 'uid' } as ICMSPage;
            component.variationResult(page);

            expect(mockPageBuilder.displayConditionSelected).toHaveBeenCalledWith(page);
        });
    });

    describe('typeSelected', () => {
        it('should set infoChanged to true, typeChanged to true and call page build pageTypeSelected', () => {
            const pageType: PageType = {
                code: 'ContentPage' as CMSPageTypes,
                description: { en: 'desc' },
                name: { en: 'name' },
                type: 'type'
            };

            component.typeSelected(pageType);

            expect(component.typeChanged).toEqual(true);
            expect(mockPageBuilder.pageTypeSelected).toHaveBeenCalledWith(pageType);
        });
    });

    describe('templateSelected', () => {
        it('should call page build pageTemplateSelected', () => {
            const pageTemplate: PageTemplateType = {
                frontEndName: 'frontname',
                name: 'name',
                uid: 'uid',
                uuid: 'uuid',
                catalogVersion: 'catalogVersion'
            };

            component.templateSelected(pageTemplate);

            expect(mockPageBuilder.pageTemplateSelected).toHaveBeenCalledWith(pageTemplate);
        });
    });

    describe('getPageTypeCode', () => {
        it('should call page builder to get page type code', () => {
            component.getPageTypeCode();

            expect(mockPageBuilder.getPageTypeCode).toHaveBeenCalled();
        });
    });

    describe('restrictionsResult', () => {
        it('should call page builder setRestrictions and set restrictions provided in param', () => {
            const data = {
                onlyOneRestrictionMustApply: true,
                restrictionUuids: [],
                alwaysEnableSubmit: false
            } as RestrictionsDTO;
            component.restrictionsResult(data);

            expect(mockPageBuilder.setRestrictions).toHaveBeenCalledWith(true, []);
        });
    });

    describe('isRestrictionsActive', () => {
        it('GIVEN type has not changed THEN it should return true AND set typeChanged to false', () => {
            component.typeChanged = false;
            wizardManager.getCurrentStepId.and.returnValue('pageTypeId');

            const actual = component.isRestrictionsActive();

            expect(actual).toEqual(true);
            expect(component.typeChanged).toEqual(false);
        });

        it('GIVEN type has changed AND current step is restrictions step THEN it should return true AND set typeChanged to false', () => {
            component.typeChanged = true;
            wizardManager.getCurrentStepId.and.returnValue(mockRestrictionStepId);

            const actual = component.isRestrictionsActive();

            expect(actual).toEqual(true);
            expect(component.typeChanged).toEqual(false);
        });

        it('GIVEN type has not changed AND current step is not restrictions step THEN it should return false', () => {
            component.typeChanged = true;
            wizardManager.getCurrentStepId.and.returnValue('pageTypeId');

            const actual = component.isRestrictionsActive();

            expect(actual).toEqual(false);
            expect(component.typeChanged).toEqual(true);
        });
    });

    describe('getPageInfo', () => {
        it('should get page from page builder, add uri context to it and return it', () => {
            const page = { uid: 'page' } as ICMSPage;
            mockPageBuilder.getPage.and.returnValue(page);

            const actual = component.getPageInfo();

            expect(mockPageBuilder.getPage).toHaveBeenCalled();
            expect(actual).toEqual({
                ...page,
                uriContext: mockUriContext
            });
        });
    });

    describe('getPageRestrictions', () => {
        const restrictions = ['uuidOfRestrictionOne', 'uuidOfRestrictionTwo'] as string[];
        it('should return page restrictions', () => {
            mockPageBuilder.getPageRestrictions.and.returnValue(restrictions);

            const actual = component.getPageRestrictions();

            expect(actual).toEqual(restrictions);
        });

        it('WHEN page does not have restrictions field THEN it should retun an empty array', () => {
            mockPageBuilder.getPageRestrictions.and.returnValue([]);

            const actual = component.getPageRestrictions();

            expect(actual).toEqual([]);
        });
    });

    describe('getPageInfoStructure', () => {
        it('should get page info struction from page builder', () => {
            component.getPageInfoStructure();

            expect(mockPageBuilder.getPageInfoStructure).toHaveBeenCalled();
        });
    });

    describe('isPageInfoActive', () => {
        it('GIVEN current step is info step THEN it should return true', () => {
            wizardManager.getCurrentStepId.and.returnValue('pageInfo');

            const actual = component.isPageInfoActive();

            expect(actual).toEqual(true);
        });

        it('GIVEN current step is not info step THEN it should return false', () => {
            wizardManager.getCurrentStepId.and.returnValue('pageTypeId');

            const actual = component.isPageInfoActive();

            expect(actual).toEqual(false);
        });
    });

    describe('onNext', () => {
        it('should return Promise that resolves to true', async () => {
            const actual = await component.onNext();

            expect(actual).toEqual(true);
        });
    });

    describe('onCancel', () => {
        it('should return Promise from confirmationModal', async () => {
            confirmationModalService.confirm.and.returnValue(Promise.resolve(true));

            const actual = await component.onCancel();

            expect(actual).toEqual(true);
        });
    });

    describe('isFormValid', () => {
        describe('PageType step', () => {
            it('should return true when pageBuilder returns page type code', () => {
                mockPageBuilder.getPageTypeCode.and.returnValue('ContentPage');

                const actual = component.isFormValid('pageType');

                expect(actual).toEqual(true);
            });
        });

        describe('PageTemplate step', () => {
            it('should return true when pageBuilder returns page tempalte uuid', () => {
                mockPageBuilder.getTemplateUuid.and.returnValue('uuid');

                const actual = component.isFormValid('pageTemplate');

                expect(actual).toEqual(true);
            });
        });

        describe('PageDisplayCondition step', () => {
            it('should return true', () => {
                expect(component.isFormValid('pageDisplayCondition')).toEqual(true);
            });
        });

        describe('PageInfo step', () => {
            it('WHEN save is not in progress and isDirtyPageInfo and isValidPageInfo from callbacks are defined and they both return true THEN it should return true', () => {
                component.callbacks = {
                    isDirtyPageInfo: () => true,
                    isValidPageInfo: () => true
                };
                component.saveInProgress = false;

                const actual = component.isFormValid('pageInfo');

                expect(actual).toEqual(true);
            });

            it('WHEN save is in progress THEN it should return false', () => {
                component.callbacks = {
                    isDirtyPageInfo: () => true,
                    isValidPageInfo: () => true
                };
                component.saveInProgress = true;

                const actual = component.isFormValid('pageInfo');

                expect(actual).toEqual(false);
            });

            it('WHEN isDirtyPageInfo is not defined THEN it should return false', () => {
                component.callbacks = {
                    isValidPageInfo: () => true
                };
                component.saveInProgress = false;

                const actual = component.isFormValid('pageInfo');

                expect(actual).toEqual(false);
            });

            it('WHEN isValidPageInfo is defined but returns false THEN it should return false', () => {
                component.callbacks = {
                    isDirtyPageInfo: () => true,
                    isValidPageInfo: () => false
                };
                component.saveInProgress = false;

                const actual = component.isFormValid('pageInfo');

                expect(actual).toEqual(false);
            });
        });

        describe('PageRestrictions step', () => {
            it('WHEN save is not in progress and restrictionsStepHandler confirms that step is valid THEN it should return true ', () => {
                mockRestrictionStepHandler.isStepValid.and.returnValue(true);

                const actual = component.isFormValid(mockRestrictionStepId);

                expect(actual).toEqual(true);
            });

            it('WHEN save is in progress THEN it should return false', () => {
                component.saveInProgress = true;
                mockRestrictionStepHandler.isStepValid.and.returnValue(true);

                const actual = component.isFormValid(mockRestrictionStepId);

                expect(actual).toEqual(false);
            });
            it('WHEN save is not in progress AND restriction step is not valid THEN it should return false', () => {
                component.saveInProgress = false;
                mockRestrictionStepHandler.isStepValid.and.returnValue(false);

                const actual = component.isFormValid(mockRestrictionStepId);

                expect(actual).toEqual(false);
            });
        });
    });

    describe('onDone', () => {
        beforeEach(() => {
            component.callbacks = {
                savePageInfo: jasmine.createSpy().and.returnValue(
                    Promise.resolve({
                        uid: 'cmsUid'
                    })
                )
            };
        });

        describe('WHEN creating page rejects', () => {
            it('AND there are no errors with "restrictions" in subject THEN it should call wizard manager to go to page info step and', async () => {
                pageFacade.createPage.and.returnValue(
                    Promise.reject({
                        error: {
                            errors: []
                        }
                    })
                );
                try {
                    await component.onDone();

                    functionsUtils.assertFail();
                } catch {
                    expect(component.saveInProgress).toEqual(false);
                    expect(systemEventService.publishAsync).toHaveBeenCalledWith(
                        'UnrelatedValidationMessagesEvent',
                        {
                            messages: []
                        }
                    );
                    expect(wizardManager.goToStepWithId).toHaveBeenCalledWith('pageInfo');
                }
            });

            it('AND there are errors with "restrictions" in subject THEN it should call wizard manager to go to page info step and', async () => {
                pageFacade.createPage.and.returnValue(
                    Promise.reject({
                        error: {
                            errors: [{ subject: 'restrictions.error' }]
                        }
                    })
                );
                try {
                    await component.onDone();

                    functionsUtils.assertFail();
                } catch {
                    expect(component.saveInProgress).toEqual(false);
                    expect(systemEventService.publishAsync).toHaveBeenCalledWith(
                        'UnrelatedValidationMessagesEvent',
                        {
                            messages: [{ subject: 'restrictions.error' }]
                        }
                    );
                    expect(wizardManager.goToStepWithId).not.toHaveBeenCalled();
                }
            });
        });

        describe('WHEN creating page resolves', () => {
            it('AND created page type code is Email page THEN it should publish an event', async () => {
                pageFacade.createPage.and.returnValue(
                    Promise.resolve<any>({ uid: 'newUid', typeCode: 'EmailPage' })
                );

                await component.onDone();

                expect(mockPageBuilder.setPageUid).toHaveBeenCalledWith('newUid');
                expect(systemEventService.publishAsync).toHaveBeenCalledWith(
                    'EVENT_CONTENT_CATALOG_UPDATE',
                    {
                        uid: 'newUid',
                        typeCode: 'EmailPage'
                    }
                );
                expect(experienceService.loadExperience).not.toHaveBeenCalled();
            });

            it('THEN it should load experience', async () => {
                pageFacade.createPage.and.returnValue(
                    Promise.resolve<any>({ uid: 'newUid', typeCode: 'OtherPage' })
                );
                mockPageBuilder.getPage.and.returnValue({ uid: 'pageUid' });

                await component.onDone();

                expect(mockPageBuilder.setPageUid).toHaveBeenCalledWith('newUid');
                expect(systemEventService.publishAsync).not.toHaveBeenCalled();
                expect(experienceService.loadExperience).toHaveBeenCalledWith({
                    siteId: 'siteId',
                    catalogId: 'catalog',
                    catalogVersion: 'catalogVersion',
                    pageId: 'pageUid'
                });
            });
        });
    });
});
