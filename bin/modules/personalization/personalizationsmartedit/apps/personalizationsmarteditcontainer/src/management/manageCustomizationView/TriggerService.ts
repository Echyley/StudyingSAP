/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    TargetGroupState,
    TriggerType,
    TriggerActionId,
    Segment,
    TriggerAction,
    Trigger,
    ContainerTrigger,
    ExpressionData,
    SegmentTrigger,
    ExpressionTrigger
} from 'personalizationcommons';
import { stringUtils } from 'smarteditcommons';

@Injectable()
export class TriggerService {
    public readonly actions: TriggerAction[] = [
        {
            id: TriggerActionId.AND,
            name: 'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.and'
        },
        {
            id: TriggerActionId.OR,
            name: 'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.or'
        },
        {
            id: TriggerActionId.NOT,
            name: 'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.not'
        }
    ];
    private readonly supportedTypes = [
        TriggerType.DEFAULT_TRIGGER,
        TriggerType.SEGMENT_TRIGGER,
        TriggerType.EXPRESSION_TRIGGER
    ];

    public isContainer(element: Trigger): boolean {
        return this.isElementOfType(element, TriggerType.CONTAINER_TYPE);
    }

    public isEmptyContainer(element: Trigger): boolean {
        return this.isContainer(element) && element.nodes.length === 0;
    }

    public isNotEmptyContainer(element: Trigger): boolean {
        return this.isContainer(element) && element.nodes.length > 0;
    }

    public isDropzone(element: Trigger): boolean {
        return this.isElementOfType(element, TriggerType.DROPZONE_TYPE);
    }

    public isItem(element: Trigger): boolean {
        return this.isElementOfType(element, TriggerType.ITEM_TYPE);
    }

    public isValidExpression(element: Trigger): boolean {
        if (!element) {
            return false;
        }
        if (this.isContainer(element)) {
            return (
                element.nodes &&
                element.nodes.length > 0 &&
                element.nodes.every((node) => this.isValidExpression(node))
            );
        } else {
            return element.selectedSegment !== undefined;
        }
    }

    public buildTriggers(form: TargetGroupState, existingTriggers: Trigger[]): Trigger[] {
        let trigger = {} as Trigger;

        if (this.isDefaultData(form)) {
            trigger = this.buildDefaultTrigger();
        } else if (!!form?.expression && form.expression.length > 0) {
            const element = form.expression[0];

            if (this.isEmptyContainer(element)) {
                trigger = {} as Trigger;
            } else if (this.isExpressionData(element)) {
                trigger = this.buildExpressionTrigger(element);
            } else {
                trigger = this.buildSegmentTrigger(element);
            }
        }

        return this.mergeTriggers(existingTriggers, trigger);
    }

    public buildData(triggers: any): Trigger[] {
        let trigger = {} as Trigger;
        let data = this.getBaseData();
        if (triggers && Array.isArray(triggers) && triggers.length > 0) {
            trigger = triggers.filter((elem) => this.isSupportedTrigger(elem))[0];
        }
        if (this.isDefaultTrigger(trigger)) {
            // we leave baseData - it will be used if user removes default trigger
        } else if (this.isExpressionTrigger(trigger)) {
            data = this.buildExpressionTriggerData(trigger);
        } else if (this.isSegmentTrigger(trigger)) {
            data = this.buildSegmentTriggerData(trigger);
        }
        return data;
    }

    public isDefault(triggers: Trigger[]): boolean {
        const defaultTrigger = (triggers || []).filter((elem) => this.isDefaultTrigger(elem))[0];
        return triggers && defaultTrigger ? true : false;
    }

    public getExpressionAsString(expressionContainer: Trigger): string {
        let retStr = '';
        if (expressionContainer === undefined) {
            return retStr;
        }

        const currOperator = this.getCurrOperator(expressionContainer);
        retStr = this.getRetStr(retStr, expressionContainer) + '(';

        if (this.isEmptyContainer(expressionContainer)) {
            retStr += ' [] ';
        } else {
            expressionContainer.nodes.forEach((element, index) => {
                if (this.isEmptyContainer(element)) {
                    retStr = this.getEmptyEleContainerRetStr(retStr, index, currOperator);
                } else {
                    retStr = this.getNonEmptyEleContainerRetStr(retStr, index, currOperator);
                    retStr += this.isItem(element)
                        ? element.selectedSegment.code
                        : this.getExpressionAsString(element);
                }
            });
        }

        retStr += ')';

        return retStr;
    }

    private getCurrOperator(expressionContainer: Trigger): string {
        return this.isNegation(expressionContainer) ? 'AND' : expressionContainer.operation.id;
    }

    private getRetStr(retStr: string, expressionContainer: Trigger): string {
        retStr += this.isNegation(expressionContainer) ? ' NOT ' : '';
        return retStr;
    }

    private getEmptyEleContainerRetStr(
        retStr: string,
        index: number,
        currOperator: string
    ): string {
        const emptyStr = ' ';
        const emptyBracketsStr = ' ( [] )';
        const bracketsStr = '( [] )';
        retStr += index > 0 ? emptyStr + currOperator + emptyBracketsStr : bracketsStr;
        return retStr;
    }

    private getNonEmptyEleContainerRetStr(
        retStr: string,
        index: number,
        currOperator: string
    ): string {
        const emptyStr = ' ';
        retStr += index > 0 ? emptyStr + currOperator + emptyStr : '';
        return retStr;
    }

    private isElementOfType(element: Trigger | ExpressionData, myType: TriggerType): boolean {
        return typeof element !== 'undefined' ? element.type === myType : false;
    }

    private isNegation(element: any): boolean {
        return this.isContainer(element) && element.operation.id === 'NOT';
    }

    private isDefaultData(form?: TargetGroupState): boolean {
        return !!form?.isDefault;
    }

    private isExpressionData(element: Trigger): boolean {
        return (
            element.operation.id === TriggerActionId.NOT ||
            element.nodes.some((item) => !this.isItem(item))
        );
    }

    private isSupportedTrigger(trigger: Trigger): boolean {
        return this.supportedTypes.indexOf(trigger.type) >= 0;
    }

    private isDefaultTrigger(trigger: Trigger): boolean {
        return this.isElementOfType(trigger, TriggerType.DEFAULT_TRIGGER);
    }

    private isSegmentTrigger(trigger: Trigger): trigger is SegmentTrigger {
        return this.isElementOfType(trigger, TriggerType.SEGMENT_TRIGGER);
    }

    private isExpressionTrigger(trigger: Trigger): trigger is ExpressionTrigger {
        return this.isElementOfType(trigger, TriggerType.EXPRESSION_TRIGGER);
    }

    private isGroupExpressionData(expression: ExpressionData): boolean {
        return this.isElementOfType(expression, TriggerType.GROUP_EXPRESSION);
    }

    private isSegmentExpressionData(expression: ExpressionData): boolean {
        return this.isElementOfType(expression, TriggerType.SEGMENT_EXPRESSION);
    }

    private isNegationExpressionData(expression: ExpressionData): boolean {
        return this.isElementOfType(expression, TriggerType.NEGATION_EXPRESSION);
    }

    // ------------------------ FORM DATA -> TRIGGER ---------------------------

    private buildSegmentsForTrigger(element: Trigger): Segment[] {
        return element.nodes
            .filter((node) => this.isItem(node))
            .map((node) => node.selectedSegment);
    }

    private buildExpressionForTrigger(element: Trigger): any {
        if (this.isNegation(element)) {
            const negationElements: any[] = [];
            element.nodes.forEach((node) => {
                negationElements.push(this.buildExpressionForTrigger(node));
            });
            return {
                type: TriggerType.NEGATION_EXPRESSION,
                element: {
                    type: TriggerType.GROUP_EXPRESSION,
                    operator: TriggerActionId.AND,
                    elements: negationElements
                }
            };
        } else if (this.isContainer(element)) {
            const groupElements = [];
            element.nodes.forEach((node) => {
                groupElements.push(this.buildExpressionForTrigger(node));
            });
            return {
                type: TriggerType.GROUP_EXPRESSION,
                operator: element.operation.id,
                elements: groupElements
            };
        } else {
            return {
                type: TriggerType.SEGMENT_EXPRESSION,
                code: element.selectedSegment.code
            };
        }
    }

    private buildDefaultTrigger(): Trigger {
        return {
            type: TriggerType.DEFAULT_TRIGGER
        };
    }

    private buildExpressionTrigger(element: Trigger): ExpressionTrigger {
        return {
            type: TriggerType.EXPRESSION_TRIGGER,
            expression: this.buildExpressionForTrigger(element)
        };
    }

    private buildSegmentTrigger(element: Trigger): SegmentTrigger {
        return {
            type: TriggerType.SEGMENT_TRIGGER,
            groupBy: element.operation.id,
            segments: this.buildSegmentsForTrigger(element)
        };
    }

    private mergeTriggers(triggers: Trigger[], target: Trigger): Trigger[] {
        if (typeof triggers === 'undefined') {
            return [target];
        }

        const index = triggers.findIndex((t: any) => t.type === target.type);
        if (index >= 0) {
            target.code = triggers[index].code;
        }

        // remove other instanced of supported types (there can be only one) but maintain unsupported types
        const result = triggers.filter((trigger) => !this.isSupportedTrigger(trigger));
        result.push(target);
        return result;
    }

    // ------------------------ TRIGGER -> FORM DATA ---------------------------

    private buildContainer(actionId: TriggerActionId): ContainerTrigger {
        const action = this.actions.filter((a) => a.id === actionId)[0];
        return {
            type: TriggerType.CONTAINER_TYPE,
            operation: action,
            nodes: [],
            uid: stringUtils.generateIdentifier()
        };
    }

    private buildItem(value: string): Trigger {
        return {
            type: TriggerType.ITEM_TYPE,
            operation: null,
            selectedSegment: {
                code: value
            },
            nodes: [],
            uid: stringUtils.generateIdentifier()
        };
    }

    private getBaseData(): Trigger[] {
        const data = this.buildContainer(TriggerActionId.AND);
        return [data];
    }

    private buildExpressionFromTrigger(expression: ExpressionData): Trigger {
        let data: Trigger;
        if (this.isGroupExpressionData(expression)) {
            data = this.buildContainer(expression.operator);
            data.nodes = expression.elements.map((item) => this.buildExpressionFromTrigger(item));
        } else if (this.isNegationExpressionData(expression)) {
            data = this.buildContainer(TriggerActionId.NOT);
            const element = this.buildExpressionFromTrigger(expression.element);

            if (
                this.isGroupExpressionData(expression.element) &&
                expression.element.operator === 'AND'
            ) {
                data.nodes = element.nodes;
            } else {
                data.nodes.push(element);
            }
        } else if (this.isSegmentExpressionData(expression)) {
            data = this.buildItem(expression.code);
        }
        return data;
    }

    private buildSegmentTriggerData(trigger: SegmentTrigger): Trigger[] {
        const data = this.buildContainer(trigger.groupBy);

        trigger.segments.forEach((segment) => {
            data.nodes.push(this.buildItem(segment.code));
        });
        return [data];
    }

    private buildExpressionTriggerData(trigger: ExpressionTrigger): Trigger[] {
        const data = this.buildExpressionFromTrigger(trigger.expression);
        return [data];
    }
}
