/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Input, OnChanges, OnInit } from '@angular/core';
import { TriggerActionId, SegmentExpression, Trigger } from 'personalizationcommons';
import { TypedMap } from 'smarteditcommons';
import { TriggerService } from '../TriggerService';
@Component({
    selector: 'segment-expression-as-html',
    templateUrl: './SegmentExpressionAsHtmlComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SegmentExpressionAsHtmlComponent implements OnInit, OnChanges {
    @Input() segmentExpression: SegmentExpression | Trigger | undefined;

    public expression: string[];

    public readonly segmentActionI18n: TypedMap<string>;
    public readonly operators: string[];
    public readonly emptyGroup: string;
    public readonly emptyGroupAndOperators: string[];

    constructor(private triggerService: TriggerService) {
        this.expression = [];

        this.segmentActionI18n = {
            [TriggerActionId.AND]:
                'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.and',
            [TriggerActionId.OR]:
                'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.or',
            [TriggerActionId.NOT]:
                'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.not'
        };
        this.operators = ['AND', 'OR', 'NOT'];
        this.emptyGroup = '[]';
        this.emptyGroupAndOperators = this.operators.concat(this.emptyGroup);
    }

    ngOnInit(): void {
        if (!this.segmentExpression) {
            return;
        }
        this.update();
    }

    ngOnChanges(): void {
        this.update();
    }

    private update(): void {
        this.expression = this.isSegmentExpression(this.segmentExpression)
            ? this.buildExpression(this.segmentExpression)
            : this.mapExpressionToString(this.segmentExpression);
    }

    private buildExpression(segmentExpression: SegmentExpression): string[] {
        const expression = this.triggerService.buildData(segmentExpression)[0];

        return this.mapExpressionToString(expression);
    }

    private mapExpressionToString(expression: Trigger): string[] {
        return this.triggerService.getExpressionAsString(expression).split(' ');
    }

    private isSegmentExpression(expression: any): expression is SegmentExpression {
        return typeof expression.operation === 'undefined';
    }
}
