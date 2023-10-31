/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { SelectTargetCatalogVersionComponent } from 'cmssmarteditcontainer/components/pages/addPageWizard/components/selectTargetCatalogVersion/SelectTargetCatalogVersionComponent';
import { CatalogVersionRestService } from 'cmssmarteditcontainer/dao';
import { PageFacade } from 'cmssmarteditcontainer/facades';
import { ICatalogService, ICatalogVersion, SelectApi, IPageService } from 'smarteditcommons';

describe('SelectTargetCatalogVersionComponent', () => {
    let component: SelectTargetCatalogVersionComponent;
    let componentAny: any;
    let pageFacade: jasmine.SpyObj<PageFacade>;
    let catalogVersionRestService: jasmine.SpyObj<CatalogVersionRestService>;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let pageService: jasmine.SpyObj<IPageService>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;

    const mockCatalogVersions = [
        {
            active: false,
            name: { en: 'Apparel UK Content Catalog - Staged' },
            uuid: 'apparel-ukContentCatalog/Staged',
            version: 'Staged',
            catalogId: 'ApparelUK_staged'
        },
        {
            active: true,
            name: { en: 'Apparel UK Content Catalog - Online' },
            uuid: 'apparel-ukContentCatalog/Online',
            version: 'Online',
            catalogId: 'ApparelUK_online',
            siteId: 'Apparel'
        }
    ] as unknown as ICatalogVersion[];
    let mockSelectApi: SelectApi;

    beforeEach(() => {
        pageFacade = jasmine.createSpyObj<PageFacade>('pageFacade', ['contentPageWithLabelExists']);
        catalogVersionRestService = jasmine.createSpyObj<CatalogVersionRestService>(
            'catalogVersionRestService',
            ['getCloneableTargets']
        );
        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'getCatalogVersionByUuid',
            'getCatalogVersionUUid'
        ]);
        pageService = jasmine.createSpyObj<IPageService>('pageService', [
            'primaryPageForPageTypeExists'
        ]);
        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);

        mockSelectApi = {
            resetValidationState: jasmine.createSpy(),
            setValidationState: jasmine.createSpy()
        };

        component = new SelectTargetCatalogVersionComponent(
            pageFacade,
            catalogVersionRestService,
            catalogService,
            pageService,
            cdr
        );
        componentAny = component;
    });

    describe('initialize', () => {
        it('should define fetch strategy, onSelectChange handler and setup catalog versions', async () => {
            spyOn(componentAny, 'setupCatalogVersions');
            component.catalogVersions = mockCatalogVersions;

            await component.ngOnInit();

            expect(component.catalogVersionSelectorFetchStrategy).toEqual({
                fetchAll: jasmine.any(Function)
            });
            expect(component.onSelectionChange).toEqual(jasmine.any(Function));
            expect(componentAny.setupCatalogVersions).toHaveBeenCalled();
        });
    });

    describe('setSelectApi', () => {
        it('should assign api to selectApi', () => {
            component.setSelectApi(mockSelectApi);

            expect(component.selectApi).toEqual(mockSelectApi);
        });
    });

    describe('setupCatalogVersions', () => {
        it('WHEN catalog version service returns empty clonable targets THEN it should do nothing', async () => {
            catalogVersionRestService.getCloneableTargets.and.returnValue(
                Promise.resolve({ versions: [] })
            );

            await componentAny.setupCatalogVersions();

            expect(catalogService.getCatalogVersionUUid).not.toHaveBeenCalled();
        });

        describe('GIVEN catalog version service return clonable targets', () => {
            beforeEach(() => {
                catalogVersionRestService.getCloneableTargets.and.returnValue(
                    Promise.resolve({ versions: mockCatalogVersions })
                );
            });

            it('WHEN there is catalog version for given uri context THEN it should set current catalog version to selectedCatalogVersion', async () => {
                catalogService.getCatalogVersionUUid.and.returnValue(
                    Promise.resolve(mockCatalogVersions[1].uuid)
                );
                component.uriContext = { context: 'context' };

                await componentAny.setupCatalogVersions();

                expect(component.catalogVersions).toEqual(mockCatalogVersions);
                expect(component.selectedCatalogVersion).toEqual(mockCatalogVersions[1].uuid);
                expect(catalogService.getCatalogVersionUUid).toHaveBeenCalledWith({
                    context: 'context'
                });
            });

            it('WHEN there is no catalog version for given uri context THEN it should set the first available catalog version from catalog version rest api', async () => {
                catalogService.getCatalogVersionUUid.and.returnValue(
                    Promise.resolve('not existing')
                );
                component.uriContext = { context: 'context' };

                await componentAny.setupCatalogVersions();
                expect(component.catalogVersions).toEqual(mockCatalogVersions);
                expect(component.selectedCatalogVersion).toEqual(mockCatalogVersions[0].uuid);
                expect(catalogService.getCatalogVersionUUid).toHaveBeenCalledWith({
                    context: 'context'
                });
            });
        });
    });

    describe('selectionChangeHandler', () => {
        beforeEach(async () => {
            catalogVersionRestService.getCloneableTargets.and.returnValue(
                Promise.resolve({ versions: mockCatalogVersions })
            );
            await component.ngOnInit();
        });

        it('WHEN selected catalog version is not specified THEN it should do nothing', async () => {
            spyOn(component.onTargetCatalogVersionSelected, 'emit');

            component.selectedCatalogVersion = null;

            await component.onSelectionChange();

            expect(component.onTargetCatalogVersionSelected.emit).not.toHaveBeenCalled();
        });

        describe('GIVEN selected catalog is specified', () => {
            beforeEach(() => {
                component.selectedCatalogVersion = mockCatalogVersions[0].uuid;
                catalogService.getCatalogVersionByUuid.and.returnValue(
                    Promise.resolve(mockCatalogVersions[1])
                );
                spyOn(component.onTargetCatalogVersionSelected, 'emit');
                spyOn(componentAny, 'updateSelectValidationState');
                component.pageLabel = 'pageLabel';
            });

            it('WHEN page type code is ContentPage THEN it should emit target catalog change, call page facade to get information whether page exists and update select validation state', async () => {
                component.pageTypeCode = 'ContentPage';
                pageFacade.contentPageWithLabelExists.and.returnValue(Promise.resolve(true));

                await component.onSelectionChange();

                expect(component.catalogVersionContainsPageWithSameLabel).toEqual(true);

                expect(componentAny.updateSelectValidationState).toHaveBeenCalled();
                expect(pageFacade.contentPageWithLabelExists).toHaveBeenCalledWith(
                    'pageLabel',
                    'ApparelUK_online',
                    'Online'
                );
            });
        });
    });

    describe('updateSelectValidationState', () => {
        it('WHEN selectApi is not defined THEN it should do nothing', () => {
            component.selectApi = null;

            componentAny.updateSelectValidationState();

            expect(mockSelectApi.resetValidationState).not.toHaveBeenCalled();
            expect(mockSelectApi.setValidationState).not.toHaveBeenCalled();
        });

        describe('GIVEN selectAPI is specified', () => {
            beforeEach(() => {
                component.selectApi = mockSelectApi;
            });
            it('WHEN catalog version contains page with the same label THEN it should set validationState', () => {
                component.catalogVersionContainsPageWithSameLabel = true;

                componentAny.updateSelectValidationState();

                // Warning comes from: VALIDATION_MESSAGE_TYPES.WARNING
                expect(mockSelectApi.setValidationState).toHaveBeenCalledWith('Warning');
                expect(mockSelectApi.resetValidationState).not.toHaveBeenCalled();
            });

            it('WHEN catalog version does not contain pages with the same label THEN it should reset validationState', () => {
                component.catalogVersionContainsPageWithSameLabel = false;

                componentAny.updateSelectValidationState();

                expect(mockSelectApi.resetValidationState).toHaveBeenCalled();

                expect(mockSelectApi.setValidationState).not.toHaveBeenCalled();
            });
        });
    });
});
