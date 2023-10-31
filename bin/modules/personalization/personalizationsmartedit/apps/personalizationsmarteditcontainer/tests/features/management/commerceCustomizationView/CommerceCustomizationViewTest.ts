import { Injector } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
    PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES,
    PersonlizationSearchExtensionComponent,
    PersonlizationPromotionExtensionComponent
} from 'personalizationcommons';
import { ActionsDataFactory } from 'personalizationsmarteditcontainer/management/commerceCustomizationView/ActionsDataFactory';
import { CommerceCustomizationViewComponent } from 'personalizationsmarteditcontainer/management/commerceCustomizationView/CommerceCustomizationViewComponent';
import { SystemEventService, LogService, promiseUtils } from 'smarteditcommons';

describe('CommerceCustomizationViewController', () => {
    let injector: Injector;
    let translateService: TranslateService;
    let personalizationsmarteditRestService: jasmine.SpyObj<any>;
    let personalizationsmarteditContextService: jasmine.SpyObj<any>;
    let actionsDataFactory: ActionsDataFactory;
    let personalizationsmarteditMessageHandler: jasmine.SpyObj<any>;
    let systemEventService: SystemEventService;
    let personalizationsmarteditCommerceCustomizationService: jasmine.SpyObj<any>;
    let personalizationsmarteditUtils: jasmine.SpyObj<any>;
    let modalManager: jasmine.SpyObj<any>;
    let commerceCustomizationViewController: CommerceCustomizationViewComponent;
    let logService: LogService;

    let searchInjectedData: PersonlizationSearchExtensionComponent;
    let promotionInjectedData: PersonlizationPromotionExtensionComponent;

    const mockAction = {
        type: 'mock',
        code: 'mock'
    };
    const mockActionWrapper = {
        action: mockAction,
        status: PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.OLD
    };
    const mockPromotionAction = {
        type: 'cxPromotionActionData',
        code: 'promotion code',
        promotionId: 'promotion id'
    };
    const mockPromotionActionWrapper = {
        action: mockPromotionAction
    };
    const availableTypes = [
        {
            type: 'cxPromotionActionData',
            getName: (action: any) => {
                return 'P - ' + action.promotionId;
            }
        }
    ];
    const mockComparer = (a1: any, a2: any) => {
        return a1.type === a2.type && a1.code === a2.code;
    };

    beforeEach(() => {
        actionsDataFactory = new ActionsDataFactory();
        personalizationsmarteditRestService = jasmine.createSpyObj(
            'personalizationsmarteditRestService',
            ['getActions']
        );
        personalizationsmarteditRestService.getActions.and.returnValue(
            promiseUtils.defer().promise
        );
        personalizationsmarteditContextService = jasmine.createSpyObj(
            'personalizationsmarteditContextService',
            ['getSeData']
        );
        personalizationsmarteditContextService.getSeData.and.callFake(() => {
            return {
                seConfigurationData: {}
            };
        });
        personalizationsmarteditMessageHandler = jasmine.createSpyObj(
            'personalizationsmarteditMessageHandler',
            ['sendError']
        );
        personalizationsmarteditCommerceCustomizationService = jasmine.createSpyObj(
            'personalizationsmarteditCommerceCustomizationService',
            ['getAvailableTypes']
        );
        personalizationsmarteditCommerceCustomizationService.getAvailableTypes.and.callFake(() => {
            return availableTypes;
        });
        personalizationsmarteditUtils = jasmine.createSpyObj('personalizationsmarteditUtils', [
            'getEnablementTextForCustomization',
            'getEnablementTextForVariation',
            'getActivityStateForCustomization',
            'getActivityStateForVariation'
        ]);
        modalManager = jasmine.createSpyObj('modalManager', [
            'setButtonHandler',
            'enableButton',
            'disableButton'
        ]);

        commerceCustomizationViewController = new CommerceCustomizationViewComponent(
            injector,
            searchInjectedData,
            promotionInjectedData,
            translateService,
            actionsDataFactory,
            personalizationsmarteditRestService,
            personalizationsmarteditMessageHandler,
            systemEventService,
            personalizationsmarteditCommerceCustomizationService,
            personalizationsmarteditContextService,
            personalizationsmarteditUtils,
            modalManager,
            logService
        );
    });

    describe('getActionsToDisplay', () => {
        it('should be empty', () => {
            // then
            expect(commerceCustomizationViewController.getActionsToDisplay).toBeDefined();
        });

        it('should contain action', () => {
            // when
            commerceCustomizationViewController.addAction(mockAction, mockComparer);

            // then
            expect(commerceCustomizationViewController.getActionsToDisplay).toBeDefined();
            expect(commerceCustomizationViewController.getActionsToDisplay().length).toBe(1);
        });
    });

    describe('displayAction', () => {
        it('should use default display', () => {
            expect(commerceCustomizationViewController.displayAction(mockActionWrapper)).toBe(
                'mock'
            );
        });

        it('should use promotion display', () => {
            expect(
                commerceCustomizationViewController.displayAction(mockPromotionActionWrapper)
            ).toBe('P - promotion id');
        });
    });

    describe('addAction', () => {
        it('should add new item', () => {
            // given
            expect(commerceCustomizationViewController.getActionsToDisplay().length).toBe(0);

            // when
            commerceCustomizationViewController.addAction(mockAction, mockComparer);

            // then
            expect(commerceCustomizationViewController.getActionsToDisplay().length).toBe(1);
        });

        it('should ignore existing item', () => {
            // given
            expect(commerceCustomizationViewController.getActionsToDisplay().length).toBe(0);

            // when
            commerceCustomizationViewController.addAction(mockAction, mockComparer);
            commerceCustomizationViewController.addAction(mockAction, mockComparer);

            // then
            expect(commerceCustomizationViewController.getActionsToDisplay().length).toBe(1);
        });

        it('should restore item from delete queue', () => {
            // given
            expect(commerceCustomizationViewController.getActionsToDisplay().length).toBe(0);
            commerceCustomizationViewController.addAction(mockAction, mockComparer);
            expect(commerceCustomizationViewController.getActionsToDisplay().length).toBe(1);
            commerceCustomizationViewController.removeSelectedAction(
                commerceCustomizationViewController.actions[0]
            );
            expect(commerceCustomizationViewController.getActionsToDisplay().length).toBe(0);

            // when
            commerceCustomizationViewController.addAction(mockAction, mockComparer);

            // then
            expect(commerceCustomizationViewController.getActionsToDisplay().length).toBe(1);
            expect(commerceCustomizationViewController.removedActions.length).toBe(0);
        });
    });

    describe('removeSelectedAction', () => {
        it('should delete item', () => {
            // given
            commerceCustomizationViewController.addAction(mockAction, mockComparer);
            expect(commerceCustomizationViewController.getActionsToDisplay().length).toBe(1);

            // when
            commerceCustomizationViewController.removeSelectedAction(
                commerceCustomizationViewController.actions[0]
            );

            // then
            expect(commerceCustomizationViewController.getActionsToDisplay().length).toBe(0);
        });
    });

    describe('isDirty', () => {
        it('should not be dirty after adding exiting item', () => {
            // given
            commerceCustomizationViewController.actions.push(mockActionWrapper);

            // when
            commerceCustomizationViewController.addAction(mockAction, mockComparer);

            // then
            expect(commerceCustomizationViewController.isDirty()).toBe(false);
        });

        it('should not be dirty after removing new item', () => {
            // given
            commerceCustomizationViewController.addAction(mockAction, mockComparer);

            // when
            commerceCustomizationViewController.removeSelectedAction(
                commerceCustomizationViewController.actions[0]
            );

            // then
            expect(commerceCustomizationViewController.isDirty()).toBe(false);
        });

        it('should be dirty after adding new item', () => {
            // when
            commerceCustomizationViewController.addAction(mockAction, mockComparer);

            // then
            expect(commerceCustomizationViewController.isDirty()).toBe(true);
        });

        it('should be dirty after removing exiting item', () => {
            // given
            commerceCustomizationViewController.actions.push(mockActionWrapper);

            // when
            commerceCustomizationViewController.removeSelectedAction(
                commerceCustomizationViewController.actions[0]
            );

            // then
            expect(commerceCustomizationViewController.isDirty()).toBe(true);
        });
    });
});
