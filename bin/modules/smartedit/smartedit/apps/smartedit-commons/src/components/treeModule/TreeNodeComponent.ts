/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CdkDragDrop } from '@angular/cdk/drag-drop';
import { Component, Input } from '@angular/core';
import { LogService } from '@smart/utils';
import { TreeDragAndDropService } from './TreeDragAndDropService';
import { ITreeNodeItem } from './types';

@Component({
    selector: 'se-tree-node',
    templateUrl: './TreeNodeComponent.html'
})
export class TreeNodeComponent<T, D> {
    @Input() source: ITreeNodeItem<T>[];

    constructor(
        private readonly treeDragAndDropService: TreeDragAndDropService<T, D>,
        private readonly logService: LogService
    ) {}

    public get isDisabled(): boolean {
        return !this.treeDragAndDropService.isDragEnabled;
    }

    public onDrop(event: CdkDragDrop<ITreeNodeItem<T>[]>): void {
        this.treeDragAndDropService.handleDrop(event).catch((reason) => {
            this.logService.debug(`TreeNodeComponent - onDrop: error: ${reason}`);
        });
    }
}
