/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Input } from '@angular/core';
import {
    SmarteditRoutingService,
    IUserTrackingService,
    USER_TRACKING_FUNCTIONALITY
} from 'smarteditcommons';
import { IframeManagerService } from 'smarteditcontainer/services/iframe/IframeManagerService';
import './sitesLink.scss';

@Component({
    selector: 'sites-link',
    templateUrl: './sitesLinkTemplate.html'
})
export class SitesLinkComponent {
    @Input()
    cssClass: string;

    @Input()
    iconCssClass = 'sap-icon--navigation-right-arrow';

    @Input()
    shortcutLink: any;

    constructor(
        private routingService: SmarteditRoutingService,
        private readonly iframeManagerService: IframeManagerService,
        private readonly userTrackingService: IUserTrackingService
    ) {}

    goToSites(): void {
        this.userTrackingService.trackingUserAction(
            USER_TRACKING_FUNCTIONALITY.NAVIGATION,
            'Sites'
        );
        this.iframeManagerService.setCurrentLocation(null);
        this.routingService.go('/');
    }
}
