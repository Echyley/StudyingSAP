/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/** @internal */
export interface DeviceSupport {
    type: string;
    width: number | string;
    height?: number | string;
    iconClass?: string;
    icon?: string;
    default?: boolean;
    selectedIcon?: string;
    blueIcon?: string;
    name: string;
}

/** @internal */
export const DEVICE_SUPPORTS: DeviceSupport[] = [
    {
        iconClass: 'sap-icon--iphone',
        type: 'phone',
        width: 480,
        name: 'Mobile Portrait'
    },
    {
        iconClass: 'sap-icon--iphone-2',
        type: 'wide-phone',
        width: 600,
        name: 'Mobile Landscape'
    },
    {
        iconClass: 'sap-icon--ipad',
        type: 'tablet',
        width: 700,
        name: 'Tablet Portrait'
    },
    {
        iconClass: 'sap-icon--ipad-2',
        type: 'wide-tablet',
        width: 1024,
        name: 'Tablet Landscape'
    },
    {
        iconClass: 'sap-icon--sys-monitor',
        type: 'desktop',
        width: 1200,
        name: 'Desktop'
    },
    {
        iconClass: 'hyicon hyicon-wide-screen',
        type: 'wide-desktop',
        width: '100%',
        default: true,
        name: 'Widescreen'
    }
];
