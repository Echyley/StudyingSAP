/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, OnInit, Input, ChangeDetectionStrategy } from '@angular/core';
import { NAVIGATION_MANAGEMENT_RESOURCE_URI, NAVIGATION_NODE_ROOT_NODE_UID } from 'cmscommons';
import {
    SystemEventService,
    IUriContext,
    URIBuilder,
    TypedMap,
    TreeService,
    NavigationNodeItem,
    NavigationNodeItemDTO,
    CmsitemsRestService,
    CMSItemSearch
} from 'smarteditcommons';
import { NavigationNodeUidsDTO } from '../../types';
import { SELECTED_NODE } from '../navigationNodeConstants';
import { NavigationNodePickerRenderComponent } from './NavigationNodePickerRenderComponent';

@Component({
    selector: 'se-navigation-node-picker',
    templateUrl: './NavigationNodePickerComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavigationNodePickerComponent implements OnInit {
    @Input() uriContext: IUriContext;
    @Input() editable: boolean;

    public nodeURI: string;
    public rootNodeUid: string;
    public removeDefaultTemplate: boolean;
    public actions: TypedMap<(...args: any[]) => void>;
    public nodePickerRenderComponent: typeof NavigationNodePickerRenderComponent;

    constructor(
        private cmsitemsRestService: CmsitemsRestService,
        private systemEventService: SystemEventService
    ) {
        this.nodePickerRenderComponent = NavigationNodePickerRenderComponent;
        this.removeDefaultTemplate = true;
        this.rootNodeUid = NAVIGATION_NODE_ROOT_NODE_UID;
        this.editable = true;
    }

    ngOnInit(): void {
        this.nodeURI = new URIBuilder(NAVIGATION_MANAGEMENT_RESOURCE_URI)
            .replaceParams(this.uriContext)
            .build();

        this.actions = {
            pick: async (
                treeService: TreeService<NavigationNodeItem, NavigationNodeItemDTO>,
                node: NavigationNodeItem
            ): Promise<void> => {
                const requestParams: CMSItemSearch = {
                    pageSize: 10,
                    currentPage: 0,
                    mask: node.uid,
                    typeCode: 'CMSNavigationNode',
                    itemSearchParams: ''
                };

                const result = await this.cmsitemsRestService.get(requestParams);
                const foundNode = result.response.find((element) => element.uid === node.uid);
                const idObject: NavigationNodeUidsDTO = {
                    nodeUuid: foundNode?.uuid,
                    nodeUid: node.uid
                };
                this.systemEventService.publishAsync(SELECTED_NODE, idObject);
            },

            isEditable: (): boolean => this.editable
        };
    }
}
