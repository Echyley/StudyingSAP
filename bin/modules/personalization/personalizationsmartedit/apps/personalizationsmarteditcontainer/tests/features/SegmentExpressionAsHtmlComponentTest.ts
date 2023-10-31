/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TriggerService } from 'personalizationsmarteditcontainer/management/manageCustomizationView';
import { SegmentExpressionAsHtmlComponent } from 'personalizationsmarteditcontainer/management/manageCustomizationView/segmentExpressionAsHtml';

describe('SegmentExpressionAsHtml', () => {
    let triggerService: TriggerService;
    let component: SegmentExpressionAsHtmlComponent;

    beforeEach(() => {
        triggerService = new TriggerService();
        component = new SegmentExpressionAsHtmlComponent(triggerService);
    });

    describe('Component API', () => {
        it('should have proper api when initialized', () => {
            expect(component.segmentExpression).toBeUndefined();
            expect(component.operators).toEqual(['AND', 'OR', 'NOT']);
            expect(component.emptyGroup).toEqual('[]');
            expect(component.emptyGroupAndOperators).toEqual(['AND', 'OR', 'NOT', '[]']);
        });
    });
});
