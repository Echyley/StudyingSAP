/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'jasmine';
import { ManagePageVersionService } from 'cmssmarteditcontainer/components/versioning';
import { GenericEditorModalService } from 'cmssmarteditcontainer/services/GenericEditorModalService';
import {
    IAlertService,
    IConfirmationModalService,
    IExperienceService,
    IPageInfoService,
    PageVersionSelectionService,
    PageVersioningService,
    IGenericEditorModalServiceComponent
} from 'smarteditcommons';

describe('Test ManagePageVersionService', () => {
    const RESOURCE_URI = '/someUrl/:pageUuid/versions';
    const PAGE_UUID = '1234';
    const PAGE_VERSION = {
        uid: 'someVersion',
        itemUUID: 'someItem',
        label: 'label',
        description: 'description',
        creationtime: new Date()
    };

    let alertService: jasmine.SpyObj<IAlertService>;
    let experienceService: jasmine.SpyObj<IExperienceService>;
    let genericEditorModalService: jasmine.SpyObj<GenericEditorModalService>;
    let confirmationModalService: jasmine.SpyObj<IConfirmationModalService>;
    let pageInfoService: jasmine.SpyObj<IPageInfoService>;
    let pageVersioningService: jasmine.SpyObj<PageVersioningService>;
    let pageVersionSelectionService: jasmine.SpyObj<PageVersionSelectionService>;

    let service: ManagePageVersionService;
    beforeEach(() => {
        alertService = jasmine.createSpyObj<IAlertService>('alertService', ['showSuccess']);
        experienceService = jasmine.createSpyObj('experienceService', ['updateExperience']);
        confirmationModalService = jasmine.createSpyObj('confirmationModalService', ['confirm']);
        genericEditorModalService = jasmine.createSpyObj('genericEditorModalService', ['open']);
        pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', [
            'getPageUUID'
        ]);

        pageVersioningService = jasmine.createSpyObj<PageVersioningService>(
            'pageVersioningService',
            ['getResourceURI', 'deletePageVersion']
        );
        pageVersioningService.getResourceURI.and.returnValue(RESOURCE_URI);

        pageVersionSelectionService = jasmine.createSpyObj<PageVersionSelectionService>(
            'pageVersionSelectionService',
            [
                'selectPageVersion',
                'updatePageVersionDetails',
                'getSelectedPageVersion',
                'deselectPageVersion'
            ]
        );

        service = new ManagePageVersionService(
            alertService,
            experienceService,
            confirmationModalService,
            genericEditorModalService,
            pageInfoService,
            pageVersioningService,
            pageVersionSelectionService
        );
    });

    describe('createPageVersion', () => {
        describe('GIVEN the method was called AND the modal is opened', () => {
            it('WHEN called THEN it opens a modal', async () => {
                pageInfoService.getPageUUID.and.returnValue(Promise.resolve(PAGE_UUID));
                genericEditorModalService.open.and.returnValue(Promise.resolve(PAGE_VERSION));
                experienceService.updateExperience.and.returnValue(Promise.resolve() as any);

                await service.createPageVersion();

                const mockComponentData: IGenericEditorModalServiceComponent = {
                    title: 'se.cms.versions.create',
                    structure: {
                        attributes: [
                            {
                                cmsStructureType: 'ShortString',
                                qualifier: 'label',
                                i18nKey: 'se.cms.versions.editor.label.name',
                                required: true
                            },
                            {
                                cmsStructureType: 'ShortString',
                                qualifier: 'description',
                                i18nKey: 'se.cms.versions.editor.description.name'
                            }
                        ]
                    },
                    contentApi: RESOURCE_URI.replace(':pageUuid', PAGE_UUID)
                };

                expect(genericEditorModalService.open).toHaveBeenCalledWith(
                    mockComponentData,
                    jasmine.any(Function)
                );
            });

            it('WHEN modal is confirmed THEN the experience is updated AND success alert is shown AND the version is selected', async () => {
                pageInfoService.getPageUUID.and.returnValue(Promise.resolve(PAGE_UUID));
                genericEditorModalService.open.and.returnValue(Promise.resolve(PAGE_VERSION));
                experienceService.updateExperience.and.returnValue(Promise.resolve() as any);

                await service.createPageVersion();

                const modalConfirmCallback = genericEditorModalService.open.calls.argsFor(0)[1];
                await modalConfirmCallback(PAGE_VERSION);

                expect(experienceService.updateExperience).toHaveBeenCalledWith(
                    jasmine.objectContaining({
                        versionId: PAGE_VERSION.uid
                    })
                );
                expect(alertService.showSuccess).toHaveBeenCalled();
                expect(pageVersionSelectionService.selectPageVersion).toHaveBeenCalledWith(
                    PAGE_VERSION
                );
            });
        });
    });

    describe('editPageVersion', () => {
        beforeEach(() => {
            pageInfoService.getPageUUID.and.returnValue(Promise.resolve(PAGE_UUID));
            genericEditorModalService.open.and.returnValue(Promise.resolve(PAGE_VERSION));
        });

        it('WHEN called THEN the modal is opened with prepopulated data', async () => {
            await service.editPageVersion(PAGE_VERSION);

            const mockComponentData: IGenericEditorModalServiceComponent = {
                title: 'se.cms.versions.edit',
                structure: {
                    attributes: [
                        {
                            cmsStructureType: 'ShortString',
                            qualifier: 'label',
                            i18nKey: 'se.cms.versions.editor.label.name',
                            required: true
                        },
                        {
                            cmsStructureType: 'ShortString',
                            qualifier: 'description',
                            i18nKey: 'se.cms.versions.editor.description.name'
                        }
                    ]
                },
                contentApi: RESOURCE_URI.replace(':pageUuid', PAGE_UUID),
                content: PAGE_VERSION,
                componentUuid: PAGE_VERSION.uid,
                componentType: 'versioning'
            };

            expect(genericEditorModalService.open).toHaveBeenCalledWith(
                mockComponentData,
                jasmine.any(Function)
            );
        });

        it('GIVEN modal was opened WHEN the modal is confirmed THEN the page version is updated', async () => {
            pageVersionSelectionService.getSelectedPageVersion.and.returnValue({
                uid: 'someVersion'
            } as any);

            const editedPageVersion = await service.editPageVersion(PAGE_VERSION);

            const modalConfirmCallback = genericEditorModalService.open.calls.argsFor(0)[1];
            modalConfirmCallback(editedPageVersion);

            expect(pageVersionSelectionService.updatePageVersionDetails).toHaveBeenCalledWith(
                editedPageVersion
            );
        });
    });

    describe('deletePageVersion', () => {
        beforeEach(() => {
            pageInfoService.getPageUUID.and.returnValue(Promise.resolve(PAGE_UUID));
            confirmationModalService.confirm.and.returnValue(Promise.resolve() as any);
            pageVersioningService.deletePageVersion.and.returnValue(Promise.resolve());
            experienceService.updateExperience.and.returnValue(Promise.resolve() as any);
        });

        it('WHEN called then a confirmation is opened and a success alert is shown', async () => {
            pageVersionSelectionService.getSelectedPageVersion.and.returnValue({
                uid: 'someOtherVersion'
            } as any);

            await service.deletePageVersion(PAGE_VERSION.uid);

            expect(pageVersioningService.deletePageVersion).toHaveBeenCalledWith(
                PAGE_UUID,
                PAGE_VERSION.uid
            );
            expect(alertService.showSuccess).toHaveBeenCalled();
            expect(experienceService.updateExperience).not.toHaveBeenCalled();
        });

        it('WHEN called for version that is currently loaded THEN a confirmation is opened, a success alert is shown AND experience is updated to show current page', async () => {
            pageVersionSelectionService.getSelectedPageVersion.and.returnValue({
                uid: PAGE_VERSION.uid
            } as any);

            await service.deletePageVersion(PAGE_VERSION.uid);

            expect(pageVersioningService.deletePageVersion).toHaveBeenCalledWith(
                PAGE_UUID,
                PAGE_VERSION.uid
            );
            expect(alertService.showSuccess).toHaveBeenCalled();
            expect(experienceService.updateExperience).toHaveBeenCalled();
            expect(pageVersionSelectionService.deselectPageVersion).toHaveBeenCalled();
        });
    });
});
