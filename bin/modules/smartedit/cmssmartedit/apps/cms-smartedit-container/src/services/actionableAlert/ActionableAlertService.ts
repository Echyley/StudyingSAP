/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { IAlertConfig, IAlertService, IAlertServiceType } from 'smarteditcommons';
/**
 * AlertContent interface
 * This interface can be removed when clonePageAlertService and componentVisibilityAlertService are migrated
 */
export interface AlertContent {
    /**
     *  Function defining Angular controller consumed by the contextual alert.
     */
    controller: (...args: any[]) => void;
    /**
     * Description displayed within the contextual alert.
     */
    description: string;
    /**
     * Label for the hyperlink displayed within the contextual alert.
     */
    hyperLinkLabel: string;
    /**
     * The timeout duration of the cms alert item, in milliseconds. If not provided, the default is used.
     */
    timeoutDuration?: number;
}

enum AlertTypesMapping {
    information = 'showInfo',
    success = 'showSuccess',
    warning = 'showWarning',
    error = 'showDanger'
}

/**
 * The actionableAlertService is used by external modules to render an
 * Alert structured around a description, an hyperlink label and a custom
 * controller.
 **/
@Injectable()
export class ActionableAlertService {
    constructor(private alertService: IAlertService) {}

    /**
     * @param alertContent A JSON object containing the specific configuration to be applied on the actionableAlert.
     * @param alertType A string representing the type of alert to display. The string must match one of the types
     * If the alert type is null, or it doesn't match any of the valid types this method will default to INFO.
     */
    public displayActionableAlert(alertContent: IAlertConfig, alertType?: IAlertServiceType): void {
        const alertMethodName = this.getMethodNameByAlertType(alertType);
        return this.alertService[alertMethodName](alertContent);
    }

    private getMethodNameByAlertType(alertType: IAlertServiceType): AlertTypesMapping {
        if (alertType && AlertTypesMapping[alertType]) {
            return AlertTypesMapping[alertType];
        }
        return AlertTypesMapping.information;
    }
}
