/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NavigationNodeEditorModalService } from 'cmssmarteditcontainer/components/navigation/navigationNodeEditor/NavigationNodeEditorModalService';
import { NavigationNode } from 'cmssmarteditcontainer/components/navigation/types';
import { GenericEditorModalService } from 'cmssmarteditcontainer/services/GenericEditorModalService';
import { ICatalogService, IUriContext } from 'smarteditcommons';

describe('NavigationNodeEditorModalService', () => {
    let genericEditorModalService: jasmine.SpyObj<GenericEditorModalService>;
    let catalogService: jasmine.SpyObj<ICatalogService>;

    let navigationNodeEditorModalService: NavigationNodeEditorModalService;

    const uriContext: IUriContext = {
        siteId: 'SiteId'
    };

    const parentNode: NavigationNode = {
        hasChildren: false,
        name: 'ParentNode',
        parentUid: 'ParentNodeParent',
        position: 0,
        itemType: 'CMSNavigationNode',
        uid: 'ParentNodeUID',
        uuid: 'ParentNodeUUID',
        id: 'parent-id',
        nodes: [],
        parent: null
    };

    const currentNode: NavigationNode = {
        hasChildren: false,
        name: 'CurrentNode',
        parentUid: 'CurrentNodeParent',
        position: 0,
        itemType: 'CMSNavigationNode',
        uid: 'CurrentNodeUID',
        uuid: 'CurrentNodeUUID',
        id: 'current-id',
        nodes: [],
        parent: parentNode
    };

    beforeEach(() => {
        genericEditorModalService = jasmine.createSpyObj<GenericEditorModalService>(
            'genericEditorModalService',
            ['open']
        );
        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'getCatalogVersionUUid'
        ]);

        genericEditorModalService.open.and.returnValue(Promise.resolve({ status: 'success' }));
        catalogService.getCatalogVersionUUid.and.returnValue(Promise.resolve('catalogVersion'));

        navigationNodeEditorModalService = new NavigationNodeEditorModalService(
            genericEditorModalService,
            catalogService
        );
    });

    it('should open the generic editor modal in edit mode', async () => {
        const result = await navigationNodeEditorModalService.open(
            uriContext,
            parentNode,
            currentNode
        );

        expect(result).toEqual({ status: 'success' });
        expect(catalogService.getCatalogVersionUUid).not.toHaveBeenCalled();
        expect(genericEditorModalService.open).toHaveBeenCalledWith(
            {
                componentUuid: 'CurrentNodeUUID',
                componentType: 'CMSNavigationNode',
                content: null,
                title: 'se.cms.navigationmanagement.node.edit.title'
            },
            jasmine.any(Function),
            null,
            {
                dialogPanelClass: 'modal-stretched'
            }
        );
    });

    it('should open the generic editor modal in create mode', async () => {
        const result = await navigationNodeEditorModalService.open(uriContext, parentNode);

        expect(result).toEqual({ status: 'success' });
        expect(catalogService.getCatalogVersionUUid).toHaveBeenCalledWith({
            siteId: 'SiteId'
        });
        expect(genericEditorModalService.open).toHaveBeenCalledWith(
            {
                componentUuid: null,
                componentType: 'CMSNavigationNode',
                content: {
                    catalogVersion: 'catalogVersion',
                    parent: 'ParentNodeUUID',
                    itemtype: 'CMSNavigationNode',
                    visible: true
                },
                title: 'se.cms.navigationmanagement.node.edit.title'
            },
            jasmine.any(Function),
            null,
            {
                dialogPanelClass: 'modal-stretched'
            }
        );

        const saveCallback = genericEditorModalService.open.calls.argsFor(0)[1];
        expect(saveCallback({ payload: 'payload' })).toEqual({
            payload: 'payload'
        } as any);
    });
});
