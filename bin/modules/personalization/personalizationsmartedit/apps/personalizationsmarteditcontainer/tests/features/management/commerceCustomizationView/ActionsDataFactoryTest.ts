import { PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES } from 'personalizationcommons';
import { ActionsDataFactory } from 'personalizationsmarteditcontainer/management/commerceCustomizationView/ActionsDataFactory';

describe('ActionsDataFactory', () => {
    // Service being tested
    let actionsDataFactory: ActionsDataFactory;

    const mockAction = {
        type: 'mock',
        code: 'mock'
    };
    const mockComparer = (a1: any, a2: any) => {
        return a1.type === a2.type && a1.code === a2.code;
    };

    // === SETUP ===
    beforeEach(() => {
        actionsDataFactory = new ActionsDataFactory();
    });

    it('Public API', () => {
        expect(actionsDataFactory.getActions).toBeDefined();
        expect(actionsDataFactory.getRemovedActions).toBeDefined();
        expect(actionsDataFactory.resetActions).toBeDefined();
        expect(actionsDataFactory.resetRemovedActions).toBeDefined();
        expect(actionsDataFactory.addAction).toBeDefined();
        expect(actionsDataFactory.isItemInSelectedActions).toBeDefined();
    });

    describe('getActions', () => {
        it('should be defined', () => {
            expect(actionsDataFactory.getActions).toBeDefined();
        });

        it('should return empty array after init', () => {
            expect(actionsDataFactory.getActions()).toEqual([]);
        });
    });

    describe('getRemovedActions', () => {
        it('should be defined', () => {
            expect(actionsDataFactory.getRemovedActions).toBeDefined();
        });

        it('should return empty array after init', () => {
            expect(actionsDataFactory.getRemovedActions()).toEqual([]);
        });
    });

    describe('resetActions', () => {
        it('should be defined', () => {
            expect(actionsDataFactory.resetActions).toBeDefined();
        });

        it('should reset action', () => {
            actionsDataFactory.addAction(mockAction, mockComparer);
            actionsDataFactory.resetActions();
            expect(actionsDataFactory.getActions()).toEqual([]);
        });
    });

    describe('addAction', () => {
        it('should be defined', () => {
            expect(actionsDataFactory.addAction).toBeDefined();
        });

        it('should add new action', () => {
            const expectedAction = [
                {
                    action: mockAction,
                    status: PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.NEW
                }
            ];

            actionsDataFactory.addAction(mockAction, mockComparer);

            expect(actionsDataFactory.getActions()).toEqual(expectedAction);
        });
    });

    describe('isItemInSelectedActions', () => {
        it('should be defined', () => {
            expect(actionsDataFactory.isItemInSelectedActions).toBeDefined();
        });

        it('should return found action', () => {
            const expectedAction = {
                action: mockAction,
                status: PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.NEW
            };
            actionsDataFactory.addAction(mockAction, mockComparer);
            expect(actionsDataFactory.isItemInSelectedActions(mockAction, mockComparer)).toEqual(
                expectedAction
            );
        });
    });
});
