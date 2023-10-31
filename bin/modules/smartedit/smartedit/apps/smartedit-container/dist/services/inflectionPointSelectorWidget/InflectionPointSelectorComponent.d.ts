import './inflectionPointSelectorWidget.scss';
import { OnDestroy, OnInit } from '@angular/core';
import { SystemEventService, YJQuery, IUserTrackingService } from 'smarteditcommons';
import { DeviceSupport } from '../iframe/DeviceSupportsValue';
import { IframeManagerService } from '../iframe/IframeManagerService';
import { IframeClickDetectionService } from '../IframeClickDetectionServiceOuter';
export declare class InflectionPointSelectorComponent implements OnInit, OnDestroy {
    private systemEventService;
    private iframeManagerService;
    private iframeClickDetectionService;
    private readonly yjQuery;
    private readonly userTrackingService;
    points: DeviceSupport[];
    currentPointSelected: DeviceSupport | undefined;
    isOpen: boolean;
    private unRegFn;
    private documentClick$;
    constructor(systemEventService: SystemEventService, iframeManagerService: IframeManagerService, iframeClickDetectionService: IframeClickDetectionService, yjQuery: YJQuery, userTrackingService: IUserTrackingService);
    ngOnInit(): void;
    ngOnDestroy(): void;
    selectPoint(choice: DeviceSupport): void;
    toggleDropdown(event: MouseEvent): void;
    getIconClass(choice: DeviceSupport | undefined): string;
    isSelected(choice: DeviceSupport | undefined): boolean;
}
