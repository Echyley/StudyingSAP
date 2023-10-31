/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
export interface SliderPanelModalConfiguration {
    showDismissButton: boolean;
    title: string;
    save: SliderPanelAction;
    dismiss?: SliderPanelAction;
    cancel?: SliderPanelAction;
}

export interface SliderPanelConfiguration {
    modal?: SliderPanelModalConfiguration;
    /**
     *  CSS pattern used to select the element to which Slider Panel will be appended.
     *  It will start traversing from this element and search for "fd-dialog__content".
     *  If no such a parent has been found, the component will be appended to body.
     */
    cssSelector?: string;
    /**
     *   Used to indicate if a greyed-out overlay is to be displayed or not.
     */
    noGreyedOutOverlay?: boolean;
    /**
     *    Indicates the dimension of the container slider panel once it is displayed.
     */
    overlayDimension?: string;
    /**
     *   Specifies from which side of its container the slider panel slides out.
     */
    slideFrom?: string;
    /**
     *    Specifies whether the slider panel is to be shown by default.
     */
    displayedByDefault?: boolean;
    /**
     *     Indicates the z-index value to be applied on the slider panel.
     */
    zIndex?: string;
}

export interface SliderPanelAction {
    onClick: () => void;
    label: string;
    isDisabledFn: () => boolean;
}
