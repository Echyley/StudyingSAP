import { OnChanges, OnInit } from '@angular/core';
import { SegmentExpression, Trigger } from 'personalizationcommons';
import { TypedMap } from 'smarteditcommons';
import { TriggerService } from '../TriggerService';
export declare class SegmentExpressionAsHtmlComponent implements OnInit, OnChanges {
    private triggerService;
    segmentExpression: SegmentExpression | Trigger | undefined;
    expression: string[];
    readonly segmentActionI18n: TypedMap<string>;
    readonly operators: string[];
    readonly emptyGroup: string;
    readonly emptyGroupAndOperators: string[];
    constructor(triggerService: TriggerService);
    ngOnInit(): void;
    ngOnChanges(): void;
    private update;
    private buildExpression;
    private mapExpressionToString;
    private isSegmentExpression;
}
