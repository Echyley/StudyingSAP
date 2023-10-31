/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NavigationEditorNodeService } from 'cmssmarteditcontainer/components/navigation/navigationEditor/NavigationEditorNodeService';
import { NodeAncestryService } from 'cmssmarteditcontainer/components/navigation/navigationEditor/NodeAncestryService';
import {
    apiUtils,
    IRestService,
    IRestServiceFactory,
    NavigationNode,
    functionsUtils,
    CmsitemsRestService
} from 'smarteditcommons';

describe('NavigationEditorNodeServiceTest - ', () => {
    let navigationEditorNodeService: NavigationEditorNodeService;
    let navigationNodeRestService: jasmine.SpyObj<IRestService<NavigationNode>>;
    let cmsitemsRestService: jasmine.SpyObj<CmsitemsRestService>;
    let nodeAncestryService: jasmine.SpyObj<NodeAncestryService>;
    let restServiceFactory: jasmine.SpyObj<IRestServiceFactory>;

    const uriParams = {
        siteId: 'siteId',
        catalogId: 'catalogId',
        catalogVersion: 'catalogVersion'
    };

    const node: any = {
        uid: '1',
        entries: [
            {
                itemId: 'Item-ID-1.1',
                itemType: 'CMSLinkComponent',
                navigationNodeUid: '1',
                uid: '1',
                name: 'Entry 1.1',
                parent: {
                    uid: 'bla'
                }
            },
            {
                itemId: 'Item-ID-1.2',
                itemType: 'CMSLinkComponent',
                navigationNodeUid: '1',
                uid: '2',
                name: 'Entry 1.2',
                parent: {
                    uid: 'bla'
                }
            },
            {
                itemId: 'Item-ID-1.3',
                itemType: 'CMSLinkComponent',
                navigationNodeUid: '1',
                uid: '3',
                name: 'Entry 1.3',
                parent: {
                    uid: 'bla'
                }
            }
        ],
        name: 'node1',
        nodes: [],
        title: {
            en: 'node1_en',
            fr: 'node1_fr'
        },
        parentUid: 'root',
        position: 0,
        parent: {
            uid: 'root'
        }
    };

    const nodes = [
        {
            uid: '1',
            entries: [],
            name: 'node1',
            title: {
                en: 'node1_en',
                fr: 'node1_fr'
            },
            parentUid: 'root',
            position: 0,
            hasChildren: true,
            hasEntries: true
        },
        {
            uid: '2',
            entries: [],
            name: 'node2',
            title: {
                en: 'node2_en',
                fr: 'node2_fr'
            },
            parentUid: 'root',
            position: 1,
            hasChildren: true,
            hasEntries: false
        },
        {
            uid: '4',
            entries: [],
            name: 'node4',
            title: {
                en: 'nodeA',
                fr: 'nodeA'
            },
            parentUid: '1',
            position: 0,
            hasChildren: true,
            hasEntries: true
        },
        {
            uid: '5',
            entries: [],
            name: 'node5',
            title: {
                en: 'nodeB',
                fr: 'nodeB'
            },
            parentUid: '1',
            position: 1,
            hasChildren: false,
            hasEntries: false
        },
        {
            uid: '6',
            entries: [],
            name: 'node6',
            title: {
                en: 'nodeC',
                fr: 'nodeC'
            },
            parentUid: '2',
            position: 0,
            hasChildren: false,
            hasEntries: false
        },
        {
            uid: '7',
            entries: [],
            name: 'node7',
            title: {
                en: 'nodeC',
                fr: 'nodeC'
            },
            parentUid: '1',
            position: 2,
            hasChildren: false,
            hasEntries: false
        },
        {
            uid: '8',
            entries: [],
            name: 'node8',
            title: {
                en: 'nodeC',
                fr: 'nodeC'
            },
            parentUid: '4',
            position: 0,
            hasChildren: true,
            hasEntries: true
        },
        {
            uid: '9',
            entries: [],
            name: 'node9',
            title: {
                en: 'nodeC',
                fr: 'nodeC'
            },
            parentUid: '8',
            position: 0,
            hasChildren: false,
            hasEntries: false
        }
    ];

    beforeEach(() => {
        cmsitemsRestService = jasmine.createSpyObj<CmsitemsRestService>('cmsitemsRestService', [
            'getById',
            'update'
        ]);

        nodeAncestryService = jasmine.createSpyObj<NodeAncestryService>('nodeAncestryService', [
            'buildOrderedListOfAncestors'
        ]);
        nodeAncestryService.buildOrderedListOfAncestors.and.callThrough();

        navigationNodeRestService = jasmine.createSpyObj<IRestService<NavigationNode>>(
            'navigationNodeRestService',
            ['get', 'update']
        );

        restServiceFactory = jasmine.createSpyObj<IRestServiceFactory>('restServiceFactory', [
            'get'
        ]);
        restServiceFactory.get.and.returnValue(navigationNodeRestService);

        navigationEditorNodeService = new NavigationEditorNodeService(
            cmsitemsRestService,
            nodeAncestryService,
            restServiceFactory
        );
    });

    describe('updateNavigationNodePosition', () => {
        const PARENT_UUID = 'someParentUuuid';
        const UUID_A = 'uuid_a';
        const UUID_B = 'uuid_b';
        const UUID_C = 'uuid_c';

        // create a node that by default points to the parent node returned by
        // cmsitems getById below, and has some defaults
        function getBaseNode(uuid = UUID_A, position = 0): any {
            return {
                uuid,
                position,
                parent: {
                    uuid: PARENT_UUID
                }
            };
        }

        beforeEach(() => {
            cmsitemsRestService.getById.and.returnValue(
                Promise.resolve<any>({
                    uuid: PARENT_UUID,
                    children: [UUID_A, UUID_B, UUID_C]
                })
            );
            cmsitemsRestService.update.and.returnValue(Promise.resolve<any>(true));
        });

        it('GIVEN a nodes parent node does not exist WHEN I try to update the position of that node THEN it fails', async () => {
            cmsitemsRestService.getById.and.returnValue(Promise.reject());

            try {
                await navigationEditorNodeService.updateNavigationNodePosition(node);

                functionsUtils.assertFail();
            } catch (error) {
                expect(error).toBeUndefined();
            }
        });

        it('WHEN I try to update the position of a node AND the position is undefined THEN it fails', async () => {
            const baseNode = getBaseNode();
            baseNode.position = undefined;

            try {
                await navigationEditorNodeService.updateNavigationNodePosition(baseNode);

                functionsUtils.assertFail();
            } catch (error) {
                expect(error).toBeDefined();
            }
        });

        it('GIVEN a node is referencing the wrong parent (parent does not contain the node in its children) WHEN I try to update the position of that node THEN it fails', async () => {
            const baseNode = getBaseNode();
            baseNode.uuid = 'any value not a uuid in parent';

            try {
                await navigationEditorNodeService.updateNavigationNodePosition(baseNode);

                functionsUtils.assertFail();
            } catch (error) {
                expect(error).toBeDefined();
            }
        });

        describe('GIVEN a parent with 3 children A,B,C, with positions 0, 1 and 2 respectively', () => {
            it('WHEN I move A to 1 THEN parents chilren is updated correctly with B,A,C', async () => {
                const data = getBaseNode(UUID_A, 1);

                await navigationEditorNodeService.updateNavigationNodePosition(data);

                expect(cmsitemsRestService.update).toHaveBeenCalledWith(
                    jasmine.objectContaining({
                        children: [UUID_B, UUID_A, UUID_C]
                    })
                );
            });

            it('WHEN I move A to 2 THEN parents children is updated correctly with B,C,A', async () => {
                const data = getBaseNode(UUID_A, 2);

                await navigationEditorNodeService.updateNavigationNodePosition(data);

                expect(cmsitemsRestService.update).toHaveBeenCalledWith(
                    jasmine.objectContaining({
                        children: [UUID_B, UUID_C, UUID_A]
                    })
                );
            });

            it('WHEN I move B to same location (1) THEN parents children is updated correctly with A,B,C', async () => {
                const data = getBaseNode(UUID_B, 1);

                await navigationEditorNodeService.updateNavigationNodePosition(data);

                expect(cmsitemsRestService.update).toHaveBeenCalledWith(
                    jasmine.objectContaining({
                        children: [UUID_A, UUID_B, UUID_C]
                    })
                );
            });

            it('WHEN I move B to 0 THEN parents children is updated correctly with B,A,C', async () => {
                const data = getBaseNode(UUID_B, 0);

                await navigationEditorNodeService.updateNavigationNodePosition(data);

                expect(cmsitemsRestService.update).toHaveBeenCalledWith(
                    jasmine.objectContaining({
                        children: [UUID_B, UUID_A, UUID_C]
                    })
                );
            });

            it('WHEN I move B to 2 THEN parents chilren is updated correctly with A,C,B', async () => {
                const data = getBaseNode(UUID_B, 2);

                await navigationEditorNodeService.updateNavigationNodePosition(data);

                expect(cmsitemsRestService.update).toHaveBeenCalledWith(
                    jasmine.objectContaining({
                        children: [UUID_A, UUID_C, UUID_B]
                    })
                );
            });

            it('WHEN I move C to same location (2) THEN parents chilren is updated correctly with A,B,C', async () => {
                const data = getBaseNode(UUID_C, 2);

                await navigationEditorNodeService.updateNavigationNodePosition(data);

                expect(cmsitemsRestService.update).toHaveBeenCalledWith(
                    jasmine.objectContaining({
                        children: [UUID_A, UUID_B, UUID_C]
                    })
                );
            });

            it('WHEN I move C to 0 THEN parents children is updated correctly with C,A,B', async () => {
                const data = getBaseNode(UUID_C, 0);

                await navigationEditorNodeService.updateNavigationNodePosition(data);

                expect(cmsitemsRestService.update).toHaveBeenCalledWith(
                    jasmine.objectContaining({
                        children: [UUID_C, UUID_A, UUID_B]
                    })
                );
            });

            it('WHEN I move C to 1 THEN parents children is updated correctly with A,C,B', async () => {
                const data = getBaseNode(UUID_C, 1);

                await navigationEditorNodeService.updateNavigationNodePosition(data);

                expect(cmsitemsRestService.update).toHaveBeenCalledWith(
                    jasmine.objectContaining({
                        children: [UUID_A, UUID_C, UUID_B]
                    })
                );
            });
        });
    });

    it('WHEN the server returns too many nodes for the ancestry THEN the service still filters, orders and formats the list', async () => {
        navigationNodeRestService.get.and.returnValue(
            Promise.resolve<any>({
                sompropertyName: nodes
            })
        );

        await navigationEditorNodeService.getNavigationNodeAncestry('8', uriParams);

        expect(navigationNodeRestService.get).toHaveBeenCalledWith({
            ancestorTrailFrom: '8',
            siteId: 'siteId',
            catalogId: 'catalogId',
            catalogVersion: 'catalogVersion'
        });
        expect(nodeAncestryService.buildOrderedListOfAncestors).toHaveBeenCalledWith(
            apiUtils.getDataFromResponse({
                sompropertyName: nodes
            }),
            '8'
        );
    });
});
