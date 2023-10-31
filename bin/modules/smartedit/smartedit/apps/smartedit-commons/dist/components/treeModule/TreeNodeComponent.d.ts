import { CdkDragDrop } from '@angular/cdk/drag-drop';
import { LogService } from '@smart/utils';
import { TreeDragAndDropService } from './TreeDragAndDropService';
import { ITreeNodeItem } from './types';
export declare class TreeNodeComponent<T, D> {
    private readonly treeDragAndDropService;
    private readonly logService;
    source: ITreeNodeItem<T>[];
    constructor(treeDragAndDropService: TreeDragAndDropService<T, D>, logService: LogService);
    get isDisabled(): boolean;
    onDrop(event: CdkDragDrop<ITreeNodeItem<T>[]>): void;
}
