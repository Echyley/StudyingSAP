import { Injector, SimpleChanges } from '@angular/core';
import { TreeComponent } from './TreeComponent';
import { TreeService } from './TreeService';
import { ITreeNodeItem } from './types';
export declare class TreeNodeRendererComponent<T, D> {
    private tree;
    private treeService;
    private injector;
    node: ITreeNodeItem<T>;
    nodeComponentInjector: Injector;
    constructor(tree: TreeComponent<T, D>, treeService: TreeService<T, D>, injector: Injector);
    ngOnInit(): void;
    ngOnChanges(changes: SimpleChanges): void;
    toggle($event: Event): void;
    onMouseOver(): void;
    onMouseOut(): void;
    getPaddingLeft(level: number): string;
    get showAsList(): boolean;
    get isDisabled(): boolean;
    get collapsed(): boolean;
    get displayDefaultTemplate(): boolean;
    get isRootNodeDescendant(): boolean;
    getNavigationTreeIconIsCollapse(): string;
    private createNodeComponentInjector;
}
