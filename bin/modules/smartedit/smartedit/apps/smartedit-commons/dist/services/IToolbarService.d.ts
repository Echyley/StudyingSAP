import { InjectionToken, Type } from '@angular/core';
import { LogService } from '@smart/utils';
import { TypedMap } from 'smarteditcommons/dtos';
import { IPermissionService, IPrioritized } from 'smarteditcommons/services/interfaces';
export declare const TOOLBAR_ITEM: InjectionToken<ToolbarItemInternal>;
export declare enum ToolbarItemType {
    TEMPLATE = "TEMPLATE",
    ACTION = "ACTION",
    HYBRID_ACTION = "HYBRID_ACTION"
}
export declare enum ToolbarSection {
    left = "left",
    middle = "middle",
    right = "right"
}
export declare enum ToolbarDropDownPosition {
    left = "left",
    center = "center",
    right = "right"
}
export interface CoreToolbarItemDefinition extends IPrioritized {
    /** Unique identifier of the toolbar action item. */
    key: string;
    type: ToolbarItemType;
    /** List of image URLs for the icon images (only for ACTION and HYBRID_ACTION) */
    icons?: string[];
    /** Component rendered within a toolbar */
    component?: Type<any>;
    /** Determines the sections(left, middle or right) of the item in the toolbar. */
    section?: ToolbarSection;
    /** List of classes used to display icons from fonts. */
    iconClassName?: string;
    /** Formats the button in compact/header-toolbar manner if 'compact' param is passed */
    actionButtonFormat?: string;
    /** Keeps the dropdown content in the DOM on close. */
    keepAliveOnClose?: boolean;
    /** The context component that needs to be displayed for the toolbar item. */
    contextComponent?: Type<any>;
    /** Arranges the dropdown in either of these left/center/right positions. */
    dropdownPosition?: ToolbarDropDownPosition;
    /** List of permission names used to determine whether to show or hide the toolbar item. */
    permissions?: string[];
}
export interface ToolbarItemDefinition extends CoreToolbarItemDefinition {
    /** Callback triggered when this toolbar action item is clicked. */
    callback?: () => void;
    /** Description translation key */
    descriptionI18nKey?: string;
    /** Name translation key */
    nameI18nKey?: string;
    /** Determines the position of the item in the toolbar, ranging from 0-1000 with the default priority being 500. */
    priority?: number;
}
export interface ToolbarItemInternal extends CoreToolbarItemDefinition {
    description: string;
    name: string;
    isOpen: boolean;
    isPermissionGranted: boolean;
    priority?: number;
}
/**
 * Provides an abstract extensible toolbar service. Used to manage and perform actions to either the SmartEdit
 * application or the SmartEdit container.
 *
 * This class serves as an interface and should be extended, not instantiated.
 */
export declare abstract class IToolbarService {
    protected logService: LogService;
    private readonly permissionService;
    protected aliases: ToolbarItemInternal[];
    protected actions: TypedMap<() => void>;
    constructor(logService: LogService, permissionService: IPermissionService);
    getActions(): TypedMap<() => void>;
    getAliases(): ToolbarItemInternal[];
    /**
     * Takes an array of items and maps them internally for display and trigger through an internal callback key.
     * The action's properties are made available through the included template by a variable named 'item'.
     *
     * This context can be shown or hidden by calling the events `seConstantsModule.SHOW_TOOLBAR_ITEM_CONTEXT` and
     * `seConstantsModule.HIDE_TOOLBAR_ITEM_CONTEXT` respectively. Both the events need the key of the toolbar item as data.
     *
     * ### Example
     *
     *      crossFrameEventService.publish(SHOW_TOOLBAR_ITEM_CONTEXT, 'toolbar.item.key');
     *
     */
    addItems(_items: ToolbarItemDefinition[]): Promise<void>;
    _populateIsPermissionGranted(toolbarItem: ToolbarItemInternal): Promise<ToolbarItemInternal>;
    addAliases(items: ToolbarItemInternal[]): void;
    removeItemByKey(key: string): void;
    _removeItemOnInner(itemKey: string): void;
    removeAliasByKey(itemKey: string): void;
    triggerActionOnInner(action: {
        key: string;
    }): void;
}
