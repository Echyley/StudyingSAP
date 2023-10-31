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
export declare const DEVICE_SUPPORTS: DeviceSupport[];
