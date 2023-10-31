/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { NAVIGATION_NODE_TYPECODE } from 'cmscommons';
import { ICatalogService, IUriContext, Payload } from 'smarteditcommons';
import { GenericEditorModalService } from '../../../services/GenericEditorModalService';
import { NavigationNode } from '../types';

/**
 * Convenience service to open an editor modal window for a given navigation node's data.
 */
@Injectable()
export class NavigationNodeEditorModalService {
    constructor(
        private genericEditorModalService: GenericEditorModalService,
        private catalogService: ICatalogService
    ) {}

    /**
     *
     * Opens a modal for creating and editing a navigation node with the CSM items API. Leave the current parameter to trigger
     * a creation operation.
     *
     * @param uriContext The uri context of the navigational node.
     * @param parent The parent navigational node.
     * @param current The current navigational node. If the current node is left empty, the modal
     * will process a creation operation.
     */
    public async open(
        uriContext: IUriContext,
        parent: NavigationNode,
        current?: NavigationNode
    ): Promise<Payload> {
        // If the current node is provided then the content will resolve null to indicate an editing process.
        let content = null;

        if (!current) {
            const catalogVersion = await this.catalogService.getCatalogVersionUUid(uriContext);

            content = {
                catalogVersion,
                parent: parent.uuid,
                itemtype: NAVIGATION_NODE_TYPECODE,
                visible: true
            };
        }

        return this.genericEditorModalService.open(
            {
                componentUuid: current ? current.uuid : null,
                componentType: NAVIGATION_NODE_TYPECODE,
                content,
                title: 'se.cms.navigationmanagement.node.edit.title'
            },
            (item: Payload) => item,
            null,
            {
                dialogPanelClass: 'modal-stretched'
            }
        );
    }
}
