import {
    ExpressionTrigger,
    SegmentTrigger,
    TargetGroupState,
    Trigger,
    TriggerActionId,
    TriggerType
} from 'personalizationcommons';
import { TriggerService } from 'personalizationsmarteditcontainer/management/manageCustomizationView';

describe('TriggerService', () => {
    const container = {
        type: TriggerType.CONTAINER_TYPE,
        nodes: [{} as Trigger]
    };
    const item = {
        type: TriggerType.ITEM_TYPE,
        selectedSegment: { code: '' },
        nodes: null
    };
    const emptyContainer = {
        type: TriggerType.CONTAINER_TYPE,
        nodes: []
    };
    let triggerService: TriggerService;

    // === SETUP ===
    beforeEach(() => {
        triggerService = new TriggerService();
    });

    describe('isContainer', () => {
        it('should be defined', () => {
            expect(triggerService.isContainer).toBeDefined();
        });

        it('should return true for a container', () => {
            expect(triggerService.isContainer(container)).toBe(true);
        });

        it('should return false for not a container', () => {
            expect(triggerService.isContainer(item)).toBe(false);
        });

        it('should return false for undefined', () => {
            expect(triggerService.isContainer(undefined)).toBe(false);
        });
    });

    describe('isNotEmptyContainer', () => {
        it('should be defined', () => {
            expect(triggerService.isNotEmptyContainer).toBeDefined();
        });

        it('should return true for a not empty container', () => {
            expect(triggerService.isNotEmptyContainer(container)).toBe(true);
        });

        it('should return false for empty container', () => {
            expect(triggerService.isNotEmptyContainer(emptyContainer)).toBe(false);
        });

        it('should return false for undefined', () => {
            expect(triggerService.isNotEmptyContainer(undefined)).toBe(false);
        });
    });

    describe('isEmptyContainer', () => {
        it('should be defined', () => {
            expect(triggerService.isEmptyContainer).toBeDefined();
        });

        it('should return true for a empty container', () => {
            expect(triggerService.isEmptyContainer(emptyContainer)).toBe(true);
        });

        it('should return false for not empty container', () => {
            expect(triggerService.isEmptyContainer(container)).toBe(false);
        });

        it('should return false for undefined', () => {
            expect(triggerService.isEmptyContainer(undefined)).toBe(false);
        });
    });

    describe('isItem', () => {
        it('should be defined', () => {
            expect(triggerService.isItem).toBeDefined();
        });

        it('should return true for a item', () => {
            expect(triggerService.isItem(item)).toBe(true);
        });

        it('should return false for not a item', () => {
            expect(triggerService.isItem(container)).toBe(false);
        });

        it('should return false for undefined', () => {
            expect(triggerService.isItem(undefined)).toBe(false);
        });
    });

    describe('actions', () => {
        it('should be defined', () => {
            expect(triggerService.actions).toBeDefined();
        });

        it('should have id and name', () => {
            triggerService.actions.forEach((itemAction: any) => {
                expect(itemAction.id).toBeDefined();
                expect(itemAction.name).toBeDefined();
            });
        });
    });

    describe('isValidExpression', () => {
        it('should be defined', () => {
            expect(triggerService.isValidExpression).toBeDefined();
        });

        it('should return true for simple expression', () => {
            const expression = {
                type: TriggerType.CONTAINER_TYPE,
                nodes: [item, item, item]
            };

            expect(triggerService.isValidExpression(expression)).toBe(true);
        });

        it('should return true for complex expresion', () => {
            const complexExpression = {
                type: TriggerType.CONTAINER_TYPE,
                nodes: [
                    {
                        type: 'container',
                        nodes: [
                            item,
                            {
                                type: 'container',
                                nodes: [item]
                            },
                            {
                                type: 'container',
                                nodes: [item]
                            }
                        ]
                    },
                    item,
                    item
                ]
            } as any;

            expect(triggerService.isValidExpression(complexExpression)).toBe(true);
        });

        it('should return false for expression with empty container', () => {
            const emptyExpression = {
                type: TriggerType.CONTAINER_TYPE,
                nodes: [] // empty container
            };

            expect(triggerService.isValidExpression(emptyExpression)).toBe(false);
        });

        it('should return false for complex expression with empty container', () => {
            const emptyComplexExpression = {
                type: TriggerType.CONTAINER_TYPE,
                nodes: [
                    {
                        type: 'container',
                        nodes: [
                            item,
                            {
                                type: 'container',
                                nodes: [item]
                            },
                            {
                                type: 'container',
                                nodes: [{}] // invalid item in container
                            }
                        ]
                    },
                    item,
                    item
                ]
            } as any;

            expect(triggerService.isValidExpression(emptyComplexExpression)).toBe(false);
        });

        it('should return false for undefined', () => {
            expect(triggerService.isValidExpression(undefined)).toBe(false);
        });
    });

    describe('buildTriggers', () => {
        const itemA = {
            type: 'item',
            operation: {},
            selectedSegment: {
                code: 'A'
            }
        };
        const itemB = {
            type: 'item',
            operation: {},
            selectedSegment: {
                code: 'B'
            }
        };
        const undefinedTrigger = ({
            type: 'undefindedTriggerData',
            code: 'undefined'
        } as unknown) as Trigger;

        it('should be defined', () => {
            expect(triggerService.buildTriggers).toBeDefined();
        });

        it('should build default trigger', () => {
            const form = {
                isDefault: true
            } as TargetGroupState;

            const trigger = {
                type: TriggerType.DEFAULT_TRIGGER
            };

            expect(triggerService.buildTriggers(form, undefined)).toEqual([trigger] as Trigger[]);
        });

        it('should build segment trigger', () => {
            const form = ({
                expression: [
                    {
                        type: 'group',
                        operation: {
                            id: 'AND'
                        },
                        nodes: [itemA, itemB]
                    }
                ]
            } as unknown) as TargetGroupState;

            const trigger = {
                type: 'segmentTriggerData',
                groupBy: 'AND',
                segments: [
                    {
                        code: 'A'
                    },
                    {
                        code: 'B'
                    }
                ]
            } as any;

            expect(triggerService.buildTriggers(form, undefined)).toEqual([trigger]);
        });

        it('should build simple expression trigger', () => {
            const form = {
                expression: [
                    {
                        type: 'container',
                        operation: {
                            id: 'NOT'
                        },
                        nodes: [itemA, itemB]
                    }
                ]
            } as TargetGroupState;

            const trigger = {
                type: 'expressionTriggerData',
                expression: {
                    type: 'negationExpressionData',
                    element: {
                        type: 'groupExpressionData',
                        operator: 'AND',
                        elements: [
                            {
                                type: 'segmentExpressionData',
                                code: 'A'
                            },
                            {
                                type: 'segmentExpressionData',
                                code: 'B'
                            }
                        ]
                    }
                }
            } as any;

            expect(triggerService.buildTriggers(form, undefined)).toEqual([trigger]);
        });

        it('should build complex expression trigger', () => {
            const form = {
                expression: [
                    {
                        type: 'container',
                        operation: {
                            id: 'OR'
                        },
                        nodes: [
                            {
                                type: 'container',
                                operation: {
                                    id: 'NOT'
                                },
                                nodes: [itemA, itemB]
                            },
                            itemB
                        ]
                    }
                ]
            } as TargetGroupState;

            const trigger = {
                type: 'expressionTriggerData',
                expression: {
                    type: 'groupExpressionData',
                    operator: 'OR',
                    elements: [
                        {
                            type: 'negationExpressionData',
                            element: {
                                type: 'groupExpressionData',
                                operator: 'AND',
                                elements: [
                                    {
                                        type: 'segmentExpressionData',
                                        code: 'A'
                                    },
                                    {
                                        type: 'segmentExpressionData',
                                        code: 'B'
                                    }
                                ]
                            }
                        },
                        {
                            type: 'segmentExpressionData',
                            code: 'B'
                        }
                    ]
                }
            } as any;

            expect(triggerService.buildTriggers(form, undefined)).toEqual([trigger]);
        });

        it('should merge default trigger', () => {
            const form = {
                isDefault: true
            } as TargetGroupState;

            const trigger = {
                type: 'defaultTriggerData'
            } as Trigger;

            const existingTriggers = [
                {
                    type: 'expressionTriggerData',
                    code: 'codeA'
                },
                {
                    type: 'segmentTriggerData',
                    code: 'code'
                },
                undefinedTrigger
            ] as Trigger[];

            expect(triggerService.buildTriggers(form, existingTriggers)).toEqual([
                undefinedTrigger,
                trigger
            ]);
        });

        it('should merge segment trigger', () => {
            const form = ({
                expression: [
                    {
                        type: 'group',
                        operation: {
                            id: 'AND'
                        },
                        nodes: [itemA, itemB]
                    }
                ]
            } as unknown) as TargetGroupState;

            const trigger = {
                type: TriggerType.SEGMENT_TRIGGER,
                code: 'code',
                groupBy: TriggerActionId.AND,
                segments: [
                    {
                        code: 'A'
                    },
                    {
                        code: 'B'
                    }
                ]
            } as SegmentTrigger;

            const existingTriggers = [
                {
                    type: 'expressionTriggerData',
                    code: 'codeA'
                },
                {
                    type: 'segmentTriggerData',
                    code: 'code'
                },
                undefinedTrigger
            ] as Trigger[];

            expect(triggerService.buildTriggers(form, existingTriggers)).toEqual([
                undefinedTrigger,
                trigger
            ]);
        });

        it('should merge expression trigger', () => {
            const form = {
                expression: [
                    {
                        type: 'container',
                        operation: {
                            id: 'NOT'
                        },
                        nodes: [itemA, itemB]
                    }
                ]
            } as TargetGroupState;

            const trigger = {
                type: TriggerType.EXPRESSION_TRIGGER,
                code: 'code',
                expression: {
                    type: TriggerType.NEGATION_EXPRESSION,
                    element: {
                        type: 'groupExpressionData',
                        operator: 'AND',
                        elements: [
                            {
                                type: 'segmentExpressionData',
                                code: 'A'
                            },
                            {
                                type: 'segmentExpressionData',
                                code: 'B'
                            }
                        ]
                    }
                }
            } as ExpressionTrigger;

            const existingTriggers = [
                {
                    type: TriggerType.EXPRESSION_TRIGGER,
                    code: 'code'
                },
                undefinedTrigger
            ] as Trigger[];

            expect(triggerService.buildTriggers(form, existingTriggers)).toEqual([
                undefinedTrigger,
                trigger
            ]);
        });

        it('should build nothing', () => {
            expect(triggerService.buildTriggers(undefined, undefined)).toEqual([{} as any]);
        });
    });

    describe('buildData', () => {
        it('should be defined', () => {
            expect(triggerService.buildData).toBeDefined();
        });

        it('should build data from default trigger', () => {
            const trigger = {
                type: 'defaultTriggerData',
                code: 'code'
            };

            const data = ([
                {
                    type: 'container',
                    operation: {
                        id: 'AND',
                        name:
                            'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.and'
                    },
                    nodes: [],
                    uid: jasmine.any(String)
                }
            ] as unknown) as Trigger[];

            expect(triggerService.buildData([trigger])).toEqual(data);
        });

        it('should build data from segment trigger', () => {
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
            const data = [
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
                            operation: null,
                            selectedSegment: {
                                code: 'SegmentA'
                            },
                            nodes: [],
                            uid: jasmine.any(String)
                        },
                        {
                            type: 'item',
                            operation: null,
                            selectedSegment: {
                                code: 'SegmentB'
                            },
                            nodes: [],
                            uid: jasmine.any(String)
                        }
                    ]
                }
            ] as any[];

            expect(triggerService.buildData([trigger])).toEqual(data);
        });

        it('should build data from expression trigger', () => {
            const trigger = {
                type: 'expressionTriggerData',
                code: 'code',
                expression: {
                    type: 'negationExpressionData',
                    element: {
                        type: 'groupExpressionData',
                        operator: 'AND',
                        elements: [
                            {
                                type: 'segmentExpressionData',
                                code: 'A'
                            },
                            {
                                type: 'segmentExpressionData',
                                code: 'B'
                            }
                        ]
                    }
                }
            };

            const data = [
                {
                    type: 'container',
                    operation: {
                        id: 'NOT',
                        name:
                            'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.not'
                    },
                    uid: jasmine.any(String),
                    nodes: [
                        {
                            type: 'item',
                            operation: null,
                            selectedSegment: {
                                code: 'A'
                            },
                            nodes: [],
                            uid: jasmine.any(String)
                        },
                        {
                            type: 'item',
                            operation: null,
                            selectedSegment: {
                                code: 'B'
                            },
                            nodes: [],
                            uid: jasmine.any(String)
                        }
                    ]
                }
            ] as any[];

            expect(triggerService.buildData([trigger])).toEqual(data);
        });

        it('should build empty data', () => {
            const data = ([
                {
                    type: 'container',
                    operation: {
                        id: 'AND',
                        name:
                            'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.and'
                    },
                    nodes: [],
                    uid: jasmine.any(String)
                }
            ] as unknown) as Trigger[];

            expect(triggerService.buildData({})).toEqual(data);
        });
    });

    describe('getExpressionAsString', () => {
        it('should be defined', () => {
            expect(triggerService.getExpressionAsString).toBeDefined();
        });

        it('should return empty string if called with undefined expression object', () => {
            expect(triggerService.getExpressionAsString(undefined)).toBe('');
        });

        it('should return proper string object for empty expression container', () => {
            const data = [
                {
                    type: 'container',
                    operation: {
                        id: 'OR'
                    },
                    nodes: [
                        {
                            type: 'container',
                            operation: {
                                id: 'OR'
                            },
                            nodes: []
                        }
                    ]
                }
            ] as Trigger[];

            const stringExpression = '(( [] ))';

            expect(triggerService.getExpressionAsString(data[0])).toEqual(stringExpression);
        });

        it('should return proper string object for empty expression container with NOT operator', () => {
            const data = [
                {
                    type: 'container',
                    operation: {
                        id: 'NOT'
                    },
                    nodes: []
                }
            ] as Trigger[];

            const stringExpression = ' NOT ( [] )';

            expect(triggerService.getExpressionAsString(data[0])).toEqual(stringExpression);
        });

        it('should return proper string object for none empty expression container with NOT operator', () => {
            const data = [
                {
                    type: TriggerType.CONTAINER_TYPE,
                    operation: {
                        id: TriggerActionId.NOT
                    },
                    nodes: [
                        {
                            type: TriggerType.CONTAINER_TYPE,
                            operation: {
                                id: TriggerActionId.NOT
                            },
                            nodes: [
                                {
                                    type: 'item',
                                    operation: null,
                                    selectedSegment: {
                                        code: 'SegmentA'
                                    },
                                    nodes: []
                                }
                            ]
                        }
                    ]
                }
            ] as Trigger[];

            const stringExpression = ' NOT ( NOT (SegmentA))';

            expect(triggerService.getExpressionAsString(data[0])).toEqual(stringExpression);
        });

        it('should return proper string object for simple expression', () => {
            const data = [
                {
                    type: TriggerType.CONTAINER_TYPE,
                    operation: {
                        id: TriggerActionId.OR
                    },
                    nodes: [
                        {
                            type: 'item',
                            operation: null,
                            selectedSegment: {
                                code: 'SegmentA'
                            },
                            nodes: []
                        },
                        {
                            type: 'item',
                            operation: null,
                            selectedSegment: {
                                code: 'SegmentB'
                            },
                            nodes: []
                        }
                    ]
                }
            ] as Trigger[];

            const stringExpression = '(SegmentA OR SegmentB)';

            expect(triggerService.getExpressionAsString(data[0])).toEqual(stringExpression);
        });

        it('should return proper string object for expression with NOT container', () => {
            const data = [
                {
                    type: TriggerType.CONTAINER_TYPE,
                    operation: {
                        id: TriggerActionId.OR
                    },
                    nodes: [
                        {
                            type: 'item',
                            operation: null,
                            selectedSegment: {
                                code: 'SegmentA'
                            },
                            nodes: []
                        },
                        {
                            type: 'container',
                            operation: {
                                id: TriggerActionId.NOT
                            },
                            nodes: [
                                {
                                    type: 'item',
                                    operation: '',
                                    selectedSegment: {
                                        code: 'SegmentC'
                                    },
                                    nodes: []
                                },
                                {
                                    type: 'item',
                                    operation: '',
                                    selectedSegment: {
                                        code: 'SegmentB'
                                    },
                                    nodes: []
                                }
                            ]
                        }
                    ]
                }
            ] as Trigger[];

            const stringExpression = '(SegmentA OR  NOT (SegmentC AND SegmentB))';

            expect(triggerService.getExpressionAsString(data[0])).toEqual(stringExpression);
        });
    });
});
