/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    NavigationNodePickerComponent,
    NavigationNodePickerRenderComponent
} from 'cmssmarteditcontainer/components/navigation';
import {
    SystemEventService,
    IUriContext,
    TreeService,
    NavigationNodeItem,
    NavigationNodeItemDTO,
    CmsitemsRestService
} from 'smarteditcommons';

describe('NavigationNodePickerComponent', () => {
    let component: NavigationNodePickerComponent;
    let cmsitemsRestService: jasmine.SpyObj<CmsitemsRestService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;

    const mockNode = {
        uid: 'uid2',
        uuid: 'uuid2'
    } as NavigationNodeItem;
    const mockResult = {
        response: [{ uid: 'uid1', uuid: 'uuid1' }, mockNode, { uid: 'uid3', uuid: 'uuid3' }]
    };
    const mockUriContext: IUriContext = {
        CURRENT_CONTEXT_SITE_ID: 'siteId',
        CURRENT_CONTEXT_CATALOG: 'catalogId',
        CURRENT_CONTEXT_CATALOG_VERSION: 'catalogVersion'
    };

    beforeEach(() => {
        cmsitemsRestService = jasmine.createSpyObj<CmsitemsRestService>('cmsitemsRestService', [
            'get'
        ]);
        cmsitemsRestService.get.and.returnValue(Promise.resolve<any>(mockResult));

        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'publishAsync'
        ]);

        component = new NavigationNodePickerComponent(cmsitemsRestService, systemEventService);
        component.uriContext = mockUriContext;
    });

    describe('initialize', () => {
        it('WHEN editable is not defined THEN it should set to true AND set other fields', () => {
            component.ngOnInit();

            expect(component.nodeURI).toEqual(
                '/cmswebservices/v1/sites/siteId/catalogs/catalogId/versions/catalogVersion/navigations'
            );
            expect(component.nodePickerRenderComponent).toEqual(
                NavigationNodePickerRenderComponent
            );
            expect(component.removeDefaultTemplate).toEqual(true);
            expect(component.rootNodeUid).toEqual('root');
            expect(component.editable).toEqual(true);
            expect(component.actions).toEqual({
                pick: jasmine.any(Function),
                isEditable: jasmine.any(Function)
            });
        });

        it('WHEN editable is false THEN it should set to false AND set other fields', () => {
            component.editable = false;
            component.ngOnInit();

            expect(component.nodeURI).toEqual(
                '/cmswebservices/v1/sites/siteId/catalogs/catalogId/versions/catalogVersion/navigations'
            );
            expect(component.nodePickerRenderComponent).toEqual(
                NavigationNodePickerRenderComponent
            );
            expect(component.removeDefaultTemplate).toEqual(true);
            expect(component.rootNodeUid).toEqual('root');
            expect(component.editable).toEqual(false);
            expect(component.actions).toEqual({
                pick: jasmine.any(Function),
                isEditable: jasmine.any(Function)
            });
        });
    });

    describe('Pick Action', () => {
        type pickMethodType = (
            treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
            node: NavigationNodeItem
        ) => Promise<void>;
        let treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>;
        let pickMethod: pickMethodType;

        beforeEach(() => {
            treeService = {} as TreeService<NavigationNodeItem, NavigationNodeItemDTO>;

            component.ngOnInit();

            pickMethod = component.actions.pick as pickMethodType;
        });

        it('should get items, find one with the same uid and publish it', async () => {
            await pickMethod(treeService, mockNode);

            expect(cmsitemsRestService.get).toHaveBeenCalledWith({
                pageSize: 10,
                currentPage: 0,
                mask: mockNode.uid,
                typeCode: 'CMSNavigationNode',
                itemSearchParams: ''
            });
            expect(systemEventService.publishAsync).toHaveBeenCalledWith('selected_node', {
                nodeUuid: mockNode.uuid,
                nodeUid: mockNode.uid
            });
        });
    });
});
