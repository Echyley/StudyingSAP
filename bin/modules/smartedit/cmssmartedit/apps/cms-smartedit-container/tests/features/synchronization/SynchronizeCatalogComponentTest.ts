/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { ISyncJob, SynchronizationService } from 'cmscommons';
import { SynchronizeCatalogComponent } from 'cmssmarteditcontainer/components/synchronize/components';
import { of } from 'rxjs';
import {
    EVENT_CONTENT_CATALOG_UPDATE,
    ICatalog,
    ICatalogVersion,
    IConfirmationModalService,
    L10nPipe,
    SystemEventService,
    IUserTrackingService
} from 'smarteditcommons';

describe('SynchronizeCatalogComponent', () => {
    const mockSyncStatus = {
        creationDate: 'creationDate',
        endDate: 'endDate',
        syncStatus: 'RUNNING',
        sourceCatalogVersion: 'Staged',
        targetCatalogVersion: 'Online'
    } as ISyncJob;

    let synchronizationService: jasmine.SpyObj<SynchronizationService>;
    let confirmationModalService: jasmine.SpyObj<IConfirmationModalService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let l10nPipe: jasmine.SpyObj<L10nPipe>;
    let userTrackingService: jasmine.SpyObj<IUserTrackingService>;
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);

    let component: SynchronizeCatalogComponent;
    beforeEach(() => {
        synchronizationService = jasmine.createSpyObj<SynchronizationService>(
            'synchronizationService',
            [
                'startAutoGetSyncData',
                'stopAutoGetSyncData',
                'updateCatalogSync',
                'getCatalogSyncStatus'
            ]
        );

        confirmationModalService = jasmine.createSpyObj<IConfirmationModalService>(
            'confirmationModalService',
            ['confirm']
        );

        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'publishAsync'
        ]);

        l10nPipe = jasmine.createSpyObj<L10nPipe>('l10nPipe', ['transform']);

        userTrackingService = jasmine.createSpyObj<IUserTrackingService>('userTrackingService', [
            'trackingUserAction'
        ]);

        component = new SynchronizeCatalogComponent(
            synchronizationService,
            confirmationModalService,
            systemEventService,
            l10nPipe,
            cdr,
            userTrackingService
        );
    });

    beforeEach(() => {
        synchronizationService.getCatalogSyncStatus.and.returnValue(
            Promise.resolve(mockSyncStatus)
        );

        component.catalog = {
            catalogId: 'apparel_uk'
        } as ICatalog;
        component.activeCatalogVersion = {
            version: 'Online'
        } as ICatalogVersion;

        component.catalogVersion = {
            active: false,
            version: 'Staged'
        } as ICatalogVersion;
    });

    describe('initialize', () => {
        it('sets target catalog version properly', async () => {
            await component.ngOnInit();

            const mockActiveCatalogVersion = {
                version: 'Online'
            } as ICatalogVersion;

            component.activeCatalogVersion = mockActiveCatalogVersion;

            expect(component.targetCatalogVersion).toBe(mockActiveCatalogVersion.version);
        });

        describe('sets source catalog version properly', () => {
            it('WHEN the catalog version is not active THEN source catalog version is the given catalog version', async () => {
                await component.ngOnInit();

                expect(component.sourceCatalogVersion).toBe('Staged');
            });

            it('WHEN the catalog version is active THEN source catalog version is null', async () => {
                component.catalogVersion = {
                    active: true,
                    version: 'Staged'
                } as ICatalogVersion;

                await component.ngOnInit();

                expect(component.sourceCatalogVersion).toBe(null);
            });
        });
    });

    describe('auto updating synchronization data', () => {
        let startAutoGetSyncDataCallback: (job: ISyncJob) => void;
        beforeEach(async () => {
            synchronizationService.getCatalogSyncStatus.and.returnValue(
                Promise.resolve(mockSyncStatus)
            );
            await component.ngOnInit();

            startAutoGetSyncDataCallback =
                synchronizationService.startAutoGetSyncData.calls.argsFor(0)[1];
        });

        it('starts the process', () => {
            expect(synchronizationService.startAutoGetSyncData).toHaveBeenCalled();
        });

        it('WHEN status is updated THEN it sets sync job status properly', () => {
            const mockUpdatedSyncStatus = { ...mockSyncStatus };
            mockUpdatedSyncStatus.creationDate = 'updatedCreationDate';
            mockUpdatedSyncStatus.endDate = 'updatedEndDate';
            startAutoGetSyncDataCallback(mockUpdatedSyncStatus);

            expect(component.syncJobStatus).toEqual({
                syncStartTime: mockUpdatedSyncStatus.creationDate,
                syncEndTime: mockUpdatedSyncStatus.endDate,
                status: mockUpdatedSyncStatus.syncStatus,
                source: mockUpdatedSyncStatus.sourceCatalogVersion,
                target: mockUpdatedSyncStatus.targetCatalogVersion
            });
        });

        it('WHEN component is destroyed THEN it should stop the synchronization', () => {
            component.ngOnDestroy();

            expect(synchronizationService.stopAutoGetSyncData).toHaveBeenCalled();
        });
    });

    describe('syncCatalog', () => {
        beforeEach(() => {
            component.catalog = {
                name: {
                    en: 'apparel_uk_en'
                }
            } as unknown as ICatalog;

            l10nPipe.transform.and.callFake((catalogName: any) => of(catalogName.en));
        });

        it('GIVEN confirmation modal is opened WHEN confirmed THEN it updates sync job status AND publishes Content Catalog Update event AND track user action', async () => {
            confirmationModalService.confirm.and.returnValue(Promise.resolve() as any);
            synchronizationService.updateCatalogSync.and.returnValue(mockSyncStatus as any);

            await component.syncCatalog();

            expect(confirmationModalService.confirm).toHaveBeenCalledWith(
                jasmine.objectContaining({
                    descriptionPlaceholders: {
                        catalogName: 'apparel_uk_en'
                    }
                })
            );
            expect(synchronizationService.updateCatalogSync).toHaveBeenCalled();
            expect(systemEventService.publishAsync).toHaveBeenCalledWith(
                EVENT_CONTENT_CATALOG_UPDATE,
                mockSyncStatus
            );
            expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
        });

        it('GIVEN confirmation modal is opened WHEN cancelled THEN it rejects a promise', async () => {
            confirmationModalService.confirm.and.returnValue(Promise.reject());

            await component.syncCatalog().catch(() => expect(true).toBe(true));
        });
    });

    it('sets sync job status immediately', async () => {
        await component.ngOnInit();

        expect(synchronizationService.getCatalogSyncStatus).toHaveBeenCalled();
        expect(component.syncJobStatus).toEqual(
            jasmine.objectContaining({
                syncStartTime: mockSyncStatus.creationDate,
                syncEndTime: mockSyncStatus.endDate
            })
        );
    });

    it('isSyncJobFinished returns true when sync job has finished', () => {
        component.syncJobStatus = {
            status: 'FINISHED'
        } as any;

        expect(component.isSyncJobFinished()).toBe(true);
    });

    it('isSyncJobInProgress returns true when sync job is in progress', () => {
        component.syncJobStatus = {
            status: 'RUNNING'
        } as any;
        expect(component.isSyncJobInProgress()).toBe(true);

        component.syncJobStatus = {
            status: 'UNKNOWN'
        } as any;
        expect(component.isSyncJobInProgress()).toBe(true);
    });

    it('isSyncJobFailed returns true when sync job has failed', () => {
        component.syncJobStatus = {
            status: 'ERROR'
        } as any;
        expect(component.isSyncJobFailed()).toBe(true);

        component.syncJobStatus = {
            status: 'FAILURE'
        } as any;
        expect(component.isSyncJobFailed()).toBe(true);
    });

    it('isSyncButtonEnabled returns true when sync job is not in progress', () => {
        component.syncJobStatus = {
            status: 'FINISHED'
        } as any;
        expect(component.isSyncButtonEnabled()).toBe(true);

        component.syncJobStatus = {
            status: 'RUNNING'
        } as any;
        expect(component.isSyncButtonEnabled()).toBe(false);
    });
});
