/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* forbiddenNameSpaces useValue:false */
import { forwardRef, Component, Inject, Injector, Input, SimpleChanges } from '@angular/core';
import { TreeComponent } from './TreeComponent';
import { TreeService } from './TreeService';
import { ITreeNodeItem, TREE_NODE } from './types';

const DEFAULT_PADDING_LEFT_MODIFIER_PX = 10;

@Component({
    selector: 'se-tree-node-renderer',
    templateUrl: './TreeNodeRendererComponent.html'
})
export class TreeNodeRendererComponent<T, D> {
    @Input() node: ITreeNodeItem<T>;

    public nodeComponentInjector: Injector;

    constructor(
        @Inject(forwardRef(() => TreeComponent)) private tree: TreeComponent<T, D>,
        private treeService: TreeService<T, D>,
        private injector: Injector
    ) {}

    ngOnInit(): void {
        this.createNodeComponentInjector();
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.node) {
            this.createNodeComponentInjector();
        }
    }

    public toggle($event: Event): void {
        $event.stopPropagation();
        $event.preventDefault();

        this.tree.isDropDisabled = true;
        this.treeService.toggle(this.node).then(() => {
            this.tree.isDropDisabled = false;
        });
    }

    public onMouseOver(): void {
        this.node.setMouseHovered(true);
        this.treeService.update();
    }

    public onMouseOut(): void {
        this.node.setMouseHovered(false);
        this.treeService.update();
    }

    public getPaddingLeft(level: number): string {
        return `${(level - 1) * DEFAULT_PADDING_LEFT_MODIFIER_PX}px`;
    }

    get showAsList(): boolean {
        return this.tree.showAsList;
    }

    get isDisabled(): boolean {
        return this.tree.isDropDisabled;
    }

    get collapsed(): boolean {
        return !this.node.isExpanded;
    }

    get displayDefaultTemplate(): boolean {
        return !this.tree.removeDefaultTemplate;
    }

    get isRootNodeDescendant(): boolean {
        return this.node.parentUid === 'root';
    }

    public getNavigationTreeIconIsCollapse(): string {
        return this.node.isExpanded ? 'se.arrowicon.collapse.title' : 'se.arrowicon.expand.title';
    }

    private createNodeComponentInjector(): void {
        this.nodeComponentInjector = Injector.create({
            providers: [
                {
                    provide: TREE_NODE,
                    useValue: this.node
                }
            ],
            parent: this.injector
        });
    }
}
