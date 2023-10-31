/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ComponentAttributes } from '../../di/AbstractDecorator';

/**
 * The smartedit component specific configuration being passed to {@link IContextualMenuService}
 * to retrieve the appropriate list of {@link IContextualMenuButton}
 * and being passed to condition and callback function of {@link IContextualMenuButton}
 */
export interface IContextualMenuConfiguration {
    /**
     * The map of all attributes prefixed with smartedit- collected on the DOM element
     */
    componentAttributes: ComponentAttributes;
    /**
     * The type code of the selected component.
     */
    componentType: string;
    /**
     * The ID of the selected component.
     */
    componentId: string;
    /**
     * The type code of the container of the component if applicable, this is optional.
     */
    containerType?: string;
    /**
     * The ID of the container of the component if applicable, this is optional.
     */
    containerId?: string;
    /**
     * The smarteditComponent id of the slot containing the component, null if a slot itself
     */
    slotId?: string;
    /**
     * The UUID id of the slot containing the component, null if a slot itself
     */
    slotUuid?: string;

    /**
     * The number of visible contextual menu items for a specified component.
     */
    iLeftBtns?: number;
    /**
     * The DOM element of selected component
     */
    element?: JQuery<HTMLElement>;

    isComponentHidden?: boolean;

    /**
     * The uuid of the selected component.
     */
    componentUuid?: string;
}
