/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ComponentService } from 'cmscommons';
import { ComponentsTabComponent } from 'cmssmarteditcontainer/components/cmsComponents/componentMenu/tabs/ComponentsTabComponent';
import { ComponentMenuTabModel } from 'cmssmarteditcontainer/components/cmsComponents/componentMenu/types';
import {
    ISharedDataService,
    Page,
    TabData,
    CMSItem,
    ComponentMenuService,
    CatalogVersion,
    IUserTrackingService,
    USER_TRACKING_FUNCTIONALITY
} from 'smarteditcommons';

describe('ComponentsTabComponent', () => {
    let component: ComponentsTabComponent;
    let componentMenuService: jasmine.SpyObj<ComponentMenuService>;
    let componentService: jasmine.SpyObj<ComponentService>;
    let sharedDataService: jasmine.SpyObj<ISharedDataService>;
    let userTrackingService: jasmine.SpyObj<IUserTrackingService>;

    const mockCatalogVersions = [
        {
            isCurrentCatalog: true,
            catalogName: { en: 'catalog1' },
            catalogId: 'catalog1',
            catalogVersionId: 'catalogVersion1',
            id: '1'
        },
        {
            isCurrentCatalog: false,
            catalogName: { en: 'catalog2' },
            catalogId: 'catalog2',
            catalogVersionId: 'catalogVersion2',
            id: '2'
        }
    ] as CatalogVersion[];

    let injectedData: TabData<ComponentMenuTabModel>;

    const createComponentWithInjectedData = (
        hasMultipleContentCatalogs = true,
        tabActive = true
    ) => {
        component = new ComponentsTabComponent(
            {
                ...injectedData,
                model: {
                    componentsTab: { hasMultipleContentCatalogs }
                },
                tab: {
                    active: tabActive
                }
            } as TabData<ComponentMenuTabModel>,
            componentMenuService,
            componentService,
            sharedDataService,
            userTrackingService
        );
    };

    beforeEach(() => {
        injectedData = {
            model: {
                componentsTab: { hasMultipleContentCatalogs: true }
            },
            tab: {
                active: true
            }
        } as TabData<ComponentMenuTabModel>;
        componentMenuService = jasmine.createSpyObj<ComponentMenuService>('componentMenuService', [
            'persistCatalogVersion',
            'getValidContentCatalogVersions',
            'getInitialCatalogVersion'
        ]);
        componentMenuService.persistCatalogVersion.and.returnValue(Promise.resolve());
        componentMenuService.getValidContentCatalogVersions.and.returnValue(
            Promise.resolve(mockCatalogVersions)
        );
        componentMenuService.getInitialCatalogVersion.and.returnValue(
            Promise.resolve(mockCatalogVersions[0])
        );

        componentService = jasmine.createSpyObj<ComponentService>('componentService', [
            'loadPagedComponentItemsByCatalogVersion'
        ]);

        sharedDataService = jasmine.createSpyObj<ISharedDataService>('sharedDataService', [
            'get',
            'set'
        ]);
        sharedDataService.get.and.returnValue(Promise.resolve(true));
        sharedDataService.set.and.returnValue(Promise.resolve());

        userTrackingService = jasmine.createSpyObj<IUserTrackingService>('userTrackingService', [
            'trackingUserAction'
        ]);

        component = new ComponentsTabComponent(
            injectedData,
            componentMenuService,
            componentService,
            sharedDataService,
            userTrackingService
        );
    });

    describe('Initialize', () => {
        it('WHEN it does not have multiple content catalogs THEN it should set initial values, fetch catalog versions, and trigger on catalog version change', async () => {
            createComponentWithInjectedData(false);
            spyOn(component, 'onCatalogVersionChange');

            expect(component.catalogVersions).toEqual([]);
            expect(component.catalogVersionsFetchStrategy).toEqual({
                fetchAll: jasmine.any(Function)
            });
            expect(component.componentsContext).toEqual({ items: [] });
            expect(component.forceRecompile).toEqual(true);
            expect(component.itemComponent).toEqual(jasmine.any(Function));
            expect(component.selectedCatalogVersionId).toEqual(null);
            expect(component.selectedCatalogVersion).toEqual(null);
            expect(component.searchTerm).toEqual('');

            await component.ngOnInit();

            expect(component.onCatalogVersionChange).toHaveBeenCalled();

            expect(component.cloneOnDrop).toEqual(true);
            expect(sharedDataService.get).toHaveBeenCalledWith('enableCloneComponentOnDrop');

            expect(component.catalogVersions).toEqual(mockCatalogVersions);
            expect(component.selectedCatalogVersion).toEqual(mockCatalogVersions[0]);
            expect(component.selectedCatalogVersionId).toEqual(mockCatalogVersions[0].id);
        });

        it('WHEN it has multiple content catalogs THEN it should set initial values and fetch catalog versions', async () => {
            createComponentWithInjectedData(true);
            spyOn(component, 'onCatalogVersionChange');

            expect(component.catalogVersions).toEqual([]);
            expect(component.catalogVersionsFetchStrategy).toEqual({
                fetchAll: jasmine.any(Function)
            });
            expect(component.componentsContext).toEqual({ items: [] });
            expect(component.forceRecompile).toEqual(true);
            expect(component.itemComponent).toEqual(jasmine.any(Function));
            expect(component.selectedCatalogVersionId).toEqual(null);
            expect(component.selectedCatalogVersion).toEqual(null);
            expect(component.searchTerm).toEqual('');

            await component.ngOnInit();

            expect(component.onCatalogVersionChange).not.toHaveBeenCalled();

            expect(component.cloneOnDrop).toEqual(true);
            expect(sharedDataService.get).toHaveBeenCalledWith('enableCloneComponentOnDrop');

            expect(component.catalogVersions).toEqual(mockCatalogVersions);
            expect(component.selectedCatalogVersion).toEqual(mockCatalogVersions[0]);
            expect(component.selectedCatalogVersionId).toEqual(mockCatalogVersions[0].id);
        });
    });

    describe('onCatalogVersionChange', () => {
        it('GIVEN component is initialized WHEN there is selected catalog Version id THEN it should find that catalog version, assign it, persist it and force recompile', async () => {
            jasmine.clock().install();

            await component.ngOnInit();

            component.selectedCatalogVersionId = mockCatalogVersions[1].id;

            await component.onCatalogVersionChange();

            expect(component.selectedCatalogVersion).toEqual(mockCatalogVersions[1]);
            expect(componentMenuService.persistCatalogVersion).toHaveBeenCalledWith(
                mockCatalogVersions[1].id
            );

            expect(component.forceRecompile).toEqual(false);
            jasmine.clock().tick(1);
            expect(component.forceRecompile).toEqual(true);
        });
    });

    it('WHEN search term changes THEN it should assign', () => {
        component.onSearchTermChanged('new term');

        expect(component.searchTerm).toEqual('new term');
    });

    it('WHEN Component Clone On Drop changes THEN it should assign AND track user action', () => {
        component.cloneOnDrop = false;
        component.onComponentCloneOnDropChange();

        expect(sharedDataService.set).toHaveBeenCalledWith('enableCloneComponentOnDrop', false);
        expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
    });

    describe('loadComponentItems', () => {
        it('WHEN there is no selected catalog version it should resolve with empty array', async () => {
            component.selectedCatalogVersion = null;

            const actual = await component.loadComponentItems('', 10, 1);

            expect(actual).toEqual({ results: [] } as Page<CMSItem>);
        });

        it('WHEN there is selected catalog version THEN it should build payload, call service method and return loaded component items', async () => {
            componentService.loadPagedComponentItemsByCatalogVersion.and.returnValue({
                response: [{ uid: 'component1' }, { uid: 'component2' }]
            } as any);
            await component.ngOnInit();

            const actual = await component.loadComponentItems('', 10, 1);

            expect(componentService.loadPagedComponentItemsByCatalogVersion).toHaveBeenCalledWith({
                catalogId: 'catalog1',
                catalogVersion: 'catalogVersion1',
                mask: '',
                pageSize: 10,
                page: 1
            } as any);

            expect(actual).toEqual({
                results: [{ uid: 'component1' }, { uid: 'component2' }],
                response: [{ uid: 'component1' }, { uid: 'component2' }]
            } as unknown as Page<CMSItem>);
        });
    });
});
