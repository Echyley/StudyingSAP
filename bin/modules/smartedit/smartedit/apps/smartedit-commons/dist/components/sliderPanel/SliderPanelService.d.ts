/// <reference types="jquery" />
import { SliderPanelConfiguration } from './interfaces';
/**
 * The SliderPanelService handles the initialization and the rendering of the se-slider-panel Angular component.
 */
export declare class SliderPanelService {
    private element;
    private window;
    private configuration;
    private yjQuery;
    sliderPanelConfiguration: SliderPanelConfiguration;
    inlineStyling: {
        container: CSSStyleDeclaration;
        content: CSSStyleDeclaration;
    };
    private sliderPanelDefaultConfiguration;
    private parent;
    private appendChildTarget;
    constructor(element: JQuery, window: Window, configuration: SliderPanelConfiguration, yjQuery: JQueryStatic);
    /**
     * This method sets the inline styling applied to the slider panel container according to the dimension and position values
     * of the parent element.
     */
    updateContainerInlineStyling(screenResized: boolean): void;
    private returningHigherZIndex;
    private initializeParentRawElement;
    private initializePanelConfiguration;
    private initializeInlineStyles;
    private initializeChildTarget;
    private append;
    private init;
}
