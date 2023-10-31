/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ClonePageBuilderFactory,
    ClonePageWizardComponent,
    CLONE_PAGE_WIZARD_TITLE
} from 'cmssmarteditcontainer/components/pages/clonePageWizard';
import { RestrictionCMSItem } from 'cmssmarteditcontainer/components/restrictions/types';
import { IRestrictionType } from 'cmssmarteditcontainer/dao/RestrictionTypesRestService';
import { PageFacade } from 'cmssmarteditcontainer/facades';
import { ClonePageAlertService } from 'cmssmarteditcontainer/services/actionableAlert/ClonePageAlertService';
import { RestrictionTypesService } from 'cmssmarteditcontainer/services/pageRestrictions/RestrictionTypesService';
import { RestrictionsStepHandlerFactory } from 'cmssmarteditcontainer/services/pages/RestrictionsStepHandlerFactory';
import { RestrictionsService } from 'cmssmarteditcontainer/services/RestrictionsService';
import {
    functionsUtils,
    IAlertService,
    ICatalogVersion,
    IConfirmationModalService,
    IExperience,
    IExperienceService,
    ISharedDataService,
    IUriContext,
    SystemEventService,
    WizardService,
    ICMSPage,
    ISyncPollingService
} from 'smarteditcommons';
import { RestrictionsDTO } from '../../../../dist/components/pages/pageWizard';

describe('ClonePageWizardComponent', () => {
    let component: ClonePageWizardComponent;
    let componentAny: any;
    let wizardManager: jasmine.SpyObj<WizardService>;
    let clonePageBuilderFactory: jasmine.SpyObj<ClonePageBuilderFactory>;
    let restrictionsStepHandlerFactory: jasmine.SpyObj<RestrictionsStepHandlerFactory>;
    let experienceService: jasmine.SpyObj<IExperienceService>;
    let confirmationModalService: jasmine.SpyObj<IConfirmationModalService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let restrictionTypesService: jasmine.SpyObj<RestrictionTypesService>;
    let restrictionsService: jasmine.SpyObj<RestrictionsService>;
    let sharedDataService: jasmine.SpyObj<ISharedDataService>;
    let clonePageAlertService: jasmine.SpyObj<ClonePageAlertService>;
    let alertService: jasmine.SpyObj<IAlertService>;
    let pageFacade: jasmine.SpyObj<PageFacade>;
    let syncPollingService: jasmine.SpyObj<ISyncPollingService>;

    const mockRestrictionStepId = 'restrictionsStepId';
    const mockBasePageUUId = 'basePageUUID';
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
    const mockRestrictions = [
        {
            uid: 'restrictionId1',
            uuid: 'restrictionUuid1',
            name: '',
            type: null,
            typeCode: '',
            description: '',
            language: null
        },
        {
            uid: 'restrictionId2',
            uuid: 'restrictionUuid2',
            name: '',
            type: null,
            typeCode: '',
            description: '',
            language: null
        }
    ] as unknown as RestrictionCMSItem[];
    const mockExperience = {
        catalogDescriptor: {
            catalogVersionUuid: 'catalogUid'
        }
    } as unknown as IExperience;
    let mockRestrictionStepHandler;
    let mockPageBuilder;

    beforeEach(() => {
        wizardManager = jasmine.createSpyObj<WizardService>('wizardManager', [
            'getCurrentStepId',
            'goToStepWithId'
        ]);
        clonePageBuilderFactory = jasmine.createSpyObj<ClonePageBuilderFactory>(
            'clonePageBuilderFactory',
            ['createClonePageBuilder']
        );
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
        sharedDataService = jasmine.createSpyObj<ISharedDataService>('sharedDataService', ['get']);
        clonePageAlertService = jasmine.createSpyObj<ClonePageAlertService>(
            'clonePageAlertService',
            ['displayClonePageAlert']
        );
        alertService = jasmine.createSpyObj<IAlertService>('alertService', ['showSuccess']);
        pageFacade = jasmine.createSpyObj<PageFacade>('pageFacade', ['createPageForSite']);
        syncPollingService = jasmine.createSpyObj<ISyncPollingService>('syncPollingService', [
            'getSyncStatus'
        ]);

        wizardManager.properties = {
            uriContext: mockUriContext,
            basePageUUID: mockBasePageUUId
        } as any;
        mockRestrictionStepHandler = {
            getStepId: jasmine.createSpy().and.returnValue(mockRestrictionStepId),
            isStepValid: jasmine.createSpy()
        };
        mockPageBuilder = {
            init: jasmine.createSpy(),
            displayConditionSelected: jasmine.createSpy(),
            getPageLabel: jasmine.createSpy(),
            getPageTypeCode: jasmine.createSpy(),
            getPageTemplate: jasmine.createSpy(),
            getPageInfo: jasmine.createSpy(),
            getBasePageInfo: jasmine.createSpy(),
            getPageRestrictions: jasmine.createSpy(),
            onTargetCatalogVersionSelected: jasmine.createSpy(),
            componentCloneOptionSelected: jasmine.createSpy(),
            getPageInfoStructure: jasmine.createSpy(),
            restrictionsSelected: jasmine.createSpy(),
            getTargetCatalogVersion: jasmine.createSpy(),
            isBasePageInfoAvailable: jasmine.createSpy(),
            getPageProperties: jasmine.createSpy(),
            getComponentCloneOption: jasmine.createSpy()
        };

        restrictionsStepHandlerFactory.createRestrictionsStepHandler.and.returnValue(
            mockRestrictionStepHandler
        );
        clonePageBuilderFactory.createClonePageBuilder.and.returnValue(mockPageBuilder);
        restrictionTypesService.getRestrictionTypesByPageType.and.returnValue(
            Promise.resolve(mockRestrictionType)
        );
        restrictionsService.getSupportedRestrictionTypeCodes.and.returnValue(
            Promise.resolve(mockSupportedTypes)
        );

        component = new ClonePageWizardComponent(
            wizardManager,
            clonePageBuilderFactory,
            restrictionsStepHandlerFactory,
            experienceService,
            confirmationModalService,
            systemEventService,
            restrictionTypesService,
            restrictionsService,
            sharedDataService,
            clonePageAlertService,
            alertService,
            pageFacade,
            syncPollingService
        );
        componentAny = component;
    });

    describe('initialize', () => {
        it('should initialize values when instance is created', () => {
            expect(component.uriContext).toEqual(mockUriContext);
            expect(component.basePageUUID).toEqual(mockBasePageUUId);
            expect(component.callbacks).toEqual({});
            expect(component.restrictionStepProperties).toEqual({
                id: 'restrictionsStepId',
                name: 'se.cms.restrictions.editor.tab',
                title: CLONE_PAGE_WIZARD_TITLE,
                component: jasmine.any(Function)
            });
            expect(component.restrictionsEditorFunctionBindings).toEqual({});
            expect(componentAny.restrictionsStepHandler).toEqual(mockRestrictionStepHandler);
            expect(componentAny.pageBuilder).toEqual(mockPageBuilder);
            expect(component.infoChanged).toEqual(true);
            expect(componentAny.cloneInProgress).toEqual(false);
            expect(component.typeChanged).toEqual(true);
            expect(componentAny.CLONE_PAGE_WIZARD_STEPS).toEqual({
                PAGE_CLONE_OPTIONS: 'cloneOptions',
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
                        id: 'cloneOptions',
                        name: 'se.cms.clonepagewizard.pagecloneoptions.tabname',
                        title: CLONE_PAGE_WIZARD_TITLE,
                        component: jasmine.any(Function)
                    },
                    {
                        id: 'pageInfo',
                        name: 'se.cms.clonepagewizard.pageinfo.tabname',
                        title: CLONE_PAGE_WIZARD_TITLE,
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

            expect(component.infoChanged).toEqual(true);
            expect(mockPageBuilder.displayConditionSelected).toHaveBeenCalledWith(page);
        });
    });

    describe('getPageTypeCode', () => {
        it('should return getPageTypeCode from pageBuilder', () => {
            component.getPageTypeCode();

            expect(mockPageBuilder.getPageTypeCode).toHaveBeenCalled();
        });
    });

    describe('getPageLabel', () => {
        it('should return getPageLabel from pageBuilder', () => {
            component.getPageLabel();

            expect(mockPageBuilder.getPageLabel).toHaveBeenCalled();
        });
    });

    describe('getPageTypeCode', () => {
        it('should return getPageTypeCode from pageBuilder', () => {
            component.getPageTypeCode();

            expect(mockPageBuilder.getPageTypeCode).toHaveBeenCalled();
        });
    });

    describe('getPageInfo', () => {
        it('should return page with uriContext', () => {
            mockPageBuilder.getPageInfo.and.returnValue({ uid: 'uid' });

            const actual = component.getPageInfo();

            expect(actual).toEqual({
                uid: 'uid',
                uriContext: mockUriContext
            } as unknown as ICMSPage);
        });
    });

    describe('getBasePageInfo', () => {
        it('should return base page with uriContext', () => {
            mockPageBuilder.getBasePageInfo.and.returnValue({ uid: 'uid' });

            const actual = component.getBasePageInfo();

            expect(actual).toEqual({
                uid: 'uid',
                uriContext: mockUriContext
            } as unknown as ICMSPage);
        });
    });

    describe('getPageRestrictions', () => {
        it('should return getPageRestrictions from pageBuilder', () => {
            component.getPageRestrictions();

            expect(mockPageBuilder.getPageRestrictions).toHaveBeenCalled();
        });
    });

    describe('onTargetCatalogVersionSelected', () => {
        it('should call onTargetCatalogVersionSelected in pageBuilder', () => {
            component.onTargetCatalogVersionSelected({
                active: true,
                uuid: 'uuid',
                version: 'online',
                pageDisplayConditions: []
            });

            expect(mockPageBuilder.onTargetCatalogVersionSelected).toHaveBeenCalledWith({
                active: true,
                uuid: 'uuid',
                version: 'online',
                pageDisplayConditions: []
            });
        });
    });

    describe('triggerUpdateCloneOptionResult', () => {
        it('should call componentCloneOptionSelected in pageBuilder', () => {
            component.triggerUpdateCloneOptionResult('clone');

            expect(mockPageBuilder.componentCloneOptionSelected).toHaveBeenCalledWith('clone');
        });
    });

    describe('getPageInfoStructure', () => {
        it('should return getPageInfoStructure from pageBuilder', () => {
            component.getPageInfoStructure();

            expect(mockPageBuilder.getPageInfoStructure).toHaveBeenCalled();
        });
    });

    describe('restrictionsResult', () => {
        it('should call page builder restrictionsSelected and set restrictions provided in param', () => {
            const data = {
                onlyOneRestrictionMustApply: true,
                restrictionUuids: [],
                alwaysEnableSubmit: false
            } as RestrictionsDTO;
            component.restrictionsResult(data);

            expect(mockPageBuilder.restrictionsSelected).toHaveBeenCalledWith(true, []);
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

    describe('isPageInfoActive', () => {
        it('GIVEN info has not changed THEN it should return true AND set typeChanged to false', () => {
            component.infoChanged = false;
            wizardManager.getCurrentStepId.and.returnValue('pageTypeId');

            const actual = component.isPageInfoActive();

            expect(actual).toEqual(true);
            expect(component.infoChanged).toEqual(false);
        });

        it('GIVEN info has changed AND current step is info step THEN it should return true AND set infoChanged to false', () => {
            component.infoChanged = true;
            wizardManager.getCurrentStepId.and.returnValue('pageInfo');

            const actual = component.isPageInfoActive();

            expect(actual).toEqual(true);
            expect(component.infoChanged).toEqual(false);
        });

        it('GIVEN info has not changed AND current step is not info step THEN it should return false', () => {
            component.infoChanged = true;
            wizardManager.getCurrentStepId.and.returnValue('pageTypeId');

            const actual = component.isPageInfoActive();

            expect(actual).toEqual(false);
            expect(component.typeChanged).toEqual(true);
        });
    });

    describe('getTargetCatalogVersion', () => {
        it('should return getTargetCatalogVersion from pageBuilder', () => {
            component.getTargetCatalogVersion();

            expect(mockPageBuilder.getTargetCatalogVersion).toHaveBeenCalled();
        });
    });

    describe('isBasePageInfoAvailable', () => {
        it('should return isBasePageInfoAvailable from pageBuilder', () => {
            component.isBasePageInfoAvailable();

            expect(mockPageBuilder.isBasePageInfoAvailable).toHaveBeenCalled();
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
        it('should return false when given step is not recognized', () => {
            const actual = component.isFormValid('notknownstep');

            expect(actual).toEqual(false);
        });

        describe('PageCloneOptions step', () => {
            it('should return true when basePageInfo is available', () => {
                mockPageBuilder.isBasePageInfoAvailable.and.returnValue(true);

                const actual = component.isFormValid('cloneOptions');

                expect(actual).toEqual(true);
            });
        });

        describe('PageInfo step', () => {
            it('WHEN clone is not in progress and isValidPageInfo from callbacks are defined and they both return true THEN it should return true', () => {
                component.callbacks = {
                    isValidPageInfo: () => true
                };
                componentAny.cloneInProgress = false;

                const actual = component.isFormValid('pageInfo');

                expect(actual).toEqual(true);
            });

            it('WHEN clone is in progress THEN it should return false', () => {
                component.callbacks = {
                    isValidPageInfo: () => true
                };
                componentAny.cloneInProgress = true;

                const actual = component.isFormValid('pageInfo');

                expect(actual).toEqual(false);
            });

            it('WHEN isValidPageInfo is not defined THEN it should return false', () => {
                component.callbacks = {};
                componentAny.cloneInProgress = false;

                const actual = component.isFormValid('pageInfo');

                expect(actual).toEqual(false);
            });

            it('WHEN isValidPageInfo is defined but returns false THEN it should return false', () => {
                component.callbacks = {
                    isValidPageInfo: () => false
                };
                componentAny.cloneInProgress = false;

                const actual = component.isFormValid('pageInfo');

                expect(actual).toEqual(false);
            });
        });

        describe('PageRestrictions step', () => {
            it('WHEN clone is not in progress and there are restrictions THEN it should return true ', () => {
                mockPageBuilder.getPageRestrictions.and.returnValue(mockRestrictions);

                const actual = component.isFormValid(mockRestrictionStepId);

                expect(actual).toEqual(true);
            });

            it('WHEN clone is in progress THEN it should return false', () => {
                componentAny.cloneInProgress = true;
                mockPageBuilder.getPageRestrictions.and.returnValue(mockRestrictions);

                const actual = component.isFormValid(mockRestrictionStepId);

                expect(actual).toEqual(false);
            });
            it('WHEN clone is not in progress AND there are no restrictions THEN it should return false', () => {
                componentAny.cloneInProgress = false;
                mockPageBuilder.getPageRestrictions.and.returnValue([]);

                const actual = component.isFormValid(mockRestrictionStepId);

                expect(actual).toEqual(false);
            });
        });
    });

    describe('OnDone', () => {
        const mockCatalogVersion = {
            siteId: 'siteId',
            catalogId: 'catalogId',
            version: 'version'
        } as unknown as ICatalogVersion;

        beforeEach(() => {
            component.callbacks = {
                savePageInfo: jasmine.createSpy().and.returnValue(
                    Promise.resolve({
                        uid: 'cmsUid'
                    })
                )
            };
            sharedDataService.get.and.returnValue(Promise.resolve(mockExperience));
            spyOn(componentAny, 'preparePagePayload');
            spyOn(componentAny, 'createPage');
        });

        it('WHEN save page throws error THEN it should reject whole operation', async () => {
            component.callbacks = {
                savePageInfo: jasmine.createSpy().and.returnValue(Promise.reject())
            };

            try {
                await component.onDone();

                functionsUtils.assertFail();
            } catch {
                expect(componentAny.cloneInProgress).toEqual(false);
            }
        });

        it('WHEN pagebuilder has tagetCatalogVersion THEN it should extend payload with siteId, catalogId and version', async () => {
            componentAny.preparePagePayload.and.returnValue({
                cloneComponents: true,
                itemtype: 'ContentPage'
            });
            mockPageBuilder.getTargetCatalogVersion.and.returnValue(mockCatalogVersion);

            await component.onDone();

            expect(sharedDataService.get).toHaveBeenCalledWith('experience');
            expect(componentAny.preparePagePayload).toHaveBeenCalledWith({ uid: 'cmsUid' });
            expect(componentAny.createPage).toHaveBeenCalledWith(
                {
                    cloneComponents: true,
                    itemtype: 'ContentPage',
                    siteId: 'siteId',
                    catalogId: 'catalogId',
                    version: 'version'
                },
                mockExperience
            );
        });

        it('WHEN pagebuilder does not have tagetCatalogVersion THEN it should extend payload with siteId, catalogId and version', async () => {
            componentAny.preparePagePayload.and.returnValue({
                cloneComponents: true,
                itemtype: 'ContentPage'
            });
            mockPageBuilder.getTargetCatalogVersion.and.returnValue(null);

            await component.onDone();

            expect(sharedDataService.get).toHaveBeenCalledWith('experience');
            expect(componentAny.preparePagePayload).toHaveBeenCalledWith({ uid: 'cmsUid' });
            expect(componentAny.createPage).toHaveBeenCalledWith(
                {
                    cloneComponents: true,
                    itemtype: 'ContentPage'
                },
                mockExperience
            );
        });
    });

    describe('createPage', () => {
        describe('WHEN createPageForSite rejects', () => {
            it('AND there are no errors with "restrictions" in subject THEN it should call wizard manager to go to page info step and', async () => {
                pageFacade.createPageForSite.and.returnValue(
                    Promise.reject({
                        error: {
                            errors: []
                        }
                    })
                );
                try {
                    await componentAny.createPage({}, mockExperience);

                    functionsUtils.assertFail();
                } catch {
                    expect(componentAny.cloneInProgress).toEqual(false);
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
                pageFacade.createPageForSite.and.returnValue(
                    Promise.reject({
                        error: {
                            errors: [{ subject: 'restrictions.error' }]
                        }
                    })
                );
                try {
                    await componentAny.createPage({}, mockExperience);

                    functionsUtils.assertFail();
                } catch {
                    expect(componentAny.cloneInProgress).toEqual(false);
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

        describe('WHEN createPageForSite resolves', () => {
            beforeEach(() => {
                pageFacade.createPageForSite.and.returnValue(
                    Promise.resolve<any>({
                        catalogVersion: 'catalogUid',
                        uid: 'newUid'
                    })
                );
            });

            it('AND catalogVersion from experience is equal to the one from create page response THEN it should load experience', async () => {
                const payload: any = {
                    siteId: 'siteId',
                    pageUuid: 'pageUuid',
                    catalogId: 'catalogId',
                    version: 'version'
                };

                await componentAny.createPage(payload, mockExperience);

                expect(pageFacade.createPageForSite).toHaveBeenCalledWith(payload, 'siteId');
                expect(syncPollingService.getSyncStatus).toHaveBeenCalledWith(
                    'pageUuid',
                    mockUriContext,
                    true
                );
                expect(experienceService.loadExperience).toHaveBeenCalledWith({
                    siteId: 'siteId',
                    catalogId: 'catalogId',
                    catalogVersion: 'version',
                    pageId: 'newUid'
                });
                expect(alertService.showSuccess).toHaveBeenCalledWith({
                    message: 'se.cms.clonepage.alert.success',
                    minWidth: '',
                    mousePersist: true,
                    duration: 1000,
                    dismissible: true,
                    width: '300px'
                });
            });

            it('AND catalogVersion from experience is diffrent from the one from create page response THEN it should display clone page alert', async () => {
                const payload: any = {
                    siteId: 'siteId',
                    pageUuid: 'pageUuid',
                    catalogId: 'catalogId',
                    version: 'version'
                };

                await componentAny.createPage(payload, {
                    ...mockExperience,
                    catalogDescriptor: {
                        catalogVersionUuid: 'catalogUidButDifferent'
                    }
                });

                expect(pageFacade.createPageForSite).toHaveBeenCalledWith(payload, 'siteId');
                expect(syncPollingService.getSyncStatus).toHaveBeenCalledWith(
                    'pageUuid',
                    mockUriContext,
                    true
                );
                expect(experienceService.loadExperience).not.toHaveBeenCalled();
                expect(clonePageAlertService.displayClonePageAlert).toHaveBeenCalled();
                expect(alertService.showSuccess).toHaveBeenCalledWith({
                    message: 'se.cms.clonepage.alert.success',
                    minWidth: '',
                    mousePersist: true,
                    duration: 1000,
                    dismissible: true,
                    width: '300px'
                });
            });
        });
    });

    describe('preparePagePayload', () => {
        const mockPage = { uid: 'cmsUid', typeCode: 'ContentPage' } as ICMSPage;

        it('should extend given page info without modifying it', () => {
            mockPageBuilder.getPageProperties.and.returnValue({ template: 'template' });
            mockPageBuilder.getComponentCloneOption.and.returnValue('clone');

            const actual = componentAny.preparePagePayload(mockPage);

            // not modified
            expect(mockPage).toEqual({
                uid: 'cmsUid',
                typeCode: 'ContentPage'
            } as unknown as ICMSPage);
            // new page
            expect(actual).toEqual({
                uid: 'cmsUid',
                typeCode: 'ContentPage',
                itemtype: 'ContentPage',
                cloneComponents: true,
                template: 'template'
            });
        });

        it('WHEN old page has linkComponents THEN new clone page should not has same linkComponents', () => {
            const mocPageWithLinkComponents = {
                uid: 'cmsUid',
                typeCode: 'ContentPage',
                linkComponents: [{ id: '0001' }]
            };
            mockPageBuilder.getPageProperties.and.returnValue({ template: 'template' });
            mockPageBuilder.getComponentCloneOption.and.returnValue('clone');

            const actual = componentAny.preparePagePayload(mocPageWithLinkComponents);

            // not modified
            expect(mocPageWithLinkComponents).toEqual({
                uid: 'cmsUid',
                typeCode: 'ContentPage',
                linkComponents: [
                    {
                        id: '0001'
                    }
                ]
            });
            // new page
            expect(actual).toEqual({
                uid: 'cmsUid',
                typeCode: 'ContentPage',
                itemtype: 'ContentPage',
                cloneComponents: true,
                template: 'template',
                linkComponents: []
            });
        });

        it('WHEN restriction is active THEN should extend given page info without modifying it and add restrictions', () => {
            mockPageBuilder.getPageProperties.and.returnValue({ template: 'template' });
            mockPageBuilder.getPageRestrictions.and.returnValue(mockRestrictions);
            mockPageBuilder.getComponentCloneOption.and.returnValue('clone');

            // making restrictions active
            wizardManager.getCurrentStepId.and.returnValue(mockRestrictionStepId);

            const actual = componentAny.preparePagePayload(mockPage);

            // not modified
            expect(mockPage).toEqual({
                uid: 'cmsUid',
                typeCode: 'ContentPage'
            } as unknown as ICMSPage);
            // new page
            expect(actual).toEqual({
                uid: 'cmsUid',
                typeCode: 'ContentPage',
                itemtype: 'ContentPage',
                cloneComponents: true,
                template: 'template',
                restrictions: mockRestrictions
            });
        });
    });
});
