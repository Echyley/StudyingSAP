/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NodeAncestryService } from 'cmssmarteditcontainer/components/navigation/navigationEditor/NodeAncestryService';
import { TreeNodeWithLevel } from 'smarteditcommons';

describe('NodeAncestryServiceTest - ', () => {
    const mockNavigationNodes = [
        {
            entries: [],
            hasChildren: true,
            name: 'Footer Pages',
            parentUid: 'ApparelDENavNode',
            position: 3,
            title: {},
            uid: 'FooterNavNode',
            uuid: 'eyJpdGVtSWQiOiJGb290ZXJOYXZOb2RlIiwiY2F0YWxvZ0lkIjoiYXBwYXJlbC1kZUNvbnRlbnRDYXRhbG9nIiwiY2F0YWxvZ1ZlcnNpb24iOiJTdGFnZWQifQ==',
            parent: undefined,
            itemType: undefined
        },

        {
            entries: [],
            hasChildren: true,
            name: 'Apparel DE Site',
            parentUid: 'SiteRootNode',
            position: 0,
            title: {
                de: 'Apparel DE Site'
            },
            uid: 'ApparelDENavNode',
            uuid: 'eyJpdGVtSWQiOiJBcHBhcmVsREVOYXZOb2RlIiwiY2F0YWxvZ0lkIjoiYXBwYXJlbC1kZUNvbnRlbnRDYXRhbG9nIiwiY2F0YWxvZ1ZlcnNpb24iOiJTdGFnZWQifQ==',
            parent: undefined,
            itemType: undefined
        },

        {
            entries: [],
            hasChildren: true,
            name: 'SiteRootNode',
            parentUid: 'root',
            position: 0,
            title: {},
            uid: 'SiteRootNode',
            uuid: 'eyJpdGVtSWQiOiJTaXRlUm9vdE5vZGUiLCJjYXRhbG9nSWQiOiJhcHBhcmVsLWRlQ29udGVudENhdGFsb2ciLCJjYXRhbG9nVmVyc2lvbiI6IlN0YWdlZCJ9',
            parent: undefined,
            itemType: undefined
        }
    ];

    let nodeAncestryService: NodeAncestryService;
    beforeEach(() => {
        nodeAncestryService = new NodeAncestryService();
    });

    it('WHEN called THEN it converts Navigation Nodes to an array of nodes, ordered by level starting from the root node', () => {
        const ancestors = nodeAncestryService.buildOrderedListOfAncestors(
            mockNavigationNodes,
            'FooterNavNode'
        );

        const expected = mockNavigationNodes.reverse() as unknown as TreeNodeWithLevel[];
        expected[0].level = 0;
        expected[0].formattedLevel = 'se.cms.navigationcomponent.management.node.level.root';
        expected[1].level = 1;
        expected[1].formattedLevel = 'se.cms.navigationcomponent.management.node.level.non.root';
        expected[2].level = 2;
        expected[2].formattedLevel = 'se.cms.navigationcomponent.management.node.level.non.root';

        expect(ancestors).toEqual(expected);
    });
});
