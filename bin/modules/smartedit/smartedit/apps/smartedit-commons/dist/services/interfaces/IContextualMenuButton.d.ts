import { Type } from '@angular/core';
import { TypedMap } from '@smart/utils';
import { IContextualMenuConfiguration } from './IContextualMenuConfiguration';
import { IFeature } from './IFeature';
import { IPrioritized } from './IPrioritized';
/**
 * A full representation of a component contextual menu,
 * specifying the layout of [buttons]{@link IContextualMenuButton}
 * to be displayed for a speicific smartedit component.
 */
export interface ContextualMenu {
    leftMenuItems: IContextualMenuButton[];
    moreMenuItems: IContextualMenuButton[];
}
/**
 * Payload passed to {@link /smartedit/injectables/ContextualMenuService.html#addItems addItems}, to describe a contextual menu button.
 */
export interface IContextualMenuButton extends IFeature, IPrioritized {
    /**
     * The action to be performed when clicking the menu item.
     * Action is an object that must contain exactly one of callback | callbacks | template | templateUrl<br />
     */
    action: {
        /**
         * Component to be displayed when the menu is clicked.
         */
        component?: Type<any>;
        /**
         * Map of DOM events occuring on the contextual menu button and callbacks to be invoked when they occur
         */
        callbacks?: TypedMap<(configuration?: IContextualMenuConfiguration, $event?: Event) => void>;
        /**
         * A function executed on clicking of the menu item. It is invoked with the component specific configuration.
         * @param configuration the smartedit component specific configuration
         * @param $event the yjQuery event triggering the callback
         */
        callback?(configuration?: IContextualMenuConfiguration, $event?: Event): void;
    };
    /**
     * The message key of the contextual menu item to be translated.
     */
    i18nKey?: string;
    /**
     * Contains the CSS classes used to style the contextual menu item
     */
    displayClass?: string;
    /**
     * Contains the CSS classes used to style the non-idle icon of the contextual menu item to be displayed.
     */
    displayIconClass?: string;
    /**
     * Contains the location of the smaller version of the icon to be displayed when the menu item is part of the More... menu options.
     */
    displaySmallIconClass?: string;
    /**
     * Contains the location of the idle icon of the contextual menu item to be displayed.
     */
    iconIdle?: string;
    /**
     * Contains the location of the non-idle icon of the contextual menu item to be displayed.
     */
    iconNonIdle?: string;
    /**
     * Adds optional css classes to the menu button.
     */
    customCss?: string;
    /**
     * Array of regular expressions matching component types eligible for this button
     */
    regexpKeys?: string[];
    /**
     * Entry that holds the condition function required to activate the menu item.
     */
    condition?(configuration: IContextualMenuConfiguration): boolean | Promise<boolean>;
}
