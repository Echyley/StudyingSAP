/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    NavigationNodeItem,
    TreeDragAndDropEvent,
    TreeDragAndDropOptions,
    TypedMap
} from 'smarteditcommons';

type dragAndDropFunction = (event: TreeDragAndDropEvent<NavigationNodeItem>) => void;

@Injectable()
export class NavigationEditorTreeDragOptions {
    private dragAndDropHandler: dragAndDropFunction;

    /**
     * Sets the dragAndDrop callback handler
     *
     * @param dragAndDropFunc Function to be called when `onDropCallback` is called
     */
    public setup(dragAndDropFunc: dragAndDropFunction): void {
        this.dragAndDropHandler = dragAndDropFunc;
    }

    /**
     * Exposes methods of this service to a literal object.
     *
     * This literal object is used by `TreeComponent#setNodeActions` (smarteditcommons)
     * It sets new context and "inject" param for all methods using `.bind`
     *
     * It is done this way, so TreeComponent can easily iterate over the methods, if we would passed instance of this class it wouldn't be easy to iterate over these methods
     */
    public getDragOptions(): TreeDragAndDropOptions<NavigationNodeItem> {
        return {
            onDropCallback: (event: TreeDragAndDropEvent<NavigationNodeItem>): void =>
                this.onDropCallback(event),
            allowDropCallback: this.allowDropCallback,
            beforeDropCallback: this.beforeDropCallback
        };
    }

    private onDropCallback(event: TreeDragAndDropEvent<NavigationNodeItem>): void {
        this.dragAndDropHandler(event);
    }

    private allowDropCallback(event: TreeDragAndDropEvent<NavigationNodeItem>): boolean {
        return event.sourceNode.parent.uid === event.destinationNodes[0].parent.uid;
    }

    private beforeDropCallback(
        event: TreeDragAndDropEvent<NavigationNodeItem>
    ): Promise<TypedMap<string> | boolean> {
        if (event.sourceNode.parent.uid !== event.destinationNodes[0].parent.uid) {
            return Promise.resolve({
                confirmDropI18nKey: 'se.cms.navigationmanagement.navnode.confirmation'
            });
        }
        return Promise.resolve(true);
    }
}
