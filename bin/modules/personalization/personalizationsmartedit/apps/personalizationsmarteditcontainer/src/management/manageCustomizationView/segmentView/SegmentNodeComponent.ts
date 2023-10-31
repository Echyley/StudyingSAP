/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

import {
    CdkDrag,
    CdkDragDrop,
    CdkDragStart,
    CdkDragMove,
    CdkDropList
} from '@angular/cdk/drag-drop';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { isEqual, cloneDeep } from 'lodash';
import { Trigger, TriggerAction, TriggerActionId, TriggerType } from 'personalizationcommons';
import { FetchStrategy, IConfirmationModalService, stringUtils } from 'smarteditcommons';
import { TriggerService } from '../TriggerService';
import { SegmentDragAndDropService } from './SegmentDragAndDropService';

@Component({
    selector: 'segment-node',
    templateUrl: './SegmentNodeComponent.html',
    styleUrls: ['./SegmentNodeComponent.scss']
})
export class SegmentNodeComponent {
    @Input() expression: Trigger[];
    @Input() node: Trigger;
    @Input() connectedDropListsIds: string[];
    @Output() expressionChange: EventEmitter<Trigger[]>;
    @Output() onDrop: EventEmitter<CdkDragDrop<Trigger>>;
    @Output() onDragStart: EventEmitter<CdkDragStart>;

    public fetchStrategy: FetchStrategy<TriggerAction>;
    public collapsed: boolean;

    private elementToDuplicate: Trigger;

    constructor(
        private triggerService: TriggerService,
        private confirmationModalService: IConfirmationModalService,
        private translateService: TranslateService,
        private dragDropService: SegmentDragAndDropService
    ) {
        this.connectedDropListsIds = [];
        this.expressionChange = new EventEmitter<Trigger[]>();
        this.onDrop = new EventEmitter<CdkDragDrop<Trigger>>();
        this.onDragStart = new EventEmitter<CdkDragStart>();
        this.collapsed = false;
        this.elementToDuplicate = null;
    }

    ngOnInit(): void {
        this.fetchStrategy = {
            fetchAll: (): Promise<TriggerAction[]> => Promise.resolve(this.triggerService.actions)
        };
    }

    public onDragDrop(event: CdkDragDrop<Trigger>): void {
        this.onDrop.emit(event);
    }

    public onDragStarted(event: CdkDragStart<any>): void {
        /**
         * Because this component is displayed in some old ui-modal (legacy) which z-index starts with 1040 and by default angular cdk sets z-index 1000, dragged element is displayed "behind" modal.
         * That's why it has to be overwritten while drag starts to be displayed at the top
         *
         * In current angular cdk (8.2.3) there's no possibility to set starting z-index in config (like in newer versions)
         * */
        event.source.element.nativeElement.style.zIndex = '2000';
        this.onDragStart.emit(event);
    }

    public onDragMoved(event: CdkDragMove<any>): void {
        this.dragDropService.dragMoved(event);
    }

    public onDragReleased(): void {
        this.dragDropService.dragReleased();
    }

    public canBeDropped = (drag: CdkDrag, drop: CdkDropList): boolean => {
        const isNotItem = !this.isItem(this.node);
        if (this.dragDropService.currentHoverDropListId === null) {
            return isNotItem;
        }

        return drop.id === this.dragDropService.currentHoverDropListId && isNotItem;
    };

    public newSubItem(type: TriggerType): void {
        this.node.nodes.unshift({
            uid: stringUtils.generateIdentifier(),
            type,
            operation:
                type === TriggerType.ITEM_TYPE
                    ? ({} as TriggerAction)
                    : cloneDeep(this.triggerService.actions[0]),
            nodes: []
        });
        this.collapsed = false;
        this.expressionChange.emit(this.expression);
    }

    public async removeItem(uid: string): Promise<void> {
        if (this.triggerService.isNotEmptyContainer(this.node)) {
            await this.confirmationModalService.confirm({
                description: this.translateService.instant(
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.segments.removecontainerconfirmation'
                )
            });

            // When confirm modal is cancelled it will not reach here
            // See more in https://github.tools.sap/cx-commerce/smartedit/blob/6e5efcac39c923a65db327c7b2da8468fc962f97/smartedit/apps/smartedit-container/src/services/ConfirmationModalServiceOuter.ts#L55
            this.removeFromNode(uid);
            this.expressionChange.emit(this.expression);
        } else {
            this.removeFromNode(uid);
            this.expressionChange.emit(this.expression);
        }
    }

    public operationChange(operation: TriggerActionId): void {
        this.node.operation = this.triggerService.actions.find((action) => action.id === operation);
        this.expressionChange.emit(this.expression);
    }

    public toggle(): void {
        this.collapsed = !this.collapsed;
    }

    public duplicateItem(elementToDuplicate: Trigger): void {
        this.elementToDuplicate = elementToDuplicate;
        this.expression[0].nodes.some(this.findElementAndDuplicate, this);
        this.expressionChange.emit(this.expression);
    }

    public handleUpdate(exp: Trigger[]): void {
        this.expressionChange.emit(exp);
    }

    public isContainerWithDropzone(element: Trigger): boolean {
        return this.isContainer(element) && element.nodes.length === 1;
    }

    public isItem(element: Trigger): boolean {
        return this.triggerService.isItem(element);
    }

    public isContainer(element: Trigger): boolean {
        return this.triggerService.isContainer(element);
    }

    public isTopContainer(): boolean {
        return isEqual(this.expression[0], this.node);
    }

    public isEmptyContainer(node: Trigger): boolean {
        return this.isContainer(node) && node.nodes.length === 0;
    }

    private findElementAndDuplicate(element: Trigger, index: number, array: Trigger[]): boolean {
        // 'this' is additional argument passed to function so in this case it is the component's 'this'
        if (this.elementToDuplicate === element) {
            array.splice(
                index,
                0,
                cloneDeep({
                    ...this.elementToDuplicate,
                    uid: stringUtils.generateIdentifier()
                })
            );
            return true;
        }
        if (this.isContainer(element)) {
            element.nodes.some(this.findElementAndDuplicate, this); // recursive call to check all sub containers
        }
        return false;
    }

    private removeFromNode(uid: string): void {
        const target = this.findParentByUid(uid);

        target.nodes = target.nodes.filter((node) => node.uid !== uid);
    }

    private findParentByUid(uid: string): Trigger | null {
        let target: Trigger = null;

        // recursive search
        const findParent = (parent: Trigger): void => {
            const hasChild = (parent.nodes || []).find((n) => n.uid === uid);

            if (hasChild) {
                target = parent;
                return;
            }

            for (let i = 0; i < parent.nodes.length; i++) {
                if (parent.nodes[i].uid) {
                    findParent(parent.nodes[i]);
                }
            }
        };

        // Searching for target
        findParent(this.expression[0]);
        return target;
    }
}
