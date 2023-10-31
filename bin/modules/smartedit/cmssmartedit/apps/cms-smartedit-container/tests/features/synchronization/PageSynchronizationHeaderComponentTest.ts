/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { ISyncStatus } from 'cmscommons';
import {
    PageSynchronizationHeaderComponent,
    SynchronizationPageConditions
} from 'cmssmarteditcontainer/components/synchronize/';
import { of } from 'rxjs';
import {
    ICatalogService,
    ISharedDataService,
    L10nPipe,
    CmsitemsRestService
} from 'smarteditcommons';

describe('PageSynchronizationHeaderComponent', () => {
    let component: PageSynchronizationHeaderComponent;

    let translateService: jasmine.SpyObj<TranslateService>;
    let sharedDataService: jasmine.SpyObj<ISharedDataService>;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let l10nPipe: jasmine.SpyObj<L10nPipe>;
    let cmsitemsRestService: jasmine.SpyObj<CmsitemsRestService>;
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);

    const setSyncConditions = (
        canSyncHomepage = false,
        pageHasSyncStatus = false,
        pageHasNoDepOrNoSyncStatus = false,
        pageHasUnavailableDependencies = false
    ) => {
        component.pageSyncConditions = {
            canSyncHomepage,
            pageHasSyncStatus,
            pageHasNoDepOrNoSyncStatus,
            pageHasUnavailableDependencies
        };
    };

    const setSyncStatusUnavailableDeps = (unavailableDependencies = []) => {
        component.syncStatus = {
            unavailableDependencies
        } as unknown as ISyncStatus;
    };

    beforeEach(() => {
        translateService = jasmine.createSpyObj<TranslateService>('translateService', ['instant']);
        translateService.instant.and.callFake(() => 'translation');

        sharedDataService = jasmine.createSpyObj<ISharedDataService>('sharedDataService', ['get']);
        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'getActiveContentCatalogVersionByCatalogId'
        ]);
        l10nPipe = jasmine.createSpyObj<L10nPipe>('l10nPipe', ['transform']);
        cmsitemsRestService = jasmine.createSpyObj<any>('cmsitemsRestService', ['getByIds']);

        component = new PageSynchronizationHeaderComponent(
            sharedDataService,
            catalogService,
            cmsitemsRestService,
            translateService,
            l10nPipe,
            cdr
        );
    });

    beforeEach(() => {
        l10nPipe.transform.and.callFake((catalogName: any) => of(catalogName.en));
    });

    describe('initialize', () => {
        it('sets ready flag properly', () => {
            expect(component.ready).toBe(false);
        });
    });

    describe('on changes', () => {
        it('should not update header text if there is no syncStatus', async () => {
            component.syncStatus = null;

            await component.ngOnChanges();

            expect(sharedDataService.get).not.toHaveBeenCalled();
        });

        it('should update header text if there is sync status change', async () => {
            component.syncStatus = {} as ISyncStatus;

            setSyncConditions();

            sharedDataService.get.and.returnValue(
                Promise.resolve({
                    pageContext: {
                        catalogId: 'CATALOG_ID',
                        catalogName: { en: 'CATALOG_NAME' }
                    }
                })
            );

            catalogService.getActiveContentCatalogVersionByCatalogId.and.returnValue(
                Promise.resolve('CATALOG_VERSION_UUID')
            );

            await component.ngOnChanges();

            expect(component.headerText).toBeDefined();
            expect(component.ready).toBe(true);
        });
    });

    it('WHEN sync page event has occurred THEN it displays the description indicating that page has unavailable dependencies', async () => {
        setSyncConditions(false, false, true, true);
        setSyncStatusUnavailableDeps([
            {
                itemId: 'ITEM_ID_1'
            },
            {
                itemId: 'ITEM_ID_2'
            }
        ]);

        sharedDataService.get.and.returnValue(
            Promise.resolve({
                pageContext: {
                    catalogId: 'CATALOG_ID',
                    catalogName: { en: 'CATALOG_NAME' }
                }
            })
        );
        catalogService.getActiveContentCatalogVersionByCatalogId.and.returnValue(
            Promise.resolve('CATALOG_VERSION_UUID')
        );
        cmsitemsRestService.getByIds.and.returnValue(
            Promise.resolve<any>({
                response: [{ name: 'ITEM_ID_1_PAGE' }, { name: 'ITEM_ID_2_PAGE' }]
            })
        );

        await component.ngOnChanges();

        expect(sharedDataService.get).toHaveBeenCalledWith('experience');
        expect(catalogService.getActiveContentCatalogVersionByCatalogId).toHaveBeenCalledWith(
            'CATALOG_ID'
        );
        expect(cmsitemsRestService.getByIds).toHaveBeenCalledWith(['ITEM_ID_1', 'ITEM_ID_2']);
        expect(translateService.instant.calls.argsFor(0)[0]).toBe(
            'se.cms.synchronization.page.unavailable.items.description'
        );
        expect(translateService.instant.calls.argsFor(0)[1]).toEqual({
            itemNames: 'ITEM_ID_1_PAGE, ITEM_ID_2_PAGE',
            catalogName: 'CATALOG_NAME',
            catalogVersion: 'CATALOG_VERSION_UUID'
        });
    });

    it('WHEN sync page event has occurred THEN it displays the description indicating that the page has not been synchronized yet ', async () => {
        setSyncConditions(false, false, true, false);
        setSyncStatusUnavailableDeps();

        sharedDataService.get.and.returnValue(
            Promise.resolve({
                catalogDescriptor: {
                    catalogId: 'DESCRIPTOR_CATALOG_ID',
                    name: { en: 'DESCRIPTOR_CATALOG_NAME' }
                }
            })
        );
        catalogService.getActiveContentCatalogVersionByCatalogId.and.returnValue(
            Promise.resolve('CATALOG_VERSION_UUID')
        );

        await component.ngOnChanges();

        expect(sharedDataService.get).toHaveBeenCalledWith('experience');
        expect(catalogService.getActiveContentCatalogVersionByCatalogId).toHaveBeenCalledWith(
            'DESCRIPTOR_CATALOG_ID'
        );
        expect(cmsitemsRestService.getByIds).not.toHaveBeenCalled();
        expect(translateService.instant.calls.argsFor(0)[0]).toBe(
            'se.cms.synchronization.page.new.description'
        );
        expect(translateService.instant.calls.argsFor(0)[1]).toEqual({
            catalogName: 'DESCRIPTOR_CATALOG_NAME',
            catalogVersion: 'CATALOG_VERSION_UUID'
        });
    });

    describe('getSubHeaderText', () => {
        it('returns the default text about synchronization of non-shared content slots and page information', () => {
            component.pageSyncConditions = {
                pageHasNoDepOrNoSyncStatus: false,
                pageHasSyncStatus: false,
                canSyncHomepage: false
            } as SynchronizationPageConditions;

            expect(component.getSubHeaderText()).toBe('se.cms.synchronization.page.header');
        });

        it('returns the text about an attempt to synchronize the old Homepage', () => {
            component.pageSyncConditions = {
                pageHasSyncStatus: true,
                canSyncHomepage: false
            } as SynchronizationPageConditions;

            expect(component.getSubHeaderText()).toBe(
                'se.cms.synchronization.page.header.old.homepage'
            );
        });
    });

    it('isNewPage returns true when page has no dependencies or sync status', () => {
        component.pageSyncConditions = {
            pageHasNoDepOrNoSyncStatus: true
        } as SynchronizationPageConditions;

        expect(component.isNewPage()).toBe(true);
    });

    it('isSyncOldHomeHeader returns true when the Homepage page has sync status AND it cannot be synchronized', () => {
        component.pageSyncConditions = {
            pageHasSyncStatus: true,
            canSyncHomepage: false
        } as SynchronizationPageConditions;

        expect(component.isSyncOldHomeHeader()).toBe(true);
    });
});
