/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import './inflectionPointSelectorWidget.scss';
import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { fromEvent, Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';

import {
    windowUtils,
    IIframeClickDetectionService,
    OVERLAY_DISABLED_EVENT,
    SystemEventService,
    YJQuery,
    YJQUERY_TOKEN,
    IUserTrackingService,
    USER_TRACKING_FUNCTIONALITY
} from 'smarteditcommons';
import { DeviceSupport, DEVICE_SUPPORTS } from '../iframe/DeviceSupportsValue';
import { IframeManagerService } from '../iframe/IframeManagerService';
import { IframeClickDetectionService } from '../IframeClickDetectionServiceOuter';

@Component({
    selector: 'inflection-point-selector',
    templateUrl: './inflectionPointSelectorWidgetComponentTemplate.html'
})
export class InflectionPointSelectorComponent implements OnInit, OnDestroy {
    public points: DeviceSupport[];
    public currentPointSelected: DeviceSupport | undefined;
    public isOpen: boolean;
    private unRegFn: () => void;
    private documentClick$: Subscription;

    constructor(
        private systemEventService: SystemEventService,
        private iframeManagerService: IframeManagerService,
        @Inject(IIframeClickDetectionService)
        private iframeClickDetectionService: IframeClickDetectionService,
        @Inject(YJQUERY_TOKEN) private readonly yjQuery: YJQuery,
        private readonly userTrackingService: IUserTrackingService
    ) {
        this.currentPointSelected = DEVICE_SUPPORTS.find(
            (deviceSupport: DeviceSupport) => deviceSupport.default
        );

        this.points = DEVICE_SUPPORTS;

        this.isOpen = false;
    }

    ngOnInit(): void {
        this.iframeClickDetectionService.registerCallback(
            'inflectionPointClose',
            () => (this.isOpen = false)
        );

        const window = windowUtils.getWindow();
        this.documentClick$ = fromEvent(window.document, 'click')
            .pipe(
                filter(
                    (event: MouseEvent) =>
                        this.yjQuery(event.target).parents('inflection-point-selector').length <=
                            0 && this.isOpen
                )
            )
            .subscribe((event: MouseEvent) => (this.isOpen = false));

        this.unRegFn = this.systemEventService.subscribe(
            OVERLAY_DISABLED_EVENT,
            () => (this.isOpen = false)
        );
    }

    ngOnDestroy(): void {
        this.unRegFn();
        this.documentClick$.unsubscribe();
    }

    public selectPoint(choice: DeviceSupport): void {
        this.userTrackingService.trackingUserAction(
            USER_TRACKING_FUNCTIONALITY.INFLECTION,
            choice.type
        );
        this.currentPointSelected = choice;
        this.isOpen = !this.isOpen;

        if (choice !== undefined) {
            // this.iframeManagerService.apply(choice, this.deviceOrientation);
            // deviceOrientation prop is never assigned a value in original JS source
            this.iframeManagerService.apply(choice);
        }
    }

    public toggleDropdown(event: MouseEvent): void {
        event.preventDefault();
        event.stopPropagation();
        this.isOpen = !this.isOpen;
    }

    public getIconClass(choice: DeviceSupport | undefined): string {
        if (choice !== undefined) {
            return choice.iconClass;
        } else {
            return this.currentPointSelected.iconClass;
        }
    }

    public isSelected(choice: DeviceSupport | undefined): boolean {
        if (choice !== undefined) {
            return choice.type === this.currentPointSelected.type;
        }
        return false;
    }
}
