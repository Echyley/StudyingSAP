/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ActionableAlertService,
    ComponentVisibilityAlertService
} from 'cmssmarteditcontainer/services';
import { CrossFrameEventService, IAlertService, ISharedDataService } from 'smarteditcommons';

describe('ComponentVisibilityAlertService', () => {
    let sharedDataService: jasmine.SpyObj<ISharedDataService>;
    let alertService: jasmine.SpyObj<IAlertService>;
    let actionableAlertService: jasmine.SpyObj<ActionableAlertService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;

    function createMockPayload(scenario: string) {
        return {
            itemId: 'MOCKED_ITEM_ID',
            itemType: 'MOCKED_ITEM_TYPE',
            catalogVersion: 'MOCKED_CATALOG_VERSION',
            slotId: 'MOCKED_SLOT_ID',
            restricted: scenario.includes('WITH_RESTRICTIONS'),
            visible: scenario.includes('VISIBLE')
        };
    }

    let service: ComponentVisibilityAlertService;
    beforeEach(() => {
        sharedDataService = jasmine.createSpyObj<ISharedDataService>('sharedDataService', ['get']);
        alertService = jasmine.createSpyObj<IAlertService>('alertService', ['showAlert']);
        actionableAlertService = jasmine.createSpyObj<ActionableAlertService>(
            'actionableAlertService',
            ['displayActionableAlert']
        );
        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['publish']
        );

        service = new ComponentVisibilityAlertService(
            sharedDataService,
            alertService,
            actionableAlertService,
            crossFrameEventService
        );
    });

    describe('checkAndAlertOnComponentVisibility - component not editable', () => {
        beforeEach(() => {
            sharedDataService.get.and.returnValue(
                Promise.resolve({
                    pageContext: {
                        catalogVersionUuid: 'SOME_OTHER_CATALOG_VERSION'
                    }
                })
            );
        });

        it("should display a 'will not be displayed' alert for any hidden component", async () => {
            await service.checkAndAlertOnComponentVisibility(
                createMockPayload('HIDDEN_NO_RESTRICTIONS')
            );

            expect(alertService.showAlert).toHaveBeenCalledWith({
                message: 'se.cms.component.visibility.alert.description.hidden',
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
        });

        it("should display a 'will not be displayed' alert for any hidden and restricted component", async () => {
            await service.checkAndAlertOnComponentVisibility(
                createMockPayload('HIDDEN_WITH_RESTRICTIONS')
            );

            expect(alertService.showAlert).toHaveBeenCalledWith({
                message: 'se.cms.component.visibility.alert.description.hidden',
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
        });

        it("should display a 'might not be displayed' alert for any restricted component", async () => {
            await service.checkAndAlertOnComponentVisibility(
                createMockPayload('VISIBLE_WITH_RESTRICTIONS')
            );

            expect(alertService.showAlert).toHaveBeenCalledWith({
                message: 'se.cms.component.visibility.alert.description.restricted',
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
        });
    });

    describe('checkAndAlertOnComponentVisibility - component editable', () => {
        beforeEach(() => {
            sharedDataService.get.and.returnValue(
                Promise.resolve({
                    pageContext: {
                        catalogVersionUuid: 'MOCKED_CATALOG_VERSION'
                    }
                })
            );
        });

        it("should display a 'will not be displayed' alert for any hidden component", async () => {
            const mockComponent = createMockPayload('HIDDEN_NO_RESTRICTIONS');

            await service.checkAndAlertOnComponentVisibility(mockComponent);

            expect(actionableAlertService.displayActionableAlert).toHaveBeenCalledWith(
                jasmine.objectContaining({
                    duration: 6000,
                    data: {
                        component: mockComponent,
                        message: 'se.cms.component.visibility.alert.description.hidden'
                    }
                }),
                jasmine.any(String)
            );
        });

        it("should display a 'will not be displayed' alert for any hidden and restricted component", async () => {
            const mockComponent = createMockPayload('HIDDEN_WITH_RESTRICTIONS');

            await service.checkAndAlertOnComponentVisibility(mockComponent);

            expect(actionableAlertService.displayActionableAlert).toHaveBeenCalledWith(
                jasmine.objectContaining({
                    duration: 6000,
                    data: {
                        component: mockComponent,
                        message: 'se.cms.component.visibility.alert.description.hidden'
                    }
                }),
                jasmine.any(String)
            );
        });

        it("should display a 'might not be displayed' alert for any restricted component", async () => {
            const mockComponent = createMockPayload('VISIBLE_WITH_RESTRICTIONS');

            await service.checkAndAlertOnComponentVisibility(mockComponent);

            expect(actionableAlertService.displayActionableAlert).toHaveBeenCalledWith(
                jasmine.objectContaining({
                    duration: 6000,
                    data: {
                        component: mockComponent,
                        message: 'se.cms.component.visibility.alert.description.restricted'
                    }
                }),
                jasmine.any(String)
            );
        });
    });
});
