/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NavigationEditorNodeService } from 'cmssmarteditcontainer/components/navigation/navigationEditor/NavigationEditorNodeService';
import { NavigationEditorTreeActions } from 'cmssmarteditcontainer/components/navigation/navigationEditor/NavigationEditorTreeActionsService';
import { NavigationNodeEditorModalService } from 'cmssmarteditcontainer/components/navigation/navigationNodeEditor/NavigationNodeEditorModalService';
import {
    IConfirmationModalService,
    ITreeNodeItem,
    LogService,
    NavigationNodeItem,
    NavigationNodeItemDTO,
    TreeDragAndDropEvent,
    TreeService,
    functionsUtils,
    CmsitemsRestService,
    IUserTrackingService
} from 'smarteditcommons';

describe('NavigationEditorTreeActionsService', () => {
    let service: NavigationEditorTreeActions;
    let logService: jasmine.SpyObj<LogService>;
    let cmsitemsRestService: jasmine.SpyObj<CmsitemsRestService>;
    let confirmationModalService: jasmine.SpyObj<IConfirmationModalService>;
    let navigationEditorNodeService: jasmine.SpyObj<NavigationEditorNodeService>;
    let navigationNodeEditorModalService: jasmine.SpyObj<NavigationNodeEditorModalService>;

    let treeService: jasmine.SpyObj<TreeService<NavigationNodeItem, NavigationNodeItemDTO>>;
    let userTrackingService: jasmine.SpyObj<IUserTrackingService>;

    beforeEach(() => {
        logService = jasmine.createSpyObj<LogService>('logService', ['warn', 'error']);
        cmsitemsRestService = jasmine.createSpyObj<CmsitemsRestService>('cmsitemsRestService', [
            'get',
            'delete'
        ]);
        confirmationModalService = jasmine.createSpyObj<IConfirmationModalService>(
            'confirmationModalService',
            ['confirm']
        );
        navigationEditorNodeService = jasmine.createSpyObj<NavigationEditorNodeService>(
            'navigationEditorNodeService',
            ['updateNavigationNodePosition', 'getNavigationNode']
        );
        navigationNodeEditorModalService = jasmine.createSpyObj<NavigationNodeEditorModalService>(
            'navigationNodeEditorModalService',
            ['open']
        );

        treeService = jasmine.createSpyObj<TreeService<NavigationNodeItem, NavigationNodeItemDTO>>(
            'treeService',
            ['getNodeById', 'toggle', 'fetchChildren']
        );

        userTrackingService = jasmine.createSpyObj<IUserTrackingService>('userTrackingService', [
            'trackingUserAction'
        ]);

        service = new NavigationEditorTreeActions(
            logService,
            cmsitemsRestService,
            confirmationModalService,
            navigationEditorNodeService,
            navigationNodeEditorModalService,
            userTrackingService
        );
    });

    it('WHEN setup is called THEN it should assign service properties', () => {
        service.setup(false, 'nodeName', { context: 'context' });

        expect((service as any).readOnly).toEqual(false);
        expect((service as any).rootNodeUid).toEqual('nodeName');
        expect((service as any).uriContext).toEqual({ context: 'context' });
    });

    it('WHEN getActions is called THEN it should return object with methods', () => {
        const options = service.getActions();

        const methodNames = Object.keys(options);
        expect(methodNames).toEqual([
            'isReadOnly',
            'hasChildren',
            'fetchData',
            'removeItem',
            'performMove',
            'dragAndDrop',
            'moveUp',
            'moveDown',
            'isMoveUpAllowed',
            'isMoveDownAllowed',
            'refreshNode',
            'refreshParentNode',
            'editNavigationNode',
            'addTopLevelNode',
            'getEntryString',
            'getEntryTooltipString',
            'addNewChild',
            'addNewSibling',
            'getDropdownItems',
            '_findNodeById',
            '_expandIfNeeded'
        ]);
    });

    describe('Actions', () => {
        beforeEach(() => {
            const freshNode = {
                uid: 'root',
                entries: [{ foo: 'bar' }]
            } as unknown as ITreeNodeItem<NavigationNodeItem>;

            service.setup(false, 'nodeName', { context: 'context' });

            cmsitemsRestService.get.and.returnValue(
                Promise.resolve<any>({
                    response: [freshNode]
                })
            );
        });

        describe('isReadOnly', () => {
            it('should return the value from setup', () => {
                const { isReadOnly } = service.getActions();
                const actual = isReadOnly();

                expect(actual).toEqual(false);
            });
        });

        describe('hasChildren', () => {
            it('should return value from nodeData', () => {
                const mockNodeData = { hasChildren: true } as NavigationNodeItem;

                const { hasChildren } = service.getActions();
                const actual = hasChildren(treeService, mockNodeData);

                expect(actual).toEqual(true);
            });
        });

        describe('fetchData', () => {
            let mockNodeData: ITreeNodeItem<NavigationNodeItem>;

            beforeEach(() => {
                mockNodeData = {
                    initiated: false,
                    removeAllNodes: jasmine.createSpy()
                } as unknown as ITreeNodeItem<NavigationNodeItem>;
            });

            it('WHEN node uid is root node uid THEN it should mark node data as not initiated, get node fresh data, remove its nodes and fetch children from tree service', async () => {
                mockNodeData.uid = 'root';

                const { fetchData } = service.getActions();
                await fetchData(treeService, mockNodeData);

                expect(mockNodeData).toEqual({
                    initiated: false,
                    removeAllNodes: jasmine.any(Function),
                    uid: 'root',
                    entries: [{ foo: 'bar' }]
                } as unknown as ITreeNodeItem<NavigationNodeItem>);

                expect(mockNodeData.removeAllNodes).toHaveBeenCalled();
                expect(treeService.fetchChildren).toHaveBeenCalledWith({
                    initiated: false,
                    removeAllNodes: jasmine.any(Function),
                    uid: 'root',
                    entries: [{ foo: 'bar' }]
                } as any);
            });

            it('WHEN node uid is not root node uid AND it is initiated THEN it should return node data nodes', async () => {
                const nodes = [
                    { uid: 'node1', itemType: 'node' },
                    { uid: 'node2', itemType: 'node' }
                ] as unknown as ITreeNodeItem<NavigationNodeItem>[];
                mockNodeData.initiated = true;
                mockNodeData.nodes = nodes;

                const { fetchData } = service.getActions();
                const actual = await fetchData(treeService, mockNodeData);

                expect(actual).toEqual(nodes);
            });
        });

        describe('removeItem', () => {
            it('WHEN it is read only THEN it should throw an error', async () => {
                service.setup(true, 'nodeName', { context: 'context' });

                const { removeItem } = service.getActions();
                try {
                    await removeItem(treeService, {});
                    functionsUtils.assertFail();
                } catch (e) {
                    expect(e).toEqual('navigation.in.readonly.mode');
                }
            });

            it('WHEN removal is confirmed THEN it should make a request to delete node, and another request to refresh nodes AND track user action', async () => {
                confirmationModalService.confirm.and.returnValue(Promise.resolve() as any);
                const mockNodeData = {
                    uuid: 'nodeUUID',
                    parent: {
                        uuid: 'parentNodeUUID',
                        setInitiated: jasmine.createSpy()
                    }
                } as unknown as ITreeNodeItem<NavigationNodeItem>;

                const { removeItem } = service.getActions();
                await removeItem(treeService, mockNodeData);

                expect(confirmationModalService.confirm).toHaveBeenCalledWith({
                    description:
                        'se.cms.navigationmanagement.navnode.removenode.confirmation.message',
                    title: 'se.cms.navigationmanagement.navnode.removenode.confirmation.title'
                });
                expect(cmsitemsRestService.delete).toHaveBeenCalledWith('nodeUUID');
                expect(mockNodeData.parent.setInitiated).toHaveBeenCalledWith(false);
                expect(treeService.fetchChildren).toHaveBeenCalledWith(mockNodeData.parent);
                expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
            });

            it('WHEN removal is NOT confirmed THEN it should do nothing', async () => {
                confirmationModalService.confirm.and.returnValue(Promise.reject());
                const mockNodeData = {
                    uuid: 'nodeUUID',
                    parent: {
                        uuid: 'parentNodeUUID',
                        setInitiated: jasmine.createSpy()
                    }
                } as unknown as ITreeNodeItem<NavigationNodeItem>;

                const { removeItem } = service.getActions();
                await removeItem(treeService, mockNodeData);

                expect(confirmationModalService.confirm).toHaveBeenCalledWith({
                    description:
                        'se.cms.navigationmanagement.navnode.removenode.confirmation.message',
                    title: 'se.cms.navigationmanagement.navnode.removenode.confirmation.title'
                });
                expect(cmsitemsRestService.delete).not.toHaveBeenCalled();
                expect(mockNodeData.parent.setInitiated).not.toHaveBeenCalled();
                expect(treeService.fetchChildren).not.toHaveBeenCalled();
            });
        });

        describe('performMove', () => {
            it('WHEN it is read only THEN it should throw an error', async () => {
                service.setup(true, 'nodeName', { context: 'context' });

                const { performMove } = service.getActions();
                try {
                    await performMove(treeService, {});
                    functionsUtils.assertFail();
                } catch (e) {
                    expect(e).toEqual('navigation.in.readonly.mode');
                }
            });

            it('WHEN updating navigation fails THEN it should log it', async () => {
                navigationEditorNodeService.updateNavigationNodePosition.and.returnValue(
                    Promise.reject('error msg')
                );

                const { performMove } = service.getActions();
                await performMove(treeService, {});

                expect(logService.error).toHaveBeenCalledWith(
                    'Error updating node position:\nerror msg'
                );
            });

            it('WHEN provided node param is empty THEN it should fetch new data for treeService root', async () => {
                const rootNode = {
                    uid: 'root',
                    removeAllNodes: jasmine.createSpy()
                } as unknown as ITreeNodeItem<NavigationNodeItem> & jasmine.Spy;
                treeService.root = rootNode;

                const { performMove } = service.getActions();
                await performMove(treeService, null, true);

                expect(treeService.fetchChildren).toHaveBeenCalledWith(rootNode);
            });

            it('WHEN it should refresh itself THEN it should call tree service with given node param', async () => {
                const node = {
                    uid: 'node',
                    removeAllNodes: jasmine.createSpy(),
                    setInitiated: jasmine.createSpy()
                } as unknown as ITreeNodeItem<NavigationNodeItem>;

                const { performMove } = service.getActions();
                await performMove(treeService, node, true);

                expect(treeService.fetchChildren).toHaveBeenCalledWith(node);
            });

            it('WHEN node is provided and it should not refresh itself THEN it should refresh parent node', async () => {
                const node = {
                    uid: 'node',
                    parent: {
                        uid: 'parentNode',
                        removeAllNodes: jasmine.createSpy(),
                        setInitiated: jasmine.createSpy()
                    }
                } as unknown as ITreeNodeItem<NavigationNodeItem>;

                const { performMove } = service.getActions();
                await performMove(treeService, node);

                expect(treeService.fetchChildren).toHaveBeenCalledWith(node.parent);
            });
        });

        describe('dragAndDrop', () => {
            it('WHEN destination is not provided THEN it should do nothing', async () => {
                const event = {
                    sourceNode: {
                        uid: 'uid1',
                        setParent: jasmine.createSpy()
                    },
                    destinationNodes: [{ uid: 'uid1' }]
                } as unknown as TreeDragAndDropEvent<NavigationNodeItem>;
                spyOn(service as any, 'performMove');

                const { dragAndDrop } = service.getActions();
                await dragAndDrop(treeService, event);

                expect(event.sourceNode.setParent).not.toHaveBeenCalled();
                expect((service as any).performMove).not.toHaveBeenCalled();
            });

            it('WHEN element has not been moved THEN it should do nothing', async () => {
                const event = {
                    position: 1,
                    sourceNode: {
                        uid: 'uid1',
                        position: 1,
                        parentUid: 'parentUid',
                        setParent: jasmine.createSpy()
                    },
                    destinationNodes: [
                        {
                            uid: 'uid2',
                            parent: {
                                uid: 'parentUid'
                            }
                        }
                    ]
                } as unknown as TreeDragAndDropEvent<NavigationNodeItem>;
                spyOn(service as any, 'performMove');

                const { dragAndDrop } = service.getActions();
                await dragAndDrop(treeService, event);

                expect(event.sourceNode.setParent).not.toHaveBeenCalled();
                expect((service as any).performMove).not.toHaveBeenCalled();
            });

            it('WHEN element has been moved THEN it should perform move', async () => {
                const event = {
                    position: 1,
                    sourceParentNode: {
                        uid: 'parentUid'
                    },
                    sourceNode: {
                        uid: 'uid1',
                        position: 2,
                        parentUid: 'parentUid',
                        setParent: jasmine.createSpy()
                    },
                    destinationParentNode: {
                        uid: 'parentUid'
                    },
                    destinationNodes: [
                        {
                            uid: 'uid2',
                            parent: {
                                uid: 'parentUid'
                            }
                        }
                    ]
                } as unknown as TreeDragAndDropEvent<NavigationNodeItem>;
                spyOn(service as any, 'performMove');
                spyOn(service as any, 'refreshNode');

                const { dragAndDrop } = service.getActions();
                await dragAndDrop(treeService, event);

                expect((service as any).performMove).toHaveBeenCalledWith(
                    treeService,
                    event.sourceNode,
                    true
                );
                expect((service as any).refreshNode).not.toHaveBeenCalled();
            });

            it('WHEN element has been moved to different parent THEN it should perform move and refresh source node', async () => {
                const event = {
                    position: 1,
                    sourceParentNode: {
                        uid: 'parentUid'
                    },
                    sourceNode: {
                        uid: 'uid1',
                        position: 2,
                        parentUid: 'parentUid',
                        setParent: jasmine.createSpy()
                    },
                    destinationParentNode: {
                        uid: 'parentUid2'
                    },
                    destinationNodes: [
                        {
                            uid: 'uid2',
                            parent: {
                                uid: 'parentUid'
                            }
                        }
                    ]
                } as unknown as TreeDragAndDropEvent<NavigationNodeItem>;
                spyOn(service as any, 'performMove');
                spyOn(service as any, 'refreshNode');

                const { dragAndDrop } = service.getActions();
                await dragAndDrop(treeService, event);

                expect((service as any).performMove).toHaveBeenCalledWith(
                    treeService,
                    event.sourceNode,
                    true
                );
                expect((service as any).refreshNode).toHaveBeenCalledWith(
                    treeService,
                    event.sourceParentNode
                );
            });
        });

        describe('moveUp', () => {
            it('WHEN it is read only THEN it should throw an error', async () => {
                service.setup(true, 'nodeName', { context: 'context' });

                const { moveUp } = service.getActions();
                try {
                    await moveUp(treeService, {});
                    functionsUtils.assertFail();
                } catch (e) {
                    expect(e).toEqual('navigation.in.readonly.mode');
                }
            });

            it('WHEN is not read only it should perform move AND track user action', async () => {
                const node = { position: 3 } as unknown as NavigationNodeItem;

                spyOn(service as any, 'performMove');
                const { moveUp } = service.getActions();

                await moveUp(treeService, node);

                expect((service as any).performMove).toHaveBeenCalledWith(treeService, {
                    ...node,
                    position: 2
                });
                expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
            });
        });

        describe('moveDown', () => {
            it('WHEN it is read only THEN it should throw an error', async () => {
                service.setup(true, 'nodeName', { context: 'context' });

                const { moveDown } = service.getActions();
                try {
                    await moveDown(treeService, {});
                    functionsUtils.assertFail();
                } catch (e) {
                    expect(e).toEqual('navigation.in.readonly.mode');
                }
            });

            it('WHEN is not read only it should perform move AND track user action', async () => {
                const node = { position: 3 } as unknown as NavigationNodeItem;

                spyOn(service as any, 'performMove');
                const { moveDown } = service.getActions();

                await moveDown(treeService, node);

                expect((service as any).performMove).toHaveBeenCalledWith(treeService, {
                    ...node,
                    position: 4
                });
                expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
            });
        });

        describe('isMoveUpAllowed', () => {
            it('WHEN current position is 0 THEN it should return false', () => {
                const node = { position: 0 } as unknown as NavigationNodeItem;

                const { isMoveUpAllowed } = service.getActions();
                const actual = isMoveUpAllowed(treeService, node);

                expect(actual).toEqual(false);
            });

            it('WHEN current position is 0 THEN it should return false and getDropdownItems move up item condition is false', () => {
                const node = {
                    position: 0,
                    parent: { nodes: [{}, {}] }
                } as unknown as NavigationNodeItem;

                const { isMoveUpAllowed, isMoveDownAllowed, getDropdownItems } =
                    service.getActions();
                const moveUpAllowed = isMoveUpAllowed(treeService, node);
                const moveDownAllowed = isMoveDownAllowed(treeService, node);
                const dropDownItem = getDropdownItems(treeService);

                expect(moveUpAllowed).toEqual(false);
                expect(moveDownAllowed).toEqual(true);
                expect(dropDownItem[2].condition(node)).toEqual(false);
                expect(dropDownItem[3].condition(node)).toEqual(true);
            });

            it('WHEN current position is greater than 0 THEN it should return true', () => {
                const node = { position: 1 } as unknown as NavigationNodeItem;

                const { isMoveUpAllowed } = service.getActions();
                const actual = isMoveUpAllowed(treeService, node);

                expect(actual).toEqual(true);
            });
        });

        describe('isMoveDownAllowed', () => {
            it('WHEN current position is greater than parent nodes - 1 count THEN it should return false', () => {
                const node = {
                    position: 2,
                    parent: { nodes: [{}, {}] }
                } as unknown as NavigationNodeItem;

                const { isMoveDownAllowed } = service.getActions();
                const actual = isMoveDownAllowed(treeService, node);

                expect(actual).toEqual(false);
            });

            it('WHEN current position greater than parent nodes -1 THEN it should return false and getDropdownItems move down item condition is false', () => {
                const node = {
                    position: 2,
                    parent: { nodes: [{}, {}] }
                } as unknown as NavigationNodeItem;

                const { isMoveUpAllowed, isMoveDownAllowed, getDropdownItems } =
                    service.getActions();
                const moveUpAllowed = isMoveUpAllowed(treeService, node);
                const moveDownAllowed = isMoveDownAllowed(treeService, node);
                const dropDownItem = getDropdownItems(treeService);

                expect(moveUpAllowed).toEqual(true);
                expect(moveDownAllowed).toEqual(false);
                expect(dropDownItem[2].condition(node)).toEqual(true);
                expect(dropDownItem[3].condition(node)).toEqual(false);
            });

            it('WHEN current position is equal parent nodes - 1 count THEN it should return false', () => {
                const node = {
                    position: 1,
                    parent: { nodes: [{}, {}] }
                } as unknown as NavigationNodeItem;

                const { isMoveDownAllowed } = service.getActions();
                const actual = isMoveDownAllowed(treeService, node);

                expect(actual).toEqual(false);
            });

            it('WHEN current position is lower than parent nodes - 1 count THEN it should return false', () => {
                const node = {
                    position: 0,
                    parent: { nodes: [{}, {}] }
                } as unknown as NavigationNodeItem;

                const { isMoveDownAllowed } = service.getActions();
                const actual = isMoveDownAllowed(treeService, node);

                expect(actual).toEqual(true);
            });
        });

        describe('refreshNode', () => {
            it('should call treeService with given node and return refreshed nodes', async () => {
                const node = {
                    uid: 'nodeuid',
                    setInitiated: jasmine.createSpy()
                } as unknown as NavigationNodeItem;
                treeService.fetchChildren.and.returnValue(Promise.resolve([node]));

                const { refreshNode } = service.getActions();
                const actual = await refreshNode(treeService, node);

                expect(actual).toEqual([node]);
                expect(treeService.fetchChildren).toHaveBeenCalledWith(node);
                expect(node.setInitiated).toHaveBeenCalledWith(false);
            });
        });

        describe('refreshParentNode', () => {
            it('should call treeService with given node parent and return refreshed nodes', async () => {
                const parent = {
                    uid: 'parentuid',
                    setInitiated: jasmine.createSpy()
                } as unknown as NavigationNodeItem;
                const node = {
                    uid: 'nodeuid',
                    setInitiated: jasmine.createSpy(),
                    parent
                } as unknown as NavigationNodeItem;
                treeService.fetchChildren.and.returnValue(Promise.resolve([node]));

                const { refreshParentNode } = service.getActions();
                const actual = await refreshParentNode(treeService, node);

                expect(actual).toEqual([node]);
                expect(treeService.fetchChildren).toHaveBeenCalledWith(parent);
                expect(parent.setInitiated).toHaveBeenCalledWith(false);
            });
        });

        describe('editNavigationNode', () => {
            it('WHEN edit modal is closed THEN it should do nothing', async () => {
                navigationNodeEditorModalService.open.and.returnValue(Promise.reject());

                const { editNavigationNode } = service.getActions();
                await editNavigationNode(treeService, {});

                expect(navigationEditorNodeService.getNavigationNode).not.toHaveBeenCalled();
                expect(logService.warn).toHaveBeenCalled();
            });

            it('WHEN edit modal is confirmed AND parent node is root THEN is should refresh this node AND track user action', async () => {
                navigationNodeEditorModalService.open.and.returnValue(Promise.resolve() as any);
                navigationEditorNodeService.getNavigationNode.and.returnValue(
                    Promise.resolve<any>({ title: 'node title' })
                );
                spyOn(service as any, 'refreshNode');

                const node = {
                    uid: 'nodeUid',
                    parent: { uid: 'root' }
                } as unknown as NavigationNodeItem;

                const { editNavigationNode } = service.getActions();
                await editNavigationNode(treeService, node);

                expect(navigationEditorNodeService.getNavigationNode).toHaveBeenCalledWith(
                    'nodeUid',
                    {
                        context: 'context'
                    }
                );
                expect((service as any).refreshNode).toHaveBeenCalledWith(treeService, {
                    ...node,
                    title: 'node title'
                });
                expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
            });

            it('WHEN edit modal is confirmed AND parent node is NOT root THEN is should refresh parent of this node AND track user action', async () => {
                navigationNodeEditorModalService.open.and.returnValue(Promise.resolve() as any);
                navigationEditorNodeService.getNavigationNode.and.returnValue(
                    Promise.resolve<any>({ title: 'node title' })
                );
                spyOn(service as any, 'refreshNode');

                const node = {
                    uid: 'nodeUid',
                    parent: { uid: 'parentUid' }
                } as unknown as NavigationNodeItem;

                const { editNavigationNode } = service.getActions();
                await editNavigationNode(treeService, node);

                expect(navigationEditorNodeService.getNavigationNode).toHaveBeenCalledWith(
                    'parentUid',
                    {
                        context: 'context'
                    }
                );
                expect((service as any).refreshNode).toHaveBeenCalledWith(treeService, {
                    ...node.parent,
                    title: 'node title'
                });
                expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
            });
        });

        describe('addTopLevelNode', () => {
            it('WHEN creation was successful THEN it should refresh the data AND track user action', async () => {
                const serviceAsAny = service as any;
                spyOn(serviceAsAny, 'addNewChild');
                spyOn(serviceAsAny, 'getNavigationNodeCMSItemByUid');
                spyOn(serviceAsAny, 'fetchData');
                serviceAsAny.addNewChild.and.returnValue(Promise.resolve({}));
                serviceAsAny.getNavigationNodeCMSItemByUid.and.returnValue(
                    Promise.resolve({ uid: 'newNodeUid' })
                );

                const { addTopLevelNode } = service.getActions();
                await addTopLevelNode(treeService, { uid: 'node' });

                expect(serviceAsAny.addNewChild).toHaveBeenCalledWith(treeService, { uid: 'node' });
                // nodeName comes from rootNodeUid passed when service is initialized;
                expect(serviceAsAny.getNavigationNodeCMSItemByUid).toHaveBeenCalledWith('nodeName');
                expect(serviceAsAny.fetchData).toHaveBeenCalledWith(treeService, {
                    uid: 'newNodeUid'
                });
                expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
            });

            it('WHEN node was not created THEN it should do nothing AND track user action', async () => {
                const serviceAsAny = service as any;
                spyOn(serviceAsAny, 'addNewChild');
                spyOn(serviceAsAny, 'getNavigationNodeCMSItemByUid');
                spyOn(serviceAsAny, 'fetchData');
                serviceAsAny.addNewChild.and.returnValue(Promise.resolve(null));

                const { addTopLevelNode } = service.getActions();
                await addTopLevelNode(treeService, { uid: 'node' });

                expect(serviceAsAny.addNewChild).toHaveBeenCalledWith(treeService, { uid: 'node' });
                // nodeName comes from rootNodeUid passed when service is initialized;
                expect(serviceAsAny.getNavigationNodeCMSItemByUid).not.toHaveBeenCalled();
                expect(serviceAsAny.fetchData).not.toHaveBeenCalled();
                expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
            });
        });

        describe('getEntryString', () => {
            it('should return entries comma separated', () => {
                const node = {
                    entries: [
                        { name: 'entry 1', itemType: 'type1' },
                        { name: 'entry 2', itemType: 'type2' }
                    ]
                };

                const { getEntryString } = service.getActions();
                const actual = getEntryString(treeService, node);

                expect(actual).toEqual('entry 1 (type1), entry 2 (type2)');
            });

            it('should return empty string when there is no entries', () => {
                const node = {};

                const { getEntryString } = service.getActions();
                const actual = getEntryString(treeService, node);

                expect(actual).toEqual('');
            });
        });

        describe('getEntryTooltipString', () => {
            it('should return entries wrapped in div element', () => {
                const node = {
                    entries: [
                        { name: 'entry 1', itemType: 'type1' },
                        { name: 'entry 2', itemType: 'type2' }
                    ]
                };

                const { getEntryTooltipString } = service.getActions();
                const actual = getEntryTooltipString(treeService, node);

                expect(actual).toEqual(
                    '<div><div>entry 1 (type1)</div><div>entry 2 (type2)</div></div>'
                );
            });

            it('should return empty div element when there is no entries', () => {
                const node = {};

                const { getEntryTooltipString } = service.getActions();
                const actual = getEntryTooltipString(treeService, node);

                expect(actual).toEqual('<div></div>');
            });
        });

        describe('addNewChild', () => {
            let serviceAsAny: any;

            const nodeData = { uid: 'nodeData' } as NavigationNodeItem;

            beforeEach(() => {
                serviceAsAny = service as any;
                spyOn(serviceAsAny, '_findNodeById');
                spyOn(serviceAsAny, '_expandIfNeeded');
                spyOn(serviceAsAny, 'refreshNode');
            });

            it('WHEN node is provided AND navigation editor resolves THEN it should refresh node AND track user action', async () => {
                navigationNodeEditorModalService.open.and.returnValue(Promise.resolve() as any);
                const { addNewChild } = service.getActions();
                await addNewChild(treeService, nodeData);

                expect(serviceAsAny._findNodeById).not.toHaveBeenCalled();
                expect(serviceAsAny._expandIfNeeded).toHaveBeenCalledWith(treeService, nodeData);
                expect(serviceAsAny.refreshNode).toHaveBeenCalledWith(treeService, nodeData);
                expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
            });

            it('WHEN node is not provided and navigation editor resolves THEN it should find root node and then refresh node AND track user action', async () => {
                navigationNodeEditorModalService.open.and.returnValue(Promise.resolve() as any);
                serviceAsAny._findNodeById.and.returnValue(nodeData);
                const { addNewChild } = service.getActions();
                await addNewChild(treeService, null);

                expect(serviceAsAny._findNodeById).toHaveBeenCalledWith(treeService, 'nodeName');
                expect(serviceAsAny._expandIfNeeded).toHaveBeenCalledWith(treeService, null);
                expect(serviceAsAny.refreshNode).toHaveBeenCalledWith(treeService, nodeData);
                expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
            });

            it('WHEN navigation editor rejects THEN it should not refresh node and call logService', async () => {
                navigationNodeEditorModalService.open.and.returnValue(Promise.reject());
                const { addNewChild } = service.getActions();
                await addNewChild(treeService, nodeData);

                expect(serviceAsAny._findNodeById).not.toHaveBeenCalled();
                expect(serviceAsAny._expandIfNeeded).toHaveBeenCalledWith(treeService, nodeData);
                expect(serviceAsAny.refreshNode).not.toHaveBeenCalled();
                expect(logService.warn).toHaveBeenCalled();
                expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
            });
        });

        describe('addNewSibling', () => {
            let serviceAsAny: any;

            const nodeData = {
                uid: 'nodeData',
                parent: {
                    uid: 'parentUid'
                }
            } as NavigationNodeItem;

            beforeEach(() => {
                serviceAsAny = service as any;
                spyOn(serviceAsAny, 'refreshNode');
            });

            it('WHEN navigation editor resolves THEN it should refresh node AND track user action', async () => {
                navigationNodeEditorModalService.open.and.returnValue(Promise.resolve() as any);

                const { addNewSibling } = service.getActions();
                await addNewSibling(treeService, nodeData);

                expect(serviceAsAny.refreshNode).toHaveBeenCalledWith(treeService, nodeData.parent);
                expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
            });

            it('WHEN navigation editor rejects THEN it should do nothing and call logService AND track user action', async () => {
                navigationNodeEditorModalService.open.and.returnValue(Promise.reject());

                const { addNewSibling } = service.getActions();
                await addNewSibling(treeService, nodeData);

                expect(serviceAsAny.refreshNode).not.toHaveBeenCalled();
                expect(logService.warn).toHaveBeenCalled();
                expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
            });
        });

        describe('getDropdownItems', () => {
            it('should return dropdown items', () => {
                const { getDropdownItems } = service.getActions();
                const items = getDropdownItems(treeService);

                expect(items).toEqual([
                    {
                        key: 'se.cms.navigationmanagement.navnode.edit',
                        callback: jasmine.any(Function)
                    },
                    {
                        key: 'se.cms.navigationmanagement.navnode.removenode',
                        customCss: 'se-dropdown-item__delete',
                        callback: jasmine.any(Function)
                    },
                    {
                        key: 'se.cms.navigationmanagement.navnode.move.up',
                        condition: jasmine.any(Function),
                        callback: jasmine.any(Function)
                    },
                    {
                        key: 'se.cms.navigationmanagement.navnode.move.down',
                        condition: jasmine.any(Function),
                        callback: jasmine.any(Function)
                    },
                    {
                        key: 'se.cms.navigationmanagement.navnode.addchild',
                        callback: jasmine.any(Function)
                    },
                    {
                        key: 'se.cms.navigationmanagement.navnode.addsibling',
                        callback: jasmine.any(Function)
                    }
                ]);
            });
        });

        describe('_findNodeById', () => {
            it('should use treeService to get node', () => {
                const { _findNodeById } = service.getActions();
                _findNodeById(treeService, 'uid');

                expect(treeService.getNodeById).toHaveBeenCalledWith('uid');
            });
        });

        describe('_expandIfNeeded', () => {
            it('WHEN node data is not provided THEN it should resolve to void', async () => {
                const { _expandIfNeeded } = service.getActions();
                const actual = await _expandIfNeeded(treeService, null);

                expect(actual).toBe(undefined);
                expect(treeService.toggle).not.toHaveBeenCalled();
            });

            it('WHEN node data is provided AND it is already expanded THEN it should resolve to void', async () => {
                const { _expandIfNeeded } = service.getActions();
                const actual = await _expandIfNeeded(treeService, { isExpanded: true });

                expect(actual).toBe(undefined);
                expect(treeService.toggle).not.toHaveBeenCalled();
            });

            it('WHEN node data is provided AND it is not expanded THEN it should call treeService to expand it', async () => {
                const nodeData = {
                    uid: 'nodeUid',
                    isExpanded: false
                } as unknown as ITreeNodeItem<NavigationNodeItem>;
                treeService.toggle.and.returnValue([{ ...nodeData, isExpanded: true }] as any);
                const { _expandIfNeeded } = service.getActions();
                const actual = await _expandIfNeeded(treeService, nodeData);

                expect(actual).toEqual([{ ...nodeData, isExpanded: true }]);
                expect(treeService.toggle).toHaveBeenCalledWith(nodeData);
            });
        });
    });
});
