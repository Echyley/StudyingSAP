/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'jasmine';
import { RollbackPageVersionService } from 'cmssmarteditcontainer/components/versioning';
import {
    EVENT_CONTENT_CATALOG_UPDATE,
    IAlertService,
    IConfirmationModalService,
    IExperienceService,
    IPageInfoService,
    LogService,
    SystemEventService,
    PageVersionSelectionService,
    PageVersioningService
} from 'smarteditcommons';

describe('RollbackPageVersionService', () => {
    const PAGE_UUID = '1234';
    const PAGE_VERSION = {
        uid: 'someVersion',
        itemUUID: 'someItem',
        label: 'label',
        description: 'description',
        creationtime: new Date()
    };
    const NEW_PAGE_VERSION = {
        uid: 'someOtherVersion',
        itemUUID: 'someItem',
        label: 'label',
        description: 'description',
        creationtime: new Date()
    };

    let alertService: jasmine.SpyObj<IAlertService>;
    let confirmationModalService: jasmine.SpyObj<IConfirmationModalService>;
    let experienceService: jasmine.SpyObj<IExperienceService>;
    let pageInfoService: jasmine.SpyObj<IPageInfoService>;
    let pageVersioningService: jasmine.SpyObj<PageVersioningService>;
    let pageVersionSelectionService: jasmine.SpyObj<PageVersionSelectionService>;
    const systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
        'publishAsync'
    ]);

    const logService = jasmine.createSpyObj<LogService>('logService', ['error']);

    let service: RollbackPageVersionService;
    beforeEach(() => {
        alertService = jasmine.createSpyObj<IAlertService>('alertService', ['showSuccess']);
        confirmationModalService = jasmine.createSpyObj<IConfirmationModalService>(
            'confirmationModalService',
            ['confirm']
        );
        experienceService = jasmine.createSpyObj('experienceService', ['updateExperience']);
        pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', [
            'getPageUUID'
        ]);
        pageVersioningService = jasmine.createSpyObj<PageVersioningService>(
            'pageVersioningService',
            ['rollbackPageVersion']
        );
        pageVersionSelectionService = jasmine.createSpyObj<PageVersionSelectionService>(
            'pageVersionSelectionService',
            ['deselectPageVersion', 'getSelectedPageVersion']
        );

        service = new RollbackPageVersionService(
            logService,
            alertService,
            confirmationModalService,
            experienceService,
            pageInfoService,
            pageVersioningService,
            pageVersionSelectionService,
            systemEventService
        );
    });

    it('WHEN a page version is rolled back THEN the confirmation dialog is opened, the experience is updated, and the page version is deselected', async () => {
        // GIVEN
        confirmationModalService.confirm.and.returnValue(Promise.resolve() as any);
        pageVersioningService.rollbackPageVersion.and.returnValue(Promise.resolve());
        experienceService.updateExperience.and.returnValue(Promise.resolve() as any);
        pageInfoService.getPageUUID.and.returnValue(Promise.resolve(PAGE_UUID));
        pageVersionSelectionService.getSelectedPageVersion.and.returnValue(PAGE_VERSION);
        const confirmationParam = {
            title: 'se.cms.actionitem.page.version.rollback.confirmation.title',
            description: 'se.cms.actionitem.page.version.rollback.confirmation.description',
            descriptionPlaceholders: {
                versionLabel: PAGE_VERSION.label
            }
        };

        // WHEN
        await service.rollbackPageVersion();

        // THEN
        expect(confirmationModalService.confirm).toHaveBeenCalledWith(confirmationParam);
        expect(pageVersioningService.rollbackPageVersion).toHaveBeenCalledWith(
            PAGE_UUID,
            PAGE_VERSION.uid
        );
        expect(experienceService.updateExperience).toHaveBeenCalledWith({});
        expect(systemEventService.publishAsync).toHaveBeenCalledWith(EVENT_CONTENT_CATALOG_UPDATE);
        expect(alertService.showSuccess).toHaveBeenCalled();
        expect(pageVersionSelectionService.deselectPageVersion).toHaveBeenCalled();
    });

    it('GIVEN No version is selected WHEN a page version is rolled back THEN the experience is not updated', async () => {
        // GIVEN
        confirmationModalService.confirm.and.returnValue(Promise.resolve() as any);
        pageVersioningService.rollbackPageVersion.and.returnValue(Promise.resolve());
        experienceService.updateExperience.and.returnValue(Promise.resolve() as any);
        pageInfoService.getPageUUID.and.returnValue(Promise.resolve(PAGE_UUID));
        pageVersionSelectionService.getSelectedPageVersion.and.returnValue(null);

        // WHEN
        await service.rollbackPageVersion();

        // THEN
        expect(experienceService.updateExperience).not.toHaveBeenCalledWith({});
        expect(pageVersionSelectionService.deselectPageVersion).not.toHaveBeenCalled();
    });

    it('GIVEN a different page version is selected WHEN a page version is rolled back THEN the experience is updated', async () => {
        // GIVEN
        confirmationModalService.confirm.and.returnValue(Promise.resolve() as any);
        pageVersioningService.rollbackPageVersion.and.returnValue(Promise.resolve());
        experienceService.updateExperience.and.returnValue(Promise.resolve() as any);
        pageInfoService.getPageUUID.and.returnValue(Promise.resolve(PAGE_UUID));
        pageVersionSelectionService.getSelectedPageVersion.and.returnValue(NEW_PAGE_VERSION);

        // WHEN
        await service.rollbackPageVersion(PAGE_VERSION);

        // THEN
        expect(experienceService.updateExperience).toHaveBeenCalledWith({});
        expect(pageVersionSelectionService.deselectPageVersion).toHaveBeenCalled();
    });

    it('WHEN a page version rollback fails THEN the experience is not updated and the page version stays selected', async () => {
        // GIVEN
        confirmationModalService.confirm.and.returnValue(Promise.resolve() as any);
        pageVersioningService.rollbackPageVersion.and.returnValue(Promise.reject());
        pageInfoService.getPageUUID.and.returnValue(Promise.resolve(PAGE_UUID));
        pageVersionSelectionService.getSelectedPageVersion.and.returnValue(PAGE_VERSION);
        const confirmationParam = {
            title: 'se.cms.actionitem.page.version.rollback.confirmation.title',
            description: 'se.cms.actionitem.page.version.rollback.confirmation.description',
            descriptionPlaceholders: {
                versionLabel: PAGE_VERSION.label
            }
        };

        // WHEN
        await service.rollbackPageVersion();

        // THEN
        expect(confirmationModalService.confirm).toHaveBeenCalledWith(confirmationParam);
        expect(pageVersioningService.rollbackPageVersion).toHaveBeenCalledWith(
            PAGE_UUID,
            PAGE_VERSION.uid
        );
        expect(experienceService.updateExperience).not.toHaveBeenCalled();
        expect(alertService.showSuccess).not.toHaveBeenCalled();
        expect(pageVersionSelectionService.deselectPageVersion).not.toHaveBeenCalled();
    });
});
