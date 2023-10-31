import { CdkDragDrop } from '@angular/cdk/drag-drop';
import { IAlertService, IConfirmationModalService } from '../../services/interfaces';
import { TreeService } from './TreeService';
import { ITreeNodeItem, TreeDragAndDropOptions } from './types';
export declare class TreeDragAndDropService<T, D> {
    private confirmationModalService;
    private alertService;
    private treeService;
    isDragEnabled: boolean;
    private config;
    constructor(confirmationModalService: IConfirmationModalService, alertService: IAlertService, treeService: TreeService<T, D>);
    init(options: TreeDragAndDropOptions<T>): void;
    handleDrop(event: CdkDragDrop<ITreeNodeItem<T>[]>): Promise<void>;
    private rearrangeNodes;
    private onDrop;
    private allowDrop;
    private beforeDrop;
}
