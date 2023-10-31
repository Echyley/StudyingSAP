/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { NAVIGATION_MANAGEMENT_RESOURCE_URI } from 'cmscommons';
import {
    IRestService,
    IRestServiceFactory,
    apiUtils,
    TreeNodeWithLevel,
    NavigationNodeItem,
    IUriContext,
    NavigationNode,
    CmsitemsRestService
} from 'smarteditcommons';
import { NavigationNodeCMSItem } from '../types';
import { NodeAncestryService } from './NodeAncestryService';

/*
 * This service updates the navigation node by making REST call to the cmswebservices navigation API.
 */
@Injectable()
export class NavigationEditorNodeService {
    private readonly navigationNodeRestService: IRestService<NavigationNode>;

    constructor(
        private readonly cmsitemsRestService: CmsitemsRestService,
        private readonly nodeAncestryService: NodeAncestryService,
        restServiceFactory: IRestServiceFactory
    ) {
        this.navigationNodeRestService = restServiceFactory.get(NAVIGATION_MANAGEMENT_RESOURCE_URI);
    }

    public getNavigationNode(nodeUid: string, uriParams: IUriContext): Promise<NavigationNode> {
        const payload = {
            identifier: nodeUid,
            ...uriParams
        };
        return this.navigationNodeRestService.get(payload);
    }

    /**
     * Updates the position of the navigation node within the children collection of its parent.
     * Fetches the parent node, and reorders the children, then updates the parent with the new child order.
     *
     * @param node The navigation node to be updated.
     *
     * E.g. Navigation Management Page -> Edit Node
     */
    public async updateNavigationNodePosition(
        node: NavigationNodeItem
    ): Promise<NavigationNodeCMSItem> {
        const parentNode = await this.cmsitemsRestService.getById<NavigationNodeCMSItem>(
            node.parent.uuid
        );
        parentNode.children = parentNode.children || [];
        const currentIndex = parentNode.children.findIndex((child) => child === node.uuid);
        const targetIndex = node.position;
        if (currentIndex < 0 || node.position === undefined) {
            throw new Error(
                `navigationEditorNodeService.updateNavigationNodePosition() - invalid index: move FROM [${currentIndex}] TO [${targetIndex}]`
            );
        }

        // update parent children with the new child
        parentNode.children.splice(targetIndex, 0, parentNode.children.splice(currentIndex, 1)[0]);
        parentNode.identifier = parentNode.uuid;
        return this.cmsitemsRestService.update(parentNode);
    }

    /**
     * Returns the list of nodes belonging to the ancestry of the node identified by its uid. This list includes the queried node as well.
     */
    public async getNavigationNodeAncestry(
        nodeUid: string,
        uriParams: IUriContext
    ): Promise<TreeNodeWithLevel[]> {
        const payload = {
            ancestorTrailFrom: nodeUid,
            ...uriParams
        };
        const response = await this.navigationNodeRestService.get(payload);

        return this.nodeAncestryService.buildOrderedListOfAncestors(
            apiUtils.getDataFromResponse(response),
            nodeUid
        );
    }
}
