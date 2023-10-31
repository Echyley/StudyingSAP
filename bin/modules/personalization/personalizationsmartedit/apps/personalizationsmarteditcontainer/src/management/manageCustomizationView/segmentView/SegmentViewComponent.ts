/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { Component, EventEmitter, Inject, Input, OnInit, Output } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { cloneDeep } from 'lodash';
import {
    PersonalizationsmarteditMessageHandler,
    PersonalizationsmarteditUtils,
    Segment,
    TargetGroupState,
    Trigger,
    TriggerAction,
    TriggerType
} from 'personalizationcommons';
import { FetchStrategy, SelectReset, stringUtils, TypedMap, YJQUERY_TOKEN } from 'smarteditcommons';
import { PersonalizationsmarteditRestService } from '../../../service/PersonalizationsmarteditRestService';
import { TriggerService } from '../TriggerService';
import { SegmentItemPrinterComponent } from './SegmentItemPrinterComponent';
import { SegmentNodeComponent } from './SegmentNodeComponent';

@Component({
    selector: 'segment-view',
    templateUrl: './SegmentViewComponent.html'
})
export class SegmentViewComponent implements OnInit {
    @Input() targetGroupState: TargetGroupState;
    @Output() expressionChange: EventEmitter<Trigger[]>;

    public expression: Trigger[];
    public segmentPrinterComponent: typeof SegmentItemPrinterComponent;
    public nodeTemplate: typeof SegmentNodeComponent;
    public elementToScroll: JQuery<HTMLElement>;
    public scrollZoneVisible: boolean;
    public actions: TriggerAction[];
    public segments: Segment[];
    public singleSegment: Segment;
    public segmentFetchStrategy: FetchStrategy<Segment>;
    public resetSelect: SelectReset;
    public connectedDropListsIds: string[];

    private triggers: Trigger[];
    private triggerLookupMap: TypedMap<Trigger>;

    constructor(
        private personalizationsmarteditRestService: PersonalizationsmarteditRestService,
        private personalizationsmarteditMessageHandler: PersonalizationsmarteditMessageHandler,
        private triggerService: TriggerService,
        private personalizationsmarteditUtils: PersonalizationsmarteditUtils,
        private translateService: TranslateService,
        @Inject(YJQUERY_TOKEN) private yjQuery: JQueryStatic
    ) {
        this.expressionChange = new EventEmitter();
        this.segments = [];
        this.singleSegment = { code: null };
        this.triggerLookupMap = {};
        this.actions = this.triggerService.actions;
        this.segmentPrinterComponent = SegmentItemPrinterComponent;
        this.nodeTemplate = SegmentNodeComponent;
        this.connectedDropListsIds = [];
    }

    ngOnInit(): void {
        this.triggers = this.triggers || (this.targetGroupState.selectedVariation || {}).triggers;
        this.expression = this.expression || this.targetGroupState.expression;
        if (this.triggers && this.triggers.length > 0) {
            this.expression = this.triggerService.buildData(this.triggers);
        } else {
            this.expression = [
                {
                    uid: stringUtils.generateIdentifier(),
                    type: TriggerType.CONTAINER_TYPE,
                    operation: this.actions[0],
                    nodes: []
                }
            ];
        }
        this.syncExpressionOnTriggerData();
        this.elementToScroll = this.yjQuery('.se-slider-panel__body');

        this.segmentFetchStrategy = {
            fetchPage: (search?: string, pageSize?: number, currentPage?: number): Promise<any> =>
                this.loadSegmentItems(search, pageSize, currentPage),
            fetchEntity: (id: string): Promise<Segment> =>
                Promise.resolve(this.segments.find((segment) => segment.code === id))
        };
        this.getIdsRecursive(this.rootExpression);
    }

    public get rootExpression(): Trigger {
        return this.expression?.length ? this.expression[0] : null;
    }

    public segmentSelectedEvent(itemCode: string): void {
        if (!itemCode) {
            this.singleSegment = { code: null };
            return;
        }
        const item = this.segments.find((segment) => segment.code === itemCode);
        this.expression = [
            {
                ...this.rootExpression,
                nodes: [
                    {
                        type: TriggerType.ITEM_TYPE,
                        operation: null,
                        selectedSegment: item,
                        nodes: [],
                        uid: stringUtils.generateIdentifier()
                    } as Trigger
                ].concat(this.rootExpression.nodes)
            }
        ];
        this.getIdsRecursive(this.rootExpression);

        setTimeout(() => {
            this.resetSelect();
        });

        this.syncExpressionOnTriggerData();
    }

    public handleTreeUpdated(expression: Trigger[]): void {
        this.triggerLookupMap = {};
        this.expression = cloneDeep(expression);
        this.getIdsRecursive(this.rootExpression);
        this.syncExpressionOnTriggerData();
    }

    public onDropHandler(event: CdkDragDrop<Trigger>): void {
        this.scrollZoneVisible = false;

        if (event.container.id === event.previousContainer.id) {
            const container = this.triggerLookupMap[event.container.id];
            moveItemInArray(container.nodes, event.previousIndex, event.currentIndex);
        } else {
            // Add to new list
            const target = this.triggerLookupMap[event.container.id];
            target.nodes.splice(event.currentIndex, 0, event.item.data);
            // remove from previous
            const previous = this.triggerLookupMap[event.previousContainer.id];
            previous.nodes.splice(
                previous.nodes.findIndex((n) => n.uid === event.item.data.uid),
                1
            );
        }

        this.handleTreeUpdated(this.expression);
    }

    public onDragStart(): void {
        this.scrollZoneVisible = this.isScrollZoneVisible();
    }

    public async loadSegmentItems(
        search: string,
        pageSize: number,
        currentPage: number
    ): Promise<any> {
        try {
            const response = await this.personalizationsmarteditRestService.getSegments({
                code: search,
                pageSize,
                currentPage
            });
            this.personalizationsmarteditUtils.uniqueArray(this.segments, response.segments || []);
            return {
                ...response,
                results: response.results.map((item) => ({
                    ...item,
                    id: item.code
                }))
            };
        } catch {
            this.personalizationsmarteditMessageHandler.sendError(
                this.translateService.instant('personalization.error.gettingsegments')
            );
        }
    }

    private syncExpressionOnTriggerData(): void {
        this.expressionChange.emit(this.expression);
    }

    private getIdsRecursive(item: Trigger): void {
        this.triggerLookupMap[item.uid] = item;
        (item.nodes || []).forEach((childItem) => {
            if (!this.triggerService.isItem(childItem)) {
                this.getIdsRecursive(childItem);
            }
        });

        // We reverse ids here to respect items nesting hierarchy
        this.connectedDropListsIds = Object.keys(this.triggerLookupMap).reverse();
    }
    private isScrollZoneVisible(): boolean {
        const element = this.yjQuery('.se-slider-panel__body').get(0);
        if (element) {
            return element.scrollHeight > element.clientHeight;
        }

        return false;
    }
}
