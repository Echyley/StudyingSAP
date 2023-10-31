/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    IComponentVisibilityAlertService,
    ComponentVisibilityAlertComponent,
    SlotComponent
} from 'cmscommons';
import {
    GatewayProxied,
    IAlertConfig,
    IAlertService,
    IAlertServiceType,
    IExperience,
    ISharedDataService,
    EXPERIENCE_STORAGE_KEY,
    EVENT_PAGE_TREE_SLOT_NEED_UPDATE,
    CrossFrameEventService
} from 'smarteditcommons';
import { ActionableAlertService } from './ActionableAlertService';

enum AlertMessage {
    HIDDEN = 'se.cms.component.visibility.alert.description.hidden',
    RESTRICTED = 'se.cms.component.visibility.alert.description.restricted'
}

@GatewayProxied('checkAndAlertOnComponentVisibility')
@Injectable()
export class ComponentVisibilityAlertService extends IComponentVisibilityAlertService {
    constructor(
        private readonly sharedDataService: ISharedDataService,
        private readonly alertService: IAlertService,
        private readonly actionableAlertService: ActionableAlertService,
        private readonly crossFrameEventService: CrossFrameEventService
    ) {
        super();
    }

    public async checkAndAlertOnComponentVisibility(component: SlotComponent): Promise<void> {
        // Avoid some hidden component updates that page tree can't catch
        await this.crossFrameEventService.publish(
            EVENT_PAGE_TREE_SLOT_NEED_UPDATE,
            component.slotId
        );

        const shouldShowAlert = !component.visible || component.restricted;
        if (!shouldShowAlert) {
            return;
        }

        const experience = (await this.sharedDataService.get(
            EXPERIENCE_STORAGE_KEY
        )) as IExperience;

        const message = !component.visible ? AlertMessage.HIDDEN : AlertMessage.RESTRICTED;

        const isExternal = component.catalogVersion !== experience.pageContext.catalogVersionUuid;
        if (isExternal) {
            this.alertService.showAlert({
                message,
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
        } else {
            const actionableAlertConf: IAlertConfig = {
                component: ComponentVisibilityAlertComponent,
                duration: 6000,
                data: {
                    component,
                    message
                },
                minWidth: '',
                mousePersist: true,
                dismissible: true,
                width: '300px'
            };
            this.actionableAlertService.displayActionableAlert(
                actionableAlertConf,
                IAlertServiceType.INFO
            );
        }
    }
}
