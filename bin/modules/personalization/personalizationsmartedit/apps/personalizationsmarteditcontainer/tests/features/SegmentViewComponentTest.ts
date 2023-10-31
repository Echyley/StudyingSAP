/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { EventEmitter } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
    PersonalizationsmarteditMessageHandler,
    PersonalizationsmarteditUtils,
    TargetGroupState,
    Trigger
} from 'personalizationcommons';
import { TriggerService } from 'personalizationsmarteditcontainer/management';
import { SegmentViewComponent } from 'personalizationsmarteditcontainer/management/manageCustomizationView/segmentView/SegmentViewComponent';
import { PersonalizationsmarteditRestService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditRestService';

describe('SegmentViewComponent', () => {
    const mockSegment1 = {
        code: 'testSegment1'
    };
    const mockSegment2 = {
        code: 'testSegment2'
    };
    const mockSegment3 = {
        code: 'testSegment3'
    };
    const mockBuildData = ([
        {
            type: 'container',
            operation: {
                id: 'OR',
                name:
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.or'
            },
            uid: jasmine.any(String),
            nodes: [
                {
                    type: 'item',
                    operation: '',
                    selectedSegment: {
                        code: 'SegmentA'
                    },
                    nodes: [],
                    uid: jasmine.any(String)
                },
                {
                    type: 'item',
                    operation: '',
                    selectedSegment: {
                        code: 'SegmentB'
                    },
                    nodes: [],
                    uid: jasmine.any(String)
                }
            ]
        }
    ] as unknown) as Trigger[];
    let translateService: jasmine.SpyObj<TranslateService>;

    let persoRestService: jasmine.SpyObj<PersonalizationsmarteditRestService>;
    let persoMessageHandler: jasmine.SpyObj<PersonalizationsmarteditMessageHandler>;
    let triggerService: jasmine.SpyObj<TriggerService>;
    let personalizationsmarteditUtils: jasmine.SpyObj<PersonalizationsmarteditUtils>;
    let jQuery: JQueryStatic;

    let component: SegmentViewComponent;
    beforeEach(() => {
        translateService = jasmine.createSpyObj<TranslateService>('translateService', ['instant']);
        persoMessageHandler = jasmine.createSpyObj<PersonalizationsmarteditMessageHandler>(
            'persoMessageHandler',
            ['sendError']
        );
        triggerService = jasmine.createSpyObj<TriggerService>('TriggerService', [
            'actions',
            'buildData',
            'isItem',
            'isDropzone',
            'isContainer'
        ]);

        personalizationsmarteditUtils = jasmine.createSpyObj<PersonalizationsmarteditUtils>(
            'personalizationSmartEditUtils',
            ['uniqueArray']
        );

        persoRestService = jasmine.createSpyObj<PersonalizationsmarteditRestService>(
            'personalizationsmarteditRestService',
            ['getSegments']
        );

        jQuery = (jasmine.createSpy() as unknown) as JQueryStatic;
    });

    beforeEach(() => {
        triggerService.buildData.and.callFake(() => {
            return mockBuildData;
        });
        (personalizationsmarteditUtils.uniqueArray as any).and.callFake((arr1, arr2) => {
            Array.prototype.push.apply(arr1, arr2);
        });
        persoRestService.getSegments.and.callFake(() => {
            return Promise.resolve({
                segments: [mockSegment1, mockSegment2, mockSegment3],
                pagination: {
                    count: 3,
                    page: 0,
                    totalCount: 3,
                    totalPages: 1
                }
            });
        });

        component = new SegmentViewComponent(
            persoRestService,
            persoMessageHandler,
            triggerService,
            personalizationsmarteditUtils,
            translateService,
            jQuery
        );
        component.targetGroupState = ({
            selectedVariant: [
                {
                    code: '',
                    name: '',
                    expression: [],
                    isDefault: false,
                    showExpression: true,
                    selectedVariation: undefined
                }
            ]
        } as unknown) as TargetGroupState;
        component.expressionChange = new EventEmitter<Trigger[]>();
    });

    describe('ngOnInit', () => {
        it('should be defined', () => {
            expect(component.ngOnInit).toBeDefined();
        });

        it('should properly initialize expression if no triggers object', () => {
            const initExpression = ([
                {
                    type: 'container',
                    operation: component.actions[0],
                    nodes: [],
                    uid: jasmine.any(String)
                }
            ] as unknown) as Trigger[];

            component.ngOnInit();

            expect(component.expression).toEqual(initExpression);
        });

        it('should properly initialize expression if triggers object passed', () => {
            const trigger = {
                type: 'segmentTriggerData',
                code: 'code',
                groupBy: 'OR',
                segments: [
                    {
                        code: 'SegmentA'
                    },
                    {
                        code: 'SegmentB'
                    }
                ]
            };

            component.targetGroupState = ({
                selectedVariation: { triggers: [trigger] }
            } as unknown) as TargetGroupState;

            const expressionChangeEmitSpy = spyOn(component.expressionChange, 'emit');

            component.ngOnInit();

            expect(component.expression).toEqual(mockBuildData);
            expect(expressionChangeEmitSpy).toHaveBeenCalled();
        });
    });
});
